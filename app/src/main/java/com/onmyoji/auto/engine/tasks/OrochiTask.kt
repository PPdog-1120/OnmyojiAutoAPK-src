package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 八岐大蛇 (Orochi) — 御魂副本
 *
 * 对应 Python tasks/Orochi/script_task.py
 * 支持 leader / member / alone / wild 四种模式
 */
class OrochiTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from OrochiAssets) ==========
    private val I_OROCHI = RuleImage("orochi", "tasks/Orochi/o/o_orochi.png",
        intArrayOf(260, 274, 66, 66), intArrayOf(55, 104, 295, 406), 0.8f)
    private val I_FORM_TEAM = RuleImage("form_team", "tasks/Orochi/o/o_form_team.png",
        intArrayOf(957, 582, 100, 100), intArrayOf(957, 582, 100, 100), 0.8f)
    private val I_OROCHI_LOCK = RuleImage("orochi_lock", "tasks/Orochi/o/o_orochi_lock.png",
        intArrayOf(652, 657, 22, 26), intArrayOf(628, 644, 191, 62), 0.8f)
    private val I_OROCHI_UNLOCK = RuleImage("orochi_unlock", "tasks/Orochi/o/o_orochi_unlock.png",
        intArrayOf(652, 656, 21, 21), intArrayOf(624, 644, 188, 60), 0.8f)
    private val I_OROCHI_FIRE = RuleImage("orochi_fire", "tasks/Orochi/o/o_orochi_fire.png",
        intArrayOf(1133, 584, 110, 59), intArrayOf(1122, 572, 131, 124), 0.6f)
    private val I_SHI_RECORDS = RuleImage("shi_records", "tasks/Orochi/o/o_shi_records.png",
        intArrayOf(842, 569, 48, 45), intArrayOf(842, 569, 48, 45), 0.8f)
    private val I_PET_PRESENT = RuleImage("pet_present", "tasks/Orochi/o/o_pet_present.png",
        intArrayOf(873, 184, 62, 147), intArrayOf(873, 184, 62, 147), 0.8f)

    // 层级列表 (OCR-based)
    private val L_LAYER_LIST = arrayOf("壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾", "悲", "神", "虚")

    // 组件引用 — 使用已有完整实现的组件，不定义本地 stub
    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalInvite = GeneralInvite(context, device, config)
    private val generalRoom = GeneralRoom(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)

    // 通用战斗退出图片 (用于 exitBattle / isHomeOrExplore)
    private val I_EXIT = RuleImage("exit", "general_battle/gb/gb_exit.png",
        intArrayOf(14, 12, 43, 41), intArrayOf(14, 12, 43, 41), 0.8f)
    private val I_EXIT_ENSURE = RuleImage("exit_ensure", "general_battle/gb/gb_exit_ensure.png",
        intArrayOf(674, 388, 135, 63), intArrayOf(674, 388, 135, 63), 0.8f)
    private val I_FALSE = RuleImage("false", "general_battle/gb/gb_false.png",
        intArrayOf(413, 124, 100, 100), intArrayOf(413, 124, 100, 100), 0.8f)
    // 匹配中 (房间已解散)
    private val I_MATCHING = RuleImage("matching", "general_invite/gi/gi_matching.png",
        intArrayOf(51, 574, 52, 114), intArrayOf(51, 574, 52, 114), 0.8f)
    // 探索界面标识
    private val I_CHECK_EXPLORATION = RuleImage("check_exploration", "exploration/res_check_exploration.png",
        intArrayOf(640, 500, 100, 100), intArrayOf(640, 500, 100, 100), 0.8f)
    // 庭院标识
    private val I_GI_HOME = RuleImage("gi_home", "general_invite/gi/gi_gi_home.png",
        intArrayOf(361, 34, 34, 46), intArrayOf(361, 34, 34, 46), 0.8f)
    // 探索入口标识
    private val I_GI_EXPLORE = RuleImage("gi_explore", "general_invite/gi/gi_gi_explore.png",
        intArrayOf(1138, 119, 41, 48), intArrayOf(1138, 119, 41, 48), 0.8f)

    override suspend fun run() {
        log("=== 八岐大蛇任务开始 ===")

        // 御魂切换方式一
        if (config.orochiSwitchSoulEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.orochiSwitchGroupTeam)
        }
        // 御魂切换方式二
        if (config.orochiSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.orochiGroupName, config.orochiTeamName)
        }
        // 根据选层切换御魂
        orochiSwitchSoul()

        val limitCount = config.orochiLimitCount
        val limitTimeMinutes = config.orochiLimitTimeMinutes
        currentCount = 0

        if (!generalBattle.isInBattle(true)) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_main")
            if (config.orochiSoulBuffEnable) {
                generalBuff.openBuff()
                generalBuff.soul(true)
                generalBuff.closeBuff()
            }
        }

        var success = true
        when (config.orochiUserStatus) {
            "leader" -> success = runLeader()
            "member" -> success = runMember()
            "alone" -> runAlone()
            "wild" -> success = runWild()
            else -> log("Unknown user status")
        }

        if (config.orochiSoulBuffEnable) {
            generalBuff.openBuff()
            generalBuff.soul(false)
            generalBuff.closeBuff()
        }

        log("=== 八岐大蛇完成, success=$success ===")
    }

    private suspend fun orochiEnter(): Boolean {
        log("Enter orochi")
        while (true) {
            val img = screenshot() ?: continue
            if (I_FORM_TEAM.match(img, context).matched) return true
            appearThenClick(I_OROCHI, img, 1000)
        }
    }

    private suspend fun checkLock(lock: Boolean) {
        log("Check lock: $lock")
        if (lock) {
            repeat(20) {
                val img = screenshot() ?: return
                if (I_OROCHI_LOCK.match(img, context).matched) return
                appearThenClick(I_OROCHI_UNLOCK, img, 1000)
            }
        } else {
            repeat(20) {
                val img = screenshot() ?: return
                if (I_OROCHI_UNLOCK.match(img, context).matched) return
                appearThenClick(I_OROCHI_LOCK, img, 1000)
            }
        }
    }

    private suspend fun runLeader(): Boolean {
        log("Start run leader")
        // 强制锁定阵容 (参考 Python: https://github.com/runhey/OnmyojiAutoScript/issues/592)
        config.orochiBattleConfig.lockTeamEnable = true

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_soul_zones")
        orochiEnter()
        checkLayer(config.orochiLayer)
        checkLock(config.orochiBattleConfig.lockTeamEnable)

        // 创建队伍
        log("Create team")
        while (true) {
            val img = screenshot() ?: continue
            if (isInRoom()) break
            appearThenClick(I_FORM_TEAM, img, 1000)
        }
        generalRoom.createRoom()
        generalRoom.ensurePrivate()
        generalRoom.createEnsure()

        var success = true
        var isFirst = true

        while (true) {
            val img = screenshot() ?: continue

            // 战斗后邀请队友弹窗处理
            if (generalInvite.checkAndInvite(config.orochiDefaultInvite)) continue
            // 检查猫咪奖励
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue

            if (currentCount >= config.orochiLimitCount) {
                if (isInRoom()) { log("Orochi count limit out"); break }
            }
            if (isTimeUp(config.orochiLimitTimeMinutes)) {
                if (isInRoom()) { log("Orochi time limit out"); break }
            }

            // 如果不在房间，检查房间是否已解散
            if (!isInRoom()) {
                if (isRoomDead()) { log("Orochi task failed"); success = false; break }
                continue
            }

            // 邀请队友并战斗
            if (!isFirst) {
                if (generalInvite.runInvite(config.orochiInviteConfig)) {
                    generalBattle.runGeneralBattle(config.orochiBattleConfig)
                } else {
                    log("Invite failed and exit this orochi task")
                    success = false; break
                }
            }

            if (isFirst) {
                if (!generalInvite.runInvite(config.orochiInviteConfig, isFirst = true)) {
                    log("Invite failed and exit this orochi task")
                    success = false; break
                } else {
                    isFirst = false
                    generalBattle.runGeneralBattle(config.orochiBattleConfig)
                }
            }
        }

        // 退出房间和组队界面
        generalInvite.exitRoom()
        generalRoom.exitTeam()
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")
        return success
    }

    private suspend fun runMember(): Boolean {
        log("Start run member")
        gameUi.uiGetCurrentPage()

        // 添加卡死检测标记
        device.stuckRecordAdd("BATTLE_STATUS_S")

        while (true) {
            val img = screenshot() ?: continue
            // 检查猫咪奖励
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (currentCount >= config.orochiLimitCount) { log("Orochi count limit out"); break }
            if (isTimeUp(config.orochiLimitTimeMinutes)) { log("Orochi time limit out"); break }
            // 接受邀请
            if (generalInvite.checkThenAccept()) continue
            if (isInRoom()) {
                device.stuckRecordClear()
                // 等待队长开启战斗
                if (generalInvite.waitBattle(config.orochiWaitTime)) {
                    generalBattle.runGeneralBattle(config.orochiBattleConfig)
                } else break
            } else {
                // 队长秒开时，检测是否已进入战斗
                val tookOver = generalBattle.checkTakeOverBattle(false, config.orochiBattleConfig)
                if (tookOver == true) continue
            }
        }

        while (true) {
            // 有一种情况是要退出的，但队长邀请了进入战斗加载界面
            if (isHomeOrExplore()) break
            // 如果在房间就退出
            generalInvite.exitRoom()
            // 如果还在战斗中，就退出战斗
            exitBattle()
        }
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")
        return true
    }

    private suspend fun runAlone() {
        log("Start run alone")
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_soul_zones")
        orochiEnter()
        checkLayer(config.orochiLayer)
        checkLock(config.orochiLockTeam)

        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (!isInOrochi(img)) continue
            if (currentCount >= config.orochiLimitCount) { log("Orochi count limit out"); break }
            if (isTimeUp(config.orochiLimitTimeMinutes)) { log("Orochi time limit out"); break }

            while (true) {
                val frame = screenshot() ?: continue
                appearThenClick(I_OROCHI_FIRE, frame, 1000)
                if (!I_OROCHI_FIRE.match(frame, context).matched) {
                    generalBattle.runGeneralBattle(config.orochiBattleConfig)
                    break
                }
            }
        }

        while (true) {
            val img = screenshot() ?: break
            if (!I_FORM_TEAM.match(img, context).matched) break
            appearThenClick(I_UI_BACK_YELLOW, img, 1000)
        }
        gameUi.uiGoto("page_main")
    }

    private suspend fun runWild(): Boolean {
        log("Start run wild")
        // 已经在战斗中不必初始化，保证已经组队开始战斗的情况下可以自动执行后续任务
        if (!generalBattle.isInBattle(true)) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_soul_zones")
            orochiEnter()
            checkLayer(config.orochiLayer)
            checkLock(config.orochiLockTeam)
            while (true) {
                val img = screenshot() ?: continue
                if (isInRoom()) break
                appearThenClick(I_FORM_TEAM, img, 1000)
            }
            generalRoom.createRoom()
            generalRoom.ensurePublic()
            generalRoom.createEnsure()
        }

        var success = true
        while (true) {
            val img = screenshot() ?: continue
            // 战斗后邀请队友弹窗处理
            if (generalInvite.checkAndInvite(config.orochiDefaultInvite)) continue
            // 检查猫咪奖励
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (currentCount >= config.orochiLimitCount) {
                if (isInRoom()) { log("Orochi count limit out"); break }
            }
            if (isTimeUp(config.orochiLimitTimeMinutes)) {
                if (isInRoom()) { log("Orochi time limit out"); break }
            }
            if (!isInRoom()) {
                if (isRoomDead()) { log("Orochi task failed"); success = false; break }
                continue
            }

            // 点击挑战
            log("Wait for starting")
            while (true) {
                val frame = screenshot() ?: continue
                // 在进入战斗前必然会出现挑战界面，点击失败必须重复点击防止卡住
                if (!generalBattle.isInBattle(false)) {
                    if (!isInRoom() && isRoomDead()) break
                    if (!appearThenClick(I_OROCHI_WILD_FIRE, frame, 1000, 0.8f)) continue
                }
                val frame2 = screenshot() ?: continue
                if (!I_OROCHI_WILD_FIRE.match(frame2, context).matched) {
                    generalBattle.runGeneralBattle(config.orochiBattleConfig)
                    break
                }
            }
        }

        // 退出房间和组队界面
        generalInvite.exitRoom()
        generalRoom.exitTeam()
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")
        return success
    }

    // ========== 辅助方法 ==========

    private val I_OROCHI_WILD_FIRE = RuleImage("orochi_wild_fire", "tasks/Orochi/o/o_orochi_fire.png",
        intArrayOf(1133, 584, 110, 59), intArrayOf(1122, 572, 131, 124), 0.8f)

    private val I_UI_BACK_YELLOW = RuleImage("ui_back_yellow", "tasks/GameUi/res/res_ui_back_yellow.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)

    private fun isInOrochi(img: android.graphics.Bitmap): Boolean {
        return I_OROCHI_FIRE.match(img, context).matched
    }

    private fun isInRoom(): Boolean {
        val img = screenshot() ?: return false
        return I_FORM_TEAM.match(img, context).matched
    }

    /**
     * 判断房间是否已解散 — 对应 Python is_room_dead()
     * 如果在探索界面或匹配中界面，说明房间已解散
     * 双次确认防止误判
     */
    private fun isRoomDead(): Boolean {
        kotlinx.coroutines.runBlocking { delay(500) }
        val img = screenshot() ?: return false
        if (I_MATCHING.match(img, context).matched || I_CHECK_EXPLORATION.match(img, context).matched) {
            kotlinx.coroutines.runBlocking { delay(500) }
            val img2 = screenshot() ?: return false
            return I_MATCHING.match(img2, context).matched || I_CHECK_EXPLORATION.match(img2, context).matched
        }
        return false
    }

    /**
     * 判断是否在庭院或探索界面 — 对应 Python is_home_or_explore()
     */
    private fun isHomeOrExplore(): Boolean {
        val img = screenshot() ?: return false
        return I_GI_HOME.match(img, context).matched || I_GI_EXPLORE.match(img, context).matched
    }

    /**
     * 退出战斗 — 强制退出正在进行的战斗
     */
    private suspend fun exitBattle(): Boolean {
        val img = screenshot() ?: return false
        if (!I_EXIT.match(img, context).matched) return false
        log("Exit battle")
        // 点击返回
        while (true) {
            val frame = screenshot() ?: continue
            if (appearThenClick(I_EXIT, frame, 1500)) continue
            if (appear(I_EXIT_ENSURE, frame)) break
        }
        // 点击返回确认
        while (true) {
            val frame = screenshot() ?: continue
            if (appearThenClick(I_EXIT_ENSURE, frame, 1500)) continue
            if (appearThenClick(I_FALSE, frame, 1500)) continue
            if (!appear(I_EXIT, frame)) break
        }
        return true
    }

    /**
     * 选择层级 — 对应 Python check_layer()
     * 使用 OCR 在层级列表中查找并点击目标层
     */
    private suspend fun checkLayer(layer: String): Boolean {
        log("Select layer: $layer")
        delay(1000)

        val maxRetries = 10
        for (i in 0 until maxRetries) {
            val layerIndex = L_LAYER_LIST.indexOf(layer)
            if (layerIndex < 0) {
                log("Unknown layer: $layer")
                return false
            }

            // 层级列表在屏幕右侧，每个条目约 50px 高
            val listStartY = 150
            val listItemHeight = 50
            val clickX = 850
            val clickY = listStartY + layerIndex * listItemHeight

            // 如果目标超出当前可见区域，需要滑动
            if (clickY > 650) {
                device.swipe(clickX, 500, clickX, 200)
                delay(1000)
                continue
            }

            device.click(clickX, clickY)
            delay(500)

            // 验证是否选中了正确的层级 (检查挑战按钮是否出现)
            val verifyImg = screenshot() ?: continue
            if (I_OROCHI_FIRE.match(verifyImg, context).matched) {
                log("Layer $layer selected")
                return true
            }
        }
        log("Layer selection may have failed, continuing anyway")
        return true
    }

    private suspend fun orochiSwitchSoul() {
        if (!config.orochiAutoSwitchSoul) return
        val groupTeam = when (config.orochiLayer) {
            "拾层" -> config.orochiTenSwitch
            "悲鸣" -> config.orochiElevenSwitch
            "神罚" -> config.orochiTwelveSwitch
            "虚无" -> config.orochiThirteenSwitch
            else -> return
        }
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_shikigami_records")
        switchSoul.runSwitchSoul(groupTeam)
    }
}

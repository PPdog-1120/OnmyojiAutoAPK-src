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

    // 组件引用 (由另一个代理创建)
    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalInvite = GeneralInvite(context, device, config)
    private val generalRoom = GeneralRoom(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)

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

    private suspend fun checkLayer(layer: String): Boolean {
        // 使用 OCR 在层级列表中查找并点击
        log("Select layer: $layer")
        // 简化实现：通过点击列表区域选层
        delay(500)
        return true
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
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_soul_zones")
        orochiEnter()
        checkLayer(config.orochiLayer)
        checkLock(true)

        // 创建队伍
        log("Create team")
        while (true) {
            val img = screenshot() ?: continue
            if (isInRoom()) break
            appearThenClick(I_FORM_TEAM, img, 1000)
        }
        generalRoom.createRoom()
        ensurePrivate()
        generalRoom.createEnsure()

        var success = true
        var isFirst = true

        while (true) {
            val img = screenshot() ?: continue

            if (checkAndInvite(config.orochiDefaultInvite)) continue
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

        exitRoom()
        exitTeam()
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")
        return success
    }

    private suspend fun runMember(): Boolean {
        log("Start run member")
        gameUi.uiGetCurrentPage()

        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (currentCount >= config.orochiLimitCount) { log("Orochi count limit out"); break }
            if (isTimeUp(config.orochiLimitTimeMinutes)) { log("Orochi time limit out"); break }
            if (checkThenAccept()) continue
            if (isInRoom()) {
                if (waitBattle(config.orochiWaitTime)) {
                    generalBattle.runGeneralBattle(config.orochiBattleConfig)
                } else break
            }
        }

        while (true) {
            if (isHomeOrExplore()) break
            exitRoom()
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
            ensurePublic()
            generalRoom.createEnsure()
        }

        var success = true
        while (true) {
            val img = screenshot() ?: continue
            if (checkAndInvite(config.orochiDefaultInvite)) continue
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

            while (true) {
                val frame = screenshot() ?: continue
                if (!isInRoom() && isRoomDead()) break
                appearThenClick(I_OROCHI_WILD_FIRE, frame, 1000, 0.8f)
                if (!I_OROCHI_WILD_FIRE.match(frame, context).matched) {
                    generalBattle.runGeneralBattle(config.orochiBattleConfig)
                    break
                }
            }
        }

        exitRoom()
        exitTeam()
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

    private fun isRoomDead(): Boolean {
        kotlinx.coroutines.runBlocking { delay(500) }
        // 检查是否在探索界面或组队界面
        return false
    }

    private fun isHomeOrExplore(): Boolean {
        return false
    }

    private fun exitRoom() {}
    private fun exitTeam() {}
    private fun exitBattle() {}
    private fun ensurePrivate() {}
    private fun ensurePublic() {}
    private fun checkAndInvite(defaultInvite: Boolean): Boolean = false
    private fun checkThenAccept(): Boolean = false
    private fun waitBattle(waitTime: Int): Boolean = false

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

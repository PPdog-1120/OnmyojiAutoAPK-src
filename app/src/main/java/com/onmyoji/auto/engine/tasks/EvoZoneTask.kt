package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 觉醒副本 (EvoZone)
 * 对应 Python tasks/EvoZone/script_task.py
 *
 * 支持 leader / member / alone / wild 四种模式
 */
class EvoZoneTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from EvoZoneAssets) ==========
    private val I_FIRE_KIRIN = RuleImage("fire_kirin", "tasks/EvoZone/o/o_fire_kirin.png",
        intArrayOf(104, 120, 217, 325), intArrayOf(104, 120, 217, 325), 0.8f)
    private val I_WIND_KIRIN = RuleImage("wind_kirin", "tasks/EvoZone/o/o_wind_kirin.png",
        intArrayOf(423, 116, 201, 250), intArrayOf(423, 116, 201, 250), 0.8f)
    private val I_WATER_KIRIN = RuleImage("water_kirin", "tasks/EvoZone/o/o_water_kirin.png",
        intArrayOf(728, 115, 201, 342), intArrayOf(728, 115, 201, 342), 0.8f)
    private val I_LIGHTNING_KIRIN = RuleImage("lightning_kirin", "tasks/EvoZone/o/o_lightning_kirin.png",
        intArrayOf(1016, 99, 218, 270), intArrayOf(1016, 99, 218, 270), 0.8f)
    private val I_FORM_TEAM = RuleImage("form_team", "tasks/EvoZone/o/o_form_team.png",
        intArrayOf(959, 580, 100, 100), intArrayOf(916, 572, 156, 130), 0.8f)
    private val I_EVOZONE_LOCK = RuleImage("evozone_lock", "tasks/EvoZone/o/o_evozone_lock.png",
        intArrayOf(700, 655, 31, 32), intArrayOf(690, 648, 53, 54), 0.8f)
    private val I_EVOZONE_UNLOCK = RuleImage("evozone_unlock", "tasks/EvoZone/o/o_evozone_unlock.png",
        intArrayOf(701, 657, 26, 29), intArrayOf(695, 650, 43, 45), 0.8f)
    private val I_EVOZONE_FIRE = RuleImage("evozone_fire", "tasks/EvoZone/o/o_evozone_fire.png",
        intArrayOf(1127, 568, 131, 124), intArrayOf(1127, 568, 131, 124), 0.6f)
    private val I_PET_PRESENT = RuleImage("pet_present", "tasks/EvoZone/o/o_pet_present.png",
        intArrayOf(873, 184, 62, 147), intArrayOf(873, 184, 62, 147), 0.8f)
    private val I_MATCHING = RuleImage("matching", "tasks/GeneralRoom/res/res_matching.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_CHECK_EXPLORATION = RuleImage("check_exploration", "tasks/Exploration/res/res_check_exploration.png",
        intArrayOf(1133, 124, 47, 43), intArrayOf(1100, 100, 180, 100), 0.7f)
    private val I_CHECK_TEAM = RuleImage("check_team", "tasks/GeneralRoom/res/res_check_team.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_BACK_Y = RuleImage("back_y", "tasks/GameUi/res/res_ui_back_yellow.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalInvite = GeneralInvite(context, device, config)
    private val generalRoom = GeneralRoom(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)
    private val teamHelper = TeamTaskHelper(context, device, config)

    override suspend fun run() {
        log("=== 觉醒副本任务开始 ===")

        // 切换御魂
        if (config.evoZoneSwitchSoulEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.evoZoneSwitchGroupTeam)
        }
        if (config.evoZoneSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.evoZoneGroupName, config.evoZoneTeamName)
        }

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")

        if (config.evoZoneSoulBuffEnable) {
            generalBuff.openBuff()
            generalBuff.awake(true)
            generalBuff.closeBuff()
        }

        var success = true
        when (config.evoZoneUserStatus) {
            "leader" -> success = runLeader()
            "member" -> success = runMember()
            "alone" -> runAlone()
            else -> log("Unknown user status")
        }

        if (config.evoZoneSoulBuffEnable) {
            generalBuff.openBuff()
            generalBuff.awake(false)
            generalBuff.closeBuff()
        }

        log("=== 觉醒副本完成, success=$success ===")
    }

    private suspend fun evozoneEnter(): Boolean {
        log("Enter evozone")
        val kirinType = when (config.evoZoneKirinType) {
            "fire" -> I_FIRE_KIRIN
            "wind" -> I_WIND_KIRIN
            "water" -> I_WATER_KIRIN
            else -> I_LIGHTNING_KIRIN
        }
        while (true) {
            val img = screenshot() ?: continue
            if (I_FORM_TEAM.match(img, context).matched) return true
            appearThenClick(kirinType, img, 1000)
        }
    }

    private suspend fun checkLock(lock: Boolean) {
        log("Check lock: $lock")
        if (lock) {
            repeat(20) {
                val img = screenshot() ?: return
                if (I_EVOZONE_LOCK.match(img, context).matched) return
                appearThenClick(I_EVOZONE_UNLOCK, img, 1000)
            }
        } else {
            repeat(20) {
                val img = screenshot() ?: return
                if (I_EVOZONE_UNLOCK.match(img, context).matched) return
                appearThenClick(I_EVOZONE_LOCK, img, 1000)
            }
        }
    }

    private suspend fun runLeader(): Boolean {
        log("Start run leader")
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_awake_zones")
        evozoneEnter()
        checkLock(config.evoZoneLockTeam)

        // 创建队伍
        while (true) {
            val img = screenshot() ?: continue
            if (I_CHECK_TEAM.match(img, context).matched) break
            appearThenClick(I_FORM_TEAM, img, 1000)
        }
        generalRoom.createRoom()
        generalRoom.createEnsure()

        var success = true
        var isFirst = true

        while (true) {
            val img = screenshot() ?: continue
            if (checkAndInvite(config.evoZoneDefaultInvite)) continue
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (currentCount >= config.evoZoneLimitCount) { log("EvoZone count limit out"); break }
            if (isTimeUp(config.evoZoneLimitTimeMinutes)) { log("EvoZone time limit out"); break }

            if (!isInRoom()) {
                if (isRoomDead()) { log("EvoZone task failed"); success = false; break }
                continue
            }

            if (!isFirst) {
                if (generalInvite.runInvite(config.evoZoneInviteConfig)) {
                    generalBattle.runGeneralBattle(config.evoZoneBattleConfig)
                } else {
                    log("Invite failed"); success = false; break
                }
            }
            if (isFirst) {
                if (!generalInvite.runInvite(config.evoZoneInviteConfig, isFirst = true)) {
                    log("Invite failed"); success = false; break
                } else {
                    isFirst = false
                    generalBattle.runGeneralBattle(config.evoZoneBattleConfig)
                }
            }
        }

        exitRoom(); exitTeam()
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main")
        return success
    }

    private suspend fun runMember(): Boolean {
        log("Start run member")
        gameUi.uiGetCurrentPage()
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (currentCount >= config.evoZoneLimitCount) { log("EvoZone count limit out"); break }
            if (isTimeUp(config.evoZoneLimitTimeMinutes)) { log("EvoZone time limit out"); break }
            if (checkThenAccept()) continue
            if (isInRoom()) {
                if (waitBattle(config.evoZoneWaitTime)) generalBattle.runGeneralBattle(config.evoZoneBattleConfig)
                else break
            }
        }
        while (true) { if (isHomeOrExplore()) break; exitRoom(); exitBattle() }
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main")
        return true
    }

    private suspend fun runAlone() {
        log("Start run alone")
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_awake_zones")
        evozoneEnter()
        checkLock(config.evoZoneLockTeam)

        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (!isInEvozone()) continue
            if (currentCount >= config.evoZoneLimitCount) { log("EvoZone count limit out"); break }
            if (isTimeUp(config.evoZoneLimitTimeMinutes)) { log("EvoZone time limit out"); break }

            while (true) {
                val frame = screenshot() ?: continue
                appearThenClick(I_EVOZONE_FIRE, frame, 1000)
                if (!I_EVOZONE_FIRE.match(frame, context).matched) {
                    generalBattle.runGeneralBattle(config.evoZoneBattleConfig)
                    break
                }
            }
        }

        while (true) {
            val img = screenshot() ?: break
            if (!I_FORM_TEAM.match(img, context).matched) break
            appearThenClick(I_BACK_Y, img, 1000)
        }
        gameUi.uiGoto("page_main")
    }

    private fun isInEvozone(): Boolean {
        val img = screenshot() ?: return false
        return I_EVOZONE_FIRE.match(img, context).matched
    }

    private fun isInRoom(): Boolean {
        val img = screenshot() ?: return false
        return I_FORM_TEAM.match(img, context).matched
    }

    private fun isRoomDead(): Boolean = teamHelper.isRoomDead { screenshot() }
    private fun isHomeOrExplore(): Boolean = teamHelper.isHomeOrExplore { screenshot() }
    private suspend fun exitRoom() { teamHelper.exitRoom() }
    private suspend fun exitTeam() { teamHelper.exitTeam() }
    private suspend fun exitBattle(): Boolean = teamHelper.exitBattle { screenshot() }
    private suspend fun checkAndInvite(defaultInvite: Boolean): Boolean = teamHelper.checkAndInvite(defaultInvite)
    private suspend fun checkThenAccept(): Boolean = teamHelper.checkThenAccept()
    private suspend fun waitBattle(waitTime: Int): Boolean = teamHelper.waitBattle(waitTime)
}

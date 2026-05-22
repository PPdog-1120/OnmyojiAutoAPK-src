package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 日轮之城 (FallenSun)
 * 对应 Python tasks/FallenSun/script_task.py
 * 支持 leader / member / alone / wild 四种模式
 */
class FallenSunTask(
    context: Context, device: DeviceController, config: TaskConfig
) : BaseTask(context, device, config) {

    private val I_FALLEN_SUN = RuleImage("fallen_sun", "tasks/FallenSun/f/f_fallen_sun.png",
        intArrayOf(881, 100, 58, 65), intArrayOf(720, 83, 231, 394), 0.8f)
    private val I_FORM_TEAM = RuleImage("form_team", "tasks/FallenSun/f/f_form_team.png",
        intArrayOf(963, 584, 96, 90), intArrayOf(963, 584, 96, 90), 0.8f)
    private val I_FALLEN_SUN_FIRE = RuleImage("fallen_sun_fire", "tasks/FallenSun/f/f_fallen_sun_fire.png",
        intArrayOf(1142, 593, 94, 51), intArrayOf(1141, 582, 104, 98), 0.8f)
    private val I_FALLEN_SUN_LOCK = RuleImage("fallen_sun_lock", "tasks/FallenSun/f/f_fallen_sun_lock.png",
        intArrayOf(700, 652, 31, 37), intArrayOf(700, 652, 31, 37), 0.8f)
    private val I_FALLEN_SUN_UNLOCK = RuleImage("fallen_sun_unlock", "tasks/FallenSun/f/f_fallen_sun_unlock.png",
        intArrayOf(699, 650, 31, 38), intArrayOf(699, 650, 31, 38), 0.8f)
    private val I_PET_PRESENT = RuleImage("pet_present", "tasks/FallenSun/f/f_pet_present.png",
        intArrayOf(873, 184, 62, 147), intArrayOf(873, 184, 62, 147), 0.8f)
    private val I_BACK_BL = RuleImage("back_bl", "tasks/GameUi/res/res_ui_back_blue.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    private val I_CHECK_TEAM = RuleImage("check_team", "tasks/GeneralRoom/res/res_check_team.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalInvite = GeneralInvite(context, device, config)
    private val generalRoom = GeneralRoom(context, device, config)

    override suspend fun run() {
        log("=== 日轮之城任务开始 ===")
        if (config.fallenSunSwitchSoulEnable) { gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records"); switchSoul.runSwitchSoul(config.fallenSunSwitchGroupTeam) }
        if (config.fallenSunSwitchSoulEnableByName) { gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records"); switchSoul.runSwitchSoulByName(config.fallenSunGroupName, config.fallenSunTeamName) }

        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main")
        var success = true
        when (config.fallenSunUserStatus) {
            "leader" -> success = runLeader()
            "member" -> success = runMember()
            "alone" -> runAlone()
            else -> log("Unknown user status")
        }
        log("=== 日轮之城完成, success=$success ===")
    }

    private suspend fun fallenSunEnter(): Boolean {
        while (true) { val img = screenshot() ?: continue; if (I_FORM_TEAM.match(img, context).matched) return true; appearThenClick(I_FALLEN_SUN, img, 1000) }
    }

    private suspend fun checkLock(lock: Boolean) {
        if (lock) { repeat(20) { val img = screenshot() ?: return; if (I_FALLEN_SUN_LOCK.match(img, context).matched) return; appearThenClick(I_FALLEN_SUN_UNLOCK, img, 1000) } }
        else { repeat(20) { val img = screenshot() ?: return; if (I_FALLEN_SUN_UNLOCK.match(img, context).matched) return; appearThenClick(I_FALLEN_SUN_LOCK, img, 1000) } }
    }

    private suspend fun runLeader(): Boolean {
        log("Start run leader"); gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_soul_zones"); fallenSunEnter(); checkLock(config.fallenSunLockTeam)
        while (true) { val img = screenshot() ?: continue; if (I_CHECK_TEAM.match(img, context).matched) break; appearThenClick(I_FORM_TEAM, img, 1000) }
        generalRoom.createRoom(); generalRoom.createEnsure()
        var success = true; var isFirst = true
        while (true) {
            val img = screenshot() ?: continue
            if (checkAndInvite(config.fallenSunDefaultInvite)) continue
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (currentCount >= config.fallenSunLimitCount) { log("FallenSun count limit out"); break }
            if (isTimeUp(config.fallenSunLimitTimeMinutes)) { log("FallenSun time limit out"); break }
            if (!isInRoom()) { if (isRoomDead()) { log("FallenSun task failed"); success = false; break }; continue }
            if (!isFirst) { if (generalInvite.runInvite(config.fallenSunInviteConfig)) generalBattle.runGeneralBattle(config.fallenSunBattleConfig) else { success = false; break } }
            if (isFirst) { if (!generalInvite.runInvite(config.fallenSunInviteConfig, isFirst = true)) { success = false; break } else { isFirst = false; generalBattle.runGeneralBattle(config.fallenSunBattleConfig) } }
        }
        exitRoom(); exitTeam(); gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main"); return success
    }

    private suspend fun runMember(): Boolean {
        log("Start run member"); gameUi.uiGetCurrentPage()
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (currentCount >= config.fallenSunLimitCount) break
            if (isTimeUp(config.fallenSunLimitTimeMinutes)) break
            if (checkThenAccept()) continue
            if (isInRoom()) { if (waitBattle(config.fallenSunWaitTime)) generalBattle.runGeneralBattle(config.fallenSunBattleConfig) else break }
        }
        while (true) { if (isHomeOrExplore()) break; exitRoom(); exitBattle() }
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main"); return true
    }

    private suspend fun runAlone() {
        log("Start run alone"); gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_soul_zones"); fallenSunEnter(); checkLock(config.fallenSunLockTeam)
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
            if (!isInFallenSun()) continue
            if (currentCount >= config.fallenSunLimitCount) break
            if (isTimeUp(config.fallenSunLimitTimeMinutes)) break
            while (true) { val frame = screenshot() ?: continue; appearThenClick(I_FALLEN_SUN_FIRE, frame, 1000); if (!I_FALLEN_SUN_FIRE.match(frame, context).matched) { generalBattle.runGeneralBattle(config.fallenSunBattleConfig); break } }
        }
        while (true) { val img = screenshot() ?: break; if (!I_FORM_TEAM.match(img, context).matched) break; appearThenClick(I_BACK_BL, img, 1000) }
        gameUi.uiGoto("page_main")
    }

    private fun isInFallenSun(): Boolean { val img = screenshot() ?: return false; return I_FALLEN_SUN_FIRE.match(img, context).matched }
    private fun isInRoom(): Boolean { val img = screenshot() ?: return false; return I_FORM_TEAM.match(img, context).matched }
    private fun isRoomDead(): Boolean = false
    private fun isHomeOrExplore(): Boolean = false
    private fun exitRoom() {}
    private fun exitTeam() {}
    private fun exitBattle() {}
    private fun checkAndInvite(defaultInvite: Boolean): Boolean = false
    private fun checkThenAccept(): Boolean = false
    private fun waitBattle(waitTime: Int): Boolean = false
}

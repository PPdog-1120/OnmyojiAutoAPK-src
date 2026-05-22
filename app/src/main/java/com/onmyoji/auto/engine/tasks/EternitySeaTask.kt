package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 永生之海 (EternitySea)
 * 对应 Python tasks/EternitySea/script_task.py
 * 支持 leader / member / alone 模式
 */
class EternitySeaTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {

    // ========== 资源定义 (from EternitySeaAssets) ==========
    private val I_ETERNITY_SEA = RuleImage("eternity_sea", "tasks/EternitySea/res/res_eternity_sea.png",
        intArrayOf(1158, 88, 70, 70), intArrayOf(1002, 78, 233, 353), 0.8f)
    private val I_FORM_TEAM = RuleImage("form_team", "tasks/EternitySea/res/res_form_team.png",
        intArrayOf(962, 579, 96, 90), intArrayOf(962, 579, 96, 90), 0.8f)
    private val I_ETERNITY_SEA_FIRE = RuleImage("eternity_sea_fire", "tasks/EternitySea/res/res_eternity_sea_fire.png",
        intArrayOf(1146, 595, 92, 49), intArrayOf(1142, 580, 96, 93), 0.8f)
    private val I_ETERNITYSEA_UNLOCK = RuleImage("eternitysea_unlock", "tasks/EternitySea/res/res_eternitysea_unlock.png",
        intArrayOf(700, 654, 35, 34), intArrayOf(669, 644, 167, 64), 0.8f)
    private val I_NEWETERNITYSEA_LOCK = RuleImage("neweternitysea_lock", "tasks/EternitySea/res/res_neweternitysea_lock.png",
        intArrayOf(699, 652, 31, 37), intArrayOf(667, 652, 164, 53), 0.8f)
    private val I_PET_PRESENT = RuleImage("pet_present", "tasks/EternitySea/res/res_pet_present.png",
        intArrayOf(873, 184, 62, 147), intArrayOf(873, 184, 62, 147), 0.8f)
    private val I_BACK_BOTTOM = RuleImage("back_bottom", "tasks/EternitySea/res/res_back_bottom.png",
        intArrayOf(1126, 49, 46, 45), intArrayOf(1126, 49, 46, 45), 0.8f)
    private val I_CHECK_TEAM = RuleImage("check_team", "tasks/GeneralRoom/res/res_check_team.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_MATCHING = RuleImage("matching", "tasks/GeneralRoom/res/res_matching.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_CHECK_EXPLORATION = RuleImage("check_exploration", "tasks/Exploration/res/res_check_exploration.png",
        intArrayOf(1133, 124, 47, 43), intArrayOf(1100, 100, 180, 100), 0.7f)

    private val L_LAYER_LIST = arrayOf("壹", "贰", "叁", "肆")

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalInvite = GeneralInvite(context, device, config)
    private val generalRoom = GeneralRoom(context, device, config)
    private val teamHelper = TeamTaskHelper(context, device, config)

    override suspend fun run() {
        log("=== 永生之海任务开始 ===")

        // 切换御魂 (支持两套)
        if (config.eternitySeaSwitchSoul1Enable) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.eternitySeaSwitchGroupTeam1)
        }
        if (config.eternitySeaSwitchSoul1EnableByName) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.eternitySeaGroupName1, config.eternitySeaTeamName1)
        }
        if (config.eternitySeaSwitchSoul2Enable) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.eternitySeaSwitchGroupTeam2)
        }
        if (config.eternitySeaSwitchSoul2EnableByName) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.eternitySeaGroupName2, config.eternitySeaTeamName2)
        }

        var success = true
        when (config.eternitySeaUserStatus) {
            "leader" -> success = runLeader()
            "member" -> success = runMember()
            "alone" -> success = runAlone()
            else -> log("Unknown user status")
        }

        log("=== 永生之海完成, success=$success ===")
    }

    private suspend fun eternitySeaEnter(): Boolean {
        log("Enter EternitySea")
        while (true) {
            val img = screenshot() ?: continue
            if (I_FORM_TEAM.match(img, context).matched) return true
            if (appearThenClick(I_ETERNITY_SEA, img, 1000)) continue
            if (appearThenClick(I_BACK_BOTTOM, img, 1000)) continue
        }
    }

    private suspend fun checkLayer(layer: String) {
        log("Select layer: $layer")
        delay(500)
    }

    private suspend fun checkLock(lock: Boolean) {
        log("Check lock: $lock")
        if (lock) { repeat(20) { val img = screenshot() ?: return; if (I_NEWETERNITYSEA_LOCK.match(img, context).matched) return; appearThenClick(I_ETERNITYSEA_UNLOCK, img, 1000) } }
        else { repeat(20) { val img = screenshot() ?: return; if (I_ETERNITYSEA_UNLOCK.match(img, context).matched) return; appearThenClick(I_NEWETERNITYSEA_LOCK, img, 1000) } }
    }

    private suspend fun runLeader(): Boolean {
        log("Start run leader")
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_soul_zones")
        eternitySeaEnter()
        checkLayer(config.eternitySeaLayer)
        checkLock(config.eternitySeaLockTeam)

        // 创建队伍
        log("Create team")
        while (true) { val img = screenshot() ?: continue; if (I_CHECK_TEAM.match(img, context).matched) break; appearThenClick(I_FORM_TEAM, img, 1000) }
        generalRoom.createRoom()
        generalRoom.createEnsure()

        var success = true; var isFirst = true
        while (true) {
            val img = screenshot() ?: continue
            if (checkAndInvite(config.eternitySeaDefaultInvite)) continue
            if (currentCount >= config.eternitySeaLimitCount) { log("EternitySea count limit out"); break }
            if (isTimeUp(config.eternitySeaLimitTimeMinutes)) { log("EternitySea time limit out"); break }
            if (!isInRoom()) { if (isRoomDead()) { log("EternitySea task failed"); success = false; break }; continue }
            if (!isFirst) { if (generalInvite.runInvite(config.eternitySeaInviteConfig)) generalBattle.runGeneralBattle(config.eternitySeaBattleConfig) else { success = false; break } }
            if (isFirst) { if (!generalInvite.runInvite(config.eternitySeaInviteConfig, isFirst = true)) { success = false; break } else { isFirst = false; generalBattle.runGeneralBattle(config.eternitySeaBattleConfig) } }
        }
        exitRoom(); exitTeam(); gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main")
        return success
    }

    private suspend fun runMember(): Boolean {
        log("Start run member"); gameUi.uiGetCurrentPage()
        while (true) {
            val img = screenshot() ?: continue
            if (currentCount >= config.eternitySeaLimitCount) { log("EternitySea count limit out"); break }
            if (isTimeUp(config.eternitySeaLimitTimeMinutes)) { log("EternitySea time limit out"); break }
            if (checkThenAccept()) continue
            if (isInRoom()) { if (waitBattle(config.eternitySeaWaitTime)) generalBattle.runGeneralBattle(config.eternitySeaBattleConfig) else break }
        }
        while (true) { if (isHomeOrExplore()) break; exitRoom(); exitBattle() }
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main"); return true
    }

    private suspend fun runAlone(): Boolean {
        log("Start run alone")
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_soul_zones")
        eternitySeaEnter()
        checkLock(config.eternitySeaLockTeam)

        if (!config.eternitySeaLockTeam) { log("Only supports lock team mode"); return false }

        while (true) {
            val img = screenshot() ?: continue
            if (!isInEternitySea()) continue
            if (currentCount >= config.eternitySeaLimitCount) { log("EternitySea count limit out"); break }
            if (isTimeUp(config.eternitySeaLimitTimeMinutes)) { log("EternitySea time limit out"); break }
            while (true) {
                val frame = screenshot() ?: continue
                appearThenClick(I_ETERNITY_SEA_FIRE, frame, 1000)
                if (!I_ETERNITY_SEA_FIRE.match(frame, context).matched) { generalBattle.runGeneralBattle(config.eternitySeaBattleConfig); break }
            }
        }
        return true
    }

    private fun isInEternitySea(): Boolean { val img = screenshot() ?: return false; return I_ETERNITY_SEA_FIRE.match(img, context).matched }
    private fun isInRoom(): Boolean { val img = screenshot() ?: return false; return I_FORM_TEAM.match(img, context).matched }
    private fun isRoomDead(): Boolean = teamHelper.isRoomDead { screenshot() }
    private fun isHomeOrExplore(): Boolean = teamHelper.isHomeOrExplore { screenshot() }
    private suspend fun exitRoom() { teamHelper.exitRoom() }
    private suspend fun exitTeam() { teamHelper.exitTeam() }
    private suspend fun exitBattle(): Boolean = teamHelper.exitBattle { screenshot() }
    private suspend fun checkAndInvite(defaultInvite: Boolean): Boolean = teamHelper.checkAndInvite(defaultInvite)
    private suspend fun checkThenAccept(): Boolean = teamHelper.checkThenAccept()
    private suspend fun waitBattle(waitTime: Int): Boolean = teamHelper.waitBattle(waitTime)
}

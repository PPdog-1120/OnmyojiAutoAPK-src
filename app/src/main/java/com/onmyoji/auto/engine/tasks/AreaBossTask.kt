package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 地域鬼王 (AreaBoss)
 * 对应 Python tasks/AreaBoss/script_task.py
 */
class AreaBossTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from AreaBossAssets) ==========
    private val I_EXPLORE = RuleImage("explore", "tasks/AreaBoss/res/res_explore.png",
        intArrayOf(758, 122, 66, 77), intArrayOf(339, 104, 836, 120), 0.8f)
    private val I_AREA_BOSS = RuleImage("area_boss", "tasks/AreaBoss/res/res_area_boss.png",
        intArrayOf(639, 636, 65, 68), intArrayOf(606, 619, 145, 100), 0.8f)
    private val I_FILTER = RuleImage("filter", "tasks/AreaBoss/res/res_filter.png",
        intArrayOf(1116, 33, 35, 31), intArrayOf(1076, 19, 98, 78), 0.8f)
    private val I_BATTLE_1 = RuleImage("battle_1", "tasks/AreaBoss/res/res_battle_1.png",
        intArrayOf(1083, 235, 65, 54), intArrayOf(1083, 235, 65, 54), 0.8f)
    private val I_BATTLE_2 = RuleImage("battle_2", "tasks/AreaBoss/res/res_battle_2.png",
        intArrayOf(1086, 390, 56, 53), intArrayOf(1086, 390, 56, 53), 0.8f)
    private val I_BATTLE_3 = RuleImage("battle_3", "tasks/AreaBoss/res/res_battle_3.png",
        intArrayOf(1083, 545, 66, 53), intArrayOf(1083, 545, 66, 53), 0.8f)
    private val I_FIRE = RuleImage("fire", "tasks/AreaBoss/res/res_fire.png",
        intArrayOf(1109, 490, 100, 73), intArrayOf(1075, 463, 150, 158), 0.8f)
    private val I_AB_CLOSE_RED = RuleImage("ab_close_red", "tasks/AreaBoss/res/res_ab_close_red.png",
        intArrayOf(1194, 24, 38, 37), intArrayOf(1194, 24, 38, 37), 0.8f)
    private val I_AB_FILTER_OPENED = RuleImage("ab_filter_opened", "tasks/AreaBoss/res/ab_filter_opened.png",
        intArrayOf(840, 110, 130, 70), intArrayOf(840, 110, 130, 70), 0.8f)
    private val I_AB_FILTER_TITLE_REWARD = RuleImage("ab_filter_title_reward", "tasks/AreaBoss/res/ab_filter_title_reward.png",
        intArrayOf(920, 110, 190, 70), intArrayOf(920, 110, 190, 70), 0.8f)
    private val I_AB_FILTER_TITLE_FAMOUS = RuleImage("ab_filter_title_famous", "tasks/AreaBoss/res/ab_filter_title_famous.png",
        intArrayOf(920, 110, 190, 70), intArrayOf(920, 110, 190, 70), 0.8f)
    private val I_AB_FILTER_TITLE_COLLECTION = RuleImage("ab_filter_title_collection", "tasks/AreaBoss/res/ab_filter_title_collection.png",
        intArrayOf(920, 110, 190, 70), intArrayOf(920, 110, 190, 70), 0.8f)
    private val I_AB_DIFFICULTY_JI = RuleImage("ab_difficulty_ji", "tasks/AreaBoss/res/ab_difficulty_ji.png",
        intArrayOf(260, 100, 70, 70), intArrayOf(260, 100, 70, 70), 0.8f)
    private val I_AB_DIFFICULTY_NORMAL = RuleImage("ab_difficulty_normal", "tasks/AreaBoss/res/ab_difficulty_normal.png",
        intArrayOf(260, 100, 70, 70), intArrayOf(260, 100, 70, 70), 0.8f)
    private val I_AB_LEVEL_HANDLE = RuleImage("ab_level_handle", "tasks/AreaBoss/res/ab_level_handle.png",
        intArrayOf(170, 250, 400, 75), intArrayOf(170, 250, 400, 75), 0.8f)
    private val I_AB_LEVEL_60 = RuleImage("ab_level_60", "tasks/AreaBoss/res/ab_level_60.png",
        intArrayOf(300, 160, 110, 100), intArrayOf(300, 160, 110, 100), 0.8f)
    private val I_AB_JI_FLOOR_LIST_CHECK = RuleImage("ab_ji_floor_list_check", "tasks/AreaBoss/res/ab_ji_floor_list_check.png",
        intArrayOf(390, 150, 60, 290), intArrayOf(390, 150, 60, 290), 0.8f)
    private val I_AB_JI_FLOOR_ONE = RuleImage("ab_ji_floor_one", "tasks/AreaBoss/res/ab_ji_floor_one.png",
        intArrayOf(390, 150, 60, 290), intArrayOf(390, 150, 60, 290), 0.8f)
    private val I_AB_JI_FLOOR_TEN = RuleImage("ab_ji_floor_ten", "tasks/AreaBoss/res/ab_ji_floor_ten.png",
        intArrayOf(390, 370, 60, 40), intArrayOf(390, 370, 60, 40), 0.8f)
    private val I_AB_GROUP_RANK_NONE = RuleImage("ab_group_rank_none", "tasks/AreaBoss/res/ab_rank_none.png",
        intArrayOf(890, 415, 95, 85), intArrayOf(890, 415, 95, 85), 0.8f)
    private val I_AB_NUM_CHALLENGE_RAIL = RuleImage("ab_num_challenge_rail", "tasks/AreaBoss/res/ab_num_challenge_rail.png",
        intArrayOf(895, 639, 14, 27), intArrayOf(895, 639, 14, 27), 0.8f)
    private val I_CHECK_MAIN = RuleImage("check_main", "tasks/GameUi/res/res_check_main.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.6f)

    // Click rules
    private val C_AB_FAMOUS_BTN = RuleClick("ab_famous_btn", intArrayOf(1190, 220, 50, 120))
    private val C_AB_COLLECTION_BTN = RuleClick("ab_collection_btn", intArrayOf(1190, 580, 50, 120))
    private val C_AB_REWARD_BTN = RuleClick("ab_reward_btn", intArrayOf(1190, 100, 50, 120))
    private val C_AB_JI_FLOOR_SELECTED = RuleClick("ab_ji_floor_selected", intArrayOf(380, 120, 70, 30))

    // Swipe rules
    private val S_AB_FILTER_UP = RuleSwipe("ab_filter_up", 1130, 230, 920, 680)
    private val S_AB_FILTER_DOWN = RuleSwipe("ab_filter_down", 920, 680, 1130, 230)
    private val S_AB_FLOOR_DOWN = RuleSwipe("ab_floor_down", 390, 260, 450, 500)
    private val S_AB_LEVEL_RIGHT = RuleSwipe("ab_level_right", 0, 0, 570, 270)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    override suspend fun run() {
        log("=== 地域鬼王任务开始 ===")

        // 御魂切换
        if (config.areaBossSwitchSoulEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.areaBossSwitchGroupTeam)
        }
        if (config.areaBossSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.areaBossGroupName, config.areaBossTeamName)
        }

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_area_boss")

        var bossFought = 0
        if (config.areaBossReward) {
            if (fightRewardBoss()) bossFought++
        }

        openFilter()
        if (config.areaBossUseCollect) switchToCollect() else switchToFamous()

        val remaining = config.areaBossNumber - bossFought
        if (remaining >= 1) bossFight(I_BATTLE_1)
        if (remaining >= 2) bossFight(I_BATTLE_2)
        if (remaining >= 3) bossFight(I_BATTLE_3)

        goBack()
        log("=== 地域鬼王完成 ===")
    }

    private suspend fun goBack() {
        log("Script back home")
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_UI_BACK_YELLOW, img, 2000, 0.6f)) continue
            if (I_CHECK_MAIN.match(img, context).matched) break
        }
    }

    private val I_UI_BACK_YELLOW = RuleImage("ui_back_yellow", "tasks/GameUi/res/res_ui_back_yellow.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)

    private suspend fun bossFight(battle: RuleImage, ultra: Boolean = false, filterOpen: Boolean = true): Boolean {
        if (filterOpen && !I_AB_FILTER_OPENED.match(screenshot() ?: return false, context).matched) {
            openFilter()
        }
        if (!openBossDetail(battle, 3)) return false
        if (isGroupRanked()) {
            uiClickUntilDisappear(I_AB_CLOSE_RED)
            return true
        }
        if (ultra) {
            if (!getDifficulty()) {
                if (!I_AB_DIFFICULTY_NORMAL.match(screenshot() ?: return false, context).matched && config.areaBossAttack60) {
                    switchToLevel60()
                    if (!startFight()) {
                        waitUntilAppear(I_AB_CLOSE_RED)
                        uiClickUntilDisappear(I_AB_CLOSE_RED)
                        return false
                    }
                } else {
                    uiClickUntilDisappear(I_AB_CLOSE_RED)
                    return false
                }
            }
            switchDifficulty(true)
            when (config.areaBossRewardFloor) {
                "1" -> switchToFloor1()
                "10" -> switchToFloor10()
            }
        }
        val result = startFight()
        waitUntilAppear(I_AB_CLOSE_RED)
        uiClickUntilDisappear(I_AB_CLOSE_RED)
        return result
    }

    private suspend fun startFight(): Boolean {
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_FIRE, img, 1000)) continue
            if (!I_AB_CLOSE_RED.match(img, context).matched) break
        }
        return generalBattle.runGeneralBattle(config.areaBossBattleConfig)
    }

    private suspend fun switchToLevel60() {
        while (true) {
            val img = screenshot() ?: continue
            if (I_AB_LEVEL_60.match(img, context).matched) break
            if (I_AB_LEVEL_HANDLE.match(img, context).matched) {
                device.swipe(S_AB_LEVEL_RIGHT.startX, S_AB_LEVEL_RIGHT.startY,
                    S_AB_LEVEL_RIGHT.endX, S_AB_LEVEL_RIGHT.endY)
            }
        }
    }

    private fun getDifficulty(): Boolean {
        val img = screenshot() ?: return false
        return I_AB_DIFFICULTY_JI.match(img, context).matched
    }

    private suspend fun switchDifficulty(ultra: Boolean) {
        val from = if (ultra) I_AB_DIFFICULTY_NORMAL else I_AB_DIFFICULTY_JI
        val to = if (ultra) I_AB_DIFFICULTY_JI else I_AB_DIFFICULTY_NORMAL
        while (true) {
            val img = screenshot() ?: continue
            if (to.match(img, context).matched) break
            if (from.match(img, context).matched) { appearThenClick(from, img, 3000); continue }
        }
    }

    private suspend fun switchToFloor1() {
        uiClick(C_AB_JI_FLOOR_SELECTED, I_AB_JI_FLOOR_LIST_CHECK)
        while (true) {
            val img = screenshot() ?: continue
            if (I_AB_JI_FLOOR_ONE.match(img, context).matched) { appearThenClick(I_AB_JI_FLOOR_ONE, img, 1000); break }
            device.swipe(S_AB_FLOOR_DOWN.startX, S_AB_FLOOR_DOWN.startY, S_AB_FLOOR_DOWN.endX, S_AB_FLOOR_DOWN.endY)
            delay(1000)
        }
    }

    private suspend fun switchToFloor10() {
        uiClick(C_AB_JI_FLOOR_SELECTED, I_AB_JI_FLOOR_LIST_CHECK)
        while (true) {
            val img = screenshot() ?: continue
            if (I_AB_JI_FLOOR_TEN.match(img, context).matched) { appearThenClick(I_AB_JI_FLOOR_TEN, img, 1000); break }
            delay(500)
        }
    }

    private fun fightRewardBoss(): Boolean {
        // 简化实现：挑战悬赏鬼王
        return false
    }

    private suspend fun openFilter() {
        uiClick(I_FILTER, I_AB_FILTER_OPENED)
        delay(1000)
    }

    private suspend fun switchToCollect() {
        while (true) {
            val img = screenshot() ?: continue
            if (I_AB_FILTER_TITLE_COLLECTION.match(img, context).matched) break
            if (I_AB_FILTER_OPENED.match(img, context).matched) { click(C_AB_COLLECTION_BTN); delay(1500); continue }
        }
    }

    private suspend fun switchToFamous() {
        while (true) {
            val img = screenshot() ?: continue
            if (I_AB_FILTER_TITLE_FAMOUS.match(img, context).matched) break
            if (I_AB_FILTER_OPENED.match(img, context).matched) { click(C_AB_FAMOUS_BTN); delay(1500); continue }
        }
    }

    private suspend fun switchToReward() {
        openFilter()
        while (true) {
            val img = screenshot() ?: continue
            if (I_AB_FILTER_TITLE_REWARD.match(img, context).matched) break
            if (I_AB_FILTER_OPENED.match(img, context).matched) { click(C_AB_REWARD_BTN); delay(1500); continue }
        }
    }

    private suspend fun openBossDetail(battle: RuleImage, tryNum: Int): Boolean {
        var remaining = tryNum
        while (remaining > 0) {
            click(battle); delay(3000)
            if (waitUntilAppear(I_AB_CLOSE_RED, 3000)) break
            remaining--
        }
        val img = screenshot() ?: return false
        return I_AB_CLOSE_RED.match(img, context).matched
    }

    private fun isGroupRanked(): Boolean {
        val img = screenshot() ?: return false
        return !I_AB_GROUP_RANK_NONE.match(img, context).matched
    }

    private suspend fun uiClick(clickRule: RuleImage, stopRule: RuleImage, interval: Long = 1000) {
        while (true) {
            val img = screenshot() ?: continue
            if (stopRule.match(img, context).matched) break
            appearThenClick(clickRule, img, interval)
        }
    }

    private suspend fun uiClick(clickRule: RuleClick, stopRule: RuleImage, interval: Long = 1000) {
        while (true) {
            val img = screenshot() ?: continue
            if (stopRule.match(img, context).matched) break
            val (x, y) = clickRule.coord()
            device.click(x, y)
            delay(interval)
        }
    }

    private suspend fun uiClickUntilDisappear(rule: RuleImage, interval: Long = 1000) {
        while (true) {
            val img = screenshot() ?: continue
            if (!rule.match(img, context).matched) break
            appearThenClick(rule, img, interval)
        }
    }

    private suspend fun click(rule: RuleClick) {
        val (x, y) = rule.coord()
        device.click(x, y)
        delay(500)
    }

    private suspend fun click(rule: RuleImage) {
        val img = screenshot() ?: return
        val result = rule.match(img, context)
        if (result.matched) {
            device.click(result.centerX, result.centerY)
        } else {
            val (x, y) = rule.coord()
            device.click(x, y)
        }
        delay(500)
    }
}

package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 斗技 (Duel)
 * 对应 Python tasks/Duel/script_task.py
 * 流程：切换御魂/阴阳师 → 进入斗技 → 循环匹配战斗 → 退出
 */
class DuelTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {
    private val I_D_TEAM = RuleImage("d_team", "tasks/Duel/duel/duel_d_team.png",
        intArrayOf(813, 288, 34, 74), intArrayOf(785, 259, 115, 119), 0.8f)
    private val I_D_TEAM_SWITCH = RuleImage("d_team_switch", "tasks/Duel/duel/duel_d_team_swtich.png",
        intArrayOf(1082, 85, 38, 41), intArrayOf(1082, 85, 38, 41), 0.8f)
    private val I_D_BATTLE = RuleImage("d_battle", "tasks/Duel/duel/duel_d_battle.png",
        intArrayOf(1153, 573, 100, 100), intArrayOf(1123, 537, 154, 177), 0.8f)
    private val I_D_BATTLE2 = RuleImage("d_battle2", "tasks/Duel/duel/duel_d_battle2.png",
        intArrayOf(1171, 592, 69, 78), intArrayOf(1119, 566, 154, 143), 0.8f)
    private val I_D_BATTLE_PROTECT = RuleImage("d_battle_protect", "tasks/Duel/duel/duel_d_battle_protect.png",
        intArrayOf(1153, 578, 100, 100), intArrayOf(1118, 553, 155, 149), 0.8f)
    private val I_D_AUTO_ENTRY = RuleImage("d_auto_entry", "tasks/Duel/duel/duel_d_auto_entry.png",
        intArrayOf(23, 118, 53, 54), intArrayOf(2, 97, 93, 96), 0.8f)
    private val I_D_HELP = RuleImage("d_help", "tasks/Duel/duel/duel_d_help.png",
        intArrayOf(783, 500, 29, 30), intArrayOf(778, 476, 49, 78), 0.8f)
    private val I_D_PREPARE = RuleImage("d_prepare", "tasks/Duel/duel/duel_d_prepare.png",
        intArrayOf(1127, 541, 100, 100), intArrayOf(1127, 541, 100, 100), 0.8f)
    private val I_D_VICTORY = RuleImage("d_victory", "tasks/Duel/duel/duel_d_victory.png",
        intArrayOf(433, 76, 100, 100), intArrayOf(433, 76, 100, 100), 0.8f)
    private val I_D_FAIL = RuleImage("d_fail", "tasks/Duel/duel/duel_d_fail.png",
        intArrayOf(422, 66, 100, 100), intArrayOf(422, 66, 100, 100), 0.8f)
    private val I_D_WORD_BATTLE = RuleImage("d_word_battle", "tasks/Duel/duel/duel_d_word_battle.png",
        intArrayOf(613, 41, 54, 54), intArrayOf(600, 24, 84, 85), 0.8f)
    private val I_D_CELEB_STAR = RuleImage("d_celeb_star", "tasks/Duel/duel/duel_d_celeb_star.png",
        intArrayOf(646, 450, 33, 37), intArrayOf(551, 433, 185, 67), 0.75f)
    private val I_D_CELEB_HONOR = RuleImage("d_celeb_honor", "tasks/Duel/duel/duel_d_celeb_honor.png",
        intArrayOf(338, 641, 25, 30), intArrayOf(199, 606, 222, 76), 0.75f)
    private val I_BATTLE_WITH_TRAIN = RuleImage("battle_with_train", "tasks/Duel/duel/duel_battle_with_train.png",
        intArrayOf(1161, 588, 78, 81), intArrayOf(1114, 547, 163, 172), 0.8f)
    private val I_BAN = RuleImage("ban", "tasks/Duel/duel/duel_ban.png",
        intArrayOf(1139, 565, 77, 72), intArrayOf(1122, 545, 114, 116), 0.8f)
    private val I_DUEL_EXIT = RuleImage("duel_exit", "tasks/Duel/duel/duel_duel_exit.png",
        intArrayOf(14, 12, 43, 41), intArrayOf(14, 12, 43, 41), 0.8f)
    private val I_D_CHECK_BAN = RuleImage("d_check_ban", "tasks/Duel/duel/duel_d_check_ban.png",
        intArrayOf(611, 36, 56, 62), intArrayOf(590, 14, 100, 100), 0.8f)
    private val I_WIN = RuleImage("win", "tasks/GeneralBattle/res/res_win.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_FALSE = RuleImage("false", "tasks/GeneralBattle/res/res_false.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_REWARD = RuleImage("reward", "tasks/GeneralBattle/res/res_reward.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.6f)
    private val I_UI_BACK_RED = RuleImage("ui_back_red", "tasks/GameUi/res/res_ui_back_red.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    private val I_EXIT = RuleImage("exit", "tasks/GeneralBattle/res/res_exit.png",
        intArrayOf(431, 140, 100, 100), intArrayOf(431, 140, 100, 100), 0.8f)
    private val I_EXIT_ENSURE = RuleImage("exit_ensure", "tasks/GeneralBattle/res/res_exit_ensure.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    private val I_UI_CONFIRM = RuleImage("ui_confirm", "tasks/GameUi/res/res_ui_confirm.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    private val I_UI_REWARD = RuleImage("ui_reward", "tasks/GameUi/res/res_ui_reward.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.6f)
    private val I_DUEL_HONOR = RuleImage("duel_honor", "tasks/Duel/duel/duel_duel_honor.png",
        intArrayOf(211, 636, 20, 20), intArrayOf(200, 601, 130, 100), 0.8f)
    private val I_CHECK_DUEL = RuleImage("check_duel", "tasks/Duel/duel/duel_check_duel.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    private var battleWinCount = 0
    private var battleLoseCount = 0
    private var currentScore = 0
    private var isCeleb = false

    override suspend fun run() {
        log("=== 斗技任务开始 ===")

        // 检查时间 (12:00-23:00)
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (hour !in 12..22) { log("Not in duel time"); return }

        // 切换御魂
        if (config.duelSwitchSoulEnable) { gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records"); switchSoul.runSwitchSoul(config.duelSwitchGroupTeam) }
        if (config.duelSwitchSoulEnableByName) { gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records"); switchSoul.runSwitchSoulByName(config.duelGroupName, config.duelTeamName) }

        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_duel")

        // 主循环
        while (true) {
            val img = screenshot() ?: continue
            checkAndGetReward()
            if (!duelMain()) { gameUi.uiGoto("page_duel"); continue }
            if (!canStartDuel()) break
            startDuel()
        }

        log("=== 斗技完成 ===")
    }

    private fun duelMain(): Boolean {
        val img = screenshot() ?: return false
        return I_D_HELP.match(img, context).matched || I_CHECK_DUEL.match(img, context).matched ||
            I_D_CELEB_STAR.match(img, context).matched || I_D_CELEB_HONOR.match(img, context).matched
    }

    private fun canStartDuel(): Boolean {
        if (isTimeUp(config.duelLimitTimeMinutes)) { log("Duel task is over time"); return false }
        if (currentScore >= config.duelTargetScore) { log("Duel task is over score"); return false }
        val img = screenshot() ?: return false
        if (I_BATTLE_WITH_TRAIN.match(img, context).matched) return false
        return true
    }

    private suspend fun startDuel() {
        log("Duel battle")
        currentCount++
        enterBattle()
        battlePrepare()
        val result = waitBattle()
        if (result) battleWinCount++ else battleLoseCount++
        log("Battle result: $result, count:$currentCount, win:$battleWinCount, lose:$battleLoseCount")
        gameUi.uiGoto("page_duel")
    }

    private suspend fun enterBattle() {
        log("Duel battle matching")
        while (!isInBattlePrepare()) {
            val img = screenshot() ?: continue
            uiClickUntilDisappear(I_D_BATTLE)
            uiClickUntilDisappear(I_D_BATTLE2)
            uiClickUntilDisappear(I_D_BATTLE_PROTECT)
        }
    }

    private suspend fun battlePrepare() {
        log("Duel battle preparing")
        var notInPrepareCnt = 0
        while (true) {
            if (notInPrepareCnt >= 3) break
            val img = screenshot() ?: continue
            if (isBattleEnd() || isInRealBattle()) break
            if (!isInBattlePrepare()) { notInPrepareCnt++; delay(1500); continue }
            notInPrepareCnt = 0
            if (appearThenClick(I_BAN, img, 1200)) { isCeleb = true; continue }
            if (appearThenClick(I_D_AUTO_ENTRY, img, 1200) || appearThenClick(I_D_PREPARE, img, 1200)) continue
        }
    }

    private suspend fun waitBattle(): Boolean {
        log("Duel battle waiting")
        var ret: Boolean? = null
        val timeout = System.currentTimeMillis() + 270_000
        while (true) {
            val img = screenshot() ?: continue
            checkAndGetReward()
            if (I_CHECK_DUEL.match(img, context).matched && I_D_HELP.match(img, context).matched) break
            if (appearThenClick(I_UI_BACK_RED, img, 1200)) continue
            if (isBattleWin()) { ret = true; click(randomClick()); continue }
            if (isBattleLose()) { ret = false; click(randomClick()); continue }
            if (ret == null) {
                // 战斗操作
                greenMark(config.duelGreenEnable, config.duelGreenMark)
                delay(500)
            }
            if (System.currentTimeMillis() > timeout) { log("Battle timeout"); duelExitBattle(); break }
        }
        return ret ?: false
    }

    private fun isBattleWin(): Boolean {
        val img = screenshot() ?: return false
        return I_WIN.match(img, context).matched || I_D_VICTORY.match(img, context).matched
    }

    private fun isBattleLose(): Boolean {
        val img = screenshot() ?: return false
        return I_FALSE.match(img, context).matched || I_D_FAIL.match(img, context).matched
    }

    private fun isBattleEnd(): Boolean = isBattleWin() || isBattleLose() || (screenshot()?.let { I_REWARD.match(it, context).matched } ?: false)

    private fun isInBattlePrepare(): Boolean {
        val img = screenshot() ?: return false
        return I_D_PREPARE.match(img, context).matched || I_D_AUTO_ENTRY.match(img, context).matched ||
            I_BAN.match(img, context).matched || I_D_WORD_BATTLE.match(img, context).matched || I_D_CHECK_BAN.match(img, context).matched
    }

    private fun isInRealBattle(): Boolean = false

    private suspend fun duelExitBattle() {
        while (true) {
            val img = screenshot() ?: continue
            if (I_D_FAIL.match(img, context).matched || I_FALSE.match(img, context).matched) return
            if (appearThenClick(I_EXIT_ENSURE, img)) continue
            if (appearThenClick(I_DUEL_EXIT, img, 1000) || appearThenClick(I_EXIT, img, 1000)) continue
        }
    }

    private fun checkAndGetReward() {
        val img = screenshot() ?: return
        if (I_REWARD.match(img, context).matched || I_UI_REWARD.match(img, context).matched) {
            click(randomClick())
        }
    }

    private fun randomClick(): Pair<Int, Int> = Pair((100..1180).random(), (100..620).random())

    private suspend fun greenMark(enable: Boolean, mark: Int) {}
    private suspend fun uiClickUntilDisappear(rule: RuleImage) {
        while (true) { val img = screenshot() ?: break; if (!rule.match(img, context).matched) break; appearThenClick(rule, img, 1000) }
    }

    private fun click(pair: Pair<Int, Int>) { /* random click */ }
}

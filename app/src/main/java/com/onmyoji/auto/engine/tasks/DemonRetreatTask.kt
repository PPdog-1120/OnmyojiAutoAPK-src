package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 首领退治 (DemonRetreat)
 * 对应 Python tasks/DemonRetreat/script_task.py
 * 仅周六执行，流程：进入寮 → 神社 → 首领退治 → 等待集结 → 战斗 → 领奖 → 退出
 */
class DemonRetreatTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {

    private val I_SHRINE = RuleImage("shrine", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_shrine.png",
        intArrayOf(870, 624, 65, 61), intArrayOf(870, 624, 65, 61), 0.8f)
    private val I_HUNT = RuleImage("hunt", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_hunt.png",
        intArrayOf(698, 228, 122, 78), intArrayOf(661, 164, 187, 165), 0.8f)
    private val I_HUNT_CHECK = RuleImage("hunt_check", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_hunt_check.png",
        intArrayOf(570, 12, 143, 46), intArrayOf(570, 12, 143, 46), 0.8f)
    private val I_DEMON_GATHER = RuleImage("demon_gather", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_demon_gather.png",
        intArrayOf(26, 487, 76, 48), intArrayOf(26, 487, 76, 48), 0.8f)
    private val I_DEMON_BACK_CHECK = RuleImage("demon_back_check", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_demon_back_check.png",
        intArrayOf(27, 26, 42, 36), intArrayOf(14, 2, 86, 86), 0.7f)
    private val I_ENTER_FIRE = RuleImage("enter_fire", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_enter_fire.png",
        intArrayOf(1141, 581, 100, 67), intArrayOf(1141, 581, 100, 67), 0.8f)
    private val I_QUIT_BACK = RuleImage("quit_back", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_new.png",
        intArrayOf(488, 401, 100, 44), intArrayOf(488, 401, 100, 44), 0.8f)
    private val I_RANK_LIST = RuleImage("rank_list", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_rank_lsit.png",
        intArrayOf(542, 5, 200, 54), intArrayOf(542, 5, 200, 54), 0.8f)
    private val I_REWARD_ALL = RuleImage("reward_all", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_reward_all.png",
        intArrayOf(535, 540, 177, 60), intArrayOf(535, 540, 177, 60), 0.8f)
    private val I_PRAY = RuleImage("pray", "tasks/DemonRetreat/DemonRetreat/DemonRetreat_pray.png",
        intArrayOf(1176, 385, 43, 70), intArrayOf(1176, 385, 43, 70), 0.8f)
    private val I_PREPARE_HIGHLIGHT = RuleImage("prepare_highlight", "tasks/GeneralBattle/res/res_prepare_highlight.png",
        intArrayOf(1118, 548, 100, 100), intArrayOf(1118, 548, 100, 100), 0.8f)
    private val I_BUFF = RuleImage("buff", "tasks/GeneralBattle/res/res_buff.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_WIN = RuleImage("win", "tasks/GeneralBattle/res/res_win.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_FALSE = RuleImage("false", "tasks/GeneralBattle/res/res_false.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_EXIT = RuleImage("exit", "tasks/GeneralBattle/res/res_exit.png",
        intArrayOf(431, 140, 100, 100), intArrayOf(431, 140, 100, 100), 0.8f)
    private val I_EXIT_ENSURE = RuleImage("exit_ensure", "tasks/GeneralBattle/res/res_exit_ensure.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    override suspend fun run() {
        log("=== 首领退治任务开始 ===")

        // 检查是否为周六
        val dayOfWeek = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        if (dayOfWeek != 7) { // Saturday = 7
            log("Today is not Saturday, skip")
            return
        }

        // 切换御魂
        if (config.demonRetreatSwitchSoulEnable) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.demonRetreatSwitchGroupTeam)
        }
        if (config.demonRetreatSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.demonRetreatGroupName, config.demonRetreatTeamName)
        }

        // 进入首领退治
        if (!gotoDemonRetreat()) {
            log("Failed to enter demon retreat")
            gotoMain(); return
        }

        // 战斗
        val success = demonRetreatBattle()

        // 领取奖励
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_PRAY, img, 1000)) { log("Claim rewards") }
            if (appearThenClick(I_HUNT, img, 1000)) continue
            if (appearThenClick(I_REWARD_ALL, img, 1500)) { log("Claim rewards finished"); break }
            if (I_RANK_LIST.match(img, context).matched) {
                if (appearThenClick(I_DEMON_BACK_CHECK, img, 1000)) break
            }
        }

        gotoMain()
        log("=== 首领退治完成, success=$success ===")
    }

    private suspend fun gotoDemonRetreat(): Boolean {
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_guild")
        var count = 0
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_SHRINE, img, 1000)) { log("Enter Shrine"); continue }
            if (appearThenClick(I_HUNT, img, 1500)) { count++ }
            if (appearThenClick(I_QUIT_BACK, img, 1000)) {}
            if (I_HUNT_CHECK.match(img, context).matched) {
                if (appearThenClick(I_QUIT_BACK, img, 1000)) {}
                log("Enter demon_retreat success"); return true
            }
            if (appearThenClick(I_REWARD_ALL, img, 1000)) {
                delay(1000); appearThenClick(I_DEMON_BACK_CHECK, screenshot(), 1000); return false
            }
            if (I_RANK_LIST.match(img, context).matched) { delay(3000); appearThenClick(I_DEMON_BACK_CHECK, screenshot(), 1000); delay(20_000) }
            if (count >= 5) break
        }
        return false
    }

    private suspend fun demonRetreatBattle(): Boolean {
        // 检查是否迟到
        val img = screenshot()
        if (img != null && !I_DEMON_GATHER.match(img, context).matched) {
            log("Arrive later")
            uiClickUntilDisappear(I_ENTER_FIRE)
            return runDemonBattle()
        }

        // 等待集结
        delay(5000)
        waitUntilDisappear(I_DEMON_GATHER)
        return runDemonBattle()
    }

    private suspend fun runDemonBattle(): Boolean {
        log("General battle start")
        currentCount++

        // 点击准备
        waitUntilAppear(I_PREPARE_HIGHLIGHT)
        waitUntilAppear(I_BUFF)
        while (true) {
            val img = screenshot() ?: continue
            if (!I_BUFF.match(img, context).matched) break
            appearThenClick(I_PREPARE_HIGHLIGHT, img, 1500)
        }

        // 等待战斗结束
        val stuckTimer = System.currentTimeMillis() + 180_000
        while (true) {
            val img = screenshot() ?: continue
            if (I_WIN.match(img, context).matched) { log("Battle win"); uiClickUntilDisappear(I_WIN); return true }
            if (appearThenClick(I_PREPARE_HIGHLIGHT, img, 1500)) continue
            if (I_FALSE.match(img, context).matched) { log("Battle false"); uiClickUntilDisappear(I_FALSE); return false }
            if (System.currentTimeMillis() > stuckTimer) { log("Battle timeout"); break }
        }
        return false
    }

    private suspend fun gotoMain() {
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main")
    }

    private suspend fun waitUntilAppear(rule: RuleImage, timeoutMs: Long = 10000): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) { if (rule.match(screenshot() ?: continue, context).matched) return true; delay(500) }
        return false
    }

    private suspend fun waitUntilDisappear(rule: RuleImage, timeoutMs: Long = 30000) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) { if (!rule.match(screenshot() ?: break, context).matched) break; delay(500) }
    }

    private suspend fun uiClickUntilDisappear(rule: RuleImage) {
        while (true) { val img = screenshot() ?: break; if (!rule.match(img, context).matched) break; appearThenClick(rule, img, 1000) }
    }
}

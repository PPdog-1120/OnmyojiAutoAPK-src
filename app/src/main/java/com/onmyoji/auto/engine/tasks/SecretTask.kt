package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 秘闻副本 (Secret)
 * 对应 Python tasks/Secret/script_task.py
 *
 * 流程：切换御魂 → 进入秘闻 → 寻找未通关层 → 挑战 → 退出
 */
class SecretTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from SecretAssets) ==========
    private val C_SE_CLICK_LAYER = RuleClick("se_click_layer", intArrayOf(434, 155, 100, 100))
    private val I_SE_ENTER = RuleImage("se_enter", "tasks/Secret/se/se_se_enter.png",
        intArrayOf(1145, 593, 100, 100), intArrayOf(1145, 593, 100, 100), 0.8f)
    private val I_SE_FIRE = RuleImage("se_fire", "tasks/Secret/se/se_se_fire.png",
        intArrayOf(1108, 551, 100, 100), intArrayOf(1100, 541, 120, 120), 0.7f)
    private val I_SE_PLACEMENT = RuleImage("se_placement", "tasks/Secret/se/se_se_placement.png",
        intArrayOf(1013, 570, 50, 48), intArrayOf(996, 555, 79, 81), 0.8f)
    private val I_SE_JADE = RuleImage("se_jade", "tasks/Secret/se/se_se_jade.png",
        intArrayOf(305, 208, 28, 33), intArrayOf(305, 208, 28, 33), 0.8f)
    private val I_SE_BATTLE_WIN = RuleImage("se_battle_win", "tasks/Secret/se/se_se_battle_win.png",
        intArrayOf(436, 62, 100, 100), intArrayOf(436, 62, 100, 100), 0.8f)
    private val I_SE_FINISHED_1 = RuleImage("se_finished_1", "tasks/Secret/se/se_se_finished_1.png",
        intArrayOf(441, 546, 40, 43), intArrayOf(441, 546, 40, 43), 0.8f)
    private val I_WIN = RuleImage("win", "tasks/GeneralBattle/res/res_win.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_FALSE = RuleImage("false", "tasks/GeneralBattle/res/res_false.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_REWARD = RuleImage("reward", "tasks/GeneralBattle/res/res_reward.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.6f)
    private val I_UI_BACK_BLUE = RuleImage("ui_back_blue", "tasks/GameUi/res/res_ui_back_blue.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    private val I_UI_BACK_YELLOW = RuleImage("ui_back_yellow", "tasks/GameUi/res/res_ui_back_yellow.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    private val I_CHECK_MAIN = RuleImage("check_main", "tasks/GameUi/res/res_check_main.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.6f)
    private val I_CHAT_CLOSE_BUTTON = RuleImage("chat_close_button", "tasks/GameUi/res/res_chat_close.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)

    private val S_SE_DOWN_SWIPE = RuleSwipe("se_down_swipe", 229, 520, 217, 390)

    private val layList = arrayOf("壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾")
    private val matchLayer = mapOf("壹" to 1, "贰" to 2, "叁" to 3, "肆" to 4, "伍" to 5,
        "陆" to 6, "柒" to 7, "捌" to 8, "玖" to 9, "拾" to 10)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)

    override suspend fun run() {
        log("=== 秘闻副本任务开始 ===")

        // 检查时间 (周一早上不能打)
        val cal = java.util.Calendar.getInstance()
        if (cal.get(java.util.Calendar.DAY_OF_WEEK) == 2 && cal.get(java.util.Calendar.HOUR_OF_DAY) < 8) {
            log("周一早上不能打秘闻")
            return
        }

        // 切换御魂
        if (config.secretSwitchSoulEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.secretSwitchGroupTeam)
        }
        if (config.secretSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.secretGroupName, config.secretTeamName)
        }

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_secret_zones")

        // 进入
        var success = true
        uiClick(I_SE_ENTER, I_SE_FIRE)
        delay(1000)
        val img = screenshot()
        if (img != null && !I_SE_PLACEMENT.match(img, context).matched) {
            log("Unsuccessful entry")
            success = false
        }

        // 开始
        log("Start secret zone")
        var firstBattle = true
        while (true) {
            val frame = screenshot() ?: continue
            if (!success) { log("Secret zone failed to enter, skip"); break }
            if (!I_SE_FIRE.match(frame, context).matched) continue
            if (I_SE_FINISHED_1.match(frame, context).matched) { log("Secret zone finished"); break }

            val layer = findBattle()
            log("Current layer: $layer")
            if (layer == null) continue
            if (layer >= 6) firstBattle = false

            if (firstBattle && layer <= 5) {
                firstBattle = false
                clickBattle()
                success = generalBattle.runGeneralBattle(config.secretBattleConfig)
                continue
            }
            if (!firstBattle) {
                clickBattle()
                success = generalBattle.runGeneralBattle(config.secretBattleConfig)
                if (layer == 10 && config.secretLayer10) break
                continue
            }
        }

        uiClick(I_UI_BACK_BLUE, I_UI_BACK_YELLOW)
        uiClick(I_UI_BACK_YELLOW, I_CHECK_MAIN)
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")

        // 关闭加成
        if (config.secretGold50 || config.secretGold100) {
            generalBuff.openBuff()
            if (config.secretGold50) generalBuff.gold50(false)
            if (config.secretGold100) generalBuff.gold100(false)
            generalBuff.closeBuff()
        }

        log("=== 秘闻副本完成 ===")
    }

    private suspend fun findBattle(): Int? {
        val img = screenshot() ?: return null
        // 简化实现：通过OCR查找"未通关"标记
        // 完整实现需要 OCR 识别
        return null
    }

    private suspend fun clickBattle() {
        while (true) {
            val img = screenshot() ?: continue
            if (!I_SE_FIRE.match(img, context).matched) break
            appearThenClick(I_SE_FIRE, img, 1000)
        }
    }

    private suspend fun uiClick(clickRule: RuleImage, stopRule: RuleImage) {
        while (true) {
            val img = screenshot() ?: continue
            if (stopRule.match(img, context).matched) break
            appearThenClick(clickRule, img, 1000)
        }
    }
}

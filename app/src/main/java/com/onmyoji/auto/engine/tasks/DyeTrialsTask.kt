package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 灵染试炼 (DyeTrials)
 * 对应 Python tasks/DyeTrials/script_task.py
 *
 * 流程：切换御魂 → 进入活动 → 循环挑战(最多50次) → 退出
 */
class DyeTrialsTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from DyeTrialsAssets) ==========
    private val I_FP_ACCESS = RuleImage("fp_access", "tasks/DyeTrials/fp/fp_fp_access.png",
        intArrayOf(1192, 317, 36, 34), intArrayOf(1000, 101, 261, 472), 0.8f)
    private val I_TOGGLE_BUTTON = RuleImage("toggle_button", "tasks/DyeTrials/fp/fp_toggle_button.png",
        intArrayOf(1202, 466, 22, 21), intArrayOf(1090, 127, 176, 377), 0.7f)
    private val I_FP_ACCESS_1 = RuleImage("fp_access_1", "tasks/DyeTrials/fp/fp_fp_access_1.png",
        intArrayOf(264, 339, 26, 95), intArrayOf(129, 202, 329, 380), 0.7f)
    private val I_FP_CHALLENGE = RuleImage("fp_challenge", "tasks/DyeTrials/fp/fp_fp_challenge.png",
        intArrayOf(1159, 598, 60, 29), intArrayOf(1159, 598, 60, 29), 0.8f)
    private val I_BATTLE_SUCCESS = RuleImage("battle_success", "tasks/DyeTrials/fp/battle_success.png",
        intArrayOf(435, 147, 100, 100), intArrayOf(435, 147, 100, 100), 0.8f)
    private val I_HARVEST_CHAT_CLOSE = RuleImage("harvest_chat_close", "tasks/Restart/res/res_harvest_chat_close.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    override suspend fun run() {
        log("=== 灵染试炼任务开始 ===")

        // 切换御魂
        if (config.dyeTrialsSwitchSoulEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.dyeTrialsSwitchGroupTeam)
        }
        if (config.dyeTrialsSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.dyeTrialsGroupName, config.dyeTrialsTeamName)
        }

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")

        getAll()

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")

        log("=== 灵染试炼完成 ===")
    }

    private suspend fun getAll() {
        // 进入活动
        while (true) {
            val img = screenshot() ?: continue
            if (I_FP_CHALLENGE.match(img, context).matched) break
            if (appearThenClick(I_FP_ACCESS, img, 800)) continue
            if (appearThenClick(I_FP_ACCESS_1, img, 1500)) continue
            if (appearThenClick(I_TOGGLE_BUTTON, img, 3000)) continue
        }
        log("Enter DyeTrials")

        var battleNum = 0
        val bossTimer = System.currentTimeMillis()
        val bossTimeout = 60_000L // 60 seconds

        while (true) {
            val img = screenshot() ?: continue
            delay(100)

            if (System.currentTimeMillis() - bossTimer > bossTimeout) {
                log("识别超时退出")
                break
            }

            // 获得奖励
            if (uiRewardAppearClick()) continue
            if (appearThenClick(I_HARVEST_CHAT_CLOSE, img)) continue

            if (I_FP_CHALLENGE.match(img, context).matched) {
                if (battleNum >= 50) {
                    log("Battle $battleNum, enough battle, break")
                    break
                }
                uiClickUntilDisappear(I_FP_CHALLENGE)
                battleNum++
                log("Battle num [$battleNum]")
                continue
            }

            if (appearThenClick(I_BATTLE_SUCCESS, img, 1000)) continue
        }
    }

    private suspend fun uiRewardAppearClick(): Boolean {
        val img = screenshot() ?: return false
        // 检测奖励界面并点击
        return false
    }

    private suspend fun uiClickUntilDisappear(rule: RuleImage) {
        while (true) {
            val img = screenshot() ?: break
            if (!rule.match(img, context).matched) break
            appearThenClick(rule, img, 1000)
        }
    }
}

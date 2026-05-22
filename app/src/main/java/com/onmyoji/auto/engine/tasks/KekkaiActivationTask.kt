package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.GameUi
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 结界激活任务 — 挂结界卡、收取经验
 *
 * 对应 Python tasks/KekkaiActivation/script_task.py
 * 流程：进入寮结界 → 收取经验 → 挂卡 → 返回庭院
 */
class KekkaiActivationTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 寮结界入口
    private val I_REALM_SHIN = RuleImage("realm_shin", "tasks/KekkaiActivation/res/res_realm_shin.png",
        intArrayOf(1100, 300, 100, 100), intArrayOf(1100, 300, 100, 100), 0.8f)
    // 育成界面
    private val I_SHI_GROWN = RuleImage("shi_grown", "tasks/KekkaiActivation/res/res_shi_grown.png",
        intArrayOf(1100, 400, 100, 100), intArrayOf(1100, 400, 100, 100), 0.8f)
    // 结界卡收取 (经验)
    private val I_CARD_HARVEST = RuleImage("card_harvest", "tasks/KekkaiActivation/res/res_card_harvest.png",
        intArrayOf(640, 360, 100, 100), intArrayOf(640, 360, 100, 100), 0.8f)
    // 挂卡按钮
    private val I_CARD_PLACE = RuleImage("card_place", "tasks/KekkaiActivation/res/res_card_place.png",
        intArrayOf(640, 500, 100, 100), intArrayOf(640, 500, 100, 100), 0.8f)
    // 返回按钮
    private val I_UI_BACK_RED = RuleImage("ui_back_red", "tasks/GameUi/res/res_ui_back_red.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)

    private val gameUi = GameUi(context, device, config)

    override suspend fun run() {
        log("=== 结界激活任务开始 ===")

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_guild")

        // 进入寮结界
        gotoRealm()

        // 收取经验
        harvestCard()

        // 返回庭院
        backToRealm()
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")

        log("=== 结界激活任务完成 ===")
    }

    /**
     * 进入寮结界
     */
    private suspend fun gotoRealm() {
        log("Enter guild realm")
        var attempts = 0
        while (attempts < 20) {
            val img = screenshot() ?: continue
            if (I_REALM_SHIN.match(img, context).matched || I_SHI_GROWN.match(img, context).matched) {
                log("In guild realm")
                return
            }
            // 点击结界入口
            if (appearThenClick(I_REALM_SHIN, img, 2000)) {
                attempts++
                continue
            }
            delay(1000)
            attempts++
        }
    }

    /**
     * 收取结界卡经验
     */
    private suspend fun harvestCard() {
        log("Harvest card")
        var attempts = 0
        while (attempts < 10) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_CARD_HARVEST, img, 2000)) {
                log("Harvested card experience")
                // 点击空白处关闭弹窗
                delay(1000)
                device.click(640, 360)
                delay(500)
                attempts++
                continue
            }
            // 没有可收取的了
            break
        }
    }

    /**
     * 返回结界主界面
     */
    private suspend fun backToRealm() {
        log("Back to realm")
        var attempts = 0
        while (attempts < 10) {
            val img = screenshot() ?: continue
            if (I_REALM_SHIN.match(img, context).matched || I_SHI_GROWN.match(img, context).matched) {
                return
            }
            if (appearThenClick(I_UI_BACK_RED, img, 1000)) {
                attempts++
                continue
            }
            delay(500)
            attempts++
        }
    }
}

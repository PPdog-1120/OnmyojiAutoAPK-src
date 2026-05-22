package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.engine.component.GameUi
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 回主页任务 — 导航回到游戏主页
 *
 * 使用 GameUi 组件的智能页面导航，而非硬编码点击
 */
class GotoMainTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    private val gameUi = GameUi(context, device, config)

    // 主页标识
    private val I_CHECK_MAIN = RuleImage(
        "check_main",
        "exploration/res_check_main.png",
        intArrayOf(0, 0, 100, 100),
        intArrayOf(0, 0, 1280, 720),
        0.8f
    )

    override suspend fun run() {
        log("=== 回主页任务开始 ===")
        // 使用 GameUi 的智能导航
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")

        // 验证是否回到主页
        val img = screenshot()
        if (img != null && I_CHECK_MAIN.match(img, context).matched) {
            log("已回到主页")
        } else {
            log("导航完成，但未确认到主页标识")
        }
        log("=== 回主页任务完成 ===")
    }
}

package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig

/**
 * 回主页任务 — 导航回到游戏主页
 */
class GotoMainTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

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
        // 尝试点击返回按钮直到回到主页
        var attempts = 0
        while (attempts < 20) {
            val img = screenshot() ?: continue
            if (I_CHECK_MAIN.match(img, context).matched) {
                log("已回到主页")
                break
            }
            // 点击左上角返回区域
            device.click(60, 40)
            delay(1000)
            attempts++
        }
        log("=== 回主页任务完成 ===")
    }
}

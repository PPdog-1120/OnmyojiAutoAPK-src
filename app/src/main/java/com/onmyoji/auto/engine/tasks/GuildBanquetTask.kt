package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 寮宴任务 — 等待寮宴结束
 *
 * 流程：进入寮界面 → 检测宴会旗帜 → 等待宴会结束 → 退出
 */
class GuildBanquetTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 宴会旗帜标识
    private val I_FLAG = RuleImage(
        "flag",
        "GuildBanquet/res/res_flag.png",
        intArrayOf(1035, 12, 33, 60),
        intArrayOf(1035, 12, 33, 60),
        0.8f
    )

    override suspend fun run() {
        log("=== 寮宴任务开始 ===")

        // 检测宴会旗帜
        val img = screenshot()
        if (img == null || !I_FLAG.match(img, context).matched) {
            log("未检测到宴会旗帜")
            if (isAfter10PM()) {
                log("已过22点，宴会时间配置可能有误")
            } else {
                log("宴会可能尚未开始，5分钟后重试")
            }
            return
        }

        log("检测到寮宴，开始等待")
        var waitCount = 0
        val maxWaitRounds = 3 // 每轮约230秒

        while (waitCount < maxWaitRounds) {
            var lastCheckTime = 0L
            var lastLogTime = 0L
            var lastFlagStatus = false

            val roundStart = System.currentTimeMillis()
            val roundEnd = roundStart + 230_000L

            while (System.currentTimeMillis() < roundEnd) {
                val currentTime = System.currentTimeMillis()

                // 每10秒实际检测一次
                if (currentTime - lastCheckTime >= 10_000) {
                    val frame = screenshot()
                    lastFlagStatus = frame != null && I_FLAG.match(frame, context).matched
                    lastCheckTime = currentTime
                    lastLogTime = currentTime
                }

                if (lastFlagStatus) {
                    if (currentTime - lastLogTime >= 10_000) {
                        log("宴会进行中，等待...")
                        lastLogTime = currentTime
                    }
                } else {
                    log("寮宴结束")
                    break
                }
                delay(1000)
            }

            if (!lastFlagStatus) break
            waitCount++
            log("宴会仍在进行，第 $waitCount 轮等待")
        }

        log("=== 寮宴任务完成 ===")
    }

    private fun isAfter10PM(): Boolean {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return hour >= 22
    }
}

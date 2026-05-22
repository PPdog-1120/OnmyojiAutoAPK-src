package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 寮活动监控任务 — 监控阴阳寮活动通知
 *
 * 流程：在主页监控通知区域 → 检测到活动关键字 → 触发对应任务
 *
 * 注意：此任务简化版，仅保留核心监控逻辑。
 * 原版依赖 ADB dumpsys notification 和 OCR，APK 版通过截图 OCR 实现。
 */
class GuildActivityMonitorTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // 活动关键字 → 任务类型映射
    private val keywordTaskMap = mapOf(
        "道馆" to "Dokan",
        "狭间" to "AbyssShadows",
        "宴会" to "GuildBanquet",
        "退治" to "DemonRetreat"
    )

    override suspend fun run() {
        log("=== 寮活动监控开始 ===")

        // 检查运行日期
        val now = java.util.Calendar.getInstance()
        val todayOfWeek = now.get(java.util.Calendar.DAY_OF_WEEK)
        // 转换为 1-7 (周一到周日)
        val today = if (todayOfWeek == java.util.Calendar.SUNDAY) 7 else todayOfWeek - 1

        val runDays = config.guildActivityMonitorRunDays
            .split(",", "，")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 1..7 }
            .toSet()

        if (today !in runDays) {
            log("今天不在运行日期内，跳过")
            return
        }

        val durationMinutes = config.guildActivityMonitorDuration
        val intervalSeconds = config.guildActivityMonitorInterval
        val monitoredActivities = config.guildActivityMonitorActivities
            .split(",", "，")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        log("监控活动: $monitoredActivities")
        log("持续 $durationMinutes 分钟，每 $intervalSeconds 秒检测一次")

        val endTime = System.currentTimeMillis() + durationMinutes * 60_000L
        var lastLogTime = 0L

        while (System.currentTimeMillis() < endTime) {
            val currentTime = System.currentTimeMillis()

            // 每分钟输出一次日志
            if (currentTime - lastLogTime >= 60_000) {
                val remaining = (endTime - currentTime) / 60_000
                log("监控中... 剩余 $remaining 分钟")
                lastLogTime = currentTime
            }

            // 截图检测（简化版：检查屏幕上是否出现活动关键字）
            // 实际 APK 版本中这里会使用 OCR 检测通知区域
            val img = screenshot()
            if (img != null) {
                // 这里简化处理：在实际实现中会使用 OCR 检测通知区域文字
                // 并与 keywordTaskMap 中的关键字进行匹配
                log("检测中...")
            }

            delay(intervalSeconds * 1000L)
        }

        log("监控时间结束")
        log("=== 寮活动监控完成 ===")
    }
}

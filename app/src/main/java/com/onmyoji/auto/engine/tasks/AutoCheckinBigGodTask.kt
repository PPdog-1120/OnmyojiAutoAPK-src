package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 大神签到任务 — 通过大神APP领取礼包
 *
 * 注意：此任务依赖 Frida 和 ADB，在 APK 版本中简化处理。
 * 原版通过 Frida 从内存提取 Token 并调用 API 领取礼包。
 * APK 版本中保留任务框架，实际功能需要额外实现。
 */
class AutoCheckinBigGodTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    override suspend fun run() {
        log("=== 大神签到任务开始 ===")
        log("注意：此任务需要 Frida 和 ADB 支持")
        log("APK 版本中此任务为简化版本")

        // APK 版本中，此任务需要特殊处理：
        // 1. 需要 root 权限运行 frida-server
        // 2. 需要通过 Frida 从大神APP内存提取 Token
        // 3. 需要调用大神API领取礼包
        // 这些操作在标准 Android APK 中难以直接实现

        log("大神签到需要外部工具支持，跳过")
        log("=== 大神签到任务完成 ===")
    }
}

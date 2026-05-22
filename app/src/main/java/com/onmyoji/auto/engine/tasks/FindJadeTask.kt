package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 找玉任务 — 切换小号完成协作任务
 *
 * 注意：此任务依赖账号切换和协作任务系统，在 APK 版本中简化处理。
 * 原版通过切换账号、检测协作任务、邀请好友等复杂流程实现。
 * APK 版本中保留任务框架，实际功能需要额外实现。
 */
class FindJadeTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    override suspend fun run() {
        log("=== 找玉任务开始 ===")
        log("注意：此任务依赖账号切换系统")
        log("APK 版本中此任务为简化版本")

        // APK 版本中，此任务需要特殊处理：
        // 1. 需要支持多账号切换
        // 2. 需要检测协作任务类型
        // 3. 需要邀请好友完成协作
        // 这些操作需要完整的账号管理系统支持

        log("找玉需要账号切换支持，跳过")
        log("=== 找玉任务完成 ===")
    }
}

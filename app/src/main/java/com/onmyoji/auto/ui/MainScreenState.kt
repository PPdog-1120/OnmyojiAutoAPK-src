package com.onmyoji.auto.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.onmyoji.auto.engine.TaskManager
import com.onmyoji.auto.model.TaskConfig
import com.onmyoji.auto.model.TaskType
import com.onmyoji.auto.service.AutomationService

// ========== 回调封装 ==========

class ScreenCallbacks(
    val onRequestAccessibility: () -> Unit,
    val onRequestProjection: () -> Unit,
    val onCheckAccessibility: () -> Boolean,
    val onCheckProjection: () -> Boolean,
)

// ========== State Holder ==========

class MainScreenState(
    val taskManager: TaskManager,
    val callbacks: ScreenCallbacks,
) {
    val selectedTask: MutableState<TaskType> = mutableStateOf(TaskType.EXPLORATION)
    val config: MutableState<TaskConfig> = mutableStateOf(TaskConfig())
    val accessibilityEnabled: MutableState<Boolean> = mutableStateOf(false)
    val projectionEnabled: MutableState<Boolean> = mutableStateOf(false)
    val logLines: MutableList<String> = mutableStateListOf()
    val logFilePath: MutableState<String?> = mutableStateOf(null)
    val taskState: MutableState<TaskManager.State> = mutableStateOf(TaskManager.State.IDLE)
    val runningTask: MutableState<TaskType?> = mutableStateOf(null)
}

// ========== 操作函数 ==========

fun MainScreenState.refreshPermissions() {
    accessibilityEnabled.value = callbacks.onCheckAccessibility()
    projectionEnabled.value = callbacks.onCheckProjection()
}

fun MainScreenState.startTask() {
    val controller = AutomationService.instance?.deviceController
    if (controller != null) {
        logLines.clear()
        taskManager.setDeviceController(controller)
        taskManager.startTask(selectedTask.value, config.value)
        logFilePath.value = taskManager.lastLogFilePath
    }
}

fun MainScreenState.stopTask() {
    taskManager.stopTask()
}

fun MainScreenState.clearLogs() {
    logLines.clear()
}

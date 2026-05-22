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
    val selectedTask: MutableState<TaskType>,
    val config: MutableState<TaskConfig>,
    val accessibilityEnabled: MutableState<Boolean>,
    val projectionEnabled: MutableState<Boolean>,
    val logLines: MutableList<String>,
    val logFilePath: MutableState<String?>,
    val taskState: MutableState<TaskManager.State>,
    val runningTask: MutableState<TaskType?>,
)

// ========== 状态工厂（避免寄存器溢出）==========

fun createMainScreenState(
    taskManager: TaskManager,
    callbacks: ScreenCallbacks
): MainScreenState {
    val selectedTask = mutableStateOf(TaskType.EXPLORATION)
    val config = mutableStateOf(TaskConfig())
    val accessibilityEnabled = mutableStateOf(false)
    val projectionEnabled = mutableStateOf(false)
    val logLines = mutableStateListOf<String>()
    val logFilePath = mutableStateOf<String?>(null)
    val taskState = mutableStateOf(TaskManager.State.IDLE)
    val runningTask = mutableStateOf<TaskType?>(null)

    return MainScreenState(
        taskManager = taskManager,
        callbacks = callbacks,
        selectedTask = selectedTask,
        config = config,
        accessibilityEnabled = accessibilityEnabled,
        projectionEnabled = projectionEnabled,
        logLines = logLines,
        logFilePath = logFilePath,
        taskState = taskState,
        runningTask = runningTask,
    )
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

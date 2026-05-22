package com.onmyoji.auto.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.onmyoji.auto.engine.TaskManager
import kotlinx.coroutines.launch

// ========== MainScreen ==========

@Composable
fun MainScreen(
    taskManager: TaskManager,
    onRequestAccessibility: () -> Unit,
    onRequestProjection: () -> Unit,
    onCheckAccessibility: () -> Boolean,
    onCheckProjection: () -> Boolean
) {
    val state = remember {
        createMainScreenState(
            taskManager = taskManager,
            callbacks = ScreenCallbacks(
                onRequestAccessibility = onRequestAccessibility,
                onRequestProjection = onRequestProjection,
                onCheckAccessibility = onCheckAccessibility,
                onCheckProjection = onCheckProjection
            )
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            state.refreshPermissions()
            kotlinx.coroutines.delay(2000)
        }
    }

    LaunchedEffect(taskManager) {
        launch { taskManager.state.collect { state.taskState.value = it } }
        launch { taskManager.currentTask.collect { state.runningTask.value = it } }
        launch {
            taskManager.taskLogs.collect { line ->
                state.logLines.add(line)
                while (state.logLines.size > 500) { state.logLines.removeAt(0) }
            }
        }
    }

    MainScreenContent(state)
}

// ========== Scaffold ==========

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(state: MainScreenState) {
    val taskState by state.taskState
    val runningTask by state.runningTask
    val isRunning = taskState == TaskManager.State.RUNNING
    val isSelectedTaskRunning = isRunning && runningTask == state.selectedTask.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("阴阳师自动", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A2E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0F0F23))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            PermissionSection(state)
            Spacer(modifier = Modifier.height(12.dp))
            TaskSelectionSection(state)
            Spacer(modifier = Modifier.height(4.dp))
            TaskConfigSection(state)
            Spacer(modifier = Modifier.height(12.dp))
            ControlSection(state, isSelectedTaskRunning)
            Spacer(modifier = Modifier.height(12.dp))
            LogSection(state)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

package com.onmyoji.auto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ========== 权限 ==========

@Composable
fun PermissionSection(state: MainScreenState) {
    Column {
        Text("权限状态", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        PermissionRow("无障碍服务", state.accessibilityEnabled.value, state.callbacks.onRequestAccessibility)
        PermissionRow("屏幕截图", state.projectionEnabled.value, state.callbacks.onRequestProjection)
    }
}

@Composable
fun PermissionRow(name: String, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (enabled) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (enabled) Color(0xFF4CAF50) else Color(0xFFE94560),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(name, color = Color.White, modifier = Modifier.weight(1f))
        if (!enabled) {
            TextButton(onClick = onClick) {
                Text("授权", color = Color(0xFF03DAC5))
            }
        }
    }
}

// ========== 通用组件 ==========

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskChip(label: String, type: com.onmyoji.auto.model.TaskType, selected: com.onmyoji.auto.model.TaskType, onSelect: (com.onmyoji.auto.model.TaskType) -> Unit) {
    FilterChip(
        selected = selected == type,
        onClick = { onSelect(type) },
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6200EE))
    )
}

@Composable
fun ConfigToggle(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, modifier = Modifier.weight(1f))
        Switch(checked = value, onCheckedChange = onChange)
    }
}

// ========== 控制按钮 ==========

@Composable
fun ControlSection(state: MainScreenState, isSelectedTaskRunning: Boolean) {
    val enabled = state.accessibilityEnabled.value && state.projectionEnabled.value

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { if (isSelectedTaskRunning) state.stopTask() else state.startTask() },
            enabled = enabled,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelectedTaskRunning) Color(0xFFE94560) else Color(0xFF4CAF50)
            )
        ) {
            Icon(
                if (isSelectedTaskRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isSelectedTaskRunning) "停止" else "开始")
        }

        if (state.logLines.isNotEmpty()) {
            OutlinedButton(
                onClick = { state.clearLogs() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBBBBBB))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("清空日志", fontSize = 13.sp)
            }
        }
    }
}

// ========== 日志 ==========

@Composable
fun LogSection(state: MainScreenState) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("运行日志", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White, modifier = Modifier.weight(1f))
            if (state.logLines.isNotEmpty()) {
                Text("${state.logLines.size} 条", fontSize = 12.sp, color = Color(0xFF666666))
            }
        }

        state.logFilePath.value?.let { path ->
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "日志已保存: $path", color = Color(0xFF666666), fontSize = 11.sp)
            Text(text = "adb pull \"$path\" .", color = Color(0xFF03DAC5), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A1A))
        ) {
            if (state.logLines.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("点击「开始」后日志将在此显示", color = Color(0xFF555555), fontSize = 13.sp)
                }
            } else {
                LogContent(state.logLines)
            }
        }
    }
}

@Composable
private fun LogContent(logLines: List<String>) {
    val displayLines = if (logLines.size > 200) logLines.subList(logLines.size - 200, logLines.size) else logLines
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (line in displayLines) {
            LogLine(line)
        }
    }
}

@Composable
private fun LogLine(line: String) {
    val color = when {
        line.contains("[错误]") || line.contains("[异常]") -> Color(0xFFFF4444)
        line.contains("[警告]") -> Color(0xFFFFAA00)
        line.contains("✓") -> Color(0xFF4CAF50)
        line.contains("✗") -> Color(0xFFE94560)
        line.startsWith("===") -> Color(0xFF03DAC5)
        else -> Color(0xFFBBBBBB)
    }
    Text(text = line, color = color, fontSize = 11.sp, fontFamily = FontFamily.Monospace, lineHeight = 14.sp)
}

// ========== Theme ==========

@Composable
fun OnmyojiAutoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC5),
            background = Color(0xFF0F0F23),
            surface = Color(0xFF16213E),
            error = Color(0xFFE94560)
        ),
        content = content
    )
}

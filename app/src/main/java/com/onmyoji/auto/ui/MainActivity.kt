package com.onmyoji.auto.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.ExperimentalMaterial3Api
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onmyoji.auto.engine.TaskManager
import com.onmyoji.auto.model.*
import com.onmyoji.auto.service.AutomationService
import com.onmyoji.auto.service.ScreenCaptureService

class MainActivity : ComponentActivity() {

    private lateinit var taskManager: TaskManager

    private val projectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra("resultCode", result.resultCode)
                putExtra("resultData", result.data)
            }
            startService(serviceIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskManager = TaskManager(applicationContext)

        setContent {
            OnmyojiAutoTheme {
                MainScreen(taskManager = taskManager,
                    onRequestAccessibility = { openAccessibilitySettings() },
                    onRequestProjection = { requestScreenCapture() },
                    onCheckAccessibility = { isAccessibilityEnabled() },
                    onCheckProjection = { ScreenCaptureService.instance != null }
                )
            }
        }
    }

    private fun openAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun requestScreenCapture() {
        val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        projectionLauncher.launch(projectionManager.createScreenCaptureIntent())
    }

    private fun isAccessibilityEnabled(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        return enabledServices.any {
            it.resolveInfo.serviceInfo.packageName == packageName
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    taskManager: TaskManager,
    onRequestAccessibility: () -> Unit,
    onRequestProjection: () -> Unit,
    onCheckAccessibility: () -> Boolean,
    onCheckProjection: () -> Boolean
) {
    val context = LocalContext.current
    var selectedTask by remember { mutableStateOf(TaskType.EXPLORATION) }
    var config by remember { mutableStateOf(TaskConfig()) }
    val taskState by taskManager.state.collectAsState()
    val runningTask by taskManager.currentTask.collectAsState()

    var accessibilityEnabled by remember { mutableStateOf(onCheckAccessibility()) }
    var projectionEnabled by remember { mutableStateOf(onCheckProjection()) }

    val isRunning = taskState == TaskManager.State.RUNNING
    val isSelectedTaskRunning = isRunning && runningTask == selectedTask

    // 日志收集
    val logLines = remember { mutableStateListOf<String>() }
    val logListState = rememberLazyListState()
    var logFilePath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            accessibilityEnabled = onCheckAccessibility()
            projectionEnabled = onCheckProjection()
            kotlinx.coroutines.delay(2000)
        }
    }

    // 收集任务日志
    LaunchedEffect(taskManager) {
        taskManager.taskLogs.collect { line ->
            logLines.add(line)
            while (logLines.size > 500) {
                logLines.removeAt(0)
            }
            if (logLines.isNotEmpty()) {
                logListState.animateScrollToItem(logLines.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("阴阳师自动", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                )
            )
        }
    ) { padding ->
        // 整体用一个可滚动的 Column，不预分配固定高度
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0F0F23))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ===== 权限 =====
            Text("权限状态", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            PermissionRow("无障碍服务", accessibilityEnabled, onRequestAccessibility)
            PermissionRow("屏幕截图", projectionEnabled, onRequestProjection)

            Spacer(modifier = Modifier.height(12.dp))

            // ===== 任务选择 =====
            Text("选择任务", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            // 任务分组
            val taskGroups = listOf(
                "日常" to listOf(
                    "探索" to TaskType.EXPLORATION,
                    "个人突破" to TaskType.REALM_RAID,
                    "每日杂务" to TaskType.DAILY_TRIFLES,
                    "每周杂务" to TaskType.WEEKLY_TRIFLES,
                    "花合战" to TaskType.TALISMAN_PASS,
                ),
                "御魂/觉醒" to listOf(
                    "八岐大蛇" to TaskType.OROCHI,
                    "永生之海" to TaskType.ETERNITY_SEA,
                    "真八岐大蛇" to TaskType.TRUE_OROCHI,
                    "觉醒" to TaskType.EVO_ZONE,
                    "御灵境" to TaskType.GORYOU_REALM,
                    "染色试炼" to TaskType.DYE_TRIALS,
                    "秘闻" to TaskType.SECRET,
                ),
                "妖怪/活动" to listOf(
                    "地域鬼王" to TaskType.AREA_BOSS,
                    "深渊暗影" to TaskType.ABYSS_SHADOWS,
                    "经验妖怪" to TaskType.EXPERIENCE_YOUKAI,
                    "金币妖怪" to TaskType.GOLD_YOUKAI,
                    "鬼面来袭" to TaskType.DEMON_ENCOUNTER,
                    "鬼退治" to TaskType.DEMON_RETREAT,
                    "原" to TaskType.SOUGENBI,
                    "年兽" to TaskType.NIAN,
                    "超鬼王" to TaskType.TAKO,
                    "狩猎战" to TaskType.HUNT,
                    "日轮之城" to TaskType.FALLEN_SUN,
                ),
                "社交/其他" to listOf(
                    "寮突" to TaskType.RYOUTOPPA,
                    "斗技" to TaskType.DUEL,
                    "式神试炼" to TaskType.HERO_TEST,
                    "契灵之境" to TaskType.BONDLING_FAIRYLAND,
                    "悬赏封印" to TaskType.WANTED_QUESTS,
                    "集体任务" to TaskType.COLLECTIVE_MISSIONS,
                    "委派" to TaskType.DELEGATION,
                    "记忆绘卷" to TaskType.MEMORY_SCROLLS,
                    "寮宴" to TaskType.GUILD_BANQUET,
                    "寮活动监控" to TaskType.GUILD_ACTIVITY_MONITOR,
                ),
                "商店/整理" to listOf(
                    "御魂整理" to TaskType.SOULS_TIDY,
                    "神秘商店" to TaskType.MYSTERY_SHOP,
                    "猫咪商店" to TaskType.KITTY_SHOP,
                    "花车巡游" to TaskType.FLOAT_PARADE,
                    "宠物" to TaskType.PETS,
                    "大神签到" to TaskType.AUTO_CHECKIN_BIG_GOD,
                    "找玉" to TaskType.FIND_JADE,
                    "回主页" to TaskType.GOTO_MAIN,
                ),
            )

            for ((groupName, tasks) in taskGroups) {
                Text(groupName, color = Color(0xFF888888), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for ((label, type) in tasks) {
                        TaskChip(label, type, selectedTask) { selectedTask = it }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ===== 任务配置 =====
            Text("任务配置", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTask) {
                TaskType.EXPLORATION -> ExplorationConfig(config) { config = it }
                TaskType.REALM_RAID -> RealmRaidConfig(config) { config = it }
                TaskType.OROCHI -> GenericTaskConfig("八岐大蛇", config, listOf(
                    ConfigField("层数", config.orochiLayer) { config = config.copy(orochiLayer = it) },
                    ConfigField("模式", config.orochiUserStatus) { config = config.copy(orochiUserStatus = it) },
                    ConfigToggleData("御魂Buff", config.orochiSoulBuffEnable) { config = config.copy(orochiSoulBuffEnable = it) },
                ))
                TaskType.AREA_BOSS -> GenericTaskConfig("地域鬼王", config, listOf(
                    ConfigField("数量", config.areaBossNumber.toString()) { config = config.copy(areaBossNumber = it.toIntOrNull() ?: 3) },
                    ConfigToggleData("收藏", config.areaBossUseCollect) { config = config.copy(areaBossUseCollect = it) },
                ))
                TaskType.EVO_ZONE -> GenericTaskConfig("觉醒", config, listOf(
                    ConfigField("层数", config.evoZoneLayer) { config = config.copy(evoZoneLayer = it) },
                    ConfigField("模式", config.evoZoneUserStatus) { config = config.copy(evoZoneUserStatus = it) },
                ))
                else -> GenericTaskConfig(selectedTask.name, config, listOf(
                    ConfigField("战斗次数", config.limitTimeMinutes.toString()) { config = config.copy(limitTimeMinutes = it.toIntOrNull() ?: 30) },
                ))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ===== 开始/停止 + 清空日志 =====
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (isSelectedTaskRunning) {
                            taskManager.stopTask()
                        } else {
                            val controller = AutomationService.instance?.deviceController
                            if (controller != null) {
                                logLines.clear()
                                taskManager.setDeviceController(controller)
                                taskManager.startTask(selectedTask, config)
                                logFilePath = taskManager.lastLogFilePath
                            }
                        }
                    },
                    enabled = accessibilityEnabled && projectionEnabled,
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

                if (logLines.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { logLines.clear() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFBBBBBB)
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("清空日志", fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ===== 日志区域 =====
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "运行日志",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                if (logLines.isNotEmpty()) {
                    Text(
                        "${logLines.size} 条",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
            // 日志文件路径
            logFilePath?.let { path ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "日志已保存: $path",
                    color = Color(0xFF666666),
                    fontSize = 11.sp
                )
                Text(
                    text = "adb pull \"$path\" .",
                    color = Color(0xFF03DAC5),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A1A))
            ) {
                if (logLines.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "点击「开始」后日志将在此显示",
                            color = Color(0xFF555555),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    // 用 Column + forEach 渲染日志，参与外层滚动
                    // 最多显示最后 200 条避免卡顿
                    val displayLines = if (logLines.size > 200) {
                        logLines.subList(logLines.size - 200, logLines.size)
                    } else {
                        logLines
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        for (line in displayLines) {
                            val color = when {
                                line.contains("[错误]") || line.contains("[异常]") -> Color(0xFFFF4444)
                                line.contains("[警告]") -> Color(0xFFFFAA00)
                                line.contains("✓") -> Color(0xFF4CAF50)
                                line.contains("✗") -> Color(0xFFE94560)
                                line.startsWith("===") -> Color(0xFF03DAC5)
                                else -> Color(0xFFBBBBBB)
                            }
                            Text(
                                text = line,
                                color = color,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // 底部留白
            Spacer(modifier = Modifier.height(16.dp))
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskChip(label: String, type: TaskType, selected: TaskType, onSelect: (TaskType) -> Unit) {
    FilterChip(
        selected = selected == type,
        onClick = { onSelect(type) },
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF6200EE)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorationConfig(config: TaskConfig, onChange: (TaskConfig) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = config.explorationLevel,
            onValueChange = { onChange(config.copy(explorationLevel = it)) },
            label = { Text("探索章节") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = config.minionsCount.toString(),
                onValueChange = { onChange(config.copy(minionsCount = it.toIntOrNull() ?: 30)) },
                label = { Text("战斗次数") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = config.limitTimeMinutes.toString(),
                onValueChange = { onChange(config.copy(limitTimeMinutes = it.toIntOrNull() ?: 30)) },
                label = { Text("时间限制(分)") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealmRaidConfig(config: TaskConfig, onChange: (TaskConfig) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = config.numberAttack.toString(),
                onValueChange = { onChange(config.copy(numberAttack = it.toIntOrNull() ?: 30)) },
                label = { Text("最大挑战") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = config.orderAttack,
                onValueChange = { onChange(config.copy(orderAttack = it)) },
                label = { Text("勋章优先级") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = config.exitFour,
                onClick = { onChange(config.copy(exitFour = !config.exitFour)) },
                label = { Text("退四打九") }
            )
            FilterChip(
                selected = config.threeRefresh,
                onClick = { onChange(config.copy(threeRefresh = !config.threeRefresh)) },
                label = { Text("三胜刷新") }
            )
        }
    }
}

// ========== 通用配置组件 ==========

sealed interface ConfigItem

data class ConfigField(
    val label: String,
    val value: String,
    val onChange: (String) -> Unit
) : ConfigItem

data class ConfigToggleData(
    val label: String,
    val value: Boolean,
    val onChange: (Boolean) -> Unit
) : ConfigItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenericTaskConfig(taskName: String, config: TaskConfig, fields: List<ConfigItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (field in fields) {
            when (field) {
                is ConfigField -> OutlinedTextField(
                    value = field.value,
                    onValueChange = field.onChange,
                    label = { Text(field.label) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White)
                )
                is ConfigToggleData -> ConfigToggle(field.label, field.value, field.onChange)
            }
        }
    }
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

package com.onmyoji.auto.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onmyoji.auto.model.*

// ========== 任务分组数据 ==========

private val taskGroups = listOf(
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

// ========== 任务选择 ==========

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskSelectionSection(state: MainScreenState) {
    Column {
        Text("选择任务", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        for ((groupName, tasks) in taskGroups) {
            TaskGroupRow(groupName, tasks, state.selectedTask.value) { state.selectedTask.value = it }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskGroupRow(
    groupName: String,
    tasks: List<Pair<String, TaskType>>,
    selectedTask: TaskType,
    onSelect: (TaskType) -> Unit
) {
    Column {
        Text(groupName, color = Color(0xFF888888), fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for ((label, type) in tasks) {
                TaskChip(label, type, selectedTask, onSelect)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ========== 任务配置 ==========

@Composable
fun TaskConfigSection(state: MainScreenState) {
    val config = state.config.value
    val onChange: (TaskConfig) -> Unit = { state.config.value = it }

    Column {
        Text("任务配置", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        when (state.selectedTask.value) {
            TaskType.EXPLORATION -> ExplorationConfig(config, onChange)
            TaskType.REALM_RAID -> RealmRaidConfig(config, onChange)
            TaskType.OROCHI -> GenericTaskConfig("八岐大蛇", config, listOf(
                ConfigField("层数", config.orochiLayer) { onChange(config.copy(orochiLayer = it)) },
                ConfigField("模式", config.orochiUserStatus) { onChange(config.copy(orochiUserStatus = it)) },
                ConfigToggleData("御魂Buff", config.orochiSoulBuffEnable) { onChange(config.copy(orochiSoulBuffEnable = it)) },
            ))
            TaskType.AREA_BOSS -> GenericTaskConfig("地域鬼王", config, listOf(
                ConfigField("数量", config.areaBossNumber.toString()) { onChange(config.copy(areaBossNumber = it.toIntOrNull() ?: 3)) },
                ConfigToggleData("收藏", config.areaBossUseCollect) { onChange(config.copy(areaBossUseCollect = it)) },
            ))
            TaskType.EVO_ZONE -> GenericTaskConfig("觉醒", config, listOf(
                ConfigField("层数", config.evoZoneLayer) { onChange(config.copy(evoZoneLayer = it)) },
                ConfigField("模式", config.evoZoneUserStatus) { onChange(config.copy(evoZoneUserStatus = it)) },
            ))
            else -> GenericTaskConfig(state.selectedTask.value.name, config, listOf(
                ConfigField("战斗次数", config.limitTimeMinutes.toString()) { onChange(config.copy(limitTimeMinutes = it.toIntOrNull() ?: 30)) },
            ))
        }
    }
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

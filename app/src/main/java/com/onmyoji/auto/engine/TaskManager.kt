package com.onmyoji.auto.engine

import android.content.Context
import com.onmyoji.auto.model.TaskConfig
import com.onmyoji.auto.model.TaskType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 任务管理器 — 调度和执行任务
 */
class TaskManager(private val context: Context) {

    enum class State { IDLE, RUNNING, STOPPING }

    private val _state = MutableStateFlow(State.IDLE)
    val state: StateFlow<State> = _state

    private val _currentTask = MutableStateFlow<TaskType?>(null)
    val currentTask: StateFlow<TaskType?> = _currentTask

    // 任务日志流 — UI 可以 collect
    private val _taskLogs = MutableSharedFlow<String>(replay = 200)
    val taskLogs: SharedFlow<String> = _taskLogs.asSharedFlow()

    // 日志文件路径
    private val logDir: File
        get() {
            // 优先用外部存储（方便adb pull），回退到内部存储
            val dir = context.getExternalFilesDir(null)?.let {
                File(it, "logs")
            } ?: File(context.filesDir, "logs")
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

    private var logWriter: FileWriter? = null
    private var currentLogFile: File? = null

    /** 获取最近一次日志文件路径（UI 展示用） */
    var lastLogFilePath: String? = null
        private set

    private var job: Job? = null
    private var deviceController: DeviceController? = null

    fun setDeviceController(controller: DeviceController) {
        deviceController = controller
    }

    fun startTask(type: TaskType, config: TaskConfig) {
        if (_state.value == State.RUNNING) return
        job = CoroutineScope(Dispatchers.Default).launch {
            _state.value = State.RUNNING
            _currentTask.value = type

            // 打开日志文件
            openLogWriter(type)

            try {
                val controller = deviceController
                if (controller == null) {
                    emitAndLog("[错误] DeviceController 为空，无障碍服务可能未连接")
                    return@launch
                }
                val task = when (type) {
                    TaskType.EXPLORATION -> ExplorationTask(context, controller, config)
                    TaskType.REALM_RAID -> RealmRaidTask(context, controller, config)
                    TaskType.SOULS_TIDY -> com.onmyoji.auto.engine.tasks.SoulsTidyTask(context, controller, config)
                    TaskType.COLLECTIVE_MISSIONS -> com.onmyoji.auto.engine.tasks.CollectiveMissionsTask(context, controller, config)
                    TaskType.DELEGATION -> com.onmyoji.auto.engine.tasks.DelegationTask(context, controller, config)
                    TaskType.FIND_JADE -> com.onmyoji.auto.engine.tasks.FindJadeTask(context, controller, config)
                    TaskType.GOTO_MAIN -> com.onmyoji.auto.engine.tasks.GotoMainTask(context, controller, config)
                    TaskType.GUILD_ACTIVITY_MONITOR -> com.onmyoji.auto.engine.tasks.GuildActivityMonitorTask(context, controller, config)
                    TaskType.GUILD_BANQUET -> com.onmyoji.auto.engine.tasks.GuildBanquetTask(context, controller, config)
                    TaskType.MEMORY_SCROLLS -> com.onmyoji.auto.engine.tasks.MemoryScrollsTask(context, controller, config)
                    TaskType.PETS -> com.onmyoji.auto.engine.tasks.PetsTask(context, controller, config)
                    TaskType.TALISMAN_PASS -> com.onmyoji.auto.engine.tasks.TalismanPassTask(context, controller, config)
                    TaskType.WEEKLY_TRIFLES -> com.onmyoji.auto.engine.tasks.WeeklyTriflesTask(context, controller, config)
                    TaskType.FLOAT_PARADE -> com.onmyoji.auto.engine.tasks.FloatParadeTask(context, controller, config)
                    TaskType.AUTO_CHECKIN_BIG_GOD -> com.onmyoji.auto.engine.tasks.AutoCheckinBigGodTask(context, controller, config)
                    TaskType.DAILY_TRIFLES -> com.onmyoji.auto.engine.tasks.DailyTriflesTask(context, controller, config)
                    TaskType.MYSTERY_SHOP -> com.onmyoji.auto.engine.tasks.MysteryShopTask(context, controller, config)
                    TaskType.KITTY_SHOP -> com.onmyoji.auto.engine.tasks.KittyShopTask(context, controller, config)
                    // 战斗类任务
                    TaskType.OROCHI -> com.onmyoji.auto.engine.tasks.OrochiTask(context, controller, config)
                    TaskType.AREA_BOSS -> com.onmyoji.auto.engine.tasks.AreaBossTask(context, controller, config)
                    TaskType.ABYSS_SHADOWS -> com.onmyoji.auto.engine.tasks.AbyssShadowsTask(context, controller, config)
                    TaskType.DYE_TRIALS -> com.onmyoji.auto.engine.tasks.DyeTrialsTask(context, controller, config)
                    TaskType.GORYOU_REALM -> com.onmyoji.auto.engine.tasks.GoryouRealmTask(context, controller, config)
                    TaskType.HERO_TEST -> com.onmyoji.auto.engine.tasks.HeroTestTask(context, controller, config)
                    TaskType.RYOUTOPPA -> com.onmyoji.auto.engine.tasks.RyouToppaTask(context, controller, config)
                    TaskType.SECRET -> com.onmyoji.auto.engine.tasks.SecretTask(context, controller, config)
                    TaskType.SOUGENBI -> com.onmyoji.auto.engine.tasks.SougenbiTask(context, controller, config)
                    TaskType.DEMON_ENCOUNTER -> com.onmyoji.auto.engine.tasks.DemonEncounterTask(context, controller, config)
                    TaskType.DEMON_RETREAT -> com.onmyoji.auto.engine.tasks.DemonRetreatTask(context, controller, config)
                    TaskType.DUEL -> com.onmyoji.auto.engine.tasks.DuelTask(context, controller, config)
                    TaskType.EVO_ZONE -> com.onmyoji.auto.engine.tasks.EvoZoneTask(context, controller, config)
                    TaskType.EXPERIENCE_YOUKAI -> com.onmyoji.auto.engine.tasks.ExperienceYoukaiTask(context, controller, config)
                    TaskType.FALLEN_SUN -> com.onmyoji.auto.engine.tasks.FallenSunTask(context, controller, config)
                    TaskType.GOLD_YOUKAI -> com.onmyoji.auto.engine.tasks.GoldYoukaiTask(context, controller, config)
                    TaskType.HUNT -> com.onmyoji.auto.engine.tasks.HuntTask(context, controller, config)
                    TaskType.NIAN -> com.onmyoji.auto.engine.tasks.NianTask(context, controller, config)
                    TaskType.TAKO -> com.onmyoji.auto.engine.tasks.TakoTask(context, controller, config)
                    TaskType.BONDLING_FAIRYLAND -> com.onmyoji.auto.engine.tasks.BondlingFairylandTask(context, controller, config)
                    TaskType.TRUE_OROCHI -> com.onmyoji.auto.engine.tasks.TrueOrochiTask(context, controller, config)
                    TaskType.WANTED_QUESTS -> com.onmyoji.auto.engine.tasks.WantedQuestsTask(context, controller, config)
                    TaskType.ETERNITY_SEA -> com.onmyoji.auto.engine.tasks.EternitySeaTask(context, controller, config)
                }
                val logJob = launch {
                    task.logs.collect { line ->
                        emitAndLog(line)
                    }
                }
                task.run()
                logJob.cancel()
            } catch (e: Exception) {
                android.util.Log.e("TaskManager", "Task error", e)
                emitAndLog("[异常] ${e.javaClass.simpleName}: ${e.message}")
            } finally {
                emitAndLog("=== 任务结束 ===")
                closeLogWriter()
                _state.value = State.IDLE
                _currentTask.value = null
            }
        }
    }

    fun stopTask() {
        _state.value = State.STOPPING
        job?.cancel()
        job = null
        closeLogWriter()
        _state.value = State.IDLE
        _currentTask.value = null
    }

    fun isRunning(): Boolean = _state.value == State.RUNNING

    // ========== 日志文件操作 ==========

    private fun openLogWriter(type: TaskType) {
        try {
            val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val taskName = when (type) {
                TaskType.EXPLORATION -> "exploration"
                TaskType.REALM_RAID -> "realm_raid"
                TaskType.SOULS_TIDY -> "souls_tidy"
                TaskType.COLLECTIVE_MISSIONS -> "collective_missions"
                TaskType.DELEGATION -> "delegation"
                TaskType.FIND_JADE -> "find_jade"
                TaskType.GOTO_MAIN -> "goto_main"
                TaskType.GUILD_ACTIVITY_MONITOR -> "guild_activity_monitor"
                TaskType.GUILD_BANQUET -> "guild_banquet"
                TaskType.MEMORY_SCROLLS -> "memory_scrolls"
                TaskType.PETS -> "pets"
                TaskType.TALISMAN_PASS -> "talisman_pass"
                TaskType.WEEKLY_TRIFLES -> "weekly_trifles"
                TaskType.FLOAT_PARADE -> "float_parade"
                TaskType.AUTO_CHECKIN_BIG_GOD -> "auto_checkin_big_god"
                TaskType.DAILY_TRIFLES -> "daily_trifles"
                TaskType.MYSTERY_SHOP -> "mystery_shop"
                TaskType.KITTY_SHOP -> "kitty_shop"
                TaskType.OROCHI -> "orochi"
                TaskType.AREA_BOSS -> "area_boss"
                TaskType.ABYSS_SHADOWS -> "abyss_shadows"
                TaskType.DYE_TRIALS -> "dye_trials"
                TaskType.GORYOU_REALM -> "goryou_realm"
                TaskType.HERO_TEST -> "hero_test"
                TaskType.RYOUTOPPA -> "ryou_toppa"
                TaskType.SECRET -> "secret"
                TaskType.SOUGENBI -> "sougenbi"
                TaskType.DEMON_ENCOUNTER -> "demon_encounter"
                TaskType.DEMON_RETREAT -> "demon_retreat"
                TaskType.DUEL -> "duel"
                TaskType.EVO_ZONE -> "evo_zone"
                TaskType.EXPERIENCE_YOUKAI -> "experience_youkai"
                TaskType.FALLEN_SUN -> "fallen_sun"
                TaskType.GOLD_YOUKAI -> "gold_youkai"
                TaskType.HUNT -> "hunt"
                TaskType.NIAN -> "nian"
                TaskType.TAKO -> "tako"
                TaskType.BONDLING_FAIRYLAND -> "bondling_fairyland"
                TaskType.TRUE_OROCHI -> "true_orochi"
                TaskType.WANTED_QUESTS -> "wanted_quests"
                TaskType.ETERNITY_SEA -> "eternity_sea"
            }
            val file = File(logDir, "${taskName}_${ts}.log")
            currentLogFile = file
            lastLogFilePath = file.absolutePath
            logWriter = FileWriter(file, true)
            // 写入文件头
            val header = buildString {
                appendLine("=== OnmyojiAuto 日志 ===")
                appendLine("任务: ${taskName}")
                appendLine("时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
                appendLine("设备: ${android.os.Build.MODEL} (${android.os.Build.DISPLAY})")
                appendLine("屏幕: ${context.resources.displayMetrics.widthPixels}x${context.resources.displayMetrics.heightPixels}")
                appendLine("========================")
            }
            logWriter?.write(header)
            logWriter?.flush()
        } catch (e: Exception) {
            android.util.Log.e("TaskManager", "Failed to open log file", e)
        }
    }

    private fun emitAndLog(line: String) {
        _taskLogs.tryEmit(line)
        try {
            logWriter?.write(line)
            logWriter?.write("\n")
            logWriter?.flush()
        } catch (_: Exception) {}
    }

    private fun closeLogWriter() {
        try {
            logWriter?.flush()
            logWriter?.close()
        } catch (_: Exception) {}
        logWriter = null
    }
}

package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 章鱼/超鬼王 (Tako)
 * 对应 Python tasks/Tako/script_task.py
 * 组队模式：创建房间 → 等待队友 → 挑战 → 退出
 */
class TakoTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {
    private val I_WIN = RuleImage("win", "tasks/GeneralBattle/res/res_win.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_REWARD = RuleImage("reward", "tasks/GeneralBattle/res/res_reward.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.6f)
    private val I_FALSE = RuleImage("false", "tasks/GeneralBattle/res/res_false.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_CHECK_MAIN = RuleImage("check_main", "tasks/GameUi/res/res_check_main.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.6f)
    private val I_CHECK_TEAM = RuleImage("check_team", "tasks/GeneralRoom/res/res_check_team.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_ADD_1 = RuleImage("add_1", "tasks/GeneralRoom/res/res_add_1.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val C_REWARD_2 = RuleClick("reward_2", intArrayOf(640, 400, 100, 100))

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalRoom = GeneralRoom(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)

    override suspend fun run() {
        log("=== 超鬼王任务开始 ===")
        if (config.takoSwitchSoulEnable) { gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records"); switchSoul.runSwitchSoul(config.takoSwitchGroupTeam) }
        if (config.takoSwitchSoulEnableByName) { gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records"); switchSoul.runSwitchSoulByName(config.takoGroupName, config.takoTeamName) }

        if (config.takoBuffEnable) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main"); generalBuff.openBuff()
            if (config.takoBuffGold50) generalBuff.gold50(true)
            if (config.takoBuffGold100) generalBuff.gold100(true)
            if (config.takoBuffExp50) generalBuff.exp50(true)
            if (config.takoBuffExp100) generalBuff.exp100(true)
            generalBuff.closeBuff()
        }

        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_team")
        val zoneName = if (isWeekend()) "愤怒的石距" else "石距"
        checkZones(zoneName)
        if (!generalRoom.createRoom()) { exitTask(); return }
        generalRoom.createEnsure()

        val waitStart = System.currentTimeMillis()
        while (true) {
            val img = screenshot() ?: continue
            if (!isInRoom()) continue
            if (System.currentTimeMillis() - waitStart > 60_000) { log("Wait for too long"); exitRoom(); break }
            if (!I_ADD_1.match(img, context).matched) { log("Someone in room"); clickFire(); generalBattle.runGeneralBattle(); break }
        }
        exitTask()
    }

    private suspend fun exitTask() {
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main")
        if (config.takoBuffEnable) {
            generalBuff.openBuff()
            if (config.takoBuffGold50) generalBuff.gold50(false)
            if (config.takoBuffGold100) generalBuff.gold100(false)
            if (config.takoBuffExp50) generalBuff.exp50(false)
            if (config.takoBuffExp100) generalBuff.exp100(false)
            generalBuff.closeBuff()
        }
        log("=== 超鬼王完成 ===")
    }

    private fun isWeekend(): Boolean {
        val day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        return day in intArrayOf(1, 7) // Sun, Sat
    }

    private fun isInRoom(): Boolean = true
    private fun checkZones(name: String) {}
    private fun clickFire() {}
    private fun exitRoom() {}
}

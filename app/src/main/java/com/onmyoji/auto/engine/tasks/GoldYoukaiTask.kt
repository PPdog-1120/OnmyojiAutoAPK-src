package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 金币妖怪 (GoldYoukai)
 * 对应 Python tasks/GoldYoukai/script_task.py
 * 组队模式：创建房间 → 等待队友 → 挑战2次 → 退出
 */
class GoldYoukaiTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {
    private val I_GOLD_WIN = RuleImage("gold_win", "tasks/GoldYoukai/gy/gy_gold_win.png",
        intArrayOf(380, 44, 100, 100), intArrayOf(380, 44, 100, 100), 0.8f)
    private val I_DE_WIN = RuleImage("de_win", "tasks/DemonEncounter/boss/boss_boss_win.png",
        intArrayOf(380, 43, 100, 100), intArrayOf(380, 43, 100, 100), 0.8f)
    private val I_FALSE = RuleImage("false", "tasks/GeneralBattle/res/res_false.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_PREPARE_HIGHLIGHT = RuleImage("prepare_highlight", "tasks/GeneralBattle/res/res_prepare_highlight.png",
        intArrayOf(1118, 548, 100, 100), intArrayOf(1118, 548, 100, 100), 0.8f)
    private val I_ADD_5_1 = RuleImage("add_5_1", "tasks/GeneralRoom/res/res_add_5_1.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalRoom = GeneralRoom(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)
    private val teamHelper = TeamTaskHelper(context, device, config)

    override suspend fun run() {
        log("=== 金币妖怪任务开始 ===")
        if (config.goldYoukaiSwitchSoulEnable) { gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records"); switchSoul.runSwitchSoul(config.goldYoukaiSwitchGroupTeam) }
        if (config.goldYoukaiSwitchSoulEnableByName) { gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records"); switchSoul.runSwitchSoulByName(config.goldYoukaiGroupName, config.goldYoukaiTeamName) }

        if (config.goldYoukaiBuffGold50 || config.goldYoukaiBuffGold100) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main"); generalBuff.openBuff()
            if (config.goldYoukaiBuffGold50) generalBuff.gold50(true)
            if (config.goldYoukaiBuffGold100) generalBuff.gold100(true)
            generalBuff.closeBuff()
        }

        var count = 0
        while (count < 2) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_team")
            checkZones("金币妖怪")
            if (!generalRoom.createRoom()) { goldExit(); return }
            generalRoom.createEnsure()
            val waitStart = System.currentTimeMillis()
            while (true) {
                val img = screenshot() ?: continue
                if (!isInRoom()) continue
                if (System.currentTimeMillis() - waitStart > 50_000) { log("Wait for too long"); clickFire(); count++; generalBattle.runGeneralBattle(); break }
                if (!I_ADD_5_1.match(img, context).matched) { log("Someone in room"); clickFire(); count++; generalBattle.runGeneralBattle(); break }
            }
        }
        goldExit()
    }

    private suspend fun goldExit() {
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main")
        if (config.goldYoukaiBuffGold50 || config.goldYoukaiBuffGold100) {
            generalBuff.openBuff()
            if (config.goldYoukaiBuffGold50) generalBuff.gold50(false)
            if (config.goldYoukaiBuffGold100) generalBuff.gold100(false)
            generalBuff.closeBuff()
        }
        log("=== 金币妖怪完成 ===")
    }

    // 挑战按钮
    private val I_FIRE = RuleImage("fire", "general_invite/gi/gi_fire.png",
        intArrayOf(1179, 602, 81, 74), intArrayOf(1179, 602, 81, 74), 0.8f)

    private fun isInRoom(): Boolean { val img = screenshot() ?: return false; return I_ADD_5_1.match(img, context).matched || I_CREATE_ROOM.match(img, context).matched }
    private val I_CREATE_ROOM = RuleImage("create_room", "general_room/gr/gr_create_room.png",
        intArrayOf(985, 600, 177, 58), intArrayOf(396, 569, 813, 100), 0.8f)

    private suspend fun checkZones(name: String) {
        log("Check zones: $name")
        // 通过 generalRoom 的 checkZones 实现
        generalRoom.checkZones(name)
    }

    private suspend fun clickFire() {
        log("Click fire")
        while (true) {
            val img = screenshot() ?: continue
            if (!isInRoom()) break
            if (appearThenClick(I_FIRE, img, 1000)) continue
            break
        }
    }
}

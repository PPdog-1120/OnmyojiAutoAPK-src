package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 狩猎战 (Hunt)
 * 对应 Python tasks/Hunt/script_task.py
 * 麒麟(周一-周四) / 阴界之门(周五-周日)
 */
class HuntTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {
    private val I_HUNT_SHRINE = RuleImage("hunt_shrine", "tasks/Hunt/hunt/hunt_hunt_shrine.png",
        intArrayOf(870, 624, 65, 61), intArrayOf(870, 624, 65, 61), 0.8f)
    private val I_HUNT_HUNT = RuleImage("hunt_hunt", "tasks/Hunt/hunt/hunt_hunt_hunt.png",
        intArrayOf(157, 414, 215, 166), intArrayOf(157, 414, 215, 166), 0.8f)
    private val I_KIRIN_CHALLAGE = RuleImage("kirin_challage", "tasks/Hunt/kirin/kirin_kirin_challage.png",
        intArrayOf(1134, 597, 90, 54), intArrayOf(1134, 597, 90, 54), 0.8f)
    private val I_KIRIN_END = RuleImage("kirin_end", "tasks/Hunt/kirin/kirin_kirin_end.png",
        intArrayOf(1126, 601, 104, 44), intArrayOf(1126, 601, 104, 44), 0.8f)
    private val I_NW = RuleImage("nw", "tasks/Hunt/netherworld/netherworld_nw.png",
        intArrayOf(1060, 602, 100, 100), intArrayOf(1060, 602, 100, 100), 0.8f)
    private val I_NW_CHALLAGE = RuleImage("nw_challage", "tasks/Hunt/netherworld/netherworld_nw_challage.png",
        intArrayOf(306, 590, 171, 63), intArrayOf(306, 590, 171, 63), 0.8f)
    private val I_NW_DONE = RuleImage("nw_done", "tasks/Hunt/netherworld/netherworld_nw_done.png",
        intArrayOf(308, 600, 156, 41), intArrayOf(308, 600, 156, 41), 0.9f)
    private val I_PREPARE_HIGHLIGHT = RuleImage("prepare_highlight", "tasks/GeneralBattle/res/res_prepare_highlight.png",
        intArrayOf(1118, 548, 100, 100), intArrayOf(1118, 548, 100, 100), 0.8f)
    private val I_WIN = RuleImage("win", "tasks/GeneralBattle/res/res_win.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_FALSE = RuleImage("false", "tasks/GeneralBattle/res/res_false.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_UI_CONFIRM = RuleImage("ui_confirm", "tasks/GameUi/res/res_ui_confirm.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    private val I_UI_BACK_YELLOW = RuleImage("ui_back_yellow", "tasks/GameUi/res/res_ui_back_yellow.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    private val I_UI_BACK_RED = RuleImage("ui_back_red", "tasks/GameUi/res/res_ui_back_red.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    private var kirinDay = true

    override suspend fun run() {
        log("=== 狩猎战任务开始 ===")

        // 检查日期
        val dayOfWeek = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) - 1 // 0=Mon
        kirinDay = dayOfWeek in 0..3 // Mon-Thu

        if (!checkDatetime()) return

        // 切换御魂
        if (kirinDay && config.huntKirinGroupTeam != "-1,-1") {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.huntKirinGroupTeam)
        } else if (!kirinDay && config.huntNetherworldGroupTeam != "-1,-1") {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.huntNetherworldGroupTeam)
        }

        gameUi.uiGetCurrentPage()
        if (kirinDay) {
            gameUi.uiGoto("page_hunt_kirin"); kirin()
        } else {
            gameUi.uiGoto("page_hunt"); netherworld()
        }
        delay(1000)
        log("=== 狩猎战完成 ===")
    }

    private fun checkDatetime(): Boolean {
        val now = java.util.Calendar.getInstance()
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        if (kirinDay) {
            log("Today is the Kirin day")
            if (hour < 6) return false
            if (hour > 23) return false
        } else {
            log("Today is the Netherworld day")
            if (hour < 17) return false
            if (hour > 23) return false
        }
        return true
    }

    private suspend fun kirin() {
        log("Start kirin")
        while (true) {
            val img = screenshot() ?: continue
            if (I_PREPARE_HIGHLIGHT.match(img, context).matched) break
            if (appearThenClick(I_UI_CONFIRM, img, 900)) continue
            if (appearThenClick(I_KIRIN_CHALLAGE, img, 1500)) continue
            if (I_KIRIN_END.match(img, context).matched) {
                log("Today have already challenged the Kirin")
                uiClickUntilDisappear(I_UI_BACK_YELLOW); return
            }
        }
        log("Start battle")
        generalBattle.runGeneralBattle(config.huntKirinBattleConfig)
    }

    private suspend fun netherworld() {
        log("Start netherworld")
        while (true) {
            val img = screenshot() ?: continue
            if (isInRoom()) { if (!I_FIRE.match(img, context).matched) continue; clickFire(); break }
            if (appearThenClick(I_NW, img, 900)) continue
            if (appearThenClick(I_UI_CONFIRM, img, 900)) continue
            if (appearThenClick(I_NW_CHALLAGE, img, 1500)) continue
            if (I_NW_DONE.match(img, context).matched) { log("Today have already challenged"); uiClickUntilDisappear(I_UI_BACK_RED); return }
        }
        log("Start battle")
        generalBattle.runGeneralBattle(config.huntNetherworldBattleConfig)
    }

    private val I_FIRE = RuleImage("fire", "tasks/GeneralRoom/res/res_fire.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)

    private fun isInRoom(): Boolean = true
    private fun clickFire() {}
    private suspend fun uiClickUntilDisappear(rule: RuleImage) {
        while (true) { val img = screenshot() ?: break; if (!rule.match(img, context).matched) break; appearThenClick(rule, img, 1000) }
    }
}

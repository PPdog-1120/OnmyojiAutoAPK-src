package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 契灵之境 (BondlingFairyland)
 * 对应 Python tasks/BondlingFairyland/script_task.py
 * 支持探查/结契模式，leader/member/alone 模式
 */
class BondlingFairylandTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {

    private val I_BALL_FIRE = RuleImage("ball_fire", "tasks/BondlingFairyland/ball/ball_ball_fire.png",
        intArrayOf(1140, 575, 100, 100), intArrayOf(1140, 575, 100, 100), 0.8f)
    private val I_BALL_UNLOCK = RuleImage("ball_unlock", "tasks/BondlingFairyland/ball/ball_ball_unlock.png",
        intArrayOf(714, 637, 31, 33), intArrayOf(714, 637, 31, 33), 0.8f)
    private val I_BALL_LOCK = RuleImage("ball_lock", "tasks/BondlingFairyland/ball/ball_ball_lock.png",
        intArrayOf(715, 637, 29, 32), intArrayOf(715, 637, 29, 32), 0.8f)
    private val I_BALL_HELP = RuleImage("ball_help", "tasks/BondlingFairyland/ball/ball_ball_help.png",
        intArrayOf(963, 571, 100, 100), intArrayOf(963, 571, 100, 100), 0.8f)
    private val I_CREATE_TEAM = RuleImage("create_team", "tasks/BondlingFairyland/ball/ball_create_team.png",
        intArrayOf(569, 491, 141, 50), intArrayOf(569, 491, 141, 50), 0.8f)
    private val I_BALL_AREA = RuleImage("ball_area", "tasks/BondlingFairyland/ball/ball_ball_area.png",
        intArrayOf(61, 631, 50, 48), intArrayOf(8, 582, 138, 137), 0.8f)
    private val I_CHECK_AREA = RuleImage("check_area", "tasks/BondlingFairyland/ball/ball_check_area.png",
        intArrayOf(489, 377, 238, 95), intArrayOf(454, 337, 314, 170), 0.8f)

    private val I_BF_SEARSH = RuleImage("bf_searsh", "tasks/BondlingFairyland/bf/bf_bf_searsh.png",
        intArrayOf(1144, 580, 88, 85), intArrayOf(1102, 531, 176, 188), 0.8f)
    private val I_BF_LOCK = RuleImage("bf_lock", "tasks/BondlingFairyland/bf/bf_bf_lock.png",
        intArrayOf(826, 653, 24, 26), intArrayOf(826, 653, 24, 26), 0.8f)
    private val I_BF_UNLOCK = RuleImage("bf_unlock", "tasks/BondlingFairyland/bf/bf_bf_unlock.png",
        intArrayOf(822, 651, 28, 28), intArrayOf(822, 651, 28, 28), 0.8f)
    private val I_BF_STORE = RuleImage("bf_store", "tasks/BondlingFairyland/bf/bf_bf_store.png",
        intArrayOf(261, 632, 57, 57), intArrayOf(261, 632, 57, 57), 0.8f)

    private val I_CAPTION_ENSURE = RuleImage("caption_ensure", "tasks/BondlingFairyland/capture/capture_caption_ensure.png",
        intArrayOf(665, 610, 129, 56), intArrayOf(652, 601, 153, 77), 0.8f)
    private val I_CLICK_CAPTION = RuleImage("click_caption", "tasks/BondlingFairyland/capture/capture_click_caption.png",
        intArrayOf(68, 414, 54, 42), intArrayOf(48, 398, 97, 81), 0.8f)
    private val I_C_AUTO_TRUE = RuleImage("c_auto_true", "tasks/BondlingFairyland/capture/capture_c_auto_true.png",
        intArrayOf(765, 200, 50, 24), intArrayOf(749, 187, 79, 51), 0.8f)
    private val I_C_AUTO_FALSE = RuleImage("c_auto_false", "tasks/BondlingFairyland/capture/capture_c_auto_false.png",
        intArrayOf(766, 200, 43, 25), intArrayOf(745, 189, 88, 44), 0.8f)
    private val I_C_LOW_TRUE = RuleImage("c_low_true", "tasks/BondlingFairyland/capture/capture_c_low_true.png",
        intArrayOf(523, 284, 30, 32), intArrayOf(514, 274, 51, 50), 0.8f)
    private val I_C_LOW_FALSE = RuleImage("c_low_false", "tasks/BondlingFairyland/capture/capture_c_low_false.png",
        intArrayOf(521, 282, 33, 35), intArrayOf(513, 275, 48, 47), 0.8f)
    private val I_C_MIDUM_TRUE = RuleImage("c_midum_true", "tasks/BondlingFairyland/capture/capture_c_midum_true.png",
        intArrayOf(663, 283, 31, 31), intArrayOf(653, 277, 49, 46), 0.8f)
    private val I_C_MIDUM_FALSE = RuleImage("c_midum_false", "tasks/BondlingFairyland/capture/capture_c_midum_false.png",
        intArrayOf(662, 283, 31, 32), intArrayOf(654, 276, 49, 45), 0.8f)
    private val I_C_HIGH_TRUE = RuleImage("c_high_true", "tasks/BondlingFairyland/capture/capture_c_high_true.png",
        intArrayOf(800, 283, 33, 36), intArrayOf(792, 273, 49, 50), 0.8f)
    private val I_C_HIGH_FALSE = RuleImage("c_high_false", "tasks/BondlingFairyland/capture/capture_c_high_false.png",
        intArrayOf(801, 283, 32, 34), intArrayOf(792, 273, 51, 50), 0.8f)
    private val I_C_MINIMAL_MODE_ENABLE = RuleImage("c_minimal_mode_enable", "tasks/BondlingFairyland/capture/capture_c_minimal_mode_enable.png",
        intArrayOf(764, 155, 48, 24), intArrayOf(757, 149, 63, 39), 0.8f)
    private val I_C_MINIMAL_MODE_DISABLE = RuleImage("c_minimal_mode_disable", "tasks/BondlingFairyland/capture/capture_c_minimal_mode_disable.png",
        intArrayOf(764, 156, 46, 23), intArrayOf(758, 148, 62, 39), 0.8f)

    private val I_STONE_SURE = RuleImage("stone_sure", "tasks/BondlingFairyland/stone/stone_stone_sure.png",
        intArrayOf(644, 621, 56, 39), intArrayOf(618, 614, 120, 52), 0.8f)
    private val I_STONE_CLOSE = RuleImage("stone_close", "tasks/BondlingFairyland/stone/stone_stone_close.png",
        intArrayOf(911, 56, 42, 38), intArrayOf(888, 49, 100, 100), 0.8f)
    private val I_BUY_PLUS = RuleImage("buy_plus", "tasks/BondlingFairyland/stone/buy_plus.png",
        intArrayOf(765, 543, 43, 40), intArrayOf(765, 543, 43, 40), 0.8f)

    private val C_AREA_1 = RuleClick("area_1", intArrayOf(628, 314, 108, 137))
    private val C_AREA_2 = RuleClick("area_2", intArrayOf(689, 118, 121, 77))
    private val C_STONE_1 = RuleClick("stone_1", intArrayOf(228, 502, 26, 58))
    private val C_STONE_2 = RuleClick("stone_2", intArrayOf(439, 528, 25, 62))
    private val C_STONE_3 = RuleClick("stone_3", intArrayOf(687, 505, 26, 56))
    private val C_STONE_4 = RuleClick("stone_4", intArrayOf(944, 497, 22, 50))

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalInvite = GeneralInvite(context, device, config)
    private val generalRoom = GeneralRoom(context, device, config)

    override suspend fun run() {
        log("=== 契灵之境任务开始 ===")

        // 切换御魂
        if (config.bondlingSwitchSoulEnable) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.bondlingSwitchGroupTeam)
        }
        if (config.bondlingSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.bondlingGroupName, config.bondlingTeamName)
        }

        gameUi.uiGoto("page_bondling_fairyland")

        // 进入对应地域
        gotoBallArea(config.bondlingStoneClassIndex)

        when (config.bondlingMode) {
            "mode1" -> { runSearch(config.bondlingLimitCount); gameUi.uiGoto("page_main"); return }
        }

        // 结契模式
        when (config.bondlingUserStatus) {
            "leader", "alone" -> switchBall()
            "member" -> runMember()
        }

        gameUi.uiGoto("page_main")
        log("=== 契灵之境完成 ===")
    }

    private suspend fun gotoBallArea(index: Int) {
        while (true) {
            val img = screenshot() ?: continue
            if (I_CHECK_AREA.match(img, context).matched) break
            appearThenClick(I_BALL_AREA, img, 1200)
        }
        val clickArea = if (index in 1..4) C_AREA_1 else C_AREA_2
        uiClick(clickArea, I_BALL_AREA)
    }

    private suspend fun switchBall() {
        val idx = config.bondlingStoneClassIndex
        var currentBallIndex = idx
        while (true) {
            if (!inSearchUi()) { gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_bondling_fairyland"); continue }
            if (!ballClick(currentBallIndex)) {
                if (!runStone(config.bondlingStoneEnable)) break
                continue
            }
            if (runCatch()) log("Catch successful") else break
        }
    }

    private suspend fun ballClick(index: Int): Boolean {
        val match = mapOf(1 to C_STONE_1, 2 to C_STONE_2, 3 to C_STONE_3, 4 to C_STONE_4)
        val clickTarget = match[(index - 1) % 4 + 1] ?: return false
        var clickCount = 0
        while (true) {
            val img = screenshot() ?: continue
            if (I_BALL_HELP.match(img, context).matched) return true
            if (clickCount >= 3) return false
            val (x, y) = clickTarget.coord(); device.click(x, y); delay(1000); clickCount++
        }
    }

    private suspend fun runStone(enable: Boolean): Boolean {
        if (!enable || !I_STONE_SURE.match(screenshot() ?: return false, context).matched) return false
        while (true) {
            val img = screenshot() ?: continue
            if (!I_STONE_SURE.match(img, context).matched) return true
            repeat(3) { appearThenClick(I_BUY_PLUS, img, 1000) }
            appearThenClick(I_STONE_SURE, img, 1000)
        }
    }

    private suspend fun runSearch(limitCount: Int): Boolean {
        lockTeam()
        var remaining = limitCount
        while (true) {
            if (!inSearchUi()) continue
            if (remaining <= 0) return true
            if (currentCount >= config.bondlingLimitCount) return false
            if (isTimeUp(config.bondlingLimitTimeMinutes)) return false
            if (clickSearch()) { generalBattle.runGeneralBattle(); remaining-- } else return true
        }
    }

    private suspend fun runCatch(): Boolean {
        lockTeam()
        while (true) {
            val img = screenshot() ?: continue
            if (I_BALL_AREA.match(img, context).matched) return true
            if (!inCatchUi()) continue
            if (currentCount >= config.bondlingLimitCount) return false
            if (isTimeUp(config.bondlingLimitTimeMinutes)) return false
            // 单人挑战
            clickFire()
            generalBattle.runGeneralBattle()
        }
    }

    private suspend fun clickFire() {
        var count = 0
        while (true) {
            val img = screenshot() ?: continue
            if (!I_BALL_FIRE.match(img, context).matched) break
            if (count >= 6) break
            appearThenClick(I_BALL_FIRE, img, 1000); count++
        }
    }

    private suspend fun clickSearch(): Boolean {
        var count = 0
        while (true) {
            val img = screenshot() ?: continue
            if (count >= 3) return false
            if (!I_BF_SEARSH.match(img, context).matched) return true
            appearThenClick(I_BF_SEARSH, img, 2000); count++
        }
    }

    private fun inSearchUi(): Boolean { val img = screenshot() ?: return false; return I_BF_STORE.match(img, context).matched }
    private fun inCatchUi(): Boolean { val img = screenshot() ?: return false; return I_BALL_FIRE.match(img, context).matched }

    private suspend fun lockTeam() {
        while (true) {
            val img = screenshot() ?: continue
            if (I_BALL_LOCK.match(img, context).matched || I_BF_LOCK.match(img, context).matched) break
            appearThenClick(I_BALL_UNLOCK, img, 1000)
            appearThenClick(I_BF_UNLOCK, img, 1000)
        }
    }

    private suspend fun runMember() {
        log("Start run member")
        val waitStart = System.currentTimeMillis()
        while (true) {
            val img = screenshot() ?: continue
            if (System.currentTimeMillis() - waitStart > config.bondlingWaitTimeMinutes * 60_000) break
            if (isTimeUp(config.bondlingLimitTimeMinutes)) break
            if (isInRoom()) { generalBattle.runGeneralBattle(); continue }
        }
    }

    // 通用战斗准备标识
    private val I_PREPARE_HIGHLIGHT = RuleImage("prepare_highlight", "general_battle/gb/gb_prepare_highlight.png",
        intArrayOf(1128, 536, 100, 100), intArrayOf(1110, 500, 169, 200), 0.8f)

    private fun isInRoom(): Boolean {
        val img = screenshot() ?: return false
        return I_BALL_FIRE.match(img, context).matched || I_BALL_HELP.match(img, context).matched ||
            I_PREPARE_HIGHLIGHT.match(img, context).matched
    }

    private suspend fun uiClick(clickRule: RuleClick, stopRule: RuleImage) {
        while (true) { val img = screenshot() ?: continue; if (stopRule.match(img, context).matched) break; val (x, y) = clickRule.coord(); device.click(x, y); delay(800) }
    }
}

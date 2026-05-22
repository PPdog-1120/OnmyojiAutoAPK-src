package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 式神试炼 (HeroTest)
 * 对应 Python tasks/HeroTest/script_task.py
 * 支持鬼兵演武/兵藏秘境/传承试炼/梦虚秘境
 */
class HeroTestTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {

    private val I_ONE = RuleImage("one", "tasks/HeroTest/as/as_one.png",
        intArrayOf(633, 106, 88, 105), intArrayOf(633, 106, 88, 105), 0.7f)
    private val I_TWO = RuleImage("two", "tasks/HeroTest/as/as_two.png",
        intArrayOf(819, 685, 95, 26), intArrayOf(819, 685, 200, 26), 0.8f)
    private val I_GBB = RuleImage("gbb", "tasks/HeroTest/as/as_gbb.png",
        intArrayOf(95, 344, 37, 131), intArrayOf(95, 344, 37, 131), 0.8f)
    private val I_BCMJ = RuleImage("bcmj", "tasks/HeroTest/as/as_bcmj.png",
        intArrayOf(1086, 350, 45, 131), intArrayOf(1086, 350, 45, 131), 0.8f)
    private val I_BATTLE = RuleImage("battle", "tasks/HeroTest/as/as_battle.png",
        intArrayOf(1129, 588, 99, 49), intArrayOf(1129, 588, 99, 49), 0.8f)
    private val I_BCMJ_BATTLE = RuleImage("bcmj_battle", "tasks/HeroTest/as/as_bcmj_battle.png",
        intArrayOf(1131, 598, 92, 50), intArrayOf(1131, 598, 92, 50), 0.8f)
    private val I_LOCK = RuleImage("lock", "tasks/HeroTest/as/as_lock.png",
        intArrayOf(822, 650, 23, 26), intArrayOf(736, 638, 283, 71), 0.8f)
    private val I_UNLOCK = RuleImage("unlock", "tasks/HeroTest/as/as_unlock.png",
        intArrayOf(824, 651, 20, 25), intArrayOf(734, 632, 286, 71), 0.8f)
    private val I_BCMJ_LOCK = RuleImage("bcmj_lock", "tasks/HeroTest/as/as_bcmj_lock.png",
        intArrayOf(908, 655, 20, 26), intArrayOf(825, 645, 199, 52), 0.8f)
    private val I_BCMJ_UNLOCK = RuleImage("bcmj_unlock", "tasks/HeroTest/as/as_bcmj_unlock.png",
        intArrayOf(908, 656, 20, 20), intArrayOf(825, 642, 195, 56), 0.8f)
    private val I_BCMJ_SKILL_ADD_CONFIRM = RuleImage("bcmj_skill_add_confirm", "tasks/HeroTest/as/as_bcmj_skill_add_confirm.png",
        intArrayOf(608, 633, 64, 36), intArrayOf(608, 633, 64, 36), 0.8f)
    private val I_BCMJ_SKILL_ADD1 = RuleImage("bcmj_skill_add1", "tasks/HeroTest/as/as_bcmj_skill_add1.png",
        intArrayOf(109, 361, 90, 32), intArrayOf(103, 359, 1076, 37), 0.8f)
    private val I_BCMJ_SKILL_ADD2 = RuleImage("bcmj_skill_add2", "tasks/HeroTest/as/as_bcmj_skill_add2.png",
        intArrayOf(728, 359, 61, 32), intArrayOf(106, 356, 1068, 38), 0.8f)
    private val I_BCMJ_BLESS = RuleImage("bcmj_bless", "tasks/HeroTest/as/as_bcmj_bless.png",
        intArrayOf(261, 159, 21, 20), intArrayOf(256, 156, 939, 27), 0.8f)
    private val I_BCMJ_PROPERTY_ADD_CRITICAL = RuleImage("bcmj_property_add_critical", "tasks/HeroTest/as/as_bcmj_property_add_critical.png",
        intArrayOf(704, 359, 115, 34), intArrayOf(120, 356, 1038, 39), 0.8f)
    private val I_BCMJ_RESET_CONFIRM = RuleImage("bcmj_reset_confirm", "tasks/HeroTest/as/as_bcmj_reset_confirm.png",
        intArrayOf(668, 399, 180, 70), intArrayOf(668, 399, 180, 70), 0.8f)
    private val I_START_CHALLENGE = RuleImage("start_challenge", "tasks/HeroTest/as/as_start_challenge.png",
        intArrayOf(685, 407, 142, 47), intArrayOf(685, 407, 142, 47), 0.8f)
    private val I_CHECK_HERO1 = RuleImage("check_hero1", "tasks/HeroTest/as/as_check_hero1.png",
        intArrayOf(52, 109, 55, 66), intArrayOf(19, 79, 127, 130), 0.8f)
    private val I_CHECK_HERO2 = RuleImage("check_hero2", "tasks/HeroTest/as/as_check_hero2.png",
        intArrayOf(52, 113, 57, 58), intArrayOf(3, 62, 157, 151), 0.8f)
    private val I_SWITCH_HERO1 = RuleImage("switch_hero1", "tasks/HeroTest/as/as_switch_hero1.png",
        intArrayOf(176, 113, 251, 360), intArrayOf(0, 0, 1279, 718), 0.8f)
    private val I_SWITCH_HERO2 = RuleImage("switch_hero2", "tasks/HeroTest/as/as_switch_hero2.png",
        intArrayOf(507, 126, 254, 352), intArrayOf(0, 0, 1274, 714), 0.8f)
    private val I_ENTER_CCSL = RuleImage("enter_ccsl", "tasks/HeroTest/as/as_enter_ccsl.png",
        intArrayOf(302, 243, 41, 149), intArrayOf(252, 161, 148, 303), 0.8f)
    private val I_ENTER_MXMJ = RuleImage("enter_mxmj", "tasks/HeroTest/as/as_enter_mxmj.png",
        intArrayOf(1075, 331, 40, 138), intArrayOf(1051, 302, 92, 190), 0.8f)
    private val I_CHECK_HERO1_EXP = RuleImage("check_hero1_exp", "tasks/HeroTest/as/as_check_hero1_exp.png",
        intArrayOf(36, 466, 63, 69), intArrayOf(0, 427, 137, 145), 0.8f)
    private val I_CHECK_HERO1_SKILL = RuleImage("check_hero1_skill", "tasks/HeroTest/as/as_check_hero1_skill.png",
        intArrayOf(27, 237, 338, 142), intArrayOf(0, 139, 423, 322), 0.8f)
    private val I_CHECK_HERO2_EXP = RuleImage("check_hero2_exp", "tasks/HeroTest/as/as_check_hero2_exp.png",
        intArrayOf(33, 465, 63, 65), intArrayOf(12, 448, 100, 100), 0.8f)
    private val I_CHECK_HERO2_SKILL = RuleImage("check_hero2_skill", "tasks/HeroTest/as/as_check_hero2_skill.png",
        intArrayOf(24, 308, 250, 92), intArrayOf(0, 282, 362, 143), 0.8f)
    private val I_HERO_EXP_MAX = RuleImage("hero_exp_max", "tasks/HeroTest/as/as_hero_exp_max.png",
        intArrayOf(175, 493, 20, 20), intArrayOf(162, 481, 106, 37), 0.8f)
    private val I_REAL_MONEY = RuleImage("real_money", "tasks/HeroTest/as/as_real_money.png",
        intArrayOf(536, 320, 24, 30), intArrayOf(466, 155, 530, 411), 0.8f)
    private val I_WIN = RuleImage("win", "tasks/GeneralBattle/res/res_win.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_DE_WIN = RuleImage("de_win", "tasks/DemonEncounter/boss/boss_boss_win.png",
        intArrayOf(380, 43, 100, 100), intArrayOf(380, 43, 100, 100), 0.8f)
    private val I_FALSE = RuleImage("false", "tasks/GeneralBattle/res/res_false.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_REWARD = RuleImage("reward", "tasks/GeneralBattle/res/res_reward.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.6f)
    private val I_BACK_YOLLOW = RuleImage("back_yellow", "tasks/GameUi/res/res_ui_back_yellow.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)

    private val C_SWITCH_HERO_BTN = RuleClick("switch_hero_btn", intArrayOf(54, 113, 52, 55))

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)

    private var success = true

    override suspend fun run() {
        log("=== 式神试炼任务开始 ===")

        // 切换御魂
        if (config.heroTestSwitchSoulEnable) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.heroTestSwitchGroupTeam)
        }
        if (config.heroTestSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.heroTestGroupName, config.heroTestTeamName)
        }

        // 开启经验加成
        openExpBuff()

        // 切换英杰
        switchHero(config.heroTestLayer)

        // 初始化页面
        val pageHeroMode = initPages()

        // 进入对应模式
        gameUi.uiGoto(pageHeroMode)

        // 锁定阵容
        checkAndLockTeam()

        // 主循环
        while (true) {
            if (isTimeUp(config.heroTestLimitTimeMinutes)) { log("Time out"); break }
            if (currentCount >= config.heroTestLimitCount) { log("Count out"); break }
            gameUi.uiGoto(pageHeroMode)
            if (!canRun()) break
            val entered = enterBattle()
            if (!entered) break
            if (generalBattle.runGeneralBattle(config.heroTestBattleConfig)) log("General battle success")
        }

        closeExpBuff()
        log("=== 式神试炼完成 ===")
    }

    private suspend fun enterBattle(): Boolean {
        log("Click battle")
        var clickCnt = 0
        val maxClick = (3..4).random()
        while (true) {
            val img = screenshot() ?: continue
            if (isInBattle()) return true
            if (clickCnt >= maxClick) break
            if (appearThenClick(I_START_CHALLENGE, img, 1000)) continue
            if (appearThenClick(I_BCMJ_RESET_CONFIRM, img, 1000)) continue
            if (I_REAL_MONEY.match(img, context).matched) { log("Ticket is not enough"); return false }
            if (appearThenClick(O_FIRE, img, 1200)) { clickCnt++; continue }
        }
        log("Battle cannot enter")
        success = false
        return false
    }

    // OCR for fire button
    private val O_FIRE = RuleImage("o_fire", "tasks/HeroTest/as/as_battle.png",
        intArrayOf(1130, 585, 92, 55), intArrayOf(1126, 576, 100, 99), 0.8f)

    private fun isInBattle(): Boolean {
        val img = screenshot() ?: return false
        return !I_BATTLE.match(img, context).matched && !I_BCMJ_BATTLE.match(img, context).matched
    }

    private fun canRun(): Boolean {
        // 检查门票等
        return true
    }

    private suspend fun switchHero(layer: String) {
        gameUi.uiGoto("page_hero_test")
        val (checkHero, switchHero) = when (layer) {
            "yanwu", "mijing" -> Pair(I_CHECK_HERO1, I_SWITCH_HERO1)
            "chuancheng", "mengxu" -> Pair(I_CHECK_HERO2, I_SWITCH_HERO2)
            else -> Pair(I_CHECK_HERO1, I_SWITCH_HERO1)
        }
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(switchHero, img, 1000)) continue
            if (checkHero.match(img, context).matched) break
            if (I_CHECK_HERO_TEST.match(img, context).matched) { click(C_SWITCH_HERO_BTN); delay(1500); continue }
        }
    }

    private val I_CHECK_HERO_TEST = RuleImage("check_hero_test", "tasks/HeroTest/as/as_check_hero_test.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)

    private fun initPages(): String {
        return when (config.heroTestLayer) {
            "yanwu" -> "page_hero1_exp"
            "mijing" -> "page_hero1_skill"
            "chuancheng" -> "page_hero2_exp"
            "mengxu" -> "page_hero2_skill"
            else -> "page_hero1_exp"
        }
    }

    private suspend fun checkAndLockTeam() {
        val (lockImg, unlockImg) = when (config.heroTestLayer) {
            "mijing", "mengxu" -> Pair(I_BCMJ_LOCK, I_BCMJ_UNLOCK)
            else -> Pair(I_LOCK, I_UNLOCK)
        }
        if (config.heroTestLockTeam) {
            log("Lock team")
            uiClick(unlockImg, lockImg)
        } else {
            log("Unlock team")
            uiClick(lockImg, unlockImg)
        }
    }

    private suspend fun openExpBuff() {
        if (config.heroTestExp50Buff || config.heroTestExp100Buff) {
            gameUi.uiGoto("page_main")
            generalBuff.openBuff()
            if (config.heroTestExp100Buff) generalBuff.exp100(true)
            if (config.heroTestExp50Buff) generalBuff.exp50(true)
            generalBuff.closeBuff()
        }
    }

    private suspend fun closeExpBuff() {
        if (config.heroTestExp50Buff || config.heroTestExp100Buff) {
            gameUi.uiGoto("page_main")
            generalBuff.openBuff()
            if (config.heroTestExp100Buff) generalBuff.exp100(false)
            if (config.heroTestExp50Buff) generalBuff.exp50(false)
            generalBuff.closeBuff()
        }
    }

    private suspend fun uiClick(clickRule: RuleImage, stopRule: RuleImage) {
        while (true) {
            val img = screenshot() ?: continue
            if (stopRule.match(img, context).matched) break
            appearThenClick(clickRule, img, 800)
        }
    }

    private fun click(rule: RuleClick) {
        val (x, y) = rule.coord()
        kotlinx.coroutines.runBlocking { device.click(x, y) }
    }
}

package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 逢魔之时 (DemonEncounter)
 * 对应 Python tasks/DemonEncounter/script_task.py
 * 流程：检查时间(17:00-23:00) → 切换御魂 → 点灯笼4次 → 打boss → 退出
 */
class DemonEncounterTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {

    // ========== 资源定义 (from DemonEncounterAssets) ==========
    private val C_DE_1 = RuleClick("de_1", intArrayOf(1211, 478, 61, 58))
    private val C_DE_2 = RuleClick("de_2", intArrayOf(1196, 409, 55, 56))
    private val C_DE_3 = RuleClick("de_3", intArrayOf(1225, 344, 53, 55))
    private val C_DE_4 = RuleClick("de_4", intArrayOf(1200, 282, 56, 53))
    private val C_DM_BOSS_CLICK = RuleClick("dm_boss_click", intArrayOf(593, 274, 100, 100))

    private val I_BOSS_FIRE = RuleImage("boss_fire", "tasks/DemonEncounter/boss/boss_boss_fire.png",
        intArrayOf(1062, 549, 100, 100), intArrayOf(1062, 549, 100, 100), 0.8f)
    private val I_BOSS_CONFIRM = RuleImage("boss_confirm", "tasks/DemonEncounter/boss/boss_boss_confirm.png",
        intArrayOf(671, 400, 175, 61), intArrayOf(671, 400, 175, 61), 0.8f)
    private val I_BOSS_SELECTED = RuleImage("boss_selected", "tasks/DemonEncounter/boss/boss_boss_selected.png",
        intArrayOf(543, 339, 37, 41), intArrayOf(543, 339, 37, 41), 0.8f)
    private val I_BOSS_NO_SELECT = RuleImage("boss_no_select", "tasks/DemonEncounter/boss/boss_boss_no_select.png",
        intArrayOf(544, 337, 37, 43), intArrayOf(544, 337, 37, 43), 0.8f)
    private val I_BOSS_GATHER = RuleImage("boss_gather", "tasks/DemonEncounter/boss/boss_boss_gather.png",
        intArrayOf(801, 589, 100, 100), intArrayOf(801, 589, 100, 100), 0.8f)
    private val I_BOSS_WIN = RuleImage("boss_win", "tasks/DemonEncounter/boss/boss_boss_win.png",
        intArrayOf(380, 43, 100, 100), intArrayOf(380, 43, 100, 100), 0.8f)
    private val I_BOSS_BACK_WHITE = RuleImage("boss_back_white", "tasks/DemonEncounter/boss/boss_boss_back_white.png",
        intArrayOf(16, 12, 39, 40), intArrayOf(16, 12, 39, 40), 0.8f)
    private val I_BOSS_KILLED = RuleImage("boss_killed", "tasks/DemonEncounter/boss/boss_boss_killed.png",
        intArrayOf(654, 596, 35, 68), intArrayOf(614, 537, 123, 176), 0.8f)
    private val I_BOSS_DONE_CHECK = RuleImage("boss_done_check", "tasks/DemonEncounter/boss/boss_boss_done_check.png",
        intArrayOf(0, 450, 100, 130), intArrayOf(0, 450, 100, 130), 0.8f)
    private val I_BOSS_WAIT = RuleImage("boss_wait", "tasks/DemonEncounter/boss/boss_boss_wait.png",
        intArrayOf(490, 50, 350, 150), intArrayOf(490, 50, 350, 150), 0.8f)
    private val I_BEST_BOSS_FIRE = RuleImage("best_boss_fire", "tasks/DemonEncounter/boss/boss_best_boss_fire.png",
        intArrayOf(1087, 562, 100, 36), intArrayOf(1087, 562, 100, 36), 0.8f)

    private val I_DE_RED_DHARMA = RuleImage("de_red_dharma", "tasks/DemonEncounter/demon/demon_de_red_dharma.png",
        intArrayOf(1216, 215, 32, 33), intArrayOf(1209, 208, 51, 50), 0.7f)
    private val I_DE_FIND = RuleImage("de_find", "tasks/DemonEncounter/demon/demon_de_find.png",
        intArrayOf(1136, 593, 100, 100), intArrayOf(1136, 593, 100, 100), 0.8f)
    private val I_DE_BOSS = RuleImage("de_boss", "tasks/DemonEncounter/demon/demon_de_boss.png",
        intArrayOf(1001, 645, 45, 45), intArrayOf(1001, 645, 45, 45), 0.8f)
    private val I_DE_BOSS_BEST = RuleImage("de_boss_best", "tasks/DemonEncounter/demon/demon_de_boss_best.png",
        intArrayOf(900, 644, 45, 50), intArrayOf(900, 644, 45, 50), 0.8f)
    private val I_DE_LOCATION = RuleImage("de_location", "tasks/DemonEncounter/demon/demon_de_location.png",
        intArrayOf(26, 653, 44, 47), intArrayOf(26, 653, 44, 47), 0.8f)
    private val I_DE_AWARD = RuleImage("de_award", "tasks/DemonEncounter/demon/demon_de_award.png",
        intArrayOf(1216, 214, 42, 36), intArrayOf(1195, 198, 74, 67), 0.8f)
    private val I_DE_BOX = RuleImage("de_box", "tasks/DemonEncounter/demon/demon_de_box.png",
        intArrayOf(1210, 295, 34, 33), intArrayOf(1183, 278, 96, 277), 0.8f)
    private val I_DE_LETTER = RuleImage("de_letter", "tasks/DemonEncounter/demon/demon_de_letter.png",
        intArrayOf(1236, 358, 33, 35), intArrayOf(1177, 261, 100, 294), 0.8f)
    private val I_DE_MYSTERY = RuleImage("de_mystery", "tasks/DemonEncounter/demon/demon_de_mystery.png",
        intArrayOf(1216, 296, 27, 29), intArrayOf(1192, 278, 85, 261), 0.8f)
    private val I_DE_REALM = RuleImage("de_realm", "tasks/DemonEncounter/demon/demon_de_realm.png",
        intArrayOf(1209, 431, 30, 22), intArrayOf(1190, 274, 88, 273), 0.75f)
    private val I_DE_REALM_FIRE = RuleImage("de_realm_fire", "tasks/DemonEncounter/demon/demon_de_realm_fire.png",
        intArrayOf(696, 421, 129, 63), intArrayOf(162, 99, 1106, 610), 0.8f)
    private val I_DE_FIND_BOSS = RuleImage("de_find_boss", "tasks/DemonEncounter/demon/demon_de_find_boss.png",
        intArrayOf(1210, 425, 29, 36), intArrayOf(1182, 279, 93, 266), 0.7f)
    private val I_DE_SMALL_FIRE = RuleImage("de_small_fire", "tasks/DemonEncounter/demon/demon_de_small_fire.png",
        intArrayOf(1064, 549, 100, 100), intArrayOf(1064, 549, 100, 100), 0.8f)
    private val I_DE_DEFEAT = arrayOf(
        RuleImage("de_defeat_1", "tasks/DemonEncounter/demon/demon_de_defeat_1.png", intArrayOf(1223, 489, 42, 41), intArrayOf(1223, 489, 42, 41), 0.7f),
        RuleImage("de_defeat_2", "tasks/DemonEncounter/demon/demon_de_defeat_2.png", intArrayOf(1211, 422, 28, 35), intArrayOf(1211, 422, 28, 35), 0.7f),
        RuleImage("de_defeat_3", "tasks/DemonEncounter/demon/demon_de_defeat_3.png", intArrayOf(1231, 358, 38, 35), intArrayOf(1231, 358, 38, 35), 0.7f),
        RuleImage("de_defeat_4", "tasks/DemonEncounter/demon/demon_de_defeat_4.png", intArrayOf(1215, 295, 35, 35), intArrayOf(1215, 295, 35, 35), 0.7f)
    )
    private val I_LETTER_CLOSE = RuleImage("letter_close", "tasks/DemonEncounter/demon/demon_letter_close.png",
        intArrayOf(851, 43, 45, 45), intArrayOf(851, 43, 45, 45), 0.8f)
    private val I_JADE_50 = RuleImage("jade_50", "tasks/DemonEncounter/demon/demon_jade_50.png",
        intArrayOf(593, 425, 84, 46), intArrayOf(548, 405, 182, 83), 0.8f)
    private val I_MYSTERY_AMULET = RuleImage("mystery_amulet", "tasks/DemonEncounter/demon/demon_mystery_amulet.png",
        intArrayOf(596, 314, 84, 82), intArrayOf(581, 294, 115, 116), 0.8f)
    private val I_WIN = RuleImage("win", "tasks/GeneralBattle/res/res_win.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_FALSE = RuleImage("false", "tasks/GeneralBattle/res/res_false.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_REWARD = RuleImage("reward", "tasks/GeneralBattle/res/res_reward.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.6f)
    private val I_UI_BACK_RED = RuleImage("ui_back_red", "tasks/GameUi/res/res_ui_back_red.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    private val I_UI_CONFIRM = RuleImage("ui_confirm", "tasks/GameUi/res/res_ui_confirm.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    private val I_UI_CONFIRM_SMALL = RuleImage("ui_confirm_small", "tasks/GameUi/res/res_ui_confirm_small.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    private val I_PREPARE_HIGHLIGHT = RuleImage("prepare_highlight", "tasks/GeneralBattle/res/res_prepare_highlight.png",
        intArrayOf(1118, 548, 100, 100), intArrayOf(1118, 548, 100, 100), 0.8f)

    private val C_ANSWER_1 = RuleClick("answer_1", intArrayOf(430, 264, 440, 67))
    private val C_ANSWER_2 = RuleClick("answer_2", intArrayOf(428, 351, 438, 70))
    private val C_ANSWER_3 = RuleClick("answer_3", intArrayOf(434, 435, 437, 65))

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)

    override suspend fun run() {
        log("=== 逢魔之时任务开始 ===")

        // 检查时间 (17:00-23:00)
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (hour < 17 || hour >= 23) { log("Not in demon encounter time"); return }

        gameUi.uiGetCurrentPage()

        // 切换御魂
        if (config.deSwitchSoulEnable) {
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.deGroupName, config.deTeamName)
        }

        gameUi.uiGoto("page_demon_encounter_realworld")

        // 点灯笼
        executeLantern()

        // 打boss
        executeBoss()

        log("=== 逢魔之时完成 ===")
    }

    private suspend fun executeLantern() {
        // 先点四次寻找 — 通过检测寻找按钮是否还存在来判断次数
        var findCount = 0
        val maxFinds = 4
        while (findCount < maxFinds) {
            val img = screenshot() ?: continue
            // 如果寻找按钮还在，说明还有次数
            if (I_DE_FIND.match(img, context).matched) {
                appearThenClick(I_DE_FIND, img, 2500)
                findCount++
                log("Find lantern $findCount/$maxFinds")
            } else {
                // 寻找按钮消失，说明次数用完
                break
            }
        }
        log("Lantern count success, found $findCount times")

        // 领取红色达摩
        if (!I_DE_AWARD.match(screenshot() ?: return, context).matched) {
            // get reward
        }

        // 四个灯笼
        val matchClick = mapOf(1 to C_DE_1, 2 to C_DE_2, 3 to C_DE_3, 4 to C_DE_4)
        for (i in 1..4) {
            log("Check lantern $i")
            val lanternType = checkLantern(i)
            when (lanternType) {
                LanternType.BOX -> handleBox(matchClick[i]!!)
                LanternType.MAIL -> handleMail(matchClick[i]!!)
                LanternType.REALM -> handleRealm(matchClick[i]!!)
                LanternType.EMPTY -> log("Lantern $i is empty")
                LanternType.BATTLE -> handleBattle(matchClick[i]!!)
                LanternType.MYSTERY -> log("Lantern $i is mystery")
                LanternType.BOSS -> handleBossLantern(matchClick[i]!!)
            }
            delay(1000)
        }
    }

    private enum class LanternType { BATTLE, BOX, MAIL, REALM, EMPTY, MYSTERY, BOSS }

    private fun checkLantern(index: Int): LanternType {
        val img = screenshot() ?: return LanternType.BATTLE
        // 通过排除法判断灯笼类型
        if (I_DE_BOX.match(img, context).matched) return LanternType.BOX
        if (I_DE_LETTER.match(img, context).matched) return LanternType.MAIL
        if (I_DE_MYSTERY.match(img, context).matched) return LanternType.MYSTERY
        if (I_DE_REALM.match(img, context).matched) return LanternType.REALM
        if (I_DE_DEFEAT[index - 1].match(img, context).matched) return LanternType.EMPTY
        if (I_DE_FIND_BOSS.match(img, context).matched) return LanternType.BOSS
        return LanternType.BATTLE
    }

    private suspend fun handleBox(target: RuleClick) {
        while (true) {
            val img = screenshot() ?: continue
            if (I_JADE_50.match(img, context).matched) break
            val (x, y) = target.coord(); device.click(x, y); delay(1000)
        }
        // 购买蓝票
        if (I_MYSTERY_AMULET.match(screenshot() ?: return, context).matched) {
            appearThenClick(I_JADE_50, screenshot(), 1000)
        }
    }

    private suspend fun handleMail(target: RuleClick) {
        while (true) {
            val img = screenshot() ?: continue
            if (I_LETTER_CLOSE.match(img, context).matched) break
            val (x, y) = target.coord(); device.click(x, y); delay(1000)
        }
        // 答题 — 随机选择答案（无OCR时的最优策略）
        val answers = listOf(C_ANSWER_1, C_ANSWER_2, C_ANSWER_3)
        for (i in 1..3) {
            val answerClick = answers.random()
            log("Answer $i: selecting random option")
            while (true) {
                val img = screenshot() ?: continue
                if (!I_LETTER_CLOSE.match(img, context).matched) break
                val (x, y) = answerClick.coord(); device.click(x, y); delay(1500)
            }
        }
    }

    private suspend fun handleRealm(target: RuleClick) {
        while (true) {
            val img = screenshot() ?: continue
            if (!I_DE_LOCATION.match(img, context).matched) break
            if (appearThenClick(I_DE_REALM_FIRE, img, 700)) continue
            val (x, y) = target.coord(); device.click(x, y); delay(1000)
        }
        generalBattle.runGeneralBattle()
    }

    private suspend fun handleBattle(target: RuleClick) {
        while (true) {
            val img = screenshot() ?: continue
            if (!I_DE_LOCATION.match(img, context).matched) break
            if (I_DE_SMALL_FIRE.match(img, context).matched) {
                while (true) {
                    val frame = screenshot() ?: continue
                    if (!I_DE_SMALL_FIRE.match(frame, context).matched) break
                    appearThenClick(I_DE_SMALL_FIRE, frame, 1000)
                }
                break
            }
            val (x, y) = target.coord(); device.click(x, y); delay(1000)
        }
        generalBattle.runGeneralBattle()
    }

    private suspend fun handleBossLantern(target: RuleClick) {
        while (true) {
            val img = screenshot() ?: continue
            if (I_BOSS_KILLED.match(img, context).matched) { uiClickUntilDisappear(I_UI_BACK_RED); break }
            if (I_BOSS_FIRE.match(img, context).matched) { executeBoss(); break }
            val (x, y) = target.coord(); device.click(x, y); delay(2300)
        }
    }

    private suspend fun executeBoss() {
        // 找boss
        while (true) {
            val img = screenshot() ?: continue
            if (I_BOSS_FIRE.match(img, context).matched || I_BEST_BOSS_FIRE.match(img, context).matched) break
            if (appearThenClick(I_DE_BOSS, img, 4000)) continue
        }

        // 进入boss
        while (true) {
            val img = screenshot() ?: continue
            if (I_BOSS_CONFIRM.match(img, context).matched) {
                uiClick(I_BOSS_NO_SELECT, I_BOSS_SELECTED)
                uiClick(I_BOSS_CONFIRM, I_BOSS_GATHER)
                break
            }
            if (I_BOSS_GATHER.match(img, context).matched) break
            if (appearThenClick(I_BOSS_FIRE, img, 3000) || appearThenClick(I_BEST_BOSS_FIRE, img, 3000)) continue
        }

        delay(5000)

        // 等待战斗
        while (true) {
            val img = screenshot() ?: continue
            if (I_BOSS_DONE_CHECK.match(img, context).matched) break
            if (I_BOSS_GATHER.match(img, context).matched) { delay(2000); continue }
            if (I_BOSS_WAIT.match(img, context).matched) { delay(2000); continue }
            if (I_PREPARE_HIGHLIGHT.match(img, context).matched) { generalBattle.runGeneralBattle(); continue }
        }

        // 等待回到主界面
        waitUntilAppear(I_BOSS_GATHER)
        while (true) {
            val img = screenshot() ?: continue
            if (I_DE_LOCATION.match(img, context).matched) break
            if (appearThenClick(I_UI_CONFIRM, img, 1000)) continue
            if (appearThenClick(I_BOSS_BACK_WHITE, img, 1000)) continue
        }
    }

    private suspend fun uiClick(clickRule: RuleImage, stopRule: RuleImage) {
        while (true) { val img = screenshot() ?: continue; if (stopRule.match(img, context).matched) break; appearThenClick(clickRule, img, 1000) }
    }

    private suspend fun uiClickUntilDisappear(rule: RuleImage) {
        while (true) { val img = screenshot() ?: break; if (!rule.match(img, context).matched) break; appearThenClick(rule, img, 1000) }
    }

    private fun click(rule: RuleClick) {
        val (x, y) = rule.coord()
        kotlinx.coroutines.runBlocking { device.click(x, y) }
    }
}

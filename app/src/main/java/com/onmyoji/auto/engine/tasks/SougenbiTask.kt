package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 业原火 (Sougenbi)
 * 对应 Python tasks/Sougenbi/script_task.py
 *
 * 流程：开启加成 → 切换御魂 → 进入业原火 → 选择类型(贪/嗔/痴) → 锁定阵容 → 循环挑战 → 关闭加成
 */
class SougenbiTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from SougenbiAssets) ==========
    private val C_C_GREED = RuleClick("c_greed", intArrayOf(87, 99, 138, 51))
    private val C_C_ANGER = RuleClick("c_anger", intArrayOf(89, 203, 132, 52))
    private val C_C_FOOLERY = RuleClick("c_foolery", intArrayOf(95, 313, 142, 41))

    private val I_S_SOUGENBI = RuleImage("s_sougenbi", "tasks/Sougenbi/s/s_s_sougenbi.png",
        intArrayOf(560, 139, 57, 61), intArrayOf(412, 68, 231, 359), 0.8f)
    private val I_S_CHECK_SOUGENBI = RuleImage("s_check_sougenbi", "tasks/Sougenbi/s/s_s_check_sougenbi.png",
        intArrayOf(933, 103, 176, 68), intArrayOf(915, 80, 341, 134), 0.8f)
    private val I_S_TEAM_LOCK = RuleImage("s_team_lock", "tasks/Sougenbi/s/s_s_team_lock.png",
        intArrayOf(700, 652, 31, 36), intArrayOf(667, 649, 167, 56), 0.8f)
    private val I_S_TEAM_UNLOCK = RuleImage("s_team_unlock", "tasks/Sougenbi/s/s_s_team_unlock.png",
        intArrayOf(700, 655, 33, 33), intArrayOf(669, 645, 164, 58), 0.8f)
    private val I_S_FIRE = RuleImage("s_fire", "tasks/Sougenbi/s/s_s_fire.png",
        intArrayOf(1051, 598, 100, 43), intArrayOf(1051, 580, 100, 100), 0.7f)
    private val I_S_FIRE_GREED = RuleImage("s_fire_greed", "tasks/Sougenbi/s/s_s_fire_greed.png",
        intArrayOf(1071, 641, 21, 33), intArrayOf(1039, 586, 124, 97), 0.8f)
    private val I_S_FIRE_ANGER = RuleImage("s_fire_anger", "tasks/Sougenbi/s/s_s_fire_anger.png",
        intArrayOf(1070, 642, 21, 30), intArrayOf(1033, 574, 131, 115), 0.8f)
    private val I_S_FIRE_FOOLERY = RuleImage("s_fire_foolery", "tasks/Sougenbi/s/s_s_fire_foolery.png",
        intArrayOf(1071, 641, 22, 31), intArrayOf(1024, 572, 142, 121), 0.8f)
    private val I_CHECK_EXPLORATION = RuleImage("check_exploration", "tasks/Exploration/res/res_check_exploration.png",
        intArrayOf(1133, 124, 47, 43), intArrayOf(1100, 100, 180, 100), 0.7f)
    private val I_UI_BACK_YELLOW = RuleImage("ui_back_yellow", "tasks/GameUi/res/res_ui_back_yellow.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)

    override suspend fun run() {
        log("=== 业原火任务开始 ===")

        // 开启加成
        if (config.sougenbiBuffEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_main")
            generalBuff.openBuff()
            if (config.sougenbiBuffGold50) generalBuff.gold50(true)
            if (config.sougenbiBuffGold100) generalBuff.gold100(true)
            if (config.sougenbiBuffExp50) generalBuff.exp50(true)
            if (config.sougenbiBuffExp100) generalBuff.exp100(true)
            generalBuff.closeBuff()
        }

        // 切换御魂
        if (config.sougenbiSwitchSoulEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.sougenbiSwitchGroupTeam)
        }
        if (config.sougenbiSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.sougenbiGroupName, config.sougenbiTeamName)
        }

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_soul_zones")

        // 进入业原火
        while (true) {
            val img = screenshot() ?: continue
            if (I_S_CHECK_SOUGENBI.match(img, context).matched) break
            appearThenClick(I_S_SOUGENBI, img, 1000)
        }
        log("Click sougenbi in soul zones")
        delay(500)

        // 选择类型
        val (imageTarget, clickTarget) = when (config.sougenbiClass) {
            "greed" -> Pair(I_S_FIRE_GREED, C_C_GREED)
            "anger" -> Pair(I_S_FIRE_ANGER, C_C_ANGER)
            "foolery" -> Pair(I_S_FIRE_FOOLERY, C_C_FOOLERY)
            else -> Pair(I_S_FIRE_GREED, C_C_GREED)
        }

        checkLock(config.sougenbiLockTeam)

        while (true) {
            val img = screenshot() ?: continue
            if (imageTarget.match(img, context).matched) break
            val (x, y) = clickTarget.coord()
            device.click(x, y)
            delay(500)
        }

        // 开始循环
        while (true) {
            val img = screenshot() ?: continue
            if (!I_S_CHECK_SOUGENBI.match(img, context).matched) continue
            if (currentCount >= config.sougenbiLimitCount) { log("Sougenbi count limit out"); break }
            if (isTimeUp(config.sougenbiLimitTimeMinutes)) { log("Sougenbi time limit out"); break }

            // 点击挑战
            while (true) {
                val frame = screenshot() ?: continue
                appearThenClick(I_S_FIRE, frame, 1000)
                if (!I_S_FIRE.match(frame, context).matched) {
                    generalBattle.runGeneralBattle(config.sougenbiBattleConfig)
                    break
                }
            }
        }

        // 回到探索大世界
        while (true) {
            val img = screenshot() ?: continue
            if (I_CHECK_EXPLORATION.match(img, context).matched) break
            appearThenClick(I_UI_BACK_YELLOW, img, 1000)
        }
        log("Back to exploration")

        // 关闭加成
        if (config.sougenbiBuffEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_main")
            generalBuff.openBuff()
            if (config.sougenbiBuffGold50) generalBuff.gold50(false)
            if (config.sougenbiBuffGold100) generalBuff.gold100(false)
            if (config.sougenbiBuffExp50) generalBuff.exp50(false)
            if (config.sougenbiBuffExp100) generalBuff.exp100(false)
            generalBuff.closeBuff()
        }

        log("=== 业原火完成 ===")
    }

    private suspend fun checkLock(lock: Boolean) {
        log("Check lock: $lock")
        if (lock) {
            repeat(20) {
                val img = screenshot() ?: return
                if (I_S_TEAM_LOCK.match(img, context).matched) return
                appearThenClick(I_S_TEAM_UNLOCK, img, 1000)
            }
        } else {
            repeat(20) {
                val img = screenshot() ?: return
                if (I_S_TEAM_UNLOCK.match(img, context).matched) return
                appearThenClick(I_S_TEAM_LOCK, img, 1000)
            }
        }
    }
}

package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 狭间暗域 (AbyssShadows)
 * 对应 Python tasks/AbyssShadows/script_task.py
 *
 * 流程：检查日期(周五-周日) → 切换御魂 → 进入狭间 → 选择暗域 → 等待可进攻时间 → 战斗循环 → 返回庭院
 */
class AbyssShadowsTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from AbyssShadowsAssets) ==========
    // Click rules
    private val C_BOSS_CLICK_AREA = RuleClick("boss_click_area", intArrayOf(679, 164, 83, 54))
    private val C_GENERAL_1_CLICK_AREA = RuleClick("general_1_click_area", intArrayOf(539, 306, 64, 56))
    private val C_GENERAL_2_CLICK_AREA = RuleClick("general_2_click_area", intArrayOf(856, 293, 58, 50))
    private val C_ELITE_1_CLICK_AREA = RuleClick("elite_1_click_area", intArrayOf(453, 418, 54, 45))
    private val C_ELITE_2_CLICK_AREA = RuleClick("elite_2_click_area", intArrayOf(694, 420, 58, 44))
    private val C_ELITE_3_CLICK_AREA = RuleClick("elite_3_click_area", intArrayOf(938, 413, 60, 44))
    private val C_ABYSS_DRAGON = RuleClick("abyss_dragon", intArrayOf(232, 196, 55, 151))
    private val C_ABYSS_PEACOCK = RuleClick("abyss_peacock", intArrayOf(526, 189, 48, 165))
    private val C_ABYSS_FOX = RuleClick("abyss_fox", intArrayOf(822, 184, 49, 144))
    private val C_ABYSS_LEOPARD = RuleClick("abyss_leopard", intArrayOf(1140, 190, 50, 162))

    // Image rules
    private val I_RYOU_SHENSHE = RuleImage("ryou_shenshe", "tasks/AbyssShadows/res/res_ryou_shenshe.png",
        intArrayOf(872, 659, 62, 25), intArrayOf(872, 659, 62, 25), 0.8f)
    private val I_ABYSS_DRAGON = RuleImage("abyss_dragon_img", "tasks/AbyssShadows/res/res_abyss_dragon.png",
        intArrayOf(227, 211, 55, 151), intArrayOf(190, 147, 140, 283), 0.8f)
    private val I_ABYSS_PEACOCK = RuleImage("abyss_peacock_img", "tasks/AbyssShadows/res/res_abyss_peacock.png",
        intArrayOf(521, 152, 48, 165), intArrayOf(465, 104, 145, 312), 0.8f)
    private val I_ABYSS_FOX = RuleImage("abyss_fox_img", "tasks/AbyssShadows/res/res_abyss_fox.png",
        intArrayOf(815, 174, 49, 144), intArrayOf(789, 130, 148, 249), 0.8f)
    private val I_ABYSS_LEOPARD = RuleImage("abyss_leopard_img", "tasks/AbyssShadows/res/res_abyss_leopard.png",
        intArrayOf(1142, 166, 50, 162), intArrayOf(1093, 143, 138, 297), 0.8f)
    private val I_ABYSS_NAVIGATION = RuleImage("abyss_navigation", "tasks/AbyssShadows/res/res_abyss_navigation.png",
        intArrayOf(1200, 372, 50, 44), intArrayOf(1200, 372, 50, 44), 0.8f)
    private val I_ABYSS_SHADOWS = RuleImage("abyss_shadows", "tasks/AbyssShadows/res/res_abyss_shadows.png",
        intArrayOf(711, 489, 107, 38), intArrayOf(711, 479, 107, 48), 0.8f)
    private val I_ABYSS_MAP = RuleImage("abyss_map", "tasks/AbyssShadows/res/res_abyss_map.png",
        intArrayOf(306, 147, 170, 48), intArrayOf(306, 147, 170, 48), 0.8f)
    private val I_ABYSS_MAP_EXIT = RuleImage("abyss_map_exit", "tasks/AbyssShadows/res/res_abyss_map_exit.png",
        intArrayOf(1154, 96, 32, 32), intArrayOf(1154, 96, 32, 32), 0.8f)
    private val I_ABYSS_FIRE = RuleImage("abyss_fire", "tasks/AbyssShadows/res/res_abyss_fire.png",
        intArrayOf(1121, 605, 77, 50), intArrayOf(1121, 605, 77, 50), 0.8f)
    private val I_ABYSS_GOTO_ENEMY = RuleImage("abyss_goto_enemy", "tasks/AbyssShadows/res/res_abyss_goto_enemy.png",
        intArrayOf(1120, 610, 75, 45), intArrayOf(1120, 610, 75, 45), 0.8f)
    private val I_ENSURE_BUTTON = RuleImage("ensure_button", "tasks/AbyssShadows/res/res_ensure_button.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    private val I_PEACOCK_AREA = RuleImage("peacock_area", "tasks/AbyssShadows/res/res_peacock_area.png",
        intArrayOf(577, 14, 127, 36), intArrayOf(577, 14, 127, 36), 0.8f)
    private val I_LEOPARD_AREA = RuleImage("leopard_area", "tasks/AbyssShadows/res/res_leopard_area.png",
        intArrayOf(589, 13, 104, 39), intArrayOf(589, 13, 104, 39), 0.8f)
    private val I_FOX_AREA = RuleImage("fox_area", "tasks/AbyssShadows/res/res_fox_area.png",
        intArrayOf(581, 18, 121, 29), intArrayOf(581, 18, 121, 29), 0.8f)
    private val I_DRAGON_AREA = RuleImage("dragon_area", "tasks/AbyssShadows/res/res_dragon_area.png",
        intArrayOf(584, 15, 111, 34), intArrayOf(584, 15, 111, 34), 0.8f)
    private val I_CHANGE_AREA = RuleImage("change_area", "tasks/AbyssShadows/res/res_change_area.png",
        intArrayOf(511, 20, 27, 27), intArrayOf(511, 20, 27, 27), 0.8f)
    private val I_WAIT_TO_START = RuleImage("wait_to_start", "tasks/AbyssShadows/res/res_wait_to_start.png",
        intArrayOf(588, 64, 70, 26), intArrayOf(588, 64, 70, 26), 0.8f)
    private val I_EQUIPPING = RuleImage("equipping", "tasks/AbyssShadows/res/res_equipping.png",
        intArrayOf(1126, 545, 100, 83), intArrayOf(1126, 545, 100, 83), 0.8f)
    private val I_PREPARE_HIGHLIGHT = RuleImage("prepare_highlight", "tasks/GeneralBattle/res/res_prepare_highlight.png",
        intArrayOf(1118, 548, 100, 100), intArrayOf(1118, 548, 100, 100), 0.8f)
    private val I_WIN = RuleImage("win", "tasks/GeneralBattle/res/res_win.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    private val I_EXIT = RuleImage("exit", "tasks/GeneralBattle/res/res_exit.png",
        intArrayOf(431, 140, 100, 100), intArrayOf(431, 140, 100, 100), 0.8f)
    private val I_EXIT_ENSURE = RuleImage("exit_ensure", "tasks/GeneralBattle/res/res_exit_ensure.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)

    private val S_TO_ABYSS_SHADOWS = RuleSwipe("to_abyss_shadows", 752, 395, 758, 193)

    // 暗域类型枚举
    private enum class AreaType { DRAGON, PEACOCK, FOX, LEOPARD }

    // 敌人类型枚举
    private enum class EnemyType { BOSS, GENERAL, ELITE }

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    private var bossFightCount = 0
    private var generalFightCount = 0
    private var eliteFightCount = 0

    override suspend fun run() {
        log("=== 狭间暗域任务开始 ===")

        // 御魂切换
        if (config.abyssShadowsSwitchSoulEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.abyssShadowsSwitchGroupTeam)
        }
        if (config.abyssShadowsSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.abyssShadowsGroupName, config.abyssShadowsTeamName)
        }

        // 检查日期 (周五-周日)
        val today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        // Saturday=7, Sunday=1, Friday=6
        if (today !in intArrayOf(6, 7, 1)) {
            log("Today is not abyss shadows day, exit")
            return
        }

        var success = true

        // 进入狭间
        gotoAbyssShadows()

        // 选择暗域 (默认神龙)
        if (!selectBoss(AreaType.DRAGON)) {
            log("Failed to enter abyss shadows")
            gotoMain()
            return
        }

        // 等待可进攻时间
        waitUntilDisappear(I_WAIT_TO_START, 30000)

        // 战斗循环
        if (!config.abyssShadowsCombatTimeEnable) {
            // 未开启智能伤害
            while (true) {
                val findList = listOf(EnemyType.BOSS, EnemyType.GENERAL, EnemyType.ELITE)
                for (enemyType in findList) {
                    if (!findEnemy(enemyType)) {
                        log("Failed to find ${enemyType.name} enemy")
                        break
                    }
                }
                log("Fight times: boss=$bossFightCount, general=$generalFightCount, elite=$eliteFightCount")
                if (bossFightCount >= 2 && generalFightCount >= 4 && eliteFightCount >= 6) {
                    success = true; break
                } else {
                    appearThenClick(I_ABYSS_MAP_EXIT, screenshot(), 1000)
                    val currentArea = checkCurrentArea()
                    when (currentArea) {
                        AreaType.DRAGON -> changeArea(AreaType.PEACOCK)
                        AreaType.PEACOCK -> changeArea(AreaType.FOX)
                        AreaType.FOX -> changeArea(AreaType.LEOPARD)
                        else -> { log("All areas completed"); break }
                    }
                }
            }
        } else {
            // 开启智能伤害
            while (true) {
                if (bossFightCount < 2) bossFightCount = fightAndSwitch(EnemyType.BOSS, 2, bossFightCount)
                if (generalFightCount < 4) generalFightCount = fightAndSwitch(EnemyType.GENERAL, 4, generalFightCount)
                if (eliteFightCount < 6) eliteFightCount = fightAndSwitch(EnemyType.ELITE, 6, eliteFightCount)
                if (bossFightCount >= 2 && generalFightCount >= 4 && eliteFightCount >= 6) {
                    log("All fights completed"); success = true; break
                }
                break
            }
        }

        gotoMain()
        log("=== 狭间暗域完成, success=$success ===")
    }

    private suspend fun fightAndSwitch(enemyType: EnemyType, requiredCount: Int, fightCount: Int): Int {
        var count = fightCount
        while (count < requiredCount) {
            if (!findEnemy(enemyType)) return count
            count += when (enemyType) {
                EnemyType.BOSS -> 1
                EnemyType.GENERAL -> 2
                EnemyType.ELITE -> 3
            }
            val currentArea = checkCurrentArea()
            if (count < requiredCount && currentArea != AreaType.LEOPARD) {
                switchArea()
            }
        }
        return count
    }

    private suspend fun switchArea() {
        appearThenClick(I_ABYSS_MAP_EXIT, screenshot(), 1000)
        val currentArea = checkCurrentArea()
        when (currentArea) {
            AreaType.DRAGON -> changeArea(AreaType.PEACOCK)
            AreaType.PEACOCK -> changeArea(AreaType.FOX)
            AreaType.FOX -> changeArea(AreaType.LEOPARD)
            else -> log("All areas completed")
        }
    }

    private fun checkCurrentArea(): AreaType {
        while (true) {
            val img = screenshot() ?: continue
            if (I_PEACOCK_AREA.match(img, context).matched) return AreaType.PEACOCK
            if (I_DRAGON_AREA.match(img, context).matched) return AreaType.DRAGON
            if (I_FOX_AREA.match(img, context).matched) return AreaType.FOX
            if (I_LEOPARD_AREA.match(img, context).matched) return AreaType.LEOPARD
        }
    }

    private suspend fun changeArea(areaName: AreaType): Boolean {
        while (true) {
            if (appearThenClick(I_ABYSS_MAP_EXIT, screenshot(), 1000)) continue
            val currentArea = checkCurrentArea()
            if (currentArea == areaName) break
            if (I_ABYSS_DRAGON.match(screenshot() ?: continue, context).matched) {
                selectBoss(areaName)
                continue
            }
            appearThenClick(I_CHANGE_AREA, screenshot(), 4000)
        }
        return true
    }

    private suspend fun gotoMain() {
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")
    }

    private suspend fun gotoAbyssShadows(): Boolean {
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_guild")
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_RYOU_SHENSHE, img, 1000)) continue
            if (!I_ABYSS_SHADOWS.match(img, context).matched) {
                device.swipe(S_TO_ABYSS_SHADOWS.startX, S_TO_ABYSS_SHADOWS.startY,
                    S_TO_ABYSS_SHADOWS.endX, S_TO_ABYSS_SHADOWS.endY)
                delay(3000)
                continue
            }
            if (appearThenClick(I_ABYSS_SHADOWS, img)) break
        }
        return true
    }

    private suspend fun selectBoss(areaName: AreaType): Boolean {
        var clickTimes = 0
        while (true) {
            val img = screenshot() ?: continue
            if (I_ABYSS_DRAGON.match(img, context).matched) {
                val (x, y) = when (areaName) {
                    AreaType.DRAGON -> C_ABYSS_DRAGON.coord()
                    AreaType.PEACOCK -> C_ABYSS_PEACOCK.coord()
                    AreaType.FOX -> C_ABYSS_FOX.coord()
                    AreaType.LEOPARD -> C_ABYSS_LEOPARD.coord()
                }
                device.click(x, y)
                delay(2000)
                clickTimes++
                if (clickTimes >= 3) return false
                continue
            }
            if (I_ABYSS_NAVIGATION.match(img, context).matched) break
        }
        return true
    }

    private suspend fun findEnemy(enemyType: EnemyType): Boolean {
        while (true) {
            val img = screenshot() ?: continue
            if (I_ABYSS_MAP.match(img, context).matched) break
            appearThenClick(I_ABYSS_NAVIGATION, img, 1000)
        }
        return when (enemyType) {
            EnemyType.BOSS -> runBossFight()
            EnemyType.GENERAL -> runGeneralFight()
            EnemyType.ELITE -> runEliteFight()
        }
    }

    private suspend fun runBossFight(): Boolean {
        if (bossFightCount >= 2) return true
        log("Run boss fight")
        if (clickEnemyArea(C_BOSS_CLICK_AREA)) {
            runGeneralBattleBack("BOSS")
            bossFightCount++
            log("Fight, boss_fight_count=$bossFightCount")
            return true
        }
        return false
    }

    private suspend fun runGeneralFight(): Boolean {
        log("Run general fight")
        for (general in listOf(C_GENERAL_1_CLICK_AREA, C_GENERAL_2_CLICK_AREA)) {
            if (generalFightCount >= 4) break
            if (clickEnemyArea(general)) {
                generalFightCount++
                runGeneralBattleBack("GENERAL")
                log("Fight, general_fight_count=$generalFightCount")
            }
        }
        return true
    }

    private suspend fun runEliteFight(): Boolean {
        log("Run elite fight")
        for (elite in listOf(C_ELITE_1_CLICK_AREA, C_ELITE_2_CLICK_AREA, C_ELITE_3_CLICK_AREA)) {
            if (eliteFightCount >= 6) break
            if (clickEnemyArea(elite)) {
                eliteFightCount++
                runGeneralBattleBack("ELITE")
                log("Fight, elite_fight_count=$eliteFightCount")
            }
        }
        return true
    }

    private suspend fun clickEnemyArea(clickArea: RuleClick): Boolean {
        log("Click enemy area: ${clickArea.name}")
        while (true) {
            val img = screenshot() ?: continue
            appearThenClick(I_ABYSS_NAVIGATION, img, 1500)
            if (I_ABYSS_MAP.match(img, context).matched) break
        }

        var clickTimes = 0
        while (true) {
            val img = screenshot() ?: continue
            if (clickTimes >= 3) return false
            if (I_ABYSS_GOTO_ENEMY.match(img, context).matched) break
            val (x, y) = clickArea.coord()
            device.click(x, y)
            delay(1500)
            clickTimes++
            appearThenClick(I_ENSURE_BUTTON, img, 1000)
        }

        // 点击前往
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_ABYSS_GOTO_ENEMY, img, 1000)) {
                appearThenClick(I_ENSURE_BUTTON, img, 1000)
                delay(3000)
                continue
            } else break
        }

        if (!waitUntilAppear(I_ABYSS_FIRE, 20000)) return false

        // 点击战斗
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_ABYSS_FIRE, img, 1000)) {
                appearThenClick(I_ENSURE_BUTTON, img, 1000)
                continue
            }
            if (I_PREPARE_HIGHLIGHT.match(img, context).matched) break
        }
        return true
    }

    private suspend fun runGeneralBattleBack(monsterType: String) {
        // 确保进入战斗
        while (true) {
            val img = screenshot() ?: continue
            if (waitUntilAppear(I_EQUIPPING, 4000)) {
                appearThenClick(I_EQUIPPING, img, 1500)
            }
            if (!I_EQUIPPING.match(screenshot() ?: continue, context).matched) break
        }
        log("Clicked prepare")

        val combatTime = when (monsterType) {
            "BOSS" -> config.abyssShadowsBossCombatTime
            "GENERAL" -> config.abyssShadowsGeneralCombatTime
            "ELITE" -> config.abyssShadowsEliteCombatTime
            else -> 60
        }

        if (config.abyssShadowsCombatTimeEnable) {
            // 添加卡死检测标记
            device.stuckRecordAdd("BATTLE_STATUS_S")
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < combatTime * 1000) {
                val img = screenshot() ?: continue
                if (appearThenClick(I_WIN, img, 1500)) break
            }
            log("Combat time ended, proceeding to exit")
            device.stuckRecordClear()
        }

        // 战斗提前结束，此时没有返回按钮
        if (appearThenClick(I_WIN, screenshot(), 1500)) return

        // 点击返回
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_EXIT, img, 2000)) continue
            if (appearThenClick(I_EXIT_ENSURE, img, 2000)) continue
            if (appearThenClick(I_WIN, img, 2000)) continue
            if (I_ABYSS_NAVIGATION.match(img, context).matched) break
        }
        log("Click exit_ensure")
    }

    override protected suspend fun waitUntilDisappear(rule: RuleImage, timeoutMs: Long) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            val img = screenshot() ?: break
            if (!rule.match(img, context).matched) break
            delay(500)
        }
    }
}

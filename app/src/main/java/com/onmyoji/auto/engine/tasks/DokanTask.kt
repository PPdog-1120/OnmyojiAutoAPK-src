package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 寮道馆突破任务
 *
 * 对应 Python tasks/Dokan/script_task.py
 * 流程：进入道馆 → 选择目标 → 战斗 → 攻击馆主 → 返回庭院
 */
class DokanTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from DokanAssets) ==========
    // 道馆入口
    private val I_DOKAN_ENTRY = RuleImage("dokan_entry", "tasks/Dokan/res/res_dokan_entry.png",
        intArrayOf(1100, 300, 100, 100), intArrayOf(1100, 300, 100, 100), 0.8f)
    // 道馆地图标识
    private val I_DOKAN_MAP = RuleImage("dokan_map", "tasks/Dokan/res/res_dokan_map.png",
        intArrayOf(640, 360, 200, 100), intArrayOf(640, 360, 200, 100), 0.8f)
    // 攻击按钮
    private val I_DOKAN_ATTACK = RuleImage("dokan_attack", "tasks/Dokan/res/res_dokan_attack.png",
        intArrayOf(1100, 600, 100, 50), intArrayOf(1100, 600, 100, 50), 0.8f)
    // 挑战按钮
    private val I_DOKAN_FIRE = RuleImage("dokan_fire", "tasks/Dokan/res/res_dokan_fire.png",
        intArrayOf(1133, 584, 110, 59), intArrayOf(1122, 572, 131, 124), 0.8f)
    // 馆主标识
    private val I_DOKAN_MASTER = RuleImage("dokan_master", "tasks/Dokan/res/res_dokan_master.png",
        intArrayOf(640, 200, 100, 100), intArrayOf(640, 200, 100, 100), 0.8f)
    // 道馆已通关
    private val I_DOKAN_CLEARED = RuleImage("dokan_cleared", "tasks/Dokan/res/res_dokan_cleared.png",
        intArrayOf(640, 360, 200, 100), intArrayOf(640, 360, 200, 100), 0.8f)
    // 道馆失败
    private val I_DOKAN_FAILED = RuleImage("dokan_failed", "tasks/Dokan/res/res_dokan_failed.png",
        intArrayOf(640, 360, 200, 100), intArrayOf(640, 360, 200, 100), 0.8f)
    // 准备按钮
    private val I_PREPARE_HIGHLIGHT = RuleImage("prepare_highlight", "tasks/GeneralBattle/res/res_prepare_highlight.png",
        intArrayOf(1118, 548, 100, 100), intArrayOf(1118, 548, 100, 100), 0.8f)
    // 胜利
    private val I_WIN = RuleImage("win", "tasks/GeneralBattle/res/res_win.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    // 失败
    private val I_FALSE = RuleImage("false", "tasks/GeneralBattle/res/res_false.png",
        intArrayOf(428, 66, 100, 100), intArrayOf(428, 66, 100, 100), 0.8f)
    // 返回按钮
    private val I_UI_BACK_RED = RuleImage("ui_back_red", "tasks/GameUi/res/res_ui_back_red.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)
    private val teamHelper = TeamTaskHelper(context, device, config)

    private var attackCount = 0
    private var maxAttackCount = 30

    override suspend fun run() {
        log("=== 寮道馆任务开始 ===")

        // 进入道馆
        gotoDokan()

        // 战斗循环
        while (attackCount < maxAttackCount) {
            val img = screenshot() ?: continue

            // 检查道馆是否已通关
            if (I_DOKAN_CLEARED.match(img, context).matched) {
                log("Dokan cleared!")
                break
            }

            // 检查道馆是否失败
            if (I_DOKAN_FAILED.match(img, context).matched) {
                log("Dokan failed")
                break
            }

            // 选择目标并攻击
            if (selectAndAttack()) {
                attackCount++
                log("Attack count: $attackCount")
            }

            // 检查时间限制
            if (isTimeUp(30)) {
                log("Time limit reached")
                break
            }
        }

        // 返回庭院
        backToMain()
        log("=== 寮道馆完成, attacks=$attackCount ===")
    }

    /**
     * 进入道馆
     */
    private suspend fun gotoDokan() {
        log("Enter dokan")
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_guild")

        var attempts = 0
        while (attempts < 20) {
            val img = screenshot() ?: continue
            if (I_DOKAN_MAP.match(img, context).matched) {
                log("In dokan map")
                return
            }
            if (appearThenClick(I_DOKAN_ENTRY, img, 3000)) {
                attempts++
                continue
            }
            delay(1000)
            attempts++
        }
    }

    /**
     * 选择目标并攻击
     */
    private suspend fun selectAndAttack(): Boolean {
        log("Select and attack")
        val img = screenshot() ?: return false

        // 优先攻击馆主
        if (I_DOKAN_MASTER.match(img, context).matched) {
            log("Attack master")
            appearThenClick(I_DOKAN_MASTER, img, 2000)
            return startBattle()
        }

        // 攻击普通目标
        if (appearThenClick(I_DOKAN_ATTACK, img, 2000)) {
            return startBattle()
        }

        // 点击地图上的敌人
        device.click((300..900).random(), (200..500).random())
        delay(1000)

        val img2 = screenshot() ?: return false
        if (appearThenClick(I_DOKAN_FIRE, img2, 2000)) {
            return startBattle()
        }

        return false
    }

    /**
     * 开始战斗
     */
    private suspend fun startBattle(): Boolean {
        log("Start battle")

        // 等待准备界面
        var attempts = 0
        while (attempts < 10) {
            val img = screenshot() ?: continue
            // 已经在战斗中
            if (I_WIN.match(img, context).matched || I_FALSE.match(img, context).matched) {
                return handleBattleResult()
            }
            // 点击准备
            if (appearThenClick(I_PREPARE_HIGHLIGHT, img, 800)) {
                attempts++
                continue
            }
            delay(500)
            attempts++
        }

        // 战斗结束处理
        return handleBattleResult()
    }

    /**
     * 处理战斗结果
     */
    private suspend fun handleBattleResult(): Boolean {
        var attempts = 0
        while (attempts < 30) {
            val img = screenshot() ?: continue
            if (I_WIN.match(img, context).matched) {
                log("Battle win")
                // 点击胜利
                appearThenClick(I_WIN, img, 1500)
                // 等待奖励
                delay(2000)
                // 点击空白处
                device.click(640, 360)
                delay(1000)
                return true
            }
            if (I_FALSE.match(img, context).matched) {
                log("Battle failed")
                appearThenClick(I_FALSE, img, 1500)
                delay(1000)
                return false
            }
            delay(500)
            attempts++
        }
        return false
    }

    /**
     * 返回庭院
     */
    private suspend fun backToMain() {
        log("Back to main")
        var attempts = 0
        while (attempts < 15) {
            val img = screenshot() ?: continue
            if (I_DOKAN_MAP.match(img, context).matched || I_DOKAN_ENTRY.match(img, context).matched) {
                // 已经在道馆地图或寮界面
                break
            }
            if (appearThenClick(I_UI_BACK_RED, img, 1000)) {
                attempts++
                continue
            }
            delay(500)
            attempts++
        }
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")
    }
}

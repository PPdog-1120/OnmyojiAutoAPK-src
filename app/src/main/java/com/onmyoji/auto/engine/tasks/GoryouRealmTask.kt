package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 御灵境 (GoryouRealm)
 * 对应 Python tasks/GoryouRealm/script_task.py
 *
 * 流程：切换御魂 → 进入御灵境 → 选择御灵类型 → 锁定阵容 → 循环挑战 → 返回探索
 */
class GoryouRealmTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from GoryouRealmAssets) ==========
    private val C_GR_C_1 = RuleClick("gr_c_1", intArrayOf(107, 98, 213, 409))
    private val C_GR_C_2 = RuleClick("gr_c_2", intArrayOf(405, 98, 228, 325))
    private val C_GR_C_3 = RuleClick("gr_c_3", intArrayOf(708, 118, 232, 367))
    private val C_GR_C_4 = RuleClick("gr_c_4", intArrayOf(1017, 112, 222, 316))

    private val I_GR_FIRE = RuleImage("gr_fire", "tasks/GoryouRealm/gr/gr_gr_fire.png",
        intArrayOf(1051, 577, 100, 100), intArrayOf(1051, 577, 100, 100), 0.8f)
    private val I_GR_LOCK = RuleImage("gr_lock", "tasks/GoryouRealm/gr/gr_gr_lock.png",
        intArrayOf(702, 655, 27, 32), intArrayOf(702, 655, 27, 32), 0.8f)
    private val I_GR_UNLOCK = RuleImage("gr_unlock", "tasks/GoryouRealm/gr/gr_gr_unlock.png",
        intArrayOf(703, 653, 26, 33), intArrayOf(703, 653, 26, 33), 0.8f)
    private val I_CHECK_EXPLORATION = RuleImage("check_exploration", "tasks/Exploration/res/res_check_exploration.png",
        intArrayOf(1133, 124, 47, 43), intArrayOf(1100, 100, 180, 100), 0.7f)
    private val I_UI_BACK_YELLOW = RuleImage("ui_back_yellow", "tasks/GameUi/res/res_ui_back_yellow.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    override suspend fun run() {
        log("=== 御灵境任务开始 ===")

        // 切换御魂
        if (config.goryouRealmSwitchSoulEnable) {
            gameUi.uiGetCurrentPage()
            gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.goryouRealmSwitchGroupTeam)
        }

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_goryou_realm")

        // 选择御灵类型
        val matchClick = mapOf(
            "Dark_Divine_Dragon" to C_GR_C_1,
            "Dark_Hakuzousu" to C_GR_C_2,
            "Dark_Black_Panther" to C_GR_C_3,
            "Dark_Peacock" to C_GR_C_4
        )
        val goryouClass = config.goryouRealmClass
        val clickTarget = matchClick[goryouClass] ?: C_GR_C_1

        while (true) {
            val img = screenshot() ?: continue
            if (I_GR_FIRE.match(img, context).matched) { log("Enter GoryouRealm"); break }
            val (x, y) = clickTarget.coord()
            device.click(x, y)
            delay(1000)
        }

        // 锁定阵容
        checkLock(config.goryouRealmLockTeam)

        // 开始循环
        while (true) {
            val img = screenshot() ?: continue
            if (!I_GR_FIRE.match(img, context).matched) continue

            if (currentCount >= config.goryouRealmLimitCount) { log("GoryouRealm count limit out"); break }
            if (isTimeUp(config.goryouRealmLimitTimeMinutes)) { log("GoryouRealm time limit out"); break }
            // TODO: OCR ticket check

            // 点击挑战
            while (true) {
                val frame = screenshot() ?: continue
                if (appearThenClick(I_GR_FIRE, frame, 1000)) { /* continue clicking */ }
                if (!I_GR_FIRE.match(frame, context).matched) {
                    generalBattle.runGeneralBattle(config.goryouRealmBattleConfig)
                    break
                }
            }
        }

        // 返回探索
        uiClick(I_UI_BACK_YELLOW, I_CHECK_EXPLORATION)
        log("Back to exploration")
        log("=== 御灵境完成 ===")
    }

    private suspend fun checkLock(lock: Boolean) {
        log("Check lock: $lock")
        if (lock) {
            repeat(20) {
                val img = screenshot() ?: return
                if (I_GR_LOCK.match(img, context).matched) return
                appearThenClick(I_GR_UNLOCK, img, 1000)
            }
        } else {
            repeat(20) {
                val img = screenshot() ?: return
                if (I_GR_UNLOCK.match(img, context).matched) return
                appearThenClick(I_GR_LOCK, img, 1000)
            }
        }
    }

    private suspend fun uiClick(clickRule: RuleImage, stopRule: RuleImage) {
        while (true) {
            val img = screenshot() ?: continue
            if (stopRule.match(img, context).matched) break
            appearThenClick(clickRule, img, 1000)
        }
    }
}

package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 年兽 (Nian)
 * 对应 Python tasks/Nian/script_task.py
 * 组队自动匹配模式
 */
class NianTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {
    private val I_N_CHECK = RuleImage("n_check", "tasks/Nian/nian/nian_n_check.png",
        intArrayOf(793, 279, 242, 246), intArrayOf(793, 279, 242, 246), 0.8f)
    private val I_N_WAITING = RuleImage("n_waiting", "tasks/Nian/nian/nian_n_waiting.png",
        intArrayOf(735, 28, 54, 51), intArrayOf(735, 28, 54, 51), 0.7f)
    private val I_GR_AUTO_MATCH = RuleImage("gr_auto_match", "tasks/GeneralRoom/res/res_auto_match.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_UI_CONFIRM = RuleImage("ui_confirm", "tasks/GameUi/res/res_ui_confirm.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    private val I_UI_CONFIRM_SMALL = RuleImage("ui_confirm_small", "tasks/GameUi/res/res_ui_confirm_small.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    private val C_CLICK_SAFE = RuleClick("click_safe", intArrayOf(242, 25, 100, 65))

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val generalBuff = GeneralBuff(context, device, config)

    override suspend fun run() {
        log("=== 年兽任务开始 ===")
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_team")
        checkZones("年兽")

        // 检查CD
        val cd = checkCd()
        if (cd != null) { log("Nian in CD $cd"); return }

        var cdCount = 0
        while (true) {
            val img = screenshot() ?: continue
            if (I_N_WAITING.match(img, context).matched) break
            if (cdCount >= 4) return
            if (appearThenClick(I_GR_AUTO_MATCH, img, 1500)) { cdCount++; continue }
        }

        log("Waiting for match")
        val clickTimer = System.currentTimeMillis() + 240_000
        val checkTimer = System.currentTimeMillis() + 480_000

        while (true) {
            val img = screenshot() ?: continue
            if (isInRoom()) {
                if (waitBattle(60)) {
                    generalBattle.runGeneralBattle(config.nianBattleConfig)
                    generalBuff.openBuff()
                    if (config.nianBuffGold50) generalBuff.gold50(false)
                    if (config.nianBuffGold100) generalBuff.gold100(false)
                    generalBuff.closeBuff()
                    break
                } else break
            }
            if (System.currentTimeMillis() > clickTimer) { log("Wait 240s, try again"); device.click(C_CLICK_SAFE.coord().first, C_CLICK_SAFE.coord().second); delay(1000) }
            if (System.currentTimeMillis() > checkTimer) { log("Nian match timeout"); break }
            if (I_N_WAITING.match(img, context).matched) continue
        }
        log("=== 年兽完成 ===")
    }

    private fun checkCd(): Long? {
        val img = screenshot() ?: return null
        if (!I_N_CHECK.match(img, context).matched) return null
        // 简化：OCR检查CD
        return null
    }

    private fun isInRoom(): Boolean = true
    private fun waitBattle(waitTimeSec: Int): Boolean = false
    private fun checkZones(name: String) {}
}

package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 悬赏封印 (WantedQuests)
 * 对应 Python tasks/WantedQuests/script_task.py
 * 流程：打开悬赏封印 → 追踪任务 → 前往执行(式神/探索/秘闻) → 邀请协作 → 退出
 */
class WantedQuestsTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {

    // ========== 资源定义 (from WantedQuestsAssets) ==========
    private val C_SECRET_CHAT = RuleClick("secret_chat", intArrayOf(597, 296, 59, 100))
    private val C_SPECIAL_MAIN = RuleClick("special_main", intArrayOf(409, 572, 32, 30))
    private val C_WQ_TRACE_ONE_CLOSE = RuleClick("wq_trace_one_close", intArrayOf(170, 30, 400, 100))

    private val I_WQC_LOCK = RuleImage("wqc_lock", "tasks/WantedQuests/chanllenge/chanllenge_wqc_lock.png",
        intArrayOf(1083, 509, 27, 31), intArrayOf(1083, 509, 27, 31), 0.8f)
    private val I_WQC_UNLOCK = RuleImage("wqc_unlock", "tasks/WantedQuests/chanllenge/chanllenge_wqc_unlock.png",
        intArrayOf(1083, 510, 26, 28), intArrayOf(1083, 510, 26, 28), 0.8f)
    private val I_WQC_FIRE = RuleImage("wqc_fire", "tasks/WantedQuests/chanllenge/chanllenge_wqc_fire.png",
        intArrayOf(1082, 587, 97, 44), intArrayOf(1049, 551, 169, 139), 0.7f)

    private val I_WQ_INVITE_1 = RuleImage("wq_invite_1", "tasks/WantedQuests/invite/invite_wq_invite_1.png",
        intArrayOf(137, 361, 39, 47), intArrayOf(108, 338, 100, 100), 0.8f)
    private val I_WQ_INVITE_2 = RuleImage("wq_invite_2", "tasks/WantedQuests/invite/invite_wq_invite_2.png",
        intArrayOf(462, 361, 37, 47), intArrayOf(435, 336, 100, 100), 0.8f)
    private val I_WQ_INVITE_3 = RuleImage("wq_invite_3", "tasks/WantedQuests/invite/invite_wq_invite_3.png",
        intArrayOf(754, 366, 39, 42), intArrayOf(728, 339, 100, 100), 0.8f)
    private val I_WQ_INVITE_ENSURE = RuleImage("wq_invite_ensure", "tasks/WantedQuests/invite/wq_invite_ensure.png",
        intArrayOf(500, 540, 132, 60), intArrayOf(500, 540, 140, 65), 0.8f)
    private val I_WQ_INVITE_CANCEL = RuleImage("wq_invite_cancel", "tasks/WantedQuests/invite/wq_invite_cancel.png",
        intArrayOf(230, 540, 132, 60), intArrayOf(230, 540, 140, 65), 0.8f)
    private val I_WQ_INVITE_SELECTED = RuleImage("wq_invite_selected", "tasks/WantedQuests/invite/wq_invite_selected.png",
        intArrayOf(370, 180, 32, 32), intArrayOf(370, 180, 370, 355), 0.8f)
    private val I_WQ_INVITE_DIFF_SVR = RuleImage("wq_invite_diff_svr", "tasks/WantedQuests/invite/wq_invite_diff_svr.png",
        intArrayOf(280, 90, 60, 35), intArrayOf(280, 90, 105, 65), 0.8f)
    private val I_WQ_INVITE_DIFF_SVR_HIGHLIGHT = RuleImage("wq_invite_diff_svr_highlight", "tasks/WantedQuests/invite/wq_invite_diff_svr_highlight.png",
        intArrayOf(260, 70, 120, 90), intArrayOf(260, 70, 120, 90), 0.9f)
    private val I_WQ_INVITE_FRIEND_LIST_APPEAR = RuleImage("wq_invite_friend_list_appear", "tasks/WantedQuests/invite/wq_invite_friend_list_appear.png",
        intArrayOf(370, 180, 32, 32), intArrayOf(370, 180, 370, 355), 0.8f)

    private val I_WQ_SEAL = RuleImage("wq_seal", "tasks/WantedQuests/wq/wq_wq_seal.png",
        intArrayOf(174, 184, 20, 29), intArrayOf(56, 93, 664, 455), 0.8f)
    private val I_WQ_DONE = RuleImage("wq_done", "tasks/WantedQuests/wq/wq_wq_done.png",
        intArrayOf(248, 183, 37, 39), intArrayOf(63, 134, 624, 401), 0.8f)
    private val I_TRACE_ENABLE = RuleImage("trace_enable", "tasks/WantedQuests/wq/wq_trace_enable.png",
        intArrayOf(1097, 588, 101, 70), intArrayOf(1097, 588, 101, 70), 0.8f)
    private val I_TRACE_DISABLE = RuleImage("trace_disable", "tasks/WantedQuests/wq/wq_trace_disable.png",
        intArrayOf(1091, 586, 108, 70), intArrayOf(1091, 586, 108, 70), 0.8f)
    private val I_WQ_BOX = RuleImage("wq_box", "tasks/WantedQuests/wq/wq_wq_box.png",
        intArrayOf(48, 187, 43, 38), intArrayOf(12, 78, 108, 496), 0.7f)
    private val I_TRACE_TRUE = RuleImage("trace_true", "tasks/WantedQuests/wq/wq_trace_true.png",
        intArrayOf(173, 187, 28, 29), intArrayOf(173, 187, 28, 29), 0.8f)
    private val I_TRACE_FALSE = RuleImage("trace_false", "tasks/WantedQuests/wq/wq_trace_false.png",
        intArrayOf(170, 186, 31, 32), intArrayOf(170, 186, 31, 32), 0.8f)
    private val I_GOTO_1 = RuleImage("goto_1", "tasks/WantedQuests/wq/wq_goto_1.png",
        intArrayOf(978, 234, 87, 45), intArrayOf(978, 234, 87, 60), 0.8f)
    private val I_GOTO_2 = RuleImage("goto_2", "tasks/WantedQuests/wq/wq_goto_2.png",
        intArrayOf(979, 305, 88, 43), intArrayOf(979, 305, 88, 58), 0.8f)
    private val I_GOTO_3 = RuleImage("goto_3", "tasks/WantedQuests/wq/wq_goto_3.png",
        intArrayOf(979, 373, 88, 47), intArrayOf(979, 373, 88, 62), 0.8f)
    private val I_GOTO_4 = RuleImage("goto_4", "tasks/WantedQuests/wq/wq_goto_4.png",
        intArrayOf(979, 447, 87, 42), intArrayOf(979, 447, 87, 57), 0.8f)
    private val I_WQSE_FIRE = RuleImage("wqse_fire", "tasks/WantedQuests/wq/wq_wqse_fire.png",
        intArrayOf(1041, 556, 100, 100), intArrayOf(1016, 534, 147, 138), 0.8f)
    private val I_TREASURE_BOX_CLICK = RuleImage("treasure_box_click", "tasks/Exploration/res/res_treasure_box_click.png",
        intArrayOf(33, 476, 70, 49), intArrayOf(2, 130, 135, 406), 0.7f)
    private val I_UI_BACK_RED = RuleImage("ui_back_red", "tasks/GameUi/res/res_ui_back_red.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    private val I_UI_BACK_BLUE = RuleImage("ui_back_blue", "tasks/GameUi/res/res_ui_back_blue.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    private val I_UI_BACK_YELLOW = RuleImage("ui_back_yellow", "tasks/GameUi/res/res_ui_back_yellow.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    private val I_CHECK_EXPLORATION = RuleImage("check_exploration", "tasks/Exploration/res/res_check_exploration.png",
        intArrayOf(1133, 124, 47, 43), intArrayOf(1100, 100, 180, 100), 0.7f)

    private val S_WQ_LIST_UP = RuleSwipe("wq_list_up", 60, 250, 65, 200)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    override suspend fun run() {
        log("=== 悬赏封印任务开始 ===")

        // 切换御魂
        if (config.wqSwitchSoulEnable) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.wqSwitchGroupTeam)
        }
        if (config.wqSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.wqGroupName, config.wqTeamName)
        }

        // 前置工作：追踪任务
        if (!preWork()) {
            log("Cannot pre-work"); return
        }

        // 执行任务
        var errorCount = 0
        while (true) {
            val img = screenshot() ?: continue
            if (!isWqRemained()) { log("No more wq remained"); break }
            if (I_WQ_BOX.match(img, context).matched) { log("Get reward"); continue }
            if (I_TREASURE_BOX_CLICK.match(img, context).matched) { log("Get treasure"); continue }
            if (errorCount > 3) { log("Failed too many times"); break }

            // 简化：OCR查找任务
            val found = findWq(img)
            if (!found) { errorCount++; device.swipe(S_WQ_LIST_UP.startX, S_WQ_LIST_UP.startY, S_WQ_LIST_UP.endX, S_WQ_LIST_UP.endY); delay(1000); continue }
            errorCount = 0
            delay(1500)
        }

        log("=== 悬赏封印完成 ===")
    }

    private suspend fun preWork(): Boolean {
        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_main")
        val doneTimer = System.currentTimeMillis() + 5000
        while (true) {
            val img = screenshot() ?: continue
            if (I_TRACE_DISABLE.match(img, context).matched) break
            if (appearThenClick(I_WQ_SEAL, img, 1000)) continue
            if (appearThenClick(I_WQ_DONE, img, 1000)) continue
            if (appearThenClick(I_TRACE_ENABLE, img, 1000)) continue
            if (System.currentTimeMillis() > doneTimer) { uiClickUntilDisappear(I_UI_BACK_RED); return false }
        }
        log("All wanted quests are traced")

        // 邀请协作
        val img = screenshot()
        if (img != null && (I_WQ_INVITE_1.match(img, context).matched || I_WQ_INVITE_2.match(img, context).matched || I_WQ_INVITE_3.match(img, context).matched)) {
            inviteFive()
        }
        uiClickUntilDisappear(I_UI_BACK_RED)
        gameUi.uiGoto("page_exploration")
        return true
    }

    private suspend fun inviteFive() {
        log("Invite friends")
        inviteRandom(I_WQ_INVITE_1)
        inviteRandom(I_WQ_INVITE_2)
        inviteRandom(I_WQ_INVITE_3)
    }

    private suspend fun inviteRandom(addButton: RuleImage) {
        val img = screenshot() ?: return
        if (!addButton.match(img, context).matched) return
        uiClick(addButton, I_WQ_INVITE_ENSURE)
        delay(1000)
        // 简化：点击5个好友
        delay(500)
        uiClickUntilDisappear(I_WQ_INVITE_ENSURE)
    }

    private fun findWq(img: android.graphics.Bitmap): Boolean {
        // 简化实现：检查是否有可执行的任务
        return I_GOTO_1.match(img, context).matched || I_GOTO_2.match(img, context).matched ||
            I_GOTO_3.match(img, context).matched || I_GOTO_4.match(img, context).matched
    }

    private fun isWqRemained(): Boolean {
        val img = screenshot() ?: return false
        // 检测是否还有任务
        return I_TRACE_ENABLE.match(img, context).matched || I_TRACE_DISABLE.match(img, context).matched
    }

    private suspend fun uiClick(clickRule: RuleImage, stopRule: RuleImage) {
        while (true) { val img = screenshot() ?: continue; if (stopRule.match(img, context).matched) break; appearThenClick(clickRule, img, 1000) }
    }

    private suspend fun uiClickUntilDisappear(rule: RuleImage) {
        while (true) { val img = screenshot() ?: break; if (!rule.match(img, context).matched) break; appearThenClick(rule, img, 1000) }
    }
}

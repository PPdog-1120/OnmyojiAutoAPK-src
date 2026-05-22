package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 委派任务 — 执行委派任务
 *
 * 流程：进入委派界面 → 检查奖励 → 委派指定任务 → 退出
 */
class DelegationTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 委派式神确认
    private val I_D_CONFIRM = RuleImage(
        "d_confirm",
        "Delegation/d/d_d_confirm.png",
        intArrayOf(979, 302, 152, 62),
        intArrayOf(979, 302, 152, 62),
        0.8f
    )

    // 出发
    private val I_D_START = RuleImage(
        "d_start",
        "Delegation/d/d_d_start.png",
        intArrayOf(1101, 534, 120, 129),
        intArrayOf(1101, 534, 120, 129),
        0.8f
    )

    // 跳过
    private val I_D_SKIP = RuleImage(
        "d_skip",
        "Delegation/d/d_d_skip.png",
        intArrayOf(661, 518, 70, 39),
        intArrayOf(661, 518, 70, 39),
        0.67f
    )

    // 选择标记
    private val I_D_SELECT_1 = RuleImage(
        "d_select_1",
        "Delegation/d/d_d_select_1.png",
        intArrayOf(230, 593, 44, 41),
        intArrayOf(216, 553, 71, 106),
        0.7f
    )

    private val I_D_SELECT_2 = RuleImage(
        "d_select_2",
        "Delegation/d/d_d_select_2.png",
        intArrayOf(363, 595, 40, 41),
        intArrayOf(327, 547, 97, 98),
        0.7f
    )

    private val I_D_SELECT_3 = RuleImage(
        "d_select_3",
        "Delegation/d/d_d_select_3.png",
        intArrayOf(497, 595, 38, 41),
        intArrayOf(456, 548, 105, 102),
        0.7f
    )

    private val I_D_SELECT_4 = RuleImage(
        "d_select_4",
        "Delegation/d/d_d_select_4.png",
        intArrayOf(632, 595, 42, 40),
        intArrayOf(579, 556, 111, 85),
        0.7f
    )

    // 返回
    private val I_D_BACK = RuleImage(
        "d_back",
        "Delegation/d/d_d_back.png",
        intArrayOf(940, 415, 138, 51),
        intArrayOf(940, 415, 138, 51),
        0.8f
    )

    // 完美达成
    private val I_REWARDS_GET = RuleImage(
        "rewards_get",
        "Delegation/rewards/rewards_rewards_get.png",
        intArrayOf(444, 78, 100, 100),
        intArrayOf(444, 78, 100, 100),
        0.8f
    )

    // 领取
    private val I_REWARDS_CHAT = RuleImage(
        "rewards_chat",
        "Delegation/rewards/rewards_rewards_chat.png",
        intArrayOf(1171, 124, 48, 65),
        intArrayOf(1171, 124, 48, 65),
        0.8f
    )

    // 完成
    private val I_REWARDS_DONE = RuleImage(
        "rewards_done",
        "Delegation/rewards/rewards_rewards_done.png",
        intArrayOf(982, 303, 149, 60),
        intArrayOf(982, 303, 149, 60),
        0.8f
    )

    // 最小化
    private val I_REWARDS_MIN = RuleImage(
        "rewards_min",
        "Delegation/rewards/rewards_rewards_min.png",
        intArrayOf(840, 131, 44, 58),
        intArrayOf(840, 131, 44, 58),
        0.8f
    )

    // 差强人意
    private val I_REWARDS_FALSE = RuleImage(
        "rewards_false",
        "Delegation/rewards/rewards_rewards_false.png",
        intArrayOf(430, 72, 100, 100),
        intArrayOf(430, 72, 100, 100),
        0.8f
    )

    // 对话框
    private val I_CHAT_1 = RuleImage(
        "chat_1",
        "Delegation/rewards/rewards_chat_1.png",
        intArrayOf(735, 350, 318, 100),
        intArrayOf(651, 161, 601, 391),
        0.8f
    )

    private val I_CHAT_2 = RuleImage(
        "chat_2",
        "Delegation/rewards/rewards_chat_2.png",
        intArrayOf(721, 384, 325, 100),
        intArrayOf(721, 384, 325, 100),
        0.8f
    )

    override suspend fun run() {
        log("=== 委派任务开始 ===")

        // 检查并领取奖励
        checkReward()

        // 执行委派
        val delegationNames = config.delegationNames
        for (name in delegationNames) {
            delegateOne(name)
        }

        log("=== 委派任务完成 ===")
    }

    /**
     * 委派一个任务
     */
    private suspend fun delegateOne(name: String): Boolean {
        log("委派: $name")
        // 查找任务名称
        var found = false
        var attempts = 0
        while (attempts < 20) {
            val img = screenshot() ?: continue
            // 检查是否找到出发按钮
            if (I_D_START.match(img, context).matched) {
                found = true
                break
            }
            // 如果出现返回，说明正在委派中
            if (I_D_BACK.match(img, context).matched) {
                log("$name 正在委派中")
                while (true) {
                    val frame = screenshot() ?: break
                    if (!I_D_BACK.match(frame, context).matched) break
                    appearThenClick(I_D_BACK, frame)
                }
                return false
            }
            if (appearThenClick(I_D_SKIP, img)) continue
            if (appearThenClick(I_D_CONFIRM, img)) continue
            // 点击任务区域查找
            device.click(766 + 509 / 2, 127 + 529 / 2)
            delay(1000)
            attempts++
        }

        if (!found) {
            log("$name 未找到")
            return false
        }

        log("进入委派: $name")
        // 选择式神位置
        val selectRules = listOf(
            I_D_SELECT_1 to Pair(162 + 100 / 2, 496 + 166 / 2),
            I_D_SELECT_2 to Pair(293 + 100 / 2, 493 + 170 / 2),
            I_D_SELECT_3 to Pair(427 + 100 / 2, 492 + 170 / 2),
            I_D_SELECT_4 to Pair(563 + 100 / 2, 493 + 174 / 2)
        )

        for ((rule, pos) in selectRules) {
            while (true) {
                val img = screenshot() ?: break
                if (rule.match(img, context).matched) break
                device.click(pos.first, pos.second)
                delay(1500)
            }
        }

        // 点击出发
        log("点击出发")
        while (true) {
            val img = screenshot() ?: break
            if (!I_D_START.match(img, context).matched) break
            if (appearThenClick(I_D_START, img, 1800)) continue
            device.click(695 + 100 / 2, 491 + 178 / 2)
            delay(800)
        }

        return true
    }

    /**
     * 检查并领取奖励
     */
    private suspend fun checkReward() {
        log("检查委派奖励")
        var checkTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - checkTime < 3000) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_REWARDS_GET, img)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (appearThenClick(I_REWARDS_CHAT, img)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (appearThenClick(I_CHAT_1, img)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (appearThenClick(I_CHAT_2, img)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (appearThenClick(I_REWARDS_DONE, img)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (appearThenClick(I_REWARDS_FALSE, img)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (!I_REWARDS_MIN.match(img, context).matched) continue
        }
    }
}

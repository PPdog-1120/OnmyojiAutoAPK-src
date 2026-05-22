package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 集体任务 — 捐赠材料完成寮集体任务
 *
 * 流程：进入寮 → 神社 → 集体任务 → 检测任务类型 → 捐赠/提交 → 退出
 */
class CollectiveMissionsTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 神社
    private val I_CM_SHRINE = RuleImage(
        "cm_shrine",
        "CollectiveMissions/cm/cm_cm_shrine.png",
        intArrayOf(875, 628, 55, 60),
        intArrayOf(853, 604, 100, 100),
        0.8f
    )

    // 集体任务
    private val I_CM_CM = RuleImage(
        "cm_cm",
        "CollectiveMissions/cm/cm_cm_cm.png",
        intArrayOf(157, 169, 210, 161),
        intArrayOf(137, 131, 246, 235),
        0.8f
    )

    // 提交
    private val I_CM_PRESENT = RuleImage(
        "cm_present",
        "CollectiveMissions/cm/cm_cm_present.png",
        intArrayOf(559, 594, 160, 66),
        intArrayOf(559, 594, 160, 66),
        0.8f
    )

    // 判断是否到达
    private val I_CM_RECORDS = RuleImage(
        "cm_records",
        "CollectiveMissions/cm/cm_cm_records.png",
        intArrayOf(1032, 617, 54, 63),
        intArrayOf(960, 562, 193, 138),
        0.8f
    )

    // 将材料拉满的按钮
    private val I_CM_MATTER = RuleImage(
        "cm_matter",
        "CollectiveMissions/cm/cm_cm_matter.png",
        intArrayOf(860, 396, 36, 37),
        intArrayOf(843, 119, 66, 459),
        0.8f
    )

    // 添加按钮
    private val I_CM_ADD_1 = RuleImage(
        "cm_add_1",
        "CollectiveMissions/cm/cm_cm_add_1.png",
        intArrayOf(904, 142, 48, 48),
        intArrayOf(904, 142, 48, 48),
        0.8f
    )

    private val I_CM_ADD_2 = RuleImage(
        "cm_add_2",
        "CollectiveMissions/cm/cm_cm_add_2.png",
        intArrayOf(903, 262, 52, 53),
        intArrayOf(903, 262, 52, 53),
        0.8f
    )

    private val I_CM_ADD_3 = RuleImage(
        "cm_add_3",
        "CollectiveMissions/cm/cm_cm_add_3.png",
        intArrayOf(904, 389, 48, 45),
        intArrayOf(904, 389, 48, 45),
        0.8f
    )

    private val I_CM_ADD_4 = RuleImage(
        "cm_add_4",
        "CollectiveMissions/cm/cm_cm_add_4.png",
        intArrayOf(904, 510, 47, 47),
        intArrayOf(904, 510, 47, 47),
        0.8f
    )

    // 领取奖励
    private val I_CM_REWARDS = RuleImage(
        "cm_rewards",
        "CollectiveMissions/cm/cm_cm_rewards.png",
        intArrayOf(567, 463, 145, 60),
        intArrayOf(200, 458, 914, 88),
        0.8f
    )

    // 切换任务
    private val I_CM_SWITCH = RuleImage(
        "cm_switch",
        "CollectiveMissions/cm/cm_cm_switch.png",
        intArrayOf(414, 478, 43, 58),
        intArrayOf(310, 453, 282, 114),
        0.8f
    )

    // 堆叠
    private val I_FEED_HEAP = RuleImage(
        "feed_heap",
        "CollectiveMissions/feed/feed_feed_heap.png",
        intArrayOf(20, 543, 42, 59),
        intArrayOf(20, 543, 42, 59),
        0.8f
    )

    // 喂N卡提交
    private val I_FEED_SUBMIT = RuleImage(
        "feed_submit",
        "CollectiveMissions/feed/feed_feed_submit.png",
        intArrayOf(703, 340, 114, 54),
        intArrayOf(703, 340, 114, 54),
        0.8f
    )

    // 御魂提交
    private val I_SL_SUBMIT = RuleImage(
        "sl_submit",
        "CollectiveMissions/soul/soul_sl_submit.png",
        intArrayOf(835, 588, 145, 61),
        intArrayOf(835, 588, 145, 61),
        0.8f
    )

    override suspend fun run() {
        log("=== 集体任务开始 ===")

        // 进入寮 → 神社 → 集体任务
        while (true) {
            val img = screenshot() ?: continue
            if (I_CM_RECORDS.match(img, context).matched) break
            if (appearThenClick(I_CM_SHRINE, img)) continue
            if (appearThenClick(I_CM_CM, img)) continue
        }
        log("进入集体任务页面")

        // 检查是否已完成
        delay(500)

        // 切换到目标任务
        val missionsSelect = config.collectiveMissionsSelect
        selectMission(missionsSelect)

        // 检测最佳任务并执行
        val bestIndex = detectBest()
        log("最佳任务位置: $bestIndex")

        // 根据任务类型执行
        when (config.collectiveMissionsType) {
            "donate" -> donate(bestIndex)
            "feed" -> feed(bestIndex)
            else -> donate(bestIndex) // 默认捐材料
        }

        // 退出
        while (true) {
            val img = screenshot() ?: continue
            if (I_CM_SHRINE.match(img, context).matched) break
            // 点击返回
            device.click(60, 40)
            delay(1000)
        }

        log("=== 集体任务完成 ===")
    }

    /**
     * 切换到目标任务
     */
    private suspend fun selectMission(target: String) {
        log("切换到目标任务: $target")
        var attempts = 0
        while (attempts < 10) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_CM_SWITCH, img)) {
                delay(1000)
                attempts++
                continue
            }
            break
        }
    }

    /**
     * 检测最佳任务位置
     */
    private fun detectBest(): Int {
        // 简化版：默认选择第一个可用位置
        return 0
    }

    /**
     * 捐赠材料
     */
    private suspend fun donate(index: Int) {
        log("捐赠材料，位置: $index")
        // 点击对应位置
        val clickX = when (index) {
            0 -> 231 + 141 / 2
            1 -> 567 + 143 / 2
            else -> 903 + 136 / 2
        }
        val clickY = 468 + 55 / 2
        device.click(clickX, clickY)
        delay(1500)

        // 等待提交界面
        while (true) {
            val img = screenshot() ?: continue
            if (I_CM_PRESENT.match(img, context).matched) break
            delay(500)
        }

        log("开始捐赠")
        // 点击添加按钮直到材料拉满
        val addButtons = listOf(I_CM_ADD_1, I_CM_ADD_2, I_CM_ADD_3, I_CM_ADD_4)
        var clickCount = 0
        while (clickCount < 30) {
            val img = screenshot() ?: continue
            if (I_CM_MATTER.match(img, context).matched) break
            val btn = addButtons[clickCount % addButtons.size]
            appearThenClick(btn, img)
            clickCount++
        }

        // 领取奖励
        log("领取奖励")
        var rewardCount = 0
        while (rewardCount < 2) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_CM_PRESENT, img)) {
                delay(1000)
                rewardCount++
                continue
            }
            if (I_CM_REWARDS.match(img, context).matched) {
                appearThenClick(I_CM_REWARDS, img)
                delay(500)
                break
            }
        }
        log("捐赠完成")
    }

    /**
     * 喂N卡
     */
    private suspend fun feed(index: Int) {
        log("喂N卡，位置: $index")
        val clickX = when (index) {
            0 -> 231 + 141 / 2
            1 -> 567 + 143 / 2
            else -> 903 + 136 / 2
        }
        val clickY = 468 + 55 / 2
        device.click(clickX, clickY)
        delay(1500)

        // 等待堆叠出现
        while (true) {
            val img = screenshot() ?: continue
            if (I_FEED_HEAP.match(img, context).matched) break
            delay(500)
        }

        log("提交N卡")
        // 随机点击卡牌位置
        val positions = listOf(
            Pair(160 + 50, 531 + 50),
            Pair(293 + 50, 533 + 50),
            Pair(430 + 50, 530 + 50),
            Pair(576 + 50, 529 + 50)
        )
        val selected = positions.shuffled().take(2)
        while (true) {
            val img = screenshot() ?: continue
            if (I_FEED_SUBMIT.match(img, context).matched) break
            for (pos in selected) {
                device.click(pos.first, pos.second)
                delay(200)
            }
        }

        // 领取奖励
        log("领取奖励")
        var rewardCount = 0
        while (rewardCount < 2) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_FEED_SUBMIT, img)) {
                delay(1000)
                rewardCount++
                continue
            }
            if (I_CM_REWARDS.match(img, context).matched) {
                appearThenClick(I_CM_REWARDS, img)
                delay(500)
                break
            }
        }
        log("喂N卡完成")
    }
}

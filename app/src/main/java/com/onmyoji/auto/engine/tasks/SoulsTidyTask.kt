package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 御魂整理任务 — 贪吃鬼喂食 + 奉纳
 *
 * 流程：进入式神录 → 进入御魂界面 → 贪吃鬼喂食 → 奉纳 → 退出
 */
class SoulsTidyTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 点击御魂
    private val I_ST_SOULS = RuleImage(
        "st_souls",
        "SoulsTidy/simple/simple_st_souls.png",
        intArrayOf(1170, 226, 70, 80),
        intArrayOf(1170, 226, 70, 80),
        0.8f
    )

    // 更换
    private val I_ST_REPLACE = RuleImage(
        "st_replace",
        "SoulsTidy/simple/simple_st_replace.png",
        intArrayOf(856, 170, 100, 100),
        intArrayOf(856, 170, 100, 100),
        0.8f
    )

    // 奉纳
    private val I_ST_BONGNA = RuleImage(
        "st_bongna",
        "SoulsTidy/simple/simple_st_bongna.png",
        intArrayOf(1154, 202, 77, 97),
        intArrayOf(1154, 202, 77, 97),
        0.8f
    )

    // 贪吃鬼
    private val I_ST_GREED = RuleImage(
        "st_greed",
        "SoulsTidy/simple/simple_st_greed.png",
        intArrayOf(1157, 611, 65, 60),
        intArrayOf(1157, 611, 65, 60),
        0.8f
    )

    // 整理
    private val I_ST_TIDY = RuleImage(
        "st_tidy",
        "SoulsTidy/simple/simple_st_tidy.png",
        intArrayOf(1155, 328, 74, 100),
        intArrayOf(1155, 328, 74, 100),
        0.8f
    )

    // 进食习惯
    private val I_ST_GREED_HABIT = RuleImage(
        "st_greed_habit",
        "SoulsTidy/simple/simple_st_greed_habit.png",
        intArrayOf(817, 529, 165, 61),
        intArrayOf(817, 529, 165, 61),
        0.8f
    )

    // 立即进食
    private val I_ST_FEED_NOW = RuleImage(
        "st_feed_now",
        "SoulsTidy/simple/simple_st_feed_now.png",
        intArrayOf(929, 593, 73, 65),
        intArrayOf(929, 593, 73, 65),
        0.8f
    )

    // 未选中
    private val I_ST_UNSELECTED = RuleImage(
        "st_unselected",
        "SoulsTidy/simple/simple_st_unselected.png",
        intArrayOf(542, 342, 38, 37),
        intArrayOf(542, 342, 38, 37),
        0.8f
    )

    // 关闭贪吃鬼
    private val I_ST_GREED_CLOSE = RuleImage(
        "st_greed_close",
        "SoulsTidy/simple/simple_st_greed_close.png",
        intArrayOf(933, 211, 35, 37),
        intArrayOf(933, 211, 35, 37),
        0.8f
    )

    // 招财猫
    private val I_ST_CAT = RuleImage(
        "st_cat",
        "SoulsTidy/simple/simple_st_cat.png",
        intArrayOf(799, 269, 147, 133),
        intArrayOf(799, 269, 147, 133),
        0.7f
    )

    // 奉纳按钮
    private val I_ST_DONATE = RuleImage(
        "st_donate",
        "SoulsTidy/simple/simple_st_donate.png",
        intArrayOf(813, 628, 126, 68),
        intArrayOf(813, 628, 126, 68),
        0.8f
    )

    // 神赐
    private val I_ST_GOD_PRESENT = RuleImage(
        "st_god_present",
        "SoulsTidy/simple/simple_st_god_present.png",
        intArrayOf(578, 234, 131, 73),
        intArrayOf(542, 117, 204, 219),
        0.8f
    )

    // 已弃置被选中
    private val I_ST_ABANDONED_SELECTED = RuleImage(
        "st_abandoned_selected",
        "SoulsTidy/simple/simple_st_abandoned_selected.png",
        intArrayOf(32, 112, 107, 52),
        intArrayOf(15, 89, 139, 87),
        0.8f
    )

    // 第一个御魂是+0的
    private val I_ST_LEVEL_0 = RuleImage(
        "st_level_0",
        "SoulsTidy/simple/simple_st_level_0.png",
        intArrayOf(85, 235, 45, 30),
        intArrayOf(85, 235, 45, 30),
        0.9f
    )

    // 御魂奉纳后获得的金币
    private val I_ST_GOLD = RuleImage(
        "st_gold",
        "SoulsTidy/simple/simple_st_gold.png",
        intArrayOf(170, 100, 950, 500),
        intArrayOf(170, 100, 950, 500),
        0.9f
    )

    // 御魂溢出
    private val I_ST_SOUL_OVERFLOW = RuleImage(
        "st_soul_overflow",
        "SoulsTidy/simple/simple_st_soul_overflow.png",
        intArrayOf(447, 260, 384, 42),
        intArrayOf(447, 260, 384, 42),
        0.8f
    )

    // 狗粮御魂的堆叠标识
    private val I_ST_SOUL_STACK = RuleImage(
        "st_soul_stack",
        "SoulsTidy/simple/simple_st_soul_stack.png",
        intArrayOf(142, 234, 20, 16),
        intArrayOf(142, 234, 20, 16),
        0.8f
    )

    // 御魂关闭状态
    private val I_ST_SOULS_CLOSE = RuleImage(
        "st_souls_close",
        "SoulsTidy/simple/simple_st_souls_close.png",
        intArrayOf(1180, 227, 56, 83),
        intArrayOf(1166, 215, 84, 107),
        0.8f
    )

    // 确认按钮（通用）
    private val I_UI_CONFIRM = RuleImage(
        "ui_confirm",
        "General/res_ui_confirm.png",
        intArrayOf(672, 403, 173, 59),
        intArrayOf(672, 403, 173, 59),
        0.8f
    )

    override suspend fun run() {
        log("=== 御魂整理任务开始 ===")

        // 进入御魂主界面
        gotoSouls()

        // 贪吃鬼和奉纳
        if (config.soulsTidyEnableGreed || config.soulsTidyEnableManeki) {
            greedManeki()
        }

        log("=== 御魂整理任务完成 ===")
    }

    /**
     * 进入到御魂的主界面
     */
    private suspend fun gotoSouls() {
        log("进入御魂界面")
        while (true) {
            val img = screenshot() ?: continue
            if (I_ST_GREED.match(img, context).matched && I_ST_TIDY.match(img, context).matched) {
                break
            }
            if (appearThenClick(I_ST_REPLACE, img)) continue
            if (appearThenClick(I_ST_SOULS, img)) continue
            if (appearThenClick(I_ST_SOULS_CLOSE, img)) continue
            // 点击御魂详细区域
            device.click(1188 + 87 / 2, 318 + 74 / 2)
            delay(2000)
        }
        log("进入御魂页面")
    }

    /**
     * 贪吃鬼和招财猫
     */
    private suspend fun greedManeki() {
        // === 贪吃鬼 ===
        if (config.soulsTidyEnableGreed) {
            log("开始贪吃鬼喂食")
            // 点击贪吃鬼
            while (true) {
                val img = screenshot() ?: continue
                if (I_ST_GREED_HABIT.match(img, context).matched) break
                appearThenClick(I_ST_GREED, img)
            }
            // 点击进食习惯
            while (true) {
                val img = screenshot() ?: continue
                if (I_ST_FEED_NOW.match(img, context).matched) break
                appearThenClick(I_ST_GREED_HABIT, img)
            }
            log("点击立即进食")
            var feedCount = 0
            while (true) {
                val img = screenshot() ?: continue
                if (I_ST_UNSELECTED.match(img, context).matched) {
                    // 取消选中
                    while (true) {
                        val frame = screenshot() ?: break
                        if (!I_ST_UNSELECTED.match(frame, context).matched) break
                        appearThenClick(I_ST_UNSELECTED, frame)
                    }
                    continue
                }
                if (appearThenClick(I_UI_CONFIRM, img)) continue
                if (feedCount >= 3) break
                if (appearThenClick(I_ST_FEED_NOW, img, 3500)) {
                    feedCount++
                    continue
                }
            }
            log("贪吃鬼喂食完成")
        }

        // === 关闭贪吃鬼，进入奉纳 ===
        while (true) {
            val img = screenshot() ?: continue
            if (I_ST_CAT.match(img, context).matched) break

            if (I_ST_UNSELECTED.match(img, context).matched) {
                while (true) {
                    val frame = screenshot() ?: break
                    if (!I_ST_UNSELECTED.match(frame, context).matched) break
                    appearThenClick(I_ST_UNSELECTED, frame)
                }
                continue
            }
            if (appearThenClick(I_UI_CONFIRM, img)) continue
            if (appearThenClick(I_ST_GREED_CLOSE, img)) continue
            appearThenClick(I_ST_BONGNA, img)
        }

        if (config.soulsTidyEnableManeki) {
            log("开始奉纳")
            // 确保已弃置界面
            while (true) {
                val img = screenshot() ?: continue
                if (I_ST_ABANDONED_SELECTED.match(img, context).matched) break
                device.click(32 + 107 / 2, 112 + 52 / 2)
                delay(1500)
            }

            // 开始奉纳循环
            while (true) {
                // 检查是否堆叠或+0
                val hasStack = waitUntilAppear(I_ST_SOUL_STACK, 2000)
                if (!hasStack) {
                    val hasLevel0 = waitUntilAppear(I_ST_LEVEL_0, 2000)
                    if (!hasLevel0) {
                        log("没有可奉纳的御魂")
                        break
                    }
                    // 检查是否真的是+0
                    val img = screenshot()
                    if (img != null && !I_ST_LEVEL_0.match(img, context).matched) {
                        log("第一个御魂不是+0，奉纳完成")
                        break
                    }
                }

                // 长按选择
                device.longClick(88 + 100 / 2, 272 + 78 / 2, 1500)
                delay(500)

                // 点击奉纳
                if (!I_ST_DONATE.match(screenshot() ?: continue, context).matched) {
                    log("奉纳按钮未出现")
                    continue
                }

                // 奉纳并收取奖励
                while (true) {
                    val frame = screenshot() ?: break
                    if (appearThenClick(I_UI_CONFIRM, frame)) continue
                    // 神赐出现
                    if (I_ST_GOD_PRESENT.match(frame, context).matched) {
                        device.click(717 + 100 / 2, 165 + 48 / 2)
                        delay(2000)
                        continue
                    }
                    if (appearThenClick(I_ST_DONATE, frame, 5500)) {
                        waitUntilAppear(I_ST_GOLD, 5000)
                        continue
                    }
                    if (!I_ST_GOLD.match(frame, context).matched) break
                }
                log("奉纳一次完成")
            }
        }

        log("奉纳完成")
    }
}

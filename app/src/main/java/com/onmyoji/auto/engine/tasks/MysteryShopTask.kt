package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 神秘商店任务 — 购买神秘商店商品
 *
 * 流程：进入商店 → 进入神秘商店 → 浏览好友商店购买 → 领取奖励 → 退出
 */
class MysteryShopTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 进入
    private val I_ME_ENTER = RuleImage(
        "me_enter",
        "MysteryShop/ms/ms_me_enter.png",
        intArrayOf(52, 494, 59, 44),
        intArrayOf(52, 494, 59, 44),
        0.8f
    )

    // 分享
    private val I_MS_SHARE = RuleImage(
        "ms_share",
        "MysteryShop/ms/ms_ms_share.png",
        intArrayOf(24, 571, 100, 100),
        intArrayOf(24, 571, 100, 100),
        0.8f
    )

    // 下一个
    private val I_MS_NEXT = RuleImage(
        "ms_next",
        "MysteryShop/ms/ms_ms_next.png",
        intArrayOf(1226, 315, 36, 53),
        intArrayOf(1226, 315, 36, 53),
        0.8f
    )

    // 分享确认
    private val I_INVITE_ENSURE = RuleImage(
        "invite_ensure",
        "MysteryShop/ms/ms_invite_ensure.png",
        intArrayOf(711, 545, 127, 60),
        intArrayOf(711, 545, 127, 60),
        0.8f
    )

    // 蓝票
    private val I_MS_BLUE = RuleImage(
        "ms_blue",
        "MysteryShop/ms/ms_ms_blue.png",
        intArrayOf(850, 379, 117, 98),
        intArrayOf(179, 81, 829, 471),
        0.7f
    )

    // 黑蛋
    private val I_MS_BLACK = RuleImage(
        "ms_black",
        "MysteryShop/ms/ms_ms_black.png",
        intArrayOf(851, 353, 114, 102),
        intArrayOf(170, 64, 850, 448),
        0.6f
    )

    // 太古3
    private val I_MS_TAIKO_3 = RuleImage(
        "ms_taiko_3",
        "MysteryShop/ms/ms_ms_taiko_3.png",
        intArrayOf(176, 353, 140, 104),
        intArrayOf(144, 50, 866, 494),
        0.8f
    )

    // 太古4
    private val I_MS_TAIKO_4 = RuleImage(
        "ms_taiko_4",
        "MysteryShop/ms/ms_ms_taiko_4.png",
        intArrayOf(204, 372, 90, 88),
        intArrayOf(156, 81, 874, 473),
        0.8f
    )

    // 检查蓝票
    private val I_MS_CHECK_BLUE = RuleImage(
        "ms_check_blue",
        "MysteryShop/ms/ms_ms_check_blue.png",
        intArrayOf(592, 241, 88, 94),
        intArrayOf(455, 235, 261, 146),
        0.8f
    )

    // 检查黑蛋
    private val I_MS_CHECK_BLACK = RuleImage(
        "ms_check_black",
        "MysteryShop/ms/ms_ms_check_black.png",
        intArrayOf(598, 258, 86, 92),
        intArrayOf(455, 238, 273, 123),
        0.8f
    )

    // 检查太古3
    private val I_MS_CHECK_TAIKO_3 = RuleImage(
        "ms_check_taiko_3",
        "MysteryShop/ms/ms_ms_check_taiko_3.png",
        intArrayOf(567, 259, 70, 81),
        intArrayOf(465, 245, 252, 124),
        0.8f
    )

    // 检查太古4
    private val I_MS_CHECK_TAIKO_4 = RuleImage(
        "ms_check_taiko_4",
        "MysteryShop/ms/ms_ms_check_taiko_4.png",
        intArrayOf(591, 277, 66, 73),
        intArrayOf(453, 235, 280, 146),
        0.8f
    )

    // 奖励
    private val I_MS_REWARD_3 = RuleImage(
        "ms_reward_3",
        "MysteryShop/ms/ms_ms_reward_3.png",
        intArrayOf(511, 630, 48, 47),
        intArrayOf(511, 630, 48, 47),
        0.7f
    )

    private val I_MS_REWARD_5 = RuleImage(
        "ms_reward_5",
        "MysteryShop/ms/ms_ms_reward_5.png",
        intArrayOf(682, 632, 48, 42),
        intArrayOf(682, 632, 48, 42),
        0.7f
    )

    private val I_MS_REWARD_10 = RuleImage(
        "ms_reward_10",
        "MysteryShop/ms/ms_ms_reward_10.png",
        intArrayOf(851, 637, 47, 32),
        intArrayOf(851, 637, 47, 32),
        0.7f
    )

    override suspend fun run() {
        log("=== 神秘商店任务开始 ===")

        // 检查今天是否是神秘商店日（周三/周六）
        val dayOfWeek = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        // Calendar: 1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri, 7=Sat
        if (dayOfWeek != 4 && dayOfWeek != 7) { // 周三和周六
            log("今天不是神秘商店日")
            return
        }

        // 进入神秘商店
        while (true) {
            val img = screenshot() ?: continue
            if (I_MS_SHARE.match(img, context).matched) break
            if (appearThenClick(I_ME_ENTER, img)) continue
        }
        log("进入神秘商店")

        // 购买
        runShop()

        // 浏览好友商店
        while (true) {
            if (!nextOne()) break
            runShop()
        }

        // 领取奖励
        shopReward()

        // 返回
        backToMall()

        log("=== 神秘商店任务完成 ===")
    }

    /**
     * 在当前商店购买
     */
    private suspend fun runShop() {
        if (config.mysteryShopMysteryAmulet) {
            while (buyOne(I_MS_BLUE, I_MS_CHECK_BLUE)) { }
        }
        if (config.mysteryShopBlackDarumaScrap) {
            while (buyOne(I_MS_BLACK, I_MS_CHECK_BLACK)) { }
        }
        if (config.mysteryShopTaiko3) {
            while (buyOne(I_MS_TAIKO_3, I_MS_CHECK_TAIKO_3)) { }
        }
        if (config.mysteryShopTaiko4) {
            while (buyOne(I_MS_TAIKO_4, I_MS_CHECK_TAIKO_4)) { }
        }
    }

    /**
     * 购买一个商品
     */
    private suspend fun buyOne(buyButton: RuleImage, buyCheck: RuleImage): Boolean {
        val img = screenshot() ?: return false
        if (!appearThenClick(buyButton, img)) return false
        delay(500)
        // 确认购买
        val frame = screenshot() ?: return false
        if (buyCheck.match(frame, context).matched) {
            // 点击确认购买
            device.click(640, 400)
            delay(1000)
            return true
        }
        return false
    }

    /**
     * 切换到下一个好友的商店
     */
    private suspend fun nextOne(): Boolean {
        val img = screenshot() ?: return false
        if (!I_MS_NEXT.match(img, context).matched) {
            delay(500)
            val frame = screenshot() ?: return false
            if (!I_MS_NEXT.match(frame, context).matched) {
                log("没有下一个好友")
                return false
            }
        }
        appearThenClick(I_MS_NEXT)
        delay(2500)
        log("切换到下一个好友")
        return true
    }

    /**
     * 领取商店奖励
     */
    private suspend fun shopReward() {
        log("领取商店奖励")
        // 检查购买记录
        val img = screenshot() ?: return
        // 简化版：直接检查奖励是否可领取
        if (I_MS_REWARD_3.match(img, context).matched) {
            appearThenClick(I_MS_REWARD_3)
            delay(500)
        }
        if (I_MS_REWARD_5.match(img, context).matched) {
            appearThenClick(I_MS_REWARD_5)
            delay(500)
        }
        if (I_MS_REWARD_10.match(img, context).matched) {
            appearThenClick(I_MS_REWARD_10)
            delay(500)
        }
    }

    /**
     * 返回商城
     */
    private suspend fun backToMall() {
        var attempts = 0
        while (attempts < 10) {
            device.click(60, 40)
            delay(1000)
            attempts++
        }
    }
}

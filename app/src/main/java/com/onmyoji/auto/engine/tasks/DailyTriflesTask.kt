package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 每日杂务任务 — 友情点、吉闻、商店签到、购买体力
 *
 * 流程：依次执行各子任务
 */
class DailyTriflesTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 好友页面
    private val I_L_FRIENDS = RuleImage(
        "l_friends",
        "DailyTrifles/love/love_l_friends.png",
        intArrayOf(67, 625, 70, 72),
        intArrayOf(67, 625, 70, 72),
        0.9f
    )

    private val I_L_LOVE = RuleImage(
        "l_love",
        "DailyTrifles/love/love_l_love.png",
        intArrayOf(123, 625, 67, 72),
        intArrayOf(123, 625, 67, 72),
        0.9f
    )

    // 一键收取
    private val I_L_COLLECT = RuleImage(
        "l_collect",
        "DailyTrifles/love/love_l_collect.png",
        intArrayOf(47, 537, 129, 56),
        intArrayOf(47, 537, 129, 56),
        0.8f
    )

    // 一键祝福
    private val I_ONE_CLICK_BLESS = RuleImage(
        "one_click_bless",
        "DailyTrifles/love/Screenshots_one_click_bless.png",
        intArrayOf(1115, 500, 93, 33),
        intArrayOf(1115, 500, 93, 33),
        0.8f
    )

    // 点击祝福
    private val I_CLICK_BLESS = RuleImage(
        "click_bless",
        "DailyTrifles/love/Screenshots_click_bless.png",
        intArrayOf(617, 442, 92, 39),
        intArrayOf(617, 442, 92, 39),
        0.8f
    )

    // 吉闻页
    private val I_LUCK_TITLE = RuleImage(
        "luck_title",
        "DailyTrifles/love/Screenshots_luck_title.png",
        intArrayOf(600, 52, 131, 67),
        intArrayOf(600, 52, 131, 67),
        0.8f
    )

    // 好友羁绊提升弹窗
    private val I_FRIENDSHIP_UP = RuleImage(
        "friendship_up",
        "DailyTrifles/love/friendship_up.png",
        intArrayOf(1147, 80, 27, 28),
        intArrayOf(1147, 80, 27, 28),
        0.8f
    )

    // 商店相关
    private val I_GIFT_RECOMMEND = RuleImage(
        "gift_recommend",
        "DailyTrifles/store/store_gift_recommend.png",
        intArrayOf(1183, 454, 53, 64),
        intArrayOf(1162, 77, 98, 457),
        0.7f
    )

    private val I_GIFT_SIGN = RuleImage(
        "gift_sign",
        "DailyTrifles/store/store_gift_sign.png",
        intArrayOf(186, 191, 115, 83),
        intArrayOf(130, 129, 891, 473),
        0.8f
    )

    private val I_SPECIAL_SUSHI = RuleImage(
        "special_sushi",
        "DailyTrifles/store/store_sushi.png",
        intArrayOf(180, 130, 800, 460),
        intArrayOf(180, 130, 800, 460),
        0.8f
    )

    private val I_STORE_COST_TYPE_JADE = RuleImage(
        "store_cost_type_jade",
        "DailyTrifles/store/store_cost_type_jade.png",
        intArrayOf(600, 490, 50, 60),
        intArrayOf(600, 490, 50, 60),
        0.8f
    )

    // 今忆召唤相关
    private val I_RECALL_TICKET = RuleImage(
        "recall_ticket",
        "DailyTrifles/summonRecall/recall_ticket.png",
        intArrayOf(595, 586, 65, 76),
        intArrayOf(595, 586, 65, 76),
        0.8f
    )

    private val I_RECALL_ONE_TICKET = RuleImage(
        "recall_one_ticket",
        "DailyTrifles/summonRecall/recall_one_ticket.png",
        intArrayOf(459, 604, 76, 76),
        intArrayOf(459, 604, 76, 76),
        0.8f
    )

    private val I_RECALL_SM_CONFIRM = RuleImage(
        "recall_sm_confirm",
        "DailyTrifles/summonRecall/recall_sm_confirm.png",
        intArrayOf(424, 628, 174, 61),
        intArrayOf(424, 628, 174, 61),
        0.8f
    )

    private val I_SM_CONFIRM_2 = RuleImage(
        "sm_confirm_2",
        "DailyTrifles/summonRecall/sm_sm_confirm_2.png",
        intArrayOf(377, 630, 206, 62),
        intArrayOf(377, 630, 206, 62),
        0.8f
    )

    override suspend fun run() {
        log("=== 每日杂务任务开始 ===")

        if (config.dailyTriflesFriendLove) {
            runFriendLove()
        }
        if (config.dailyTriflesLuckMsg) {
            runLuckMsg()
        }
        if (config.dailyTriflesStoreSign || config.dailyTriflesBuySushiCount > 0) {
            runStore()
        }

        log("=== 每日杂务任务完成 ===")
    }

    /**
     * 友情点收取
     */
    private suspend fun runFriendLove() {
        log("友情点收取")
        // 进入好友页面
        while (true) {
            val img = screenshot() ?: continue
            if (I_L_LOVE.match(img, context).matched) break
            if (appearThenClick(I_FRIENDSHIP_UP, img)) continue
            if (appearThenClick(I_L_FRIENDS, img)) continue
        }

        log("开始收取友情点")
        var checkTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - checkTime < 2000) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_L_COLLECT, img)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (I_L_LOVE.match(img, context).matched) break
        }
        log("友情点收取完成")

        // 返回
        backToMain()
    }

    /**
     * 吉闻
     */
    private suspend fun runLuckMsg() {
        log("吉闻")
        // 进入好友页面
        while (true) {
            val img = screenshot() ?: continue
            if (I_LUCK_TITLE.match(img, context).matched) break
            if (appearThenClick(I_FRIENDSHIP_UP, img)) continue
            // 点击吉闻区域
            device.click(15 + 70 / 2, 38 + 70 / 2)
            delay(1000)
        }

        log("开始吉闻祝福")
        var checkTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - checkTime < 2000) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_CLICK_BLESS, img)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (appearThenClick(I_ONE_CLICK_BLESS, img)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (I_LUCK_TITLE.match(img, context).matched) break
        }
        log("吉闻完成")

        // 返回
        backToMain()
    }

    /**
     * 商店
     */
    private suspend fun runStore() {
        log("商店签到/购买体力")
        // 进入商店
        while (true) {
            val img = screenshot() ?: continue
            if (I_GIFT_RECOMMEND.match(img, context).matched) break
            // 点击商店入口
            device.click(1138 + 52 / 2, 658 + 40 / 2)
            delay(1000)
        }

        if (config.dailyTriflesStoreSign) {
            runStoreSign()
        }
        if (config.dailyTriflesBuySushiCount > 0) {
            runBuySushi()
        }

        // 返回
        backToMain()
    }

    /**
     * 商店签到
     */
    private suspend fun runStoreSign() {
        log("商店签到")
        appearThenClick(I_GIFT_RECOMMEND)
        delay(1000)
        val img = screenshot()
        if (img == null || !I_GIFT_SIGN.match(img, context).matched) {
            log("没有签到奖励")
            return
        }
        appearThenClick(I_GIFT_SIGN, interval = 2500)
        log("签到完成")
    }

    /**
     * 购买体力
     */
    private suspend fun runBuySushi() {
        log("购买体力")
        var count = 0
        while (count < config.dailyTriflesBuySushiCount) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_STORE_COST_TYPE_JADE, img, 2000)) {
                count++
                log("购买体力: $count/${config.dailyTriflesBuySushiCount}")
                continue
            }
            if (I_SPECIAL_SUSHI.match(img, context).matched) {
                appearThenClick(I_SPECIAL_SUSHI, img, 2000)
                continue
            }
        }
        log("购买体力完成")
    }

    /**
     * 返回主页
     */
    private suspend fun backToMain() {
        var attempts = 0
        while (attempts < 10) {
            device.click(60, 40)
            delay(1000)
            attempts++
        }
    }
}

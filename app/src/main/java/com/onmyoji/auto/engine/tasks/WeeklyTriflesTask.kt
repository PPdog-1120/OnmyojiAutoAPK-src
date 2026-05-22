package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 每周杂务任务 — 图鉴分享、地鬼分享、秘闻分享、破碎符咒
 *
 * 流程：依次执行各分享子任务
 */
class WeeklyTriflesTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 地鬼分享
    private val I_WT_DAY_BATTLE = RuleImage(
        "wt_day_battle",
        "WeeklyTrifles/area_boss/area_boss_wt_day_battle.png",
        intArrayOf(40, 365, 65, 66),
        intArrayOf(40, 365, 65, 66),
        0.8f
    )

    private val I_WT_SHARE_AB = RuleImage(
        "wt_share_ab",
        "WeeklyTrifles/area_boss/area_boss_wt_share_ab.png",
        intArrayOf(1183, 308, 45, 39),
        intArrayOf(1183, 308, 45, 39),
        0.8f
    )

    private val I_WT_AB_JADE = RuleImage(
        "wt_ab_jade",
        "WeeklyTrifles/area_boss/area_boss_wt_ab_jade.png",
        intArrayOf(977, 552, 44, 47),
        intArrayOf(977, 552, 44, 47),
        0.8f
    )

    private val I_WT_AB_WECHAT = RuleImage(
        "wt_ab_wechat",
        "WeeklyTrifles/area_boss/area_boss_wt_ab_wechat.png",
        intArrayOf(920, 646, 50, 41),
        intArrayOf(760, 620, 240, 85),
        0.8f
    )

    // 破碎符咒
    private val I_BM_ENTER = RuleImage(
        "bm_enter",
        "WeeklyTrifles/broken_amulet/broken_amulet_bm_enter.png",
        intArrayOf(437, 600, 62, 68),
        intArrayOf(437, 600, 62, 68),
        0.8f
    )

    private val I_BM_CONFIRM = RuleImage(
        "bm_confirm",
        "WeeklyTrifles/broken_amulet/broken_amulet_bm_confirm.png",
        intArrayOf(418, 620, 173, 59),
        intArrayOf(418, 620, 173, 59),
        0.8f
    )

    private val I_BM_AGAIN = RuleImage(
        "bm_again",
        "WeeklyTrifles/broken_amulet/broken_amulet_bm_again.png",
        intArrayOf(686, 617, 178, 60),
        intArrayOf(686, 617, 178, 60),
        0.8f
    )

    // 图鉴分享
    private val I_WT_SHIKIAGMI = RuleImage(
        "wt_shikiagmi",
        "WeeklyTrifles/collect/collect_wt_shikiagmi.png",
        intArrayOf(280, 620, 920, 100),
        intArrayOf(280, 620, 920, 100),
        0.8f
    )

    private val I_WT_SCROLL = RuleImage(
        "wt_scroll",
        "WeeklyTrifles/collect/collect_wt_scroll.png",
        intArrayOf(1128, 615, 77, 67),
        intArrayOf(1128, 615, 77, 67),
        0.8f
    )

    private val I_WT_COLLECT = RuleImage(
        "wt_collect",
        "WeeklyTrifles/collect/collect_wt_collect.png",
        intArrayOf(1170, 606, 78, 83),
        intArrayOf(1170, 606, 78, 83),
        0.8f
    )

    private val I_WT_COLLECT_WECHAT = RuleImage(
        "wt_collect_wechat",
        "WeeklyTrifles/collect/collect_wt_collect_wechat.png",
        intArrayOf(280, 600, 440, 100),
        intArrayOf(280, 600, 440, 100),
        0.8f
    )

    private val I_WT_QR_CODE = RuleImage(
        "wt_qr_code",
        "WeeklyTrifles/collect/collect_wt_qr_code.png",
        intArrayOf(473, 161, 336, 96),
        intArrayOf(404, 112, 483, 168),
        0.65f
    )

    // 秘闻分享
    private val I_WT_ENTER_SE = RuleImage(
        "wt_enter_se",
        "WeeklyTrifles/secret/secret_wt_enter_se.png",
        intArrayOf(1145, 598, 100, 100),
        intArrayOf(1145, 598, 100, 100),
        0.8f
    )

    private val I_WT_SE_SHARE = RuleImage(
        "wt_se_share",
        "WeeklyTrifles/secret/secret_wt_se_share.png",
        intArrayOf(911, 570, 46, 43),
        intArrayOf(886, 547, 92, 95),
        0.8f
    )

    private val I_WT_SE_WECHAT = RuleImage(
        "wt_se_wechat",
        "WeeklyTrifles/secret/secret_wt_se_wechat.png",
        intArrayOf(823, 641, 45, 37),
        intArrayOf(700, 601, 220, 89),
        0.8f
    )

    private val I_WT_SE_JADE = RuleImage(
        "wt_se_jade",
        "WeeklyTrifles/secret/secret_wt_se_jade.png",
        intArrayOf(1126, 533, 35, 39),
        intArrayOf(1118, 525, 50, 55),
        0.8f
    )

    override suspend fun run() {
        log("=== 每周杂务任务开始 ===")

        if (config.weeklyTriflesShareCollect) {
            shareCollect()
        }
        if (config.weeklyTriflesShareAreaBoss) {
            shareAreaBoss()
        }
        if (config.weeklyTriflesShareSecret) {
            shareSecret()
        }
        if (config.weeklyTriflesBrokenAmulet > 0) {
            brokenAmulet(config.weeklyTriflesBrokenAmulet)
        }

        log("=== 每周杂务任务完成 ===")
    }

    /**
     * 图鉴分享
     */
    private suspend fun shareCollect() {
        log("图鉴分享")
        // 进入图鉴
        while (true) {
            val img = screenshot() ?: continue
            if (I_WT_COLLECT.match(img, context).matched) break
            if (appearThenClick(I_WT_SHIKIAGMI, img)) continue
            if (appearThenClick(I_WT_SCROLL, img)) continue
        }

        // 点击分享
        while (true) {
            val img = screenshot() ?: continue
            if (I_WT_QR_CODE.match(img, context).matched) break
            if (appearThenClick(I_WT_COLLECT_WECHAT, img)) continue
            if (appearThenClick(I_WT_COLLECT, img, 5000)) continue
        }

        // 等待奖励
        clickShare()

        // 返回
        backToMain()
    }

    /**
     * 地鬼分享
     */
    private suspend fun shareAreaBoss() {
        log("地鬼分享")
        // 进入地鬼页面
        while (true) {
            val img = screenshot() ?: continue
            if (I_WT_AB_WECHAT.match(img, context).matched) break
            if (appearThenClick(I_WT_DAY_BATTLE, img, 2000)) continue
            if (appearThenClick(I_WT_SHARE_AB, img)) continue
            // 点击已挑战的第一个
            device.click(168 + 68 / 2, 362 + 72 / 2)
            delay(1000)
        }

        // 检查是否已领取
        delay(1000)
        val img = screenshot()
        if (img != null && !I_WT_AB_JADE.match(img, context).matched) {
            log("本周已领取")
            return
        }

        // 点击分享
        clickShare()
    }

    /**
     * 秘闻分享
     */
    private suspend fun shareSecret() {
        log("秘闻分享")
        // 进入秘闻
        while (true) {
            val img = screenshot() ?: continue
            if (I_WT_SE_WECHAT.match(img, context).matched) break
            if (appearThenClick(I_WT_ENTER_SE, img)) continue
            if (appearThenClick(I_WT_SE_SHARE, img, 5000)) continue
        }

        // 检查是否已领取
        val img = screenshot()
        if (img != null && !I_WT_SE_JADE.match(img, context).matched) {
            log("本周已领取")
            return
        }

        // 点击分享
        clickShare()

        // 返回
        backToMain()
    }

    /**
     * 破碎符咒
     */
    private suspend fun brokenAmulet(destNum: Int) {
        log("破碎符咒，目标: $destNum")
        // 进入召唤页面
        while (true) {
            val img = screenshot() ?: continue
            if (I_BM_ENTER.match(img, context).matched) break
            delay(500)
        }

        var count = 0
        while (count < destNum) {
            val img = screenshot() ?: continue
            // 点击召唤
            if (appearThenClick(I_BM_ENTER, img) || appearThenClick(I_BM_AGAIN, img)) {
                delay(400)
                // 随机点击直到再次召唤出现
                val timeout = System.currentTimeMillis() + 5000
                while (System.currentTimeMillis() < timeout) {
                    val frame = screenshot() ?: continue
                    if (I_BM_AGAIN.match(frame, context).matched) break
                    // 随机点击屏幕
                    device.click((200..1000).random(), (200..600).random())
                    delay(800)
                }
                count += 10
                log("破碎符咒: $count/$destNum")
            } else {
                break
            }
        }

        // 退出
        while (true) {
            val img = screenshot() ?: break
            if (!I_BM_CONFIRM.match(img, context).matched) break
            appearThenClick(I_BM_CONFIRM, img)
        }
        log("破碎符咒完成")
    }

    /**
     * 点击分享并等待奖励
     */
    private suspend fun clickShare() {
        var getTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - getTime < 7000) {
            val img = screenshot() ?: continue
            if (I_WT_QR_CODE.match(img, context).matched) {
                // 点击二维码区域
                device.click(470 + 337 / 2, 111 + 44 / 2)
                delay(800)
                getTime = System.currentTimeMillis()
                continue
            }
        }
    }

    /**
     * 返回主页
     */
    private suspend fun backToMain() {
        while (true) {
            val img = screenshot() ?: break
            if (I_WT_SHIKIAGMI.match(img, context).matched) break
            // 点击返回
            device.click(60, 40)
            delay(1000)
        }
    }
}

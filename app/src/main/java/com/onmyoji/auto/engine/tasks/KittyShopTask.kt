package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 猫咪商店任务 — 经营猫咪商店
 *
 * 流程：进入经营 → 选择猫咪 → 开始营业 → 收菜 → 退出
 */
class KittyShopTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 进入经营
    private val I_GO1 = RuleImage(
        "go1",
        "KittyShop/ks/ks_go1.png",
        intArrayOf(350, 136, 46, 185),
        intArrayOf(315, 76, 100, 342),
        0.8f
    )

    // 点击经营
    private val I_START_FARMING = RuleImage(
        "start_farming",
        "KittyShop/ks/ks_start_farming.png",
        intArrayOf(1131, 579, 100, 100),
        intArrayOf(995, 484, 279, 234),
        0.8f
    )

    // 开启
    private val I_START_ENSURE = RuleImage(
        "start_ensure",
        "KittyShop/ks/ks_start_ensure.png",
        intArrayOf(1094, 583, 82, 78),
        intArrayOf(954, 522, 322, 196),
        0.8f
    )

    // 没有选择
    private val I_UNSELECTED = RuleImage(
        "unselected",
        "KittyShop/ks/ks_unselected.png",
        intArrayOf(247, 562, 76, 70),
        intArrayOf(143, 524, 941, 127),
        0.8f
    )

    // 里面的标识
    private val I_MAIN_FLAG = RuleImage(
        "main_flag",
        "KittyShop/ks/ks_main_flag.png",
        intArrayOf(13, 102, 61, 50),
        intArrayOf(1, 34, 279, 247),
        0.8f
    )

    // 加号
    private val I_MAIN_ADD = RuleImage(
        "main_add",
        "KittyShop/ks/ks_main_add.png",
        intArrayOf(159, 178, 46, 50),
        intArrayOf(125, 50, 1078, 491),
        0.8f
    )

    // 收菜按钮
    private val I_MAIN_GIFT = RuleImage(
        "main_gift",
        "KittyShop/ks/ks_main_gift.png",
        intArrayOf(155, 185, 55, 46),
        intArrayOf(89, 52, 1184, 506),
        0.8f
    )

    // 营业中
    private val I_MAIN_BUSY = RuleImage(
        "main_busy",
        "KittyShop/ks/ks_main_busy.png",
        intArrayOf(452, 626, 64, 24),
        intArrayOf(237, 549, 832, 170),
        0.8f
    )

    // 结束后的分享
    private val I_MAIN_SHARE = RuleImage(
        "main_share",
        "KittyShop/ks/ks_main_share.png",
        intArrayOf(1156, 595, 68, 77),
        intArrayOf(839, 474, 440, 245),
        0.8f
    )

    // 里面没有选猫咪
    private val I_MAIN_FLAG1 = RuleImage(
        "main_flag1",
        "KittyShop/ks/ks_main_flag1.png",
        intArrayOf(1065, 681, 89, 37),
        intArrayOf(1065, 681, 89, 37),
        0.95f
    )

    // 营业中各位置
    private val I_MAIN_BUSY_1 = RuleImage(
        "main_busy_1",
        "KittyShop/ks/ks_main_busy_1.png",
        intArrayOf(292, 633, 64, 24),
        intArrayOf(240, 571, 154, 143),
        0.8f
    )

    private val I_MAIN_BUSY_2 = RuleImage(
        "main_busy_2",
        "KittyShop/ks/ks_main_busy_2.png",
        intArrayOf(452, 626, 64, 24),
        intArrayOf(394, 569, 169, 148),
        0.8f
    )

    private val I_MAIN_BUSY_3 = RuleImage(
        "main_busy_3",
        "KittyShop/ks/ks_main_busy_3.png",
        intArrayOf(630, 623, 64, 24),
        intArrayOf(563, 567, 171, 141),
        0.8f
    )

    private val I_MAIN_BUSY_4 = RuleImage(
        "main_busy_4",
        "KittyShop/ks/ks_main_busy_4.png",
        intArrayOf(781, 634, 64, 24),
        intArrayOf(731, 572, 170, 142),
        0.8f
    )

    private val I_MAIN_BUSY_5 = RuleImage(
        "main_busy_5",
        "KittyShop/ks/ks_main_busy_5.png",
        intArrayOf(961, 626, 64, 24),
        intArrayOf(900, 571, 165, 136),
        0.8f
    )

    // 结算
    private val I_MAIN_FINISH = RuleImage(
        "main_finish",
        "KittyShop/ks/ks_main_finsh.png",
        intArrayOf(764, 16, 52, 42),
        intArrayOf(740, 1, 100, 100),
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
        log("=== 猫咪商店任务开始 ===")

        // 进入经营页面
        while (true) {
            val img = screenshot() ?: continue
            if (I_START_FARMING.match(img, context).matched) break
            if (appearThenClick(I_GO1, img, 2000)) continue
            // 点击式神区域
            device.click(350 + 46 / 2, 136 + 185 / 2)
            delay(2000)
        }

        val attempts = config.kittyShopAttempts
        log("经营次数: $attempts")

        for (i in 0 until attempts) {
            runOnce()
        }

        // 退出
        while (true) {
            val img = screenshot() ?: break
            if (I_START_FARMING.match(img, context).matched) break
            // 点击返回
            device.click(60, 40)
            delay(1000)
        }

        log("=== 猫咪商店任务完成 ===")
    }

    /**
     * 单次经营
     */
    private suspend fun runOnce() {
        log("开始经营")

        // 选择猫咪
        for (trial in 0 until 3) {
            try {
                selectKitty()
                break
            } catch (e: Exception) {
                log("选择猫咪失败，重试")
                while (true) {
                    val img = screenshot() ?: break
                    if (I_START_FARMING.match(img, context).matched) break
                    device.click(60, 40)
                    delay(1000)
                }
            }
        }

        // 点击开启
        while (true) {
            val img = screenshot() ?: continue
            if (I_MAIN_FLAG.match(img, context).matched) break
            appearThenClick(I_START_ENSURE, img)
        }
        log("开始营业")

        // 营业中
        while (true) {
            val img = screenshot() ?: continue
            if (I_MAIN_SHARE.match(img, context).matched) break

            // 处理弹窗
            if (appearThenClick(I_UI_CONFIRM, img)) continue

            if (!I_MAIN_FLAG.match(img, context).matched) continue
            if (appearThenClick(I_MAIN_GIFT, img)) continue
            if (appearThenClick(I_MAIN_ADD, img)) continue

            // 没有选猫咪
            if (I_MAIN_FLAG1.match(img, context).matched) {
                mainSelectKitty()
                continue
            }
        }

        log("营业结束")

        // 等待回到经营页面
        while (true) {
            val img = screenshot() ?: break
            if (I_START_FARMING.match(img, context).matched) break
            device.click(1065 + 89 / 2, 681 + 37 / 2)
            delay(2000)
        }
    }

    /**
     * 选择猫咪
     */
    private suspend fun selectKitty() {
        // 点击经营进入选择
        while (true) {
            val img = screenshot() ?: continue
            if (I_START_ENSURE.match(img, context).matched) break
            appearThenClick(I_START_FARMING, img)
        }

        val selectPositions = listOf(
            Pair(225 + 242 / 2, 215 + 303 / 2),
            Pair(471 + 250 / 2, 216 + 300 / 2),
            Pair(735 + 252 / 2, 213 + 307 / 2),
            Pair(990 + 181 / 2, 216 + 303 / 2)
        )

        var indexCnt = 0
        var lastTime = System.currentTimeMillis()

        while (true) {
            val img = screenshot() ?: continue
            if (!I_UNSELECTED.match(img, context).matched) break

            if (System.currentTimeMillis() - lastTime > 1200) {
                indexCnt++
                lastTime = System.currentTimeMillis()
            }

            when (indexCnt) {
                in 0..2 -> {
                    val pos = selectPositions[indexCnt]
                    device.click(pos.first, pos.second)
                    delay(1200)
                }
                in 3..6 -> {
                    val pos = selectPositions[3]
                    device.click(pos.first, pos.second)
                    delay(1200)
                }
                7 -> {
                    // 滑动
                    device.swipe(589, 239, 459, 234 + 270)
                    delay(1500)
                }
                else -> {
                    throw Exception("选择猫咪失败")
                }
            }
        }
        log("选择猫咪完成")
    }

    /**
     * 营业中选择猫咪
     */
    private suspend fun mainSelectKitty() {
        val busyRules = listOf(I_MAIN_BUSY_1, I_MAIN_BUSY_2, I_MAIN_BUSY_3, I_MAIN_BUSY_4, I_MAIN_BUSY_5)
        for (busy in busyRules) {
            val img = screenshot() ?: continue
            if (busy.match(img, context).matched) continue
            // 点击该位置
            val pos = busy.roiFront
            device.click(pos[0] + pos[2] / 2, pos[1] + pos[3] / 2)
            delay(1300)
            break
        }
    }
}

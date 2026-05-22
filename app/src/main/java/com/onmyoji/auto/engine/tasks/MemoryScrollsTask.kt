package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 记忆绘卷任务 — 捐献绘卷碎片
 *
 * 流程：进入召唤页面 → 进入绘卷 → 选择分卷 → 捐献碎片 → 退出
 */
class MemoryScrollsTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 进入绘卷主界面
    private val I_MS_ENTER = RuleImage(
        "ms_enter",
        "MemoryScrolls/ms/ms_ms_enter.png",
        intArrayOf(1169, 155, 24, 55),
        intArrayOf(1169, 155, 24, 55),
        0.5f
    )

    // 判断是否处于绘卷主界面
    private val I_MS_MAIN = RuleImage(
        "ms_main",
        "MemoryScrolls/ms/ms_ms_main.png",
        intArrayOf(689, 3, 40, 50),
        intArrayOf(689, 3, 40, 50),
        0.8f
    )

    // 退出到召唤界面
    private val I_MS_BACK = RuleImage(
        "ms_back",
        "MemoryScrolls/ms/ms_ms_back.png",
        intArrayOf(32, 31, 43, 45),
        intArrayOf(32, 31, 43, 45),
        0.8f
    )

    // 关闭分卷捐献界面
    private val I_MS_CLOSE = RuleImage(
        "ms_close",
        "MemoryScrolls/ms/ms_ms_close.png",
        intArrayOf(1145, 48, 39, 41),
        intArrayOf(1145, 48, 39, 41),
        0.8f
    )

    // 贡献碎片
    private val I_MS_CONTRIBUTE = RuleImage(
        "ms_contribute",
        "MemoryScrolls/ms/ms_ms_contribute.png",
        intArrayOf(1013, 583, 125, 35),
        intArrayOf(1013, 583, 125, 35),
        0.8f
    )

    // 贡献碎片完成
    private val I_MS_CONTRIBUTED = RuleImage(
        "ms_contributed",
        "MemoryScrolls/ms/ms_ms_contributed.png",
        intArrayOf(493, 232, 63, 40),
        intArrayOf(493, 232, 63, 40),
        0.8f
    )

    // 绘卷100%
    private val I_MS_COMPLETE = RuleImage(
        "ms_complete",
        "MemoryScrolls/ms/ms_ms_complete.png",
        intArrayOf(397, 593, 100, 21),
        intArrayOf(397, 593, 100, 21),
        0.8f
    )

    // 小绘卷不足
    private val I_MS_ZERO_S = RuleImage(
        "ms_zero_s",
        "MemoryScrolls/ms/ms_ms_zero_s.png",
        intArrayOf(968, 160, 51, 27),
        intArrayOf(968, 160, 51, 27),
        0.8f
    )

    // 中绘卷不足
    private val I_MS_ZERO_M = RuleImage(
        "ms_zero_m",
        "MemoryScrolls/ms/ms_ms_zero_m.png",
        intArrayOf(967, 312, 54, 26),
        intArrayOf(967, 312, 54, 26),
        0.8f
    )

    // 大绘卷不足
    private val I_MS_ZERO_L = RuleImage(
        "ms_zero_l",
        "MemoryScrolls/ms/ms_ms_zero_l.png",
        intArrayOf(968, 464, 51, 27),
        intArrayOf(968, 464, 51, 27),
        0.8f
    )

    // 绘卷到达95%进度
    private val I_MS_COMPLETE_95 = RuleImage(
        "ms_complete_95",
        "MemoryScrolls/ms/ms_ms_complete_100.png",
        intArrayOf(655, 594, 4, 18),
        intArrayOf(655, 594, 4, 18),
        0.8f
    )

    // 小碎片
    private val I_MS_FRAGMENT_S = RuleImage(
        "ms_fragment_s",
        "MemoryScrolls/ms/ms_ms_fragment_s.png",
        intArrayOf(290, 677, 40, 35),
        intArrayOf(290, 677, 40, 35),
        0.8f
    )

    // 双绘卷进入按钮
    private val I_MS_DOUBLE_SCROLLS_ENTER = RuleImage(
        "ms_double_scrolls_enter",
        "MemoryScrolls/ms/ms_ms_double_scrolls_enter.png",
        intArrayOf(565, 586, 154, 40),
        intArrayOf(565, 586, 154, 40),
        0.8f
    )

    override suspend fun run() {
        log("=== 记忆绘卷任务开始 ===")

        // 进入绘卷主界面
        if (!gotoMemoryScrollsMain()) {
            log("进入绘卷主界面失败")
            return
        }

        // 进入指定分卷
        gotoScroll(config.memoryScrollsNumber)

        // 返回
        while (true) {
            val img = screenshot() ?: break
            if (!I_MS_BACK.match(img, context).matched) break
            appearThenClick(I_MS_BACK, img)
        }
        log("返回召唤界面")

        log("=== 记忆绘卷任务完成 ===")
    }

    /**
     * 进入绘卷主界面
     */
    private suspend fun gotoMemoryScrollsMain(): Boolean {
        // 等待绘卷入口出现
        if (!waitUntilAppear(I_MS_ENTER, 30_000)) {
            log("未找到绘卷入口")
            return false
        }

        while (true) {
            val img = screenshot() ?: continue
            if (I_MS_FRAGMENT_S.match(img, context).matched) {
                log("进入绘卷主页面")
                break
            }
            // 双绘卷
            if (I_MS_DOUBLE_SCROLLS_ENTER.match(img, context).matched) {
                log("使用双绘卷")
                appearThenClick(I_MS_DOUBLE_SCROLLS_ENTER, img)
                continue
            }
            // 右上角绘卷铃铛
            appearThenClick(I_MS_ENTER, img)
        }
        return true
    }

    /**
     * 进入指定分卷
     */
    private suspend fun gotoScroll(scrollNumber: Int) {
        log("进入卷$scrollNumber")

        // 计算点击位置
        val scrollPositions = mapOf(
            1 to Pair(170 + 279 / 2, 157 + 192 / 2),
            2 to Pair(490 + 297 / 2, 135 + 210 / 2),
            3 to Pair(836 + 304 / 2, 135 + 210 / 2),
            4 to Pair(137 + 305 / 2, 389 + 206 / 2),
            5 to Pair(491 + 303 / 2, 390 + 207 / 2),
            6 to Pair(835 + 303 / 2, 389 + 204 / 2)
        )
        val pos = scrollPositions[scrollNumber] ?: scrollPositions[1]!!

        while (true) {
            val img = screenshot() ?: continue
            if (I_MS_CLOSE.match(img, context).matched) {
                log("进入分卷捐献页面")
                break
            }
            device.click(pos.first, pos.second)
            delay(1000)
        }

        // 检查是否需要捐献
        val img = screenshot() ?: return
        if (I_MS_CONTRIBUTE.match(img, context).matched || !I_MS_COMPLETE.match(img, context).matched) {
            log("捐献绘卷碎片")
            if (config.memoryScrollsAutoContribute) {
                contributeMemoryScrolls()
            }
        } else {
            log("绘卷已完成")
        }

        // 关闭
        while (true) {
            val frame = screenshot() ?: break
            if (!I_MS_CLOSE.match(frame, context).matched) break
            appearThenClick(I_MS_CLOSE, frame)
        }
    }

    /**
     * 捐献碎片
     */
    private suspend fun contributeMemoryScrolls() {
        while (true) {
            val img = screenshot() ?: continue
            // 检查是否所有碎片都不足
            if (I_MS_ZERO_S.match(img, context).matched &&
                I_MS_ZERO_M.match(img, context).matched &&
                I_MS_ZERO_L.match(img, context).matched
            ) {
                log("所有碎片不足")
                return
            }

            // 滑动拉满碎片
            device.swipe(838, 199 + 27 / 2, 1176, 171 + 81 / 2)
            delay(500)
            device.swipe(838, 352 + 23 / 2, 1176, 326 + 84 / 2)
            delay(500)
            device.swipe(839, 503 + 25 / 2, 1175, 473 + 85 / 2)
            delay(500)

            // 点击贡献
            if (appearThenClick(I_MS_CONTRIBUTE, img, 3000)) {
                log("贡献一次")
                // 等待贡献动画
                while (true) {
                    val frame = screenshot() ?: break
                    if (I_MS_CONTRIBUTED.match(frame, context).matched) {
                        device.click(108 + 993 / 2, 509 + 190 / 2)
                        delay(1000)
                    } else {
                        break
                    }
                }
            }
        }
    }
}

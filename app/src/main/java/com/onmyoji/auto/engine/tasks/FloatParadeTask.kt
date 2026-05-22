package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 花车巡游任务 — 收取花车巡游奖励
 *
 * 流程：进入花车 → 一键收取 → 收取等级奖励 → 退出
 */
class FloatParadeTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 从庭院进入花车活动
    private val I_FP_ACCESS = RuleImage(
        "fp_access",
        "FloatParade/fp/fp_fp_access.png",
        intArrayOf(1178, 168, 43, 34),
        intArrayOf(1051, 101, 210, 472),
        0.8f
    )

    // 一键收取（任务页面）
    private val I_FP_GETALL0 = RuleImage(
        "fp_getall0",
        "FloatParade/fp/fp_fp_getall0.png",
        intArrayOf(1153, 287, 65, 56),
        intArrayOf(1134, 261, 100, 100),
        0.8f
    )

    // 一键收取（主页面）
    private val I_FP_GETALL1 = RuleImage(
        "fp_getall1",
        "FloatParade/fp/fp_fp_getall1.png",
        intArrayOf(1198, 555, 53, 54),
        intArrayOf(1163, 506, 113, 164),
        0.8f
    )

    // 右下角的任务
    private val I_FP_TASKS = RuleImage(
        "fp_tasks",
        "FloatParade/fp/fp_fp_tasks.png",
        intArrayOf(1175, 617, 79, 76),
        intArrayOf(1162, 605, 100, 100),
        0.8f
    )

    // 升级
    private val I_FP_UPGRADE = RuleImage(
        "fp_upgrade",
        "FloatParade/fp/fp_fp_upgrade.png",
        intArrayOf(1086, 74, 116, 49),
        intArrayOf(1040, 52, 169, 100),
        0.8f
    )

    // 红色关闭
    private val I_FP_RED_CLOSE = RuleImage(
        "fp_red_close",
        "FloatParade/fp/fp_fp_red_close.png",
        intArrayOf(1170, 109, 43, 44),
        intArrayOf(1157, 101, 72, 62),
        0.8f
    )

    // 寻找花车的切换按钮
    private val I_TOGGLE_BUTTON = RuleImage(
        "toggle_button",
        "FloatParade/fp/fp_toggle_button.png",
        intArrayOf(1193, 462, 21, 23),
        intArrayOf(1116, 127, 150, 377),
        0.7f
    )

    // 批量选择
    private val I_BATCH_SELECTION = RuleImage(
        "batch_selection",
        "FloatParade/fp/fp_batch_selection.png",
        intArrayOf(933, 517, 44, 40),
        intArrayOf(933, 517, 44, 40),
        0.8f
    )

    // 批量选择确认
    private val I_BATCH_SELECTION_CONFIRM = RuleImage(
        "batch_selection_confirm",
        "FloatParade/fp/fp_batch_selection_confirm.png",
        intArrayOf(676, 392, 130, 63),
        intArrayOf(676, 392, 130, 63),
        0.8f
    )

    // 礼包关闭
    private val I_FP_GIFT_CLOSE = RuleImage(
        "fp_gift_close",
        "FloatParade/fp/fp_fp_gift_close.png",
        intArrayOf(1056, 96, 36, 38),
        intArrayOf(1046, 89, 60, 60),
        0.8f
    )

    // 礼包类型1
    private val I_FP_GIFT_FLAG1 = RuleImage(
        "fp_gift_flag1",
        "FloatParade/fp/fp_fp_gift_flag1.png",
        intArrayOf(559, 295, 55, 44),
        intArrayOf(559, 295, 55, 44),
        0.8f
    )

    // 礼包类型2
    private val I_FP_GIFT_FLAG2 = RuleImage(
        "fp_gift_flag2",
        "FloatParade/fp/fp_fp_gift_flag2.png",
        intArrayOf(557, 293, 58, 47),
        intArrayOf(557, 293, 58, 47),
        0.8f
    )

    // 花合战等级奖励（复用 TalismanPass 资源）
    private val I_TP_LEVEL_1 = RuleImage(
        "tp_level_1",
        "TalismanPass/tp/tp_tp_level_1.png",
        intArrayOf(203, 435, 122, 59),
        intArrayOf(203, 435, 122, 59),
        0.8f
    )

    private val I_TP_LEVEL_2 = RuleImage(
        "tp_level_2",
        "TalismanPass/tp/tp_tp_level_2.png",
        intArrayOf(577, 435, 122, 55),
        intArrayOf(577, 435, 122, 55),
        0.8f
    )

    private val I_TP_LEVEL_3 = RuleImage(
        "tp_level_3",
        "TalismanPass/tp/tp_tp_level_3.png",
        intArrayOf(967, 433, 109, 61),
        intArrayOf(967, 433, 109, 61),
        0.8f
    )

    // 溢出确认
    private val I_OVERFLOW_CONFIRM = RuleImage(
        "overflow_confirm",
        "TalismanPass/tp/tp_overflow_confirme.png",
        intArrayOf(585, 410, 116, 44),
        intArrayOf(585, 410, 116, 44),
        0.8f
    )

    override suspend fun run() {
        log("=== 花车巡游任务开始 ===")

        // 从庭院进入花车，一键收取
        getAll()

        // 回退到花车主页面
        while (true) {
            val img = screenshot() ?: continue
            if (I_FP_TASKS.match(img, context).matched) break
            // 点击返回
            device.click(60, 40)
            delay(1000)
        }
        log("回到花车主页面")

        // 收取花车等级奖励
        getFlower(config.floatParadeLevelReward1, config.floatParadeLevelReward2)

        log("=== 花车巡游任务完成 ===")
    }

    /**
     * 从庭院进入花车，一键收取
     */
    private suspend fun getAll() {
        // 进入花车页面
        while (true) {
            val img = screenshot() ?: continue
            if (I_FP_UPGRADE.match(img, context).matched) break
            if (appearThenClick(I_FP_ACCESS, img)) continue
            if (appearThenClick(I_FP_RED_CLOSE, img)) continue
            if (appearThenClick(I_FP_GIFT_CLOSE, img, 3000)) continue
            if (appearThenClick(I_FP_TASKS, img)) continue
            if (appearThenClick(I_TOGGLE_BUTTON, img, 3000)) continue
        }
        log("进入花车")

        // 一键收取
        if (!I_FP_GETALL1.match(screenshot()!!, context).matched) {
            log("没有可领取的奖励")
            return
        }
        appearThenClick(I_FP_GETALL1)
        delay(500)
        log("一键收取完成")
    }

    /**
     * 收取花车等级奖励
     */
    private suspend fun getFlower(level1: Int = 3, level2: Int = 1) {
        val img = screenshot() ?: return
        if (!I_FP_GETALL0.match(img, context).matched) {
            log("没有等级奖励")
            return
        }
        log("发现等级奖励")

        val matchLevel = mapOf(
            1 to I_TP_LEVEL_1,
            2 to I_TP_LEVEL_2,
            3 to I_TP_LEVEL_3
        )

        var checkTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - checkTime < 2000) {
            val frame = screenshot() ?: continue

            // 批量选择
            if (appearThenClick(I_BATCH_SELECTION, frame, 1500)) {
                checkTime = System.currentTimeMillis()
                continue
            }
            if (appearThenClick(I_BATCH_SELECTION_CONFIRM, frame)) {
                checkTime = System.currentTimeMillis()
                continue
            }

            // 选择礼包类型1的奖励
            if (I_FP_GIFT_FLAG1.match(frame, context).matched) {
                if (appearThenClick(matchLevel[level1]!!, frame)) {
                    log("选择类型1的 $level1 号奖励")
                    appearThenClick(I_OVERFLOW_CONFIRM, frame)
                    checkTime = System.currentTimeMillis()
                    continue
                }
            }

            // 选择礼包类型2的奖励
            if (I_FP_GIFT_FLAG2.match(frame, context).matched) {
                if (appearThenClick(matchLevel[level2]!!, frame)) {
                    log("选择类型2的 $level2 号奖励")
                    appearThenClick(I_OVERFLOW_CONFIRM, frame)
                    checkTime = System.currentTimeMillis()
                    continue
                }
            }

            if (appearThenClick(I_FP_GETALL0, frame, 2100)) {
                log("领取奖励")
                checkTime = System.currentTimeMillis()
                continue
            }
        }
    }
}

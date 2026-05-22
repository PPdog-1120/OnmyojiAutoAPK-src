package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 花合战任务 — 收取花合战奖励
 *
 * 流程：进入每日任务页面 → 一键收取 → 收取等级奖励 → 退出
 */
class TalismanPassTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 领取全部
    private val I_TP_GET_ALL = RuleImage(
        "tp_get_all",
        "TalismanPass/tp/tp_tp_get_all.png",
        intArrayOf(903, 599, 70, 71),
        intArrayOf(903, 599, 70, 71),
        0.8f
    )

    // 等级奖励红点
    private val I_RED_POINT_LEVEL = RuleImage(
        "red_point_level",
        "TalismanPass/tp/tp_red_point_level.png",
        intArrayOf(1232, 170, 13, 13),
        intArrayOf(1202, 156, 58, 65),
        0.7f
    )

    // 选择一号奖励
    private val I_TP_LEVEL_1 = RuleImage(
        "tp_level_1",
        "TalismanPass/tp/tp_tp_level_1.png",
        intArrayOf(203, 435, 122, 59),
        intArrayOf(203, 435, 122, 59),
        0.8f
    )

    // 选择二号奖励
    private val I_TP_LEVEL_2 = RuleImage(
        "tp_level_2",
        "TalismanPass/tp/tp_tp_level_2.png",
        intArrayOf(577, 435, 122, 55),
        intArrayOf(577, 435, 122, 55),
        0.8f
    )

    // 选择三号奖励
    private val I_TP_LEVEL_3 = RuleImage(
        "tp_level_3",
        "TalismanPass/tp/tp_tp_level_3.png",
        intArrayOf(967, 433, 109, 61),
        intArrayOf(967, 433, 109, 61),
        0.8f
    )

    // 前往
    private val I_TP_GOTO = RuleImage(
        "tp_goto",
        "TalismanPass/tp/tp_tp_goto.png",
        intArrayOf(995, 254, 85, 34),
        intArrayOf(928, 219, 206, 315),
        0.8f
    )

    // 经验
    private val I_TP_EXP = RuleImage(
        "tp_exp",
        "TalismanPass/tp/tp_tp_exp.png",
        intArrayOf(922, 254, 32, 36),
        intArrayOf(884, 215, 100, 331),
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

    // 自选御魂
    private val I_TP_SOUL_1 = RuleImage(
        "tp_soul_1",
        "TalismanPass/tp/tp_tp_soul_1.png",
        intArrayOf(248, 501, 34, 37),
        intArrayOf(165, 389, 929, 168),
        0.7f
    )

    private val I_TP_SOUL_2 = RuleImage(
        "tp_soul_2",
        "TalismanPass/tp/tp_tp_soul_2.png",
        intArrayOf(582, 438, 115, 48),
        intArrayOf(570, 427, 139, 71),
        0.8f
    )

    private val I_TP_SOUL_3 = RuleImage(
        "tp_soul_3",
        "TalismanPass/tp/tp_tp_soul_3.png",
        intArrayOf(313, 489, 188, 33),
        intArrayOf(302, 472, 216, 60),
        0.8f
    )

    override suspend fun run() {
        log("=== 花合战任务开始 ===")

        // 判断是否在任务界面
        val inTask = inTask()

        // 一键收取所有奖励
        if (inTask) {
            getAll()
        }

        // 收取花合战等级奖励
        getFlower(config.talismanPassLevelReward)

        log("=== 花合战任务完成 ===")
    }

    /**
     * 判断是否在任务界面
     */
    private fun inTask(): Boolean {
        val img = screenshot() ?: return false
        return I_TP_GOTO.match(img, context).matched || I_TP_EXP.match(img, context).matched
    }

    /**
     * 一键收取所有奖励
     */
    private suspend fun getAll() {
        val img = screenshot() ?: return
        if (!I_TP_GET_ALL.match(img, context).matched) {
            log("没有可领取的奖励")
            return
        }
        appearThenClick(I_TP_GET_ALL)
        delay(500)
        log("一键收取完成")
    }

    /**
     * 收取花合战等级奖励
     */
    private suspend fun getFlower(levelReward: Int = 2) {
        val img = screenshot() ?: return
        if (!I_RED_POINT_LEVEL.match(img, context).matched) {
            log("没有等级奖励")
            return
        }
        log("发现等级奖励")
        // 点击等级奖励红点
        while (true) {
            val frame = screenshot() ?: break
            if (I_TP_GET_ALL.match(frame, context).matched) break
            appearThenClick(I_RED_POINT_LEVEL, frame)
        }

        // 根据配置选择奖励等级
        val levelRule = when (levelReward) {
            1 -> I_TP_LEVEL_1
            3 -> I_TP_LEVEL_3
            else -> I_TP_LEVEL_2
        }

        var checkTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - checkTime < 2000) {
            val frame = screenshot() ?: continue
            if (appearThenClick(levelRule, frame)) {
                log("选择 $levelReward 号奖励")
                appearThenClick(I_OVERFLOW_CONFIRM, frame)
                checkTime = System.currentTimeMillis()
                continue
            }
            if (appearThenClick(I_TP_GET_ALL, frame)) {
                log("领取奖励")
                checkTime = System.currentTimeMillis()
                continue
            }
        }
    }
}

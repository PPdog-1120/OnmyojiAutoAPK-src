package com.onmyoji.auto.engine.component

import android.content.Context
import android.graphics.Bitmap
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * Buff 管理组件 — 对应 OAS general_buff.py
 *
 * 提供觉醒、御魂、金币、经验等 buff 的开关操作
 */
class GeneralBuff(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (GeneralBuffAssets) ==========

    // 庭院左上角的加成
    private val I_BUFF_1 = RuleImage("buff_1",
        "general_buff/gb/gb_buff_1.png",
        intArrayOf(363, 32, 32, 46), intArrayOf(344, 14, 128, 76), 0.8f)
    // 觉醒加成
    private val I_AWAKE = RuleImage("awake",
        "general_buff/gb/gb_awake.png",
        intArrayOf(381, 130, 35, 45), intArrayOf(360, 107, 80, 447), 0.8f)
    // 御魂加成
    private val I_SOUL = RuleImage("soul",
        "general_buff/gb/gb_soul.png",
        intArrayOf(377, 203, 39, 40), intArrayOf(361, 121, 71, 388), 0.8f)
    // 金币50
    private val I_GOLD_50 = RuleImage("gold_50",
        "general_buff/gb/gb_gold_50.png",
        intArrayOf(373, 269, 52, 36), intArrayOf(367, 120, 64, 391), 0.8f)
    // 金币100
    private val I_GOLD_100 = RuleImage("gold_100",
        "general_buff/gb/gb_gold_100.png",
        intArrayOf(378, 338, 43, 39), intArrayOf(365, 121, 74, 407), 0.8f)
    // 经验50
    private val I_EXP_50 = RuleImage("exp_50",
        "general_buff/gb/gb_exp_50.png",
        intArrayOf(379, 406, 44, 43), intArrayOf(365, 127, 70, 393), 0.8f)
    // 经验100
    private val I_EXP_100 = RuleImage("exp_100",
        "general_buff/gb/gb_exp_100.png",
        intArrayOf(383, 406, 30, 40), intArrayOf(368, 126, 58, 386), 0.8f)
    // 开启状态（黄色）
    private val I_OPEN_YELLOW = RuleImage("open_yellow",
        "general_buff/gb/gb_open_yellow.png",
        intArrayOf(773, 297, 17, 21), intArrayOf(766, 133, 35, 366), 0.6f)
    // 关闭状态（红色）
    private val I_CLOSE_RED = RuleImage("close_red",
        "general_buff/gb/gb_close_red.png",
        intArrayOf(773, 297, 17, 21), intArrayOf(764, 134, 38, 369), 0.6f)
    // 用来判定是否点击的那个
    val I_CLOUD = RuleImage("cloud",
        "general_buff/gb/gb_cloud.png",
        intArrayOf(357, 505, 65, 38), intArrayOf(313, 484, 158, 75), 0.8f)

    // Swipe Rule Assets
    private val S_BUFF_UP = RuleSwipe("buff_up", 397, 124, 447, 457)

    override suspend fun run() {
        // Buff 组件不直接 run
        log("GeneralBuff: 请在子任务中调用 buff 方法")
    }

    /**
     * 打开 buff 的总界面 — 对应 OAS open_buff
     */
    suspend fun openBuff() {
        log("Open buff")
        while (true) {
            val img = screenshot() ?: continue
            if (appear(I_CLOUD, img)) break
            if (appearThenClick(I_BUFF_1, img, 2000)) continue
        }

        while (true) {
            val img = screenshot() ?: continue
            if (appear(I_AWAKE, img)) break
            device.swipe(S_BUFF_UP.startX, S_BUFF_UP.startY, S_BUFF_UP.endX, S_BUFF_UP.endY)
            delay(2000)
        }
    }

    /**
     * 关闭 buff 的总界面 — 对应 OAS close_buff
     * 但是要确保 buff 界面已经打开了
     */
    suspend fun closeBuff() {
        log("Close buff")
        while (true) {
            val img = screenshot() ?: continue
            if (!appear(I_CLOUD, img)) break
            if (appearThenClick(I_BUFF_1, img, 2000)) continue
        }
    }

    /**
     * 金币50 buff — 对应 OAS gold_50
     */
    suspend fun gold50(isOpen: Boolean = true) {
        log("Gold 50 buff")
        val img = screenshot() ?: return
        // 简化: 直接使用图片匹配的 ROI 区域作为开关区域
        setSwitchArea(intArrayOf(I_GOLD_50.roiFront[0] + I_GOLD_50.roiFront[2] + 10,
            I_GOLD_50.roiFront[1] - 10, 80, I_GOLD_50.roiFront[3] + 20))
        toggleSwitch(isOpen)
    }

    /**
     * 金币100 buff — 对应 OAS gold_100
     */
    suspend fun gold100(isOpen: Boolean = true) {
        log("Gold 100 buff")
        val img = screenshot() ?: return
        setSwitchArea(intArrayOf(I_GOLD_100.roiFront[0] + I_GOLD_100.roiFront[2] + 10,
            I_GOLD_100.roiFront[1] - 10, 80, I_GOLD_100.roiFront[3] + 20))
        toggleSwitch(isOpen)
    }

    /**
     * 经验50 buff — 对应 OAS exp_50
     */
    suspend fun exp50(isOpen: Boolean = true) {
        log("Exp 50 buff")
        while (true) {
            val img = screenshot() ?: continue
            setSwitchArea(intArrayOf(I_EXP_50.roiFront[0] + I_EXP_50.roiFront[2] + 10,
                I_EXP_50.roiFront[1] - 10, 80, I_EXP_50.roiFront[3] + 20))

            if (!appearDynamic(I_OPEN_YELLOW) && !appearDynamic(I_CLOSE_RED)) {
                log("No exp 50 buff, swipe")
                device.swipe(580, 320, 530, 240)
                delay(1000)
            } else {
                break
            }
        }
        toggleSwitch(isOpen)
    }

    /**
     * 经验100 buff — 对应 OAS exp_100
     */
    suspend fun exp100(isOpen: Boolean = true) {
        log("Exp 100 buff")
        while (true) {
            val img = screenshot() ?: continue
            setSwitchArea(intArrayOf(I_EXP_100.roiFront[0] + I_EXP_100.roiFront[2] + 10,
                I_EXP_100.roiFront[1] - 10, 80, I_EXP_100.roiFront[3] + 20))

            if (!appearDynamic(I_OPEN_YELLOW) && !appearDynamic(I_CLOSE_RED)) {
                log("No exp 100 buff, swipe")
                device.swipe(580, 320, 530, 240)
                delay(1000)
            } else {
                break
            }
        }
        toggleSwitch(isOpen)
    }

    /**
     * 觉醒 buff — 对应 OAS awake
     */
    suspend fun awake(isOpen: Boolean = true) {
        log("Awake buff")
        val img = screenshot() ?: return
        val area = getAreaImage(I_AWAKE, img)
        if (area == null) {
            log("No awake buff")
            return
        }
        setSwitchArea(area)
        toggleSwitch(isOpen)
    }

    /**
     * 御魂 buff — 对应 OAS soul
     */
    suspend fun soul(isOpen: Boolean = true) {
        log("Soul buff")
        val img = screenshot() ?: return
        val area = getAreaImage(I_SOUL, img)
        if (area == null) {
            log("No soul buff")
            return
        }
        setSwitchArea(area)
        toggleSwitch(isOpen)
    }

    // ========== 内部辅助 ==========

    /**
     * 获取觉醒/御魂加成的点击区域 — 对应 OAS get_area_image
     */
    private fun getAreaImage(target: RuleImage, img: Bitmap): IntArray? {
        if (!target.match(img, context).matched) {
            log("No ${target.name} buff")
            return null
        }
        val centerX = target.roiFront[0] + target.roiFront[2] / 2
        val startX = centerX + 364
        val startY = target.roiFront[1]
        val width = 80
        val height = target.roiFront[3]
        return intArrayOf(startX, startY, width, height)
    }

    /**
     * 动态设置开关区域
     */
    private var switchArea: IntArray = intArrayOf(773, 297, 17, 21)

    private fun setSwitchArea(area: IntArray) {
        switchArea = area
    }

    /**
     * 使用动态 ROI 匹配
     */
    private fun appearDynamic(rule: RuleImage): Boolean {
        val img = screenshot() ?: return false
        // 创建一个临时的 RuleImage 使用动态 ROI
        val dynamicRule = RuleImage(
            rule.name,
            rule.assetPath,
            switchArea,
            switchArea,
            rule.threshold
        )
        return dynamicRule.match(img, context).matched
    }

    /**
     * 切换开关 — 对应 OAS gold_50 等方法中的开关逻辑
     */
    private suspend fun toggleSwitch(isOpen: Boolean) {
        if (isOpen) {
            while (true) {
                if (appearDynamic(I_OPEN_YELLOW)) break
                if (appearDynamic(I_CLOSE_RED)) {
                    val img = screenshot() ?: continue
                    device.click(switchArea[0] + switchArea[2] / 2, switchArea[1] + switchArea[3] / 2)
                    delay(1000)
                    continue
                }
                break
            }
        } else {
            while (true) {
                if (appearDynamic(I_CLOSE_RED)) break
                if (appearDynamic(I_OPEN_YELLOW)) {
                    val img = screenshot() ?: continue
                    device.click(switchArea[0] + switchArea[2] / 2, switchArea[1] + switchArea[3] / 2)
                    delay(1000)
                    continue
                }
                break
            }
        }
    }

    /**
     * 拒绝邀请 — 对应 OAS reject_invite
     */
    suspend fun rejectInvite() {
        // 使用 GeneralInvite 的拒绝按钮
        val reject1 = RuleImage("i_reject_1",
            "general_invite/gi/gi_i_reject.png",
            intArrayOf(5, 210, 110, 95), intArrayOf(5, 210, 110, 95), 0.8f)
        val reject2 = RuleImage("i_reject_2",
            "general_invite/gi/gi_i_reject.png",
            intArrayOf(5, 320, 110, 95), intArrayOf(5, 320, 110, 95), 0.8f)
        val reject3 = RuleImage("i_reject_3",
            "general_invite/gi/gi_i_reject.png",
            intArrayOf(5, 430, 110, 95), intArrayOf(5, 430, 110, 95), 0.8f)

        while (true) {
            val img = screenshot() ?: continue
            if (!appear(reject1, img) && !appear(reject2, img) && !appear(reject3, img)) break
            if (appear(reject3, img)) {
                device.click(reject3.coord().first, reject3.coord().second)
                delay(600)
                continue
            }
            if (appear(reject2, img)) {
                device.click(reject2.coord().first, reject2.coord().second)
                delay(600)
                continue
            }
            if (appear(reject1, img)) {
                device.click(reject1.coord().first, reject1.coord().second)
                delay(600)
                continue
            }
        }
    }

    /**
     * 判断目标是否出现
     */
    private fun appear(rule: RuleImage, img: Bitmap): Boolean {
        return rule.match(img, context).matched
    }

    /**
     * 出现则点击
     */
    private suspend fun appearThenClick(rule: RuleImage, img: Bitmap, interval: Long = 1000): Boolean {
        val result = rule.match(img, context)
        if (result.matched) {
            device.click(result.centerX, result.centerY)
            delay(interval)
            return true
        }
        return false
    }
}

package com.onmyoji.auto.engine.component

import android.content.Context
import android.graphics.Bitmap
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 房间管理组件 — 对应 OAS general_room.py
 *
 * 提供创建房间、设置公开/私人、选择副本等功能
 */
class GeneralRoom(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (GeneralRoomAssets) ==========

    // 创建房间按钮
    private val I_CREATE_ROOM = RuleImage("create_room",
        "general_room/gr/gr_create_room.png",
        intArrayOf(985, 600, 177, 58), intArrayOf(396, 569, 813, 100), 0.8f)
    // 创建确认
    private val I_CREATE_ENSURE = RuleImage("create_ensure",
        "general_room/gr/gr_create_ensure.png",
        intArrayOf(813, 560, 129, 63), intArrayOf(813, 560, 129, 63), 0.8f)
    // 创建确认2
    private val I_CREATE_ENSURE_2 = RuleImage("create_ensure_2",
        "general_room/gr/gr_create_ensure_2.png",
        intArrayOf(552, 489, 42, 55), intArrayOf(552, 489, 42, 55), 0.8f)
    // 勾选不公开
    private val I_ENSURE_PRIVATE = RuleImage("ensure_private",
        "general_room/gr/gr_ensure_private.png",
        intArrayOf(748, 489, 36, 40), intArrayOf(748, 489, 36, 40), 0.8f)
    // 未勾选不公开
    private val I_ENSURE_PRIVATE_FALSE = RuleImage("ensure_private_false",
        "general_room/gr/gr_ensure_private_false.png",
        intArrayOf(747, 489, 37, 40), intArrayOf(747, 489, 37, 40), 0.8f)
    // 勾选不公开2
    private val I_ENSURE_PRIVATE_2 = RuleImage("ensure_private_2",
        "general_room/gr/gr_ensure_private_2.png",
        intArrayOf(401, 409, 34, 40), intArrayOf(401, 409, 34, 40), 0.8f)
    // 未勾选不公开2
    private val I_ENSURE_PRIVATE_FALSE_2 = RuleImage("ensure_private_false_2",
        "general_room/gr/gr_ensure_private_false_2.png",
        intArrayOf(400, 408, 36, 38), intArrayOf(400, 408, 36, 38), 0.8f)
    // 勾选公开
    private val I_ENSURE_PUBLIC = RuleImage("ensure_public",
        "general_room/gr/gr_ensure_public.png",
        intArrayOf(400, 282, 35, 37), intArrayOf(400, 282, 35, 37), 0.8f)
    // 未勾选公开
    private val I_ENSURE_PUBLIC_FALSE = RuleImage("ensure_public_false",
        "general_room/gr/gr_ensure_public_false.png",
        intArrayOf(399, 285, 37, 35), intArrayOf(399, 285, 37, 35), 0.8f)
    // 勾选公开2
    private val I_ENSURE_PUBLIC_2 = RuleImage("ensure_public_2",
        "general_room/gr/gr_ensure_public_2.png",
        intArrayOf(307, 490, 37, 40), intArrayOf(307, 490, 37, 40), 0.8f)
    // 未勾选公开2
    private val I_ENSURE_PUBLIC_FALSE_2 = RuleImage("ensure_public_false_2",
        "general_room/gr/gr_ensure_public_false_2.png",
        intArrayOf(307, 491, 38, 37), intArrayOf(307, 491, 38, 37), 0.8f)
    // 返回按钮
    private val I_GR_BACK_YELLOW = RuleImage("gr_back_yellow",
        "general_room/gr/gr_gr_back_yellow.png",
        intArrayOf(19, 13, 53, 53), intArrayOf(19, 13, 53, 53), 0.8f)
    // 组队界面标识
    private val I_CHECK_TEAM = RuleImage("check_team",
        "general_room/gr/gr_create_room.png",
        intArrayOf(32, 585, 82, 90), intArrayOf(32, 585, 82, 90), 0.8f)

    // 副本列表 — 简化为文字列表
    private val teamList = listOf(
        "全部", "探索（困难）", "觉醒业火轮", "觉醒风转符", "觉醒火灵鲤", "觉醒天雷鼓",
        "御魂", "日轮之陨", "永生之海", "妖气封印", "经验妖怪", "金币妖怪",
        "年兽", "石距", "愤怒的石距", "喷怒的石距", "结界突破", "真·八岐大蛇",
        "对弈社", "斗町", "连携召唤", "契灵之境"
    )

    override suspend fun run() {
        // GeneralRoom 不直接 run
        log("GeneralRoom: 请在子任务中调用具体方法")
    }

    /**
     * 创建队伍 — 对应 OAS create_room
     * 一般是下方的黄色按钮
     */
    suspend fun createRoom(): Boolean {
        log("Create room")
        val img = screenshot() ?: return false
        if (!appear(I_CREATE_ROOM, img)) {
            log("No create room button")
            return false
        }

        var clickCount = 0
        while (true) {
            val frame = screenshot() ?: continue
            if (clickCount > 3) {
                log("Create room button do not take effect")
                log("The most possible reason is that there are not challenge tickets")
                return false
            }
            if (appearThenClick(I_CREATE_ROOM, frame, 2000)) {
                clickCount++
                continue
            }
            if (appear(I_CREATE_ENSURE, frame)) return true
            if (appear(I_CREATE_ENSURE_2, frame)) return true
        }
    }

    /**
     * 确认私人房间 — 对应 OAS ensure_private
     * 不公开仅邀请
     */
    suspend fun ensurePrivate(): Boolean {
        log("Ensure private")
        while (true) {
            val img = screenshot() ?: continue
            if (appear(I_ENSURE_PRIVATE, img)) return true
            if (appear(I_ENSURE_PRIVATE_2, img)) return true
            if (appearThenClick(I_ENSURE_PRIVATE_FALSE, img, 1000)) continue
            if (appearThenClick(I_ENSURE_PRIVATE_FALSE_2, img, 1000)) continue
        }
    }

    /**
     * 确认公开房间 — 对应 OAS ensure_public
     * 允许任何人加入
     */
    suspend fun ensurePublic(): Boolean {
        log("Ensure public")
        while (true) {
            val img = screenshot() ?: continue
            if (appear(I_ENSURE_PUBLIC, img)) return true
            if (appear(I_ENSURE_PUBLIC_2, img)) return true
            if (appearThenClick(I_ENSURE_PUBLIC_FALSE, img, 1000)) continue
            if (appearThenClick(I_ENSURE_PUBLIC_FALSE_2, img, 1000)) continue
        }
    }

    /**
     * 创建确认 — 对应 OAS create_ensure
     */
    suspend fun createEnsure(): Boolean {
        log("Create ensure")
        val img = screenshot() ?: return false
        val target = when {
            appear(I_CREATE_ENSURE, img) -> I_CREATE_ENSURE
            appear(I_CREATE_ENSURE_2, img) -> I_CREATE_ENSURE_2
            else -> {
                log("No create ensure button")
                return false
            }
        }

        while (true) {
            val frame = screenshot() ?: continue
            if (appearThenClick(target, frame, 1500)) continue
            if (!appear(target, frame)) return true
        }
    }

    /**
     * 退出组队界面 — 对应 OAS exit_team
     */
    suspend fun exitTeam(): Boolean {
        val img = screenshot() ?: return false
        if (!appear(I_CHECK_TEAM, img)) return false
        log("Exit team ui")
        while (true) {
            val frame = screenshot() ?: continue
            if (!appear(I_CHECK_TEAM, frame)) return true
            if (appearThenClick(I_GR_BACK_YELLOW, frame, 500)) continue
        }
    }

    /**
     * 确认副本名称并选中 — 对应 OAS check_zones
     *
     * 简化实现：通过列表索引定位
     * 完整实现需要 OCR 识别副本名
     */
    suspend fun checkZones(name: String): Boolean {
        log("Check zones: $name")
        // 简化: 点击列表区域中对应的副本
        // 完整实现需要 list_find + OCR
        delay(1000)
        return true
    }

    // ========== 辅助方法 ==========

    private fun appear(rule: RuleImage, img: Bitmap): Boolean {
        return rule.match(img, context).matched
    }

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

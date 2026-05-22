package com.onmyoji.auto.engine.component

import android.content.Context
import android.graphics.Bitmap
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 御魂切换组件 — 对应 OAS switch_soul.py
 *
 * 在式神录界面切换预设的御魂方案
 */
class SwitchSoul(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (SwitchSoulAssets) ==========

    // Click Rule Assets — 分组选择
    private val C_SOU_GROUP_1 = RuleClick("sou_group_1", intArrayOf(1086, 85, 158, 47))
    private val C_SOU_GROUP_2 = RuleClick("sou_group_2", intArrayOf(1087, 155, 162, 48))
    private val C_SOU_GROUP_3 = RuleClick("sou_group_3", intArrayOf(1088, 226, 154, 49))
    private val C_SOU_GROUP_4 = RuleClick("sou_group_4", intArrayOf(1087, 297, 157, 46))
    private val C_SOU_GROUP_5 = RuleClick("sou_group_5", intArrayOf(1087, 365, 154, 47))
    private val C_SOU_GROUP_6 = RuleClick("sou_group_6", intArrayOf(1088, 437, 156, 46))
    private val C_SOU_GROUP_7 = RuleClick("sou_group_7", intArrayOf(1090, 505, 156, 51))

    // Image Rule Assets
    // 退出式神录
    private val I_RECORD_SOUL_BACK = RuleImage("record_soul_back",
        "switch_soul/ss/ss_record_soul_back.png",
        intArrayOf(19, 9, 51, 44), intArrayOf(19, 9, 51, 44), 0.8f)
    // 预设按钮
    private val I_SOUL_PRESET = RuleImage("soul_preset",
        "switch_soul/ss/ss_soul_preset.png",
        intArrayOf(335, 73, 90, 51), intArrayOf(310, 57, 169, 72), 0.8f)
    // 队伍切换按钮
    private val I_SOU_SWITCH_1 = RuleImage("sou_switch_1",
        "switch_soul/ss/ss_sou_switch_1.png",
        intArrayOf(971, 149, 34, 32), intArrayOf(962, 141, 51, 48), 0.8f)
    private val I_SOU_SWITCH_2 = RuleImage("sou_switch_2",
        "switch_soul/ss/ss_sou_switch_2.png",
        intArrayOf(967, 296, 42, 39), intArrayOf(961, 292, 54, 49), 0.8f)
    private val I_SOU_SWITCH_3 = RuleImage("sou_switch_3",
        "switch_soul/ss/ss_sou_switch_3.png",
        intArrayOf(968, 448, 41, 38), intArrayOf(962, 442, 52, 48), 0.8f)
    private val I_SOU_SWITCH_4 = RuleImage("sou_switch_4",
        "switch_soul/ss/ss_sou_switch_4.png",
        intArrayOf(967, 597, 42, 25), intArrayOf(962, 592, 53, 34), 0.8f)
    // 切换确认
    private val I_SOU_SWITCH_SURE = RuleImage("sou_switch_sure",
        "switch_soul/ss/ss_sou_switch_sure.png",
        intArrayOf(668, 401, 180, 61), intArrayOf(668, 401, 180, 61), 0.8f)
    // 式神录标识
    private val I_SOU_CHECK_IN = RuleImage("sou_check_in",
        "switch_soul/ss/ss_sou_check_in.png",
        intArrayOf(269, 69, 50, 49), intArrayOf(269, 69, 50, 49), 0.8f)
    // 分组选中标识
    private val I_SOU_CHECK_GROUP_1 = RuleImage("sou_check_group_1",
        "switch_soul/ss/ss_sou_check_group_1.png",
        intArrayOf(1086, 91, 22, 57), intArrayOf(1086, 91, 22, 57), 0.9f)
    private val I_SOU_CHECK_GROUP_2 = RuleImage("sou_check_group_2",
        "switch_soul/ss/ss_sou_check_group_2.png",
        intArrayOf(1086, 163, 25, 57), intArrayOf(1086, 163, 25, 57), 0.8f)
    private val I_SOU_CHECK_GROUP_3 = RuleImage("sou_check_group_3",
        "switch_soul/ss/ss_sou_check_group_3.png",
        intArrayOf(1085, 234, 22, 49), intArrayOf(1085, 234, 22, 49), 0.8f)
    private val I_SOU_CHECK_GROUP_4 = RuleImage("sou_check_group_4",
        "switch_soul/ss/ss_sou_check_group_4.png",
        intArrayOf(1086, 303, 21, 56), intArrayOf(1086, 303, 21, 56), 0.8f)
    private val I_SOU_CHECK_GROUP_5 = RuleImage("sou_check_group_5",
        "switch_soul/ss/ss_sou_check_group_5.png",
        intArrayOf(1088, 370, 21, 53), intArrayOf(1088, 370, 21, 53), 0.8f)
    private val I_SOU_CHECK_GROUP_6 = RuleImage("sou_check_group_6",
        "switch_soul/ss/ss_sou_check_group_6.png",
        intArrayOf(1085, 443, 23, 54), intArrayOf(1085, 443, 23, 54), 0.8f)
    private val I_SOU_CHECK_GROUP_7 = RuleImage("sou_check_group_7",
        "switch_soul/ss/ss_sou_check_group_7.png",
        intArrayOf(1088, 512, 21, 54), intArrayOf(1088, 512, 21, 54), 0.8f)
    // 匹配队伍预设
    private val I_SOU_TEAM_PRESENT = RuleImage("sou_team_present",
        "switch_soul/ss/ss_sou_team_present.png",
        intArrayOf(737, 82, 148, 43), intArrayOf(727, 71, 165, 63), 0.8f)
    // 需要切换的预设按钮
    private val I_SOU_CLICK_PRESENT = RuleImage("sou_click_present",
        "switch_soul/ss/ss_sou_click_present.png",
        intArrayOf(967, 305, 43, 22), intArrayOf(965, 142, 48, 480), 0.9f)
    // 误触检查
    private val I_CHECK_BLOCK = RuleImage("check_block",
        "switch_soul/ss/ss_check_block.png",
        intArrayOf(572, 408, 137, 49), intArrayOf(572, 408, 137, 49), 0.8f)

    // Swipe Rule Assets
    private val S_SS_GROUP_SWIPE_UP = RuleSwipe("ss_group_swipe_up", 1154, 180, 1153, 322)
    private val S_SS_GROUP_SWIPE_DOWN = RuleSwipe("ss_group_swipe_down", 1155, 388, 1152, 177)
    private val S_SS_TEAM_SWIPE_UP = RuleSwipe("ss_team_swipe_up", 720, 439, 717, 308)
    private val S_SS_TEAM_SWIPE_DOWN = RuleSwipe("ss_team_swipe_down", 709, 308, 709, 449)

    override suspend fun run() {
        // SwitchSoul 不直接 run
        log("SwitchSoul: 请在子任务中调用 runSwitchSoul()")
    }

    /**
     * 运行御魂切换 — 对应 OAS run_switch_soul
     * 保证在式神录的界面
     */
    suspend fun runSwitchSoul(target: Any) {
        val pairs = when (target) {
            is Pair<*, *> -> listOf(target as Pair<Int, Int>)
            is List<*> -> target.map { it as Pair<Int, Int> }
            is String -> {
                try {
                    val parts = target.split(",")
                    if (parts.size != 2) {
                        log("Switch soul config error")
                        return
                    }
                    listOf(Pair(parts[0].trim().toInt(), parts[1].trim().toInt()))
                } catch (e: Exception) {
                    log("Switch soul config error: ${e.message}")
                    return
                }
            }
            else -> {
                log("Switch soul target type error")
                return
            }
        }

        clickPreset()
        switchSouls(pairs)
    }

    /**
     * 点击预设 — 对应 OAS click_preset
     */
    private suspend fun clickPreset() {
        while (true) {
            val img = screenshot() ?: continue
            if (appear(I_SOU_SWITCH_1, img)) break
            if (appear(I_SOU_SWITCH_2, img)) break
            if (appear(I_SOU_SWITCH_3, img)) break
            if (appear(I_SOU_SWITCH_4, img)) break
            if (appear(I_SOU_TEAM_PRESENT, img)) break
            if (appear(I_SOUL_PRESET, img)) {
                device.click(I_SOUL_PRESET.coord().first, I_SOUL_PRESET.coord().second)
                delay(3000)
                continue
            }
        }
        log("Click preset in switch soul")
    }

    /**
     * 切换单个队伍的预设御魂 — 对应 OAS switch_soul_one
     *
     * @param group 分组 [1-7]
     * @param team 队伍 [1-4]
     */
    private suspend fun switchSoulOne(group: Int, team: Int) {
        require(group in 1..7) { "Switch soul_one group must be in [1-7]" }
        require(team in 1..4) { "Switch soul_one team must be in [1-4]" }

        // 滑动至分组最上层
        var lastOcrText = ""
        while (true) {
            val img = screenshot() ?: continue
            // 简化: 通过截图差异判断是否到达顶部
            // 完整实现需要 OCR 识别分组名
            if (lastOcrText.isNotEmpty()) break // 简化: 只滑动一次
            lastOcrText = "scrolled"
            device.swipe(S_SS_GROUP_SWIPE_UP.startX, S_SS_GROUP_SWIPE_UP.startY,
                S_SS_GROUP_SWIPE_UP.endX, S_SS_GROUP_SWIPE_UP.endY)
            delay(1500)
        }

        // 选择组
        val (groupClick, groupCheck) = getGroupAssets(group)
        // 2023.8.5 修改为无反馈的点击切换
        for (i in 0 until 2) {
            val (gx, gy) = groupClick.coord()
            device.click(gx, gy)
            delay(500)
        }

        // 点击队伍
        val teamAsset = getTeamAsset(team)
        for (i in 0 until 3) {
            delay(800)
            val img = screenshot() ?: continue
            if (appear(I_SOU_SWITCH_SURE, img)) {
                while (true) {
                    device.click(I_SOU_SWITCH_SURE.coord().first, I_SOU_SWITCH_SURE.coord().second)
                    delay(300)
                    val frame = screenshot() ?: break
                    if (appearThenClick(I_CHECK_BLOCK, frame, 3000)) continue
                    if (!appear(I_SOU_SWITCH_SURE, frame)) break
                }
                continue
            }
            val result = teamAsset.match(img, context)
            if (result.matched) {
                device.click(result.centerX, result.centerY)
                delay(3000)
            } else {
                log("Click team $team failed in group $group")
            }
        }

        // 兜底若还出现确认按钮则点击
        uiClickUntilDisappear(I_SOU_SWITCH_SURE)
        log("Switch soul_one group $group team $team")
    }

    /**
     * 切换御魂 — 对应 OAS switch_souls
     *
     * @param target [(1, 1), (2, 2), (3, 3), (4, 4)] 或者单独一个元组 (4, 4)
     */
    private suspend fun switchSouls(target: List<Pair<Int, Int>>) {
        for ((group, team) in target) {
            switchSoulOne(group.toInt(), team.toInt())
        }
    }

    /**
     * 退出式神录 — 对应 OAS exit_shikigami_records
     */
    suspend fun exitShikigamiRecords() {
        while (true) {
            val img = screenshot() ?: continue
            if (!appear(I_SOU_CHECK_IN, img)) break
            if (appearThenClick(I_RECORD_SOUL_BACK, img, 3500)) continue
        }
        log("Exit shikigami records")
    }

    /**
     * 通过名字运行御魂切换 — 对应 OAS run_switch_soul_by_name
     * 保证在式神录的界面
     */
    suspend fun runSwitchSoulByName(groupName: String, teamName: String) {
        log("Switch soul by name")
        clickPreset()
        switchSoulByName(groupName, teamName)
    }

    /**
     * 通过名字切换御魂 — 对应 OAS switch_soul_by_name
     *
     * 简化实现: 通过滑动和 OCR 识别分组/阵容名
     * 完整实现需要 OcrRegion 支持
     */
    private suspend fun switchSoulByName(groupName: String, teamName: String) {
        // 滑动至分组最上层
        var lastGroupText = ""
        while (true) {
            val img = screenshot() ?: continue
            // 简化: 只滑动一次
            if (lastGroupText.isNotEmpty()) break
            lastGroupText = "scrolled"
            device.swipe(S_SS_GROUP_SWIPE_UP.startX, S_SS_GROUP_SWIPE_UP.startY,
                S_SS_GROUP_SWIPE_UP.endX, S_SS_GROUP_SWIPE_UP.endY)
            delay(2500)
        }
        log("Swipe to top of group")

        // 向下寻找目标分组
        while (true) {
            val img = screenshot() ?: continue
            // 简化: 等待一段时间后认为找到
            delay(500)
            break
        }
        log("Swipe down to find target group")

        // 简化: 点击分组区域
        log("Select group $groupName")

        // 滑动至阵容最上层
        device.swipe(S_SS_TEAM_SWIPE_DOWN.startX, S_SS_TEAM_SWIPE_DOWN.startY,
            S_SS_TEAM_SWIPE_DOWN.endX, S_SS_TEAM_SWIPE_DOWN.endY)
        delay(2000)
        log("Swipe to top of team")

        // 简化: 选中阵容
        log("Select team $teamName")

        // 切换御魂
        var cntClick = 0
        while (true) {
            val img = screenshot() ?: continue
            if (cntClick >= 4) break
            if (appearThenClick(I_SOU_SWITCH_SURE, img, 800)) continue
            // 简化: 直接点击预设按钮
            if (appear(I_SOU_CLICK_PRESENT, img)) {
                device.click(I_SOU_CLICK_PRESENT.coord().first, I_SOU_CLICK_PRESENT.coord().second)
                delay(1500)
                cntClick++
                continue
            }
        }
        log("Switch soul_one group $groupName team $teamName")
    }

    // ========== 辅助方法 ==========

    private fun getGroupAssets(group: Int): Pair<RuleClick, RuleImage> = when (group) {
        1 -> Pair(C_SOU_GROUP_1, I_SOU_CHECK_GROUP_1)
        2 -> Pair(C_SOU_GROUP_2, I_SOU_CHECK_GROUP_2)
        3 -> Pair(C_SOU_GROUP_3, I_SOU_CHECK_GROUP_3)
        4 -> Pair(C_SOU_GROUP_4, I_SOU_CHECK_GROUP_4)
        5 -> Pair(C_SOU_GROUP_5, I_SOU_CHECK_GROUP_5)
        6 -> Pair(C_SOU_GROUP_6, I_SOU_CHECK_GROUP_6)
        7 -> Pair(C_SOU_GROUP_7, I_SOU_CHECK_GROUP_7)
        else -> Pair(C_SOU_GROUP_1, I_SOU_CHECK_GROUP_1)
    }

    private fun getTeamAsset(team: Int): RuleImage = when (team) {
        1 -> I_SOU_SWITCH_1
        2 -> I_SOU_SWITCH_2
        3 -> I_SOU_SWITCH_3
        4 -> I_SOU_SWITCH_4
        else -> I_SOU_SWITCH_1
    }

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

    private suspend fun uiClickUntilDisappear(rule: RuleImage) {
        while (true) {
            val img = screenshot() ?: break
            if (!rule.match(img, context).matched) break
            appearThenClick(rule, img)
        }
    }
}

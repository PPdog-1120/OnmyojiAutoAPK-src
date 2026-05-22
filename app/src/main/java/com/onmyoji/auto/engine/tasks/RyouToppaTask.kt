package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 寮突 (RyouToppa)
 * 对应 Python tasks/RyouToppa/script_task.py
 * 流程：进入寮突 → 检查状态 → 选择区域攻击 → 循环 → 退出
 */
class RyouToppaTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {

    // ========== 资源定义 (from RyouToppaAssets) ==========
    private val C_AREA_1 = RuleClick("area_1", intArrayOf(533, 162, 177, 74))
    private val C_AREA_2 = RuleClick("area_2", intArrayOf(863, 164, 181, 71))
    private val C_AREA_3 = RuleClick("area_3", intArrayOf(532, 292, 181, 87))
    private val C_AREA_4 = RuleClick("area_4", intArrayOf(863, 301, 171, 62))
    private val C_AREA_5 = RuleClick("area_5", intArrayOf(540, 432, 169, 68))
    private val C_AREA_6 = RuleClick("area_6", intArrayOf(876, 432, 165, 74))
    private val C_AREA_7 = RuleClick("area_7", intArrayOf(538, 557, 149, 71))
    private val C_AREA_8 = RuleClick("area_8", intArrayOf(876, 562, 150, 67))
    private val C_SELECT_FIRST_RYOU = RuleClick("select_first_ryou", intArrayOf(1148, 138, 21, 22))

    private val I_AREA_FAILURE = arrayOf(
        RuleImage("area_1_fail", "tasks/RyouToppa/dev/loser_sign_1.png", intArrayOf(673, 146, 63, 32), intArrayOf(421, 127, 325, 134), 0.8f),
        RuleImage("area_2_fail", "tasks/RyouToppa/dev/loser_sign_1.png", intArrayOf(1011, 146, 63, 30), intArrayOf(757, 125, 327, 140), 0.8f),
        RuleImage("area_3_fail", "tasks/RyouToppa/dev/loser_sign_1.png", intArrayOf(665, 283, 73, 40), intArrayOf(419, 254, 327, 144), 0.8f),
        RuleImage("area_4_fail", "tasks/RyouToppa/dev/loser_sign_1.png", intArrayOf(1000, 283, 72, 38), intArrayOf(756, 260, 325, 139), 0.8f),
        RuleImage("area_5_fail", "tasks/RyouToppa/dev/loser_sign_1.png", intArrayOf(669, 419, 65, 29), intArrayOf(418, 392, 328, 142), 0.8f),
        RuleImage("area_6_fail", "tasks/RyouToppa/dev/loser_sign_1.png", intArrayOf(988, 416, 84, 37), intArrayOf(755, 395, 328, 141), 0.8f),
        RuleImage("area_7_fail", "tasks/RyouToppa/dev/loser_sign_1.png", intArrayOf(672, 556, 61, 37), intArrayOf(420, 530, 326, 127), 0.8f),
        RuleImage("area_8_fail", "tasks/RyouToppa/dev/loser_sign_1.png", intArrayOf(1004, 556, 64, 29), intArrayOf(756, 530, 327, 124), 0.8f)
    )
    private val I_AREA_FINISHED = arrayOf(
        RuleImage("area_1_done", "tasks/RyouToppa/dev/finished_1.png", intArrayOf(658, 141, 93, 91), intArrayOf(421, 127, 325, 134), 0.8f),
        RuleImage("area_2_done", "tasks/RyouToppa/dev/finished_1.png", intArrayOf(983, 137, 100, 100), intArrayOf(757, 125, 327, 140), 0.8f),
        RuleImage("area_3_done", "tasks/RyouToppa/dev/finished_1.png", intArrayOf(681, 312, 47, 37), intArrayOf(419, 254, 327, 144), 0.8f),
        RuleImage("area_4_done", "tasks/RyouToppa/dev/finished_1.png", intArrayOf(996, 271, 100, 100), intArrayOf(756, 260, 325, 139), 0.8f),
        RuleImage("area_5_done", "tasks/RyouToppa/dev/finished_1.png", intArrayOf(647, 404, 100, 100), intArrayOf(418, 392, 328, 142), 0.8f),
        RuleImage("area_6_done", "tasks/RyouToppa/dev/finished_1.png", intArrayOf(1015, 448, 56, 35), intArrayOf(755, 395, 328, 141), 0.8f),
        RuleImage("area_7_done", "tasks/RyouToppa/dev/finished_1.png", intArrayOf(643, 543, 100, 100), intArrayOf(420, 530, 326, 127), 0.8f),
        RuleImage("area_8_done", "tasks/RyouToppa/dev/finished_1.png", intArrayOf(983, 541, 100, 100), intArrayOf(756, 530, 327, 124), 0.8f)
    )
    private val areaClicks = arrayOf(C_AREA_1, C_AREA_2, C_AREA_3, C_AREA_4, C_AREA_5, C_AREA_6, C_AREA_7, C_AREA_8)

    private val I_TOPPA_RECORD = RuleImage("toppa_record", "tasks/RyouToppa/dev/res_toppa_record.png",
        intArrayOf(66, 628, 64, 39), intArrayOf(66, 628, 64, 39), 0.8f)
    private val I_TOPPA_LOCK_TEAM = RuleImage("toppa_lock_team", "tasks/RyouToppa/dev/dev_toppa_lock_team.png",
        intArrayOf(203, 602, 26, 32), intArrayOf(203, 602, 26, 32), 0.8f)
    private val I_TOPPA_UNLOCK_TEAM = RuleImage("toppa_unlock_team", "tasks/RyouToppa/dev/dev_toppa_unlock_team.png",
        intArrayOf(202, 603, 25, 31), intArrayOf(202, 603, 25, 31), 0.8f)
    private val I_RYOU_TOPPA = RuleImage("ryou_toppa", "tasks/RyouToppa/res/res_ryou_toppa.png",
        intArrayOf(1191, 352, 78, 116), intArrayOf(1161, 323, 118, 193), 0.6f)
    private val I_SELECT_RYOU_BUTTON = RuleImage("select_ryou_button", "tasks/RyouToppa/res/res_select_ryou_button.png",
        intArrayOf(560, 577, 156, 46), intArrayOf(560, 577, 156, 46), 0.8f)
    private val I_NO_SELECT_RYOU = RuleImage("no_select_ryou", "tasks/RyouToppa/res/res_no_select_ryou.png",
        intArrayOf(554, 180, 100, 167), intArrayOf(554, 180, 100, 167), 0.8f)
    private val I_START_TOPPA_BUTTON = RuleImage("start_toppa_button", "tasks/RyouToppa/res/res_start_toppa_button.png",
        intArrayOf(832, 279, 130, 43), intArrayOf(1, 1, 1055, 718), 0.8f)
    private val I_RYOU_REWARD = RuleImage("ryou_reward", "tasks/RyouToppa/res/res_ryou_reward.png",
        intArrayOf(134, 417, 241, 40), intArrayOf(122, 390, 340, 84), 0.65f)
    private val I_GUILD_ORDERS_REWARDS = RuleImage("guild_orders_rewards", "tasks/RyouToppa/res/res_guild_orders_rewards.png",
        intArrayOf(1123, 31, 115, 56), intArrayOf(1123, 31, 115, 56), 0.8f)
    private val I_SUCCESS_PENETRATION = RuleImage("success_penetration", "tasks/RyouToppa/res/res_success_penetration.png",
        intArrayOf(141, 374, 234, 37), intArrayOf(141, 374, 234, 37), 0.8f)
    private val I_REAL_RAID_REFRESH = RuleImage("real_raid_refresh", "tasks/RyouToppa/res/res_real_raid_refresh.png",
        intArrayOf(963, 569, 174, 60), intArrayOf(963, 569, 174, 60), 0.8f)
    private val I_REALM_RAID = RuleImage("realm_raid", "tasks/RealmRaid/res/res_realm_raid.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_FIRE = RuleImage("fire", "tasks/RealmRaid/res/res_fire.png",
        intArrayOf(982, 494, 136, 63), intArrayOf(140, 129, 1024, 584), 0.8f)
    private val I_RYOU_REWARD_90 = RuleImage("ryou_reward_90", "tasks/RyouToppa/res/res_ryou_reward_90.png",
        intArrayOf(134, 415, 232, 38), intArrayOf(134, 415, 232, 38), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    override suspend fun run() {
        log("=== 寮突任务开始 ===")

        if (config.ryouToppaSwitchSoulEnable) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.ryouToppaSwitchGroupTeam)
        }
        if (config.ryouToppaSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.ryouToppaGroupName, config.ryouToppaTeamName)
        }

        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_kekkai_toppa")

        var ryouToppaStartFlag = true
        var ryouToppaSuccessPenetration = false
        var ryouToppaAdminFlag = false

        // 检查寮突状态
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_REALM_RAID, img, 1000)) continue
            if (I_REAL_RAID_REFRESH.match(img, context).matched) {
                if (appearThenClick(I_RYOU_TOPPA, img, 1000)) continue
            } else if (I_SUCCESS_PENETRATION.match(img, context).matched) {
                ryouToppaStartFlag = true; ryouToppaSuccessPenetration = true; break
            } else if (I_SELECT_RYOU_BUTTON.match(img, context).matched) {
                ryouToppaStartFlag = false; ryouToppaAdminFlag = true; break
            } else if (I_NO_SELECT_RYOU.match(img, context).matched) {
                ryouToppaStartFlag = false; break
            } else if (I_RYOU_REWARD.match(img, context).matched || I_RYOU_REWARD_90.match(img, context).matched) {
                ryouToppaStartFlag = true; break
            }
        }

        // 寮突未开
        if (!ryouToppaStartFlag) {
            if (config.ryouToppaAccess && ryouToppaAdminFlag) {
                startRyouToppa()
            } else {
                log("Ryou toppa not open"); return
            }
        }

        // 100%攻破
        if (ryouToppaSuccessPenetration) { log("RyouToppa 100%"); return }

        // 锁定阵容
        if (config.ryouToppaLockTeam) uiClick(I_TOPPA_UNLOCK_TEAM, I_TOPPA_LOCK_TEAM)
        else uiClick(I_TOPPA_LOCK_TEAM, I_TOPPA_UNLOCK_TEAM)

        // 开始突破
        var areaIndex = 0
        while (true) {
            if (!hasTicket()) { log("No ticket"); break }
            if (currentCount >= config.ryouToppaLimitCount) { log("Count limit"); break }
            if (isTimeUp(config.ryouToppaLimitTimeMinutes)) { log("Time limit"); break }

            val res = attackArea(areaIndex)
            if (!res) {
                areaIndex++
                if (areaIndex >= 8) { areaIndex = 0; flushAreaCache() }
            }
        }

        log("=== 寮突完成 ===")
    }

    private suspend fun startRyouToppa() {
        while (true) { val img = screenshot() ?: continue; if (appearThenClick(I_SELECT_RYOU_BUTTON, img, 1000)) break }
        while (true) { val img = screenshot() ?: continue; if (appearThenClick(I_GUILD_ORDERS_REWARDS, img, 1000)) { device.click(C_SELECT_FIRST_RYOU.coord().first, C_SELECT_FIRST_RYOU.coord().second); break } }
        while (true) { val img = screenshot() ?: continue; if (appearThenClick(I_START_TOPPA_BUTTON, img, 1000)) continue; if (I_RYOU_REWARD.match(img, context).matched) break }
    }

    private fun hasTicket(): Boolean {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (hour >= 21 || hour <= 5) return true
        // OCR检查票数
        return true
    }

    private suspend fun checkArea(index: Int): Boolean {
        val img = screenshot() ?: return false
        if (I_AREA_FINISHED[index].match(img, context).matched) return false
        if (I_AREA_FAILURE[index].match(img, context).matched) return false
        return true
    }

    private suspend fun attackArea(index: Int): Boolean {
        if (!checkArea(index)) return false
        if (config.ryouToppaRandomDelay) delay((1000..2000).random().toLong())

        val clickArea = areaClicks[index]
        while (true) {
            val img = screenshot() ?: continue
            if (!I_TOPPA_RECORD.match(img, context).matched) {
                delay(1000)
                val frame = screenshot() ?: return false
                if (I_TOPPA_RECORD.match(frame, context).matched) continue
                return generalBattle.runGeneralBattle(config.ryouToppaBattleConfig)
            }
            if (appearThenClick(I_FIRE, img, 2000, 0.8f)) continue
            val (x, y) = clickArea.coord()
            device.click(x, y)
            delay(5000)
        }
    }

    private suspend fun flushAreaCache() {
        delay(2000)
        val count = (1..3).random()
        repeat(count) {
            val x = (540..1000).random()
            val y = (320..540).random()
            device.swipe(x, y, x, y - 101, 352)
            delay(2000)
        }
    }

    private suspend fun uiClick(clickRule: RuleImage, stopRule: RuleImage) {
        while (true) { val img = screenshot() ?: continue; if (stopRule.match(img, context).matched) break; appearThenClick(clickRule, img, 1000) }
    }
}

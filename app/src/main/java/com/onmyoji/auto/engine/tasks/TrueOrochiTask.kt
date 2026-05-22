package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 真八岐大蛇 (TrueOrochi)
 * 对应 Python tasks/TrueOrochi/script_task.py
 * 继承 OrochiTask 逻辑，特殊处理真蛇入口和10层式神战斗
 */
class TrueOrochiTask(context: Context, device: DeviceController, config: TaskConfig) : BaseTask(context, device, config) {

    // 从 OrochiAssets 复用
    private val I_OROCHI = RuleImage("orochi", "tasks/Orochi/o/o_orochi.png",
        intArrayOf(260, 274, 66, 66), intArrayOf(55, 104, 295, 406), 0.8f)
    private val I_FORM_TEAM = RuleImage("form_team", "tasks/Orochi/o/o_form_team.png",
        intArrayOf(957, 582, 100, 100), intArrayOf(957, 582, 100, 100), 0.8f)
    private val I_OROCHI_FIRE = RuleImage("orochi_fire", "tasks/Orochi/o/o_orochi_fire.png",
        intArrayOf(1133, 584, 110, 59), intArrayOf(1122, 572, 131, 124), 0.6f)
    private val I_OROCHI_LOCK = RuleImage("orochi_lock", "tasks/Orochi/o/o_orochi_lock.png",
        intArrayOf(652, 657, 22, 26), intArrayOf(628, 644, 191, 62), 0.8f)
    private val I_OROCHI_UNLOCK = RuleImage("orochi_unlock", "tasks/Orochi/o/o_orochi_unlock.png",
        intArrayOf(652, 656, 21, 21), intArrayOf(624, 644, 188, 60), 0.8f)
    private val I_PET_PRESENT = RuleImage("pet_present", "tasks/Orochi/o/o_pet_present.png",
        intArrayOf(873, 184, 62, 147), intArrayOf(873, 184, 62, 147), 0.8f)

    // TrueOrochi 特有资源
    private val I_FIND_TS = RuleImage("find_ts", "tasks/TrueOrochi/st/st_find_ts.png",
        intArrayOf(1, 613, 100, 93), intArrayOf(1, 613, 100, 93), 0.8f)
    private val I_ST_FIRE = RuleImage("st_fire", "tasks/TrueOrochi/st/st_st_fire.png",
        intArrayOf(960, 485, 100, 100), intArrayOf(960, 485, 100, 100), 0.8f)
    private val I_ST_FIRE_PREPARE = RuleImage("st_fire_prepare", "tasks/TrueOrochi/st/st_st_fire_prepare.png",
        intArrayOf(1118, 548, 100, 100), intArrayOf(1118, 548, 100, 100), 0.8f)
    private val I_ST_AUTO_FALSE = RuleImage("st_auto_false", "tasks/TrueOrochi/st/st_st_auto_false.png",
        intArrayOf(1112, 463, 33, 34), intArrayOf(1112, 463, 33, 34), 0.8f)
    private val I_ST_CREATE_ROOM = RuleImage("st_create_room", "tasks/TrueOrochi/st/st_st_create_room.png",
        intArrayOf(528, 482, 223, 62), intArrayOf(528, 482, 223, 62), 0.8f)
    private val I_ST_FRAME = RuleImage("st_frame", "tasks/TrueOrochi/st/st_st_frame.png",
        intArrayOf(571, 442, 135, 65), intArrayOf(571, 442, 135, 65), 0.8f)
    private val I_GREED_GHOST = RuleImage("greed_ghost", "tasks/GeneralBattle/res/res_greed_ghost.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_BUFF = RuleImage("buff", "tasks/GeneralBattle/res/res_buff.png",
        intArrayOf(0, 0, 100, 100), intArrayOf(0, 0, 1280, 720), 0.8f)
    private val I_UI_CONFIRM = RuleImage("ui_confirm", "tasks/GameUi/res/res_ui_confirm.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    private val I_FIRE = RuleImage("fire", "tasks/RealmRaid/res/res_fire.png",
        intArrayOf(982, 494, 136, 63), intArrayOf(140, 129, 1024, 584), 0.8f)

    private val generalBattle = GeneralBattle(context, device, config)
    private val gameUi = GameUi(context, device, config)
    private val switchSoul = SwitchSoul(context, device, config)

    override suspend fun run() {
        log("=== 真八岐大蛇任务开始 ===")

        // 检查本周次数
        if (config.trueOrochiCurrentSuccess >= 2) {
            log("This week is full"); return
        }

        // 切换御魂
        if (config.trueOrochiSwitchSoulEnable) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoul(config.trueOrochiSwitchGroupTeam)
        }
        if (config.trueOrochiSwitchSoulEnableByName) {
            gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_shikigami_records")
            switchSoul.runSwitchSoulByName(config.trueOrochiGroupName, config.trueOrochiTeamName)
        }

        gameUi.uiGetCurrentPage(); gameUi.uiGoto("page_soul_zones")
        orochiEnter()
        delay(500)

        var battle = checkTrueOrochi()
        if (!battle) {
            log("Not find true orochi")
            if (!config.trueOrochiFindTrueOrochi) { log("Not find_true_orochi"); return }

            // 打十层触发真蛇
            checkLayer("拾层")
            checkLock(false)
            var count = 0
            while (true) {
                val img = screenshot() ?: continue
                if (appearThenClick(I_PET_PRESENT, img, 1000)) continue
                if (!I_OROCHI_FIRE.match(img, context).matched) continue
                if (checkTrueOrochi()) { battle = true; break }
                if (count >= 10) { log("Not find true orochi after 10 tries"); break }
                uiClickUntilDisappear(I_OROCHI_FIRE)
                generalBattle.runGeneralBattle()
                count++
            }
        }

        if (!battle) { log("No true orochi found"); return }

        // 真蛇战斗
        log("True Orochi Battle")
        while (true) {
            val img = screenshot() ?: continue
            if (I_ST_CREATE_ROOM.match(img, context).matched) break
            if (appearThenClick(I_UI_CONFIRM, img, 1000)) continue
            if (appearThenClick(I_ST_FIRE, img, 4000)) continue
        }

        // 创建房间并准备
        while (true) {
            val img = screenshot() ?: continue
            if (I_ST_FIRE_PREPARE.match(img, context).matched) break
            if (appearThenClick(I_FIRE, img, 3000, 0.7f)) continue
            if (appearThenClick(I_UI_CONFIRM, img, 1000)) continue
            if (appearThenClick(I_ST_CREATE_ROOM, img, 1000)) continue
        }

        // 战斗准备
        log("Battle prepare")
        uiClick(I_ST_FIRE_PREPARE, I_BUFF)
        while (true) {
            val img = screenshot() ?: continue
            if (!I_BUFF.match(img, context).matched) break
            appearThenClick(I_ST_AUTO_FALSE, img, 1800)
        }

        // 战斗过程
        log("Start battle process")
        val checkTimer = System.currentTimeMillis() + 280_000
        var checkCount = 0
        while (true) {
            val img = screenshot() ?: continue
            if (I_GREED_GHOST.match(img, context).matched) {
                delay(700)
                val frame = screenshot() ?: continue
                if (!I_GREED_GHOST.match(frame, context).matched) continue
                while (true) {
                    val ghost = screenshot() ?: break
                    if (!I_GREED_GHOST.match(ghost, context).matched) break
                    appearThenClick(I_GREED_GHOST, ghost, 1000)
                    appearThenClick(I_ST_FRAME, ghost, 1000)
                }
                break
            }
            appearThenClick(I_ST_FRAME, img, 1000)
            if (System.currentTimeMillis() > checkTimer) {
                if (checkCount > 3) { log("Battle timeout"); break }
                checkCount++; delay(1000)
            }
            delay(500)
        }

        log("Battle process end")
        log("=== 真八岐大蛇完成 ===")
    }

    private suspend fun orochiEnter(): Boolean {
        while (true) {
            val img = screenshot() ?: continue
            if (I_FORM_TEAM.match(img, context).matched) return true
            appearThenClick(I_OROCHI, img, 1000)
        }
    }

    private fun checkTrueOrochi(): Boolean {
        val img = screenshot() ?: return false
        return I_FIND_TS.match(img, context).matched
    }

    private suspend fun checkLayer(layer: String) {
        log("Select layer: $layer")
        delay(500)
    }

    private suspend fun checkLock(lock: Boolean) {
        if (lock) { repeat(20) { val img = screenshot() ?: return; if (I_OROCHI_LOCK.match(img, context).matched) return; appearThenClick(I_OROCHI_UNLOCK, img, 1000) } }
        else { repeat(20) { val img = screenshot() ?: return; if (I_OROCHI_UNLOCK.match(img, context).matched) return; appearThenClick(I_OROCHI_LOCK, img, 1000) } }
    }

    private suspend fun uiClick(clickRule: RuleImage, stopRule: RuleImage) {
        while (true) { val img = screenshot() ?: continue; if (stopRule.match(img, context).matched) break; appearThenClick(clickRule, img, 1000) }
    }

    private suspend fun uiClickUntilDisappear(rule: RuleImage) {
        while (true) { val img = screenshot() ?: break; if (!rule.match(img, context).matched) break; appearThenClick(rule, img, 1000) }
    }
}

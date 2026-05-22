package com.onmyoji.auto.engine.component

import android.content.Context
import android.graphics.Bitmap
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 组队任务通用助手 — 封装所有组队类任务共用的 stub 方法
 *
 * 解决 OrochiTask / EvoZoneTask / FallenSunTask / EternitySeaTask /
 * GoldYoukaiTask / ExperienceYoukaiTask / NianTask / TakoTask / HuntTask 等
 * 任务中重复出现的 isRoomDead / exitRoom / exitTeam / exitBattle /
 * checkAndInvite / checkThenAccept / waitBattle / isHomeOrExplore 等空方法问题。
 *
 * 使用方式：在任务中创建实例，将 stub 方法委托给此助手。
 */
class TeamTaskHelper(
    private val context: Context,
    private val device: DeviceController,
    private val config: TaskConfig
) {
    // ========== 通用资源定义 ==========

    // 匹配中 (房间已解散)
    private val I_MATCHING = RuleImage("matching", "general_invite/gi/gi_matching.png",
        intArrayOf(51, 574, 52, 114), intArrayOf(51, 574, 52, 114), 0.8f)
    // 探索界面标识
    private val I_CHECK_EXPLORATION = RuleImage("check_exploration", "exploration/res_check_exploration.png",
        intArrayOf(640, 500, 100, 100), intArrayOf(640, 500, 100, 100), 0.8f)
    // 庭院标识
    private val I_GI_HOME = RuleImage("gi_home", "general_invite/gi/gi_gi_home.png",
        intArrayOf(361, 34, 34, 46), intArrayOf(361, 34, 34, 46), 0.8f)
    // 探索入口标识
    private val I_GI_EXPLORE = RuleImage("gi_explore", "general_invite/gi/gi_gi_explore.png",
        intArrayOf(1138, 119, 41, 48), intArrayOf(1138, 119, 41, 48), 0.8f)
    // 通用战斗退出
    private val I_EXIT = RuleImage("exit", "general_battle/gb/gb_exit.png",
        intArrayOf(14, 12, 43, 41), intArrayOf(14, 12, 43, 41), 0.8f)
    private val I_EXIT_ENSURE = RuleImage("exit_ensure", "general_battle/gb/gb_exit_ensure.png",
        intArrayOf(674, 388, 135, 63), intArrayOf(674, 388, 135, 63), 0.8f)
    private val I_FALSE = RuleImage("false", "general_battle/gb/gb_false.png",
        intArrayOf(413, 124, 100, 100), intArrayOf(413, 124, 100, 100), 0.8f)

    // 组件引用
    private val generalInvite = GeneralInvite(context, device, config)
    private val generalRoom = GeneralRoom(context, device, config)

    // ========== 房间状态检测 ==========

    /**
     * 判断房间是否已解散
     * 如果在探索界面或匹配中界面，说明房间已解散
     * 双次确认防止误判
     */
    fun isRoomDead(screenshot: () -> Bitmap?): Boolean {
        kotlinx.coroutines.runBlocking { delay(500) }
        val img = screenshot() ?: return false
        if (I_MATCHING.match(img, context).matched || I_CHECK_EXPLORATION.match(img, context).matched) {
            kotlinx.coroutines.runBlocking { delay(500) }
            val img2 = screenshot() ?: return false
            return I_MATCHING.match(img2, context).matched || I_CHECK_EXPLORATION.match(img2, context).matched
        }
        return false
    }

    /**
     * 判断是否在庭院或探索界面
     */
    fun isHomeOrExplore(screenshot: () -> Bitmap?): Boolean {
        val img = screenshot() ?: return false
        return I_GI_HOME.match(img, context).matched || I_GI_EXPLORE.match(img, context).matched
    }

    // ========== 退出操作 ==========

    /**
     * 退出房间
     */
    suspend fun exitRoom() {
        generalInvite.exitRoom()
    }

    /**
     * 退出组队界面
     */
    suspend fun exitTeam() {
        generalRoom.exitTeam()
    }

    /**
     * 退出战斗 — 强制退出正在进行的战斗
     */
    suspend fun exitBattle(screenshot: () -> Bitmap?): Boolean {
        val img = screenshot() ?: return false
        if (!I_EXIT.match(img, context).matched) return false
        // 点击返回
        while (true) {
            val frame = screenshot() ?: continue
            if (appearThenClick(I_EXIT, frame)) continue
            if (appear(I_EXIT_ENSURE, frame)) break
        }
        // 点击返回确认
        while (true) {
            val frame = screenshot() ?: continue
            if (appearThenClick(I_EXIT_ENSURE, frame)) continue
            if (appearThenClick(I_FALSE, frame)) continue
            if (!appear(I_EXIT, frame)) break
        }
        return true
    }

    // ========== 邀请与等待 ==========

    /**
     * 队长战斗后邀请队友
     */
    suspend fun checkAndInvite(defaultInvite: Boolean): Boolean {
        return generalInvite.checkAndInvite(defaultInvite)
    }

    /**
     * 队员接受邀请
     */
    suspend fun checkThenAccept(): Boolean {
        return generalInvite.checkThenAccept()
    }

    /**
     * 在房间等待队长开启战斗
     */
    suspend fun waitBattle(waitTimeSeconds: Int): Boolean {
        return generalInvite.waitBattle(waitTimeSeconds)
    }

    // ========== 通用房间检测 ==========

    /**
     * 通用的组队界面标识检测
     * 传入 formTeam 图片资源，检测是否在房间中
     */
    fun isInRoom(screenshot: () -> Bitmap?, formTeamImage: RuleImage): Boolean {
        val img = screenshot() ?: return false
        return formTeamImage.match(img, context).matched
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

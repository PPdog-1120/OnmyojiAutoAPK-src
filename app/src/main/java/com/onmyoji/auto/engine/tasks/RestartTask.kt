package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.GameUi
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 重启任务 — 应用重启、登录处理、奖励收取
 *
 * 对应 Python tasks/Restart/script_task.py
 * 功能：重启游戏 → 处理登录 → 收取签到/奖励 → 返回庭院
 */
class RestartTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (from RestartAssets) ==========
    // 取消继续战斗
    private val I_CANCEL_BATTLE = RuleImage("cancel_battle", "tasks/Restart/res/res_cancel_battle.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 庭院标识
    private val I_LOGIN_COURTYARD = RuleImage("login_courtyard", "tasks/Restart/res/res_login_courtyard.png",
        intArrayOf(361, 34, 34, 46), intArrayOf(361, 34, 34, 46), 0.8f)
    // 式神录按钮 (庭院标识)
    private val I_MAIN_GOTO_SHIKIGAMI_RECORDS = RuleImage("main_goto_shikigami_records",
        "tasks/Restart/res/res_main_goto_shikigami_records.png",
        intArrayOf(1138, 119, 41, 48), intArrayOf(1138, 119, 41, 48), 0.8f)
    // 下载插画
    private val I_LOGIN_LOAD_DOWN = RuleImage("login_load_down", "tasks/Restart/res/res_login_load_down.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 不观看视频
    private val I_WATCH_VIDEO_CANCEL = RuleImage("watch_video_cancel", "tasks/Restart/res/res_watch_video_cancel.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 红色关闭按钮
    private val I_LOGIN_RED_CLOSE = RuleImage("login_red_close", "tasks/Restart/res/res_login_red_close.png",
        intArrayOf(1200, 30, 50, 50), intArrayOf(1200, 30, 50, 50), 0.8f)
    // 黄色关闭按钮
    private val I_LOGIN_YELLOW_CLOSE = RuleImage("login_yellow_close", "tasks/Restart/res/res_login_yellow_close.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    // 绑定手机号取消
    private val I_LOGIN_CANCEL_BIND_PHONE = RuleImage("login_cancel_bind_phone",
        "tasks/Restart/res/res_login_cancel_bind_phone.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 阴阳师精灵
    private val I_LOGIN_ONMYOJI_GENIE = RuleImage("login_onmyoji_genie",
        "tasks/Restart/res/res_login_onmyoji_genie.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 进入游戏按钮
    private val I_LOGIN_ENTER_GAME = RuleImage("login_enter_game", "tasks/Restart/res/res_login_enter_game.png",
        intArrayOf(540, 600, 200, 60), intArrayOf(540, 600, 200, 60), 0.8f)
    // 签到
    private val I_HARVEST_SIGN = RuleImage("harvest_sign", "tasks/Restart/res/res_harvest_sign.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 签到2
    private val I_HARVEST_SIGN_2 = RuleImage("harvest_sign_2", "tasks/Restart/res/res_harvest_sign_2.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 勾玉
    private val I_HARVEST_JADE = RuleImage("harvest_jade", "tasks/Restart/res/res_harvest_jade.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 体力
    private val I_HARVEST_AP = RuleImage("harvest_ap", "tasks/Restart/res/res_harvest_ap.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 获得奖励
    private val I_UI_AWARD = RuleImage("ui_award", "tasks/Restart/res/res_ui_award.png",
        intArrayOf(547, 518, 172, 96), intArrayOf(547, 518, 172, 96), 0.8f)
    // 确认按钮(小)
    private val I_UI_CONFIRM_SMALL = RuleImage("ui_confirm_small", "tasks/GameUi/res/res_ui_confirm_small.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)
    // 御魂溢确认
    private val I_UI_CONFIRM = RuleImage("ui_confirm", "tasks/GameUi/res/res_ui_confirm.png",
        intArrayOf(672, 405, 169, 55), intArrayOf(672, 405, 169, 55), 0.8f)

    private val gameUi = GameUi(context, device, config)

    override suspend fun run() {
        log("=== 重启任务开始 ===")

        // 应用重启
        appRestart()

        // 设置下次运行时间 (24小时后)
        setNextRun("Restart", success = true)

        log("=== 重启任务完成 ===")
    }

    /**
     * 应用重启 — 停止并重新启动应用，处理登录
     */
    private suspend fun appRestart() {
        log("App restart")
        // 注意：在Android无障碍服务中，无法直接停止/启动应用
        // 这里只处理登录后的情况

        // 处理登录
        appHandleLogin()

        // 收取奖励
        harvest()
    }

    /**
     * 处理登录流程 — 等待进入庭院
     * 对应 Python _app_handle_login
     */
    private suspend fun appHandleLogin(): Boolean {
        log("App login")
        val maxAttempts = 60
        var attempts = 0

        while (attempts < maxAttempts) {
            attempts++
            val img = screenshot() ?: continue

            // 取消继续战斗
            if (appearThenClick(I_CANCEL_BATTLE, img, 800)) {
                log("Cancel continue battle")
                continue
            }

            // 检测到庭院/式神录按钮 → 登录成功
            if (I_MAIN_GOTO_SHIKIGAMI_RECORDS.match(img, context).matched) {
                log("Login success: shikigami records button appears")
                return true
            }

            // 下载插画
            if (appearThenClick(I_LOGIN_LOAD_DOWN, img, 1000)) {
                log("Download inbetweening")
                continue
            }

            // 不观看视频
            if (appearThenClick(I_WATCH_VIDEO_CANCEL, img, 600)) {
                log("Close video")
                continue
            }

            // 红色关闭
            if (appearThenClick(I_LOGIN_RED_CLOSE, img, 600)) {
                log("Close red close")
                continue
            }

            // 黄色关闭
            if (appearThenClick(I_LOGIN_YELLOW_CLOSE, img, 600)) {
                log("Close yellow close")
                continue
            }

            // 绑定手机号取消
            if (appearThenClick(I_LOGIN_CANCEL_BIND_PHONE, img, 1000)) {
                log("Close bind phone")
                continue
            }

            // 阴阳师精灵
            if (appearThenClick(I_LOGIN_ONMYOJI_GENIE, img, 1000)) {
                log("Click onmyoji genie")
                continue
            }

            // 点击进入游戏
            if (appearThenClick(I_LOGIN_ENTER_GAME, img, 3000)) {
                log("Click enter game")
                continue
            }

            delay(1000)
        }

        log("Login timeout after $maxAttempts attempts")
        return false
    }

    /**
     * 收取奖励 — 签到、勾玉、体力等
     * 对应 Python harvest
     */
    private suspend fun harvest() {
        log("Harvest")
        var noRewardCount = 0
        val maxNoReward = 5 // 连续5秒无奖励则退出

        while (noRewardCount < maxNoReward) {
            val img = screenshot() ?: continue
            var found = false

            // 点击获得奖励
            if (appearThenClick(I_UI_AWARD, img, 200)) {
                found = true
            }

            // 签到
            if (appearThenClick(I_HARVEST_SIGN, img, 1500)) {
                log("Sign in")
                found = true
                delay(2000)
                // 等待签到弹窗
                appearThenClick(I_HARVEST_SIGN_2, screenshot(), 1500)
            }

            // 勾玉
            if (appearThenClick(I_HARVEST_JADE, img, 1500)) {
                log("Harvest jade")
                found = true
            }

            // 体力
            if (appearThenClick(I_HARVEST_AP, img, 1000)) {
                log("Harvest AP")
                found = true
            }

            // 御魂溢确认
            if (appearThenClick(I_UI_CONFIRM_SMALL, img, 2500)) {
                log("Soul overflow confirm")
                found = true
            }

            // 红色关闭
            if (appearThenClick(I_LOGIN_RED_CLOSE, img, 2000)) {
                found = true
            }

            if (found) {
                noRewardCount = 0
            } else {
                noRewardCount++
                delay(1000)
            }
        }

        log("No more reward")
    }
}

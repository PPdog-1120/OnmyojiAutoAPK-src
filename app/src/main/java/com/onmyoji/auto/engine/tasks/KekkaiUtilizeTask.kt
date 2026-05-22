package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.engine.component.GameUi
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 结界蹭卡任务 — 在好友结界寄养式神
 *
 * 对应 Python tasks/KekkaiUtilize/script_task.py
 * 流程：进入寮结界 → 进入育成 → 蹭卡 → 检查满级 → 收取奖励 → 返回庭院
 */
class KekkaiUtilizeTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 寮结界入口
    private val I_REALM_SHIN = RuleImage("realm_shin", "tasks/KekkaiUtilize/res/res_realm_shin.png",
        intArrayOf(1100, 300, 100, 100), intArrayOf(1100, 300, 100, 100), 0.8f)
    // 育成界面
    private val I_SHI_GROWN = RuleImage("shi_grown", "tasks/KekkaiUtilize/res/res_shi_grown.png",
        intArrayOf(1100, 400, 100, 100), intArrayOf(1100, 400, 100, 100), 0.8f)
    // 蹭卡加号
    private val I_UTILIZE_ADD = RuleImage("utilize_add", "tasks/KekkaiUtilize/res/res_utilize_add.png",
        intArrayOf(640, 400, 80, 80), intArrayOf(640, 400, 80, 80), 0.8f)
    // 满级标识
    private val I_RS_LEVEL_MAX = RuleImage("rs_level_max", "tasks/KekkaiUtilize/res/res_rs_level_max.png",
        intArrayOf(640, 300, 100, 30), intArrayOf(640, 300, 100, 30), 0.8f)
    // 蹭卡收获
    private val I_UTILIZE_HARVEST = RuleImage("utilize_harvest", "tasks/KekkaiUtilize/res/res_utilize_harvest.png",
        intArrayOf(640, 500, 100, 100), intArrayOf(640, 500, 100, 100), 0.8f)
    // 体力盒子
    private val I_BOX_AP = RuleImage("box_ap", "tasks/KekkaiUtilize/res/res_box_ap.png",
        intArrayOf(300, 400, 80, 80), intArrayOf(300, 400, 80, 80), 0.8f)
    // 经验盒子
    private val I_BOX_EXP = RuleImage("box_exp", "tasks/KekkaiUtilize/res/res_box_exp.png",
        intArrayOf(500, 400, 80, 80), intArrayOf(500, 400, 80, 80), 0.8f)
    // 返回按钮
    private val I_UI_BACK_RED = RuleImage("ui_back_red", "tasks/GameUi/res/res_ui_back_red.png",
        intArrayOf(12, 12, 54, 54), intArrayOf(12, 12, 54, 54), 0.8f)
    // 寮资金/体力收取
    private val I_GUILD_AP = RuleImage("guild_ap", "tasks/KekkaiUtilize/res/res_guild_ap.png",
        intArrayOf(640, 400, 100, 100), intArrayOf(640, 400, 100, 100), 0.8f)
    private val I_GUILD_ASSETS = RuleImage("guild_assets", "tasks/KekkaiUtilize/res/res_guild_assets.png",
        intArrayOf(640, 400, 100, 100), intArrayOf(640, 400, 100, 100), 0.8f)

    private val gameUi = GameUi(context, device, config)

    override suspend fun run() {
        log("=== 结界蹭卡任务开始 ===")

        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_guild")

        // 进入寮结界
        gotoRealm()

        // 检查蹭卡
        checkUtilizeAdd()

        // 检查满级式神
        checkMaxLv()

        // 检查蹭卡收获
        checkUtilizeHarvest()

        // 收盒子
        checkBox()

        // 收取寮资金和体力
        receiveGuildApOrAssets()

        // 返回庭院
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_main")

        // 设置下次运行时间 (蹭卡完成后1小时再检查)
        val nextRun = System.currentTimeMillis() + 60 * 60 * 1000L
        setNextRun(nextRun)

        log("=== 结界蹭卡任务完成 ===")
    }

    /**
     * 进入寮结界
     */
    private suspend fun gotoRealm() {
        log("Enter guild realm")
        var attempts = 0
        while (attempts < 20) {
            val img = screenshot() ?: continue
            if (I_REALM_SHIN.match(img, context).matched || I_SHI_GROWN.match(img, context).matched) {
                log("In guild realm")
                return
            }
            if (appearThenClick(I_REALM_SHIN, img, 2000)) {
                attempts++
                continue
            }
            delay(1000)
            attempts++
        }
    }

    /**
     * 检查蹭卡 — 如果有空位就蹭卡
     */
    private suspend fun checkUtilizeAdd() {
        log("Check utilize add")
        // 进入育成界面
        var attempts = 0
        while (attempts < 10) {
            val img = screenshot() ?: continue
            if (I_SHI_GROWN.match(img, context).matched) break
            if (appearThenClick(I_SHI_GROWN, img, 1000)) {
                attempts++
                continue
            }
            delay(500)
            attempts++
        }

        // 检查是否有蹭卡加号
        val img = screenshot()
        if (img != null && I_UTILIZE_ADD.match(img, context).matched) {
            log("Found utilize add slot")
            // 点击加号进入好友列表
            appearThenClick(I_UTILIZE_ADD, img, 2000)
            // 等待好友列表加载
            delay(2000)
            // 选择第一个好友 (简化实现)
            device.click(640, 300)
            delay(2000)
            // 确认
            device.click(640, 500)
            delay(1000)
            log("Utilize added")
        } else {
            log("No utilize slot available")
        }
    }

    /**
     * 检查满级式神
     */
    private suspend fun checkMaxLv() {
        log("Check max level")
        val img = screenshot() ?: return
        if (I_RS_LEVEL_MAX.match(img, context).matched) {
            log("Found max level shikigami, replacing...")
            // 点击满级标识进入替换界面
            appearThenClick(I_RS_LEVEL_MAX, img, 2000)
            delay(1000)
            // 选择新的式神 (简化：点击第一个未上阵的)
            device.click(300, 300)
            delay(1000)
            // 确认替换
            device.click(640, 500)
            delay(1000)
        }
    }

    /**
     * 检查蹭卡收获
     */
    private suspend fun checkUtilizeHarvest() {
        log("Check utilize harvest")
        var attempts = 0
        while (attempts < 5) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_UTILIZE_HARVEST, img, 2000)) {
                log("Harvested utilize reward")
                delay(1000)
                attempts++
                continue
            }
            break
        }
    }

    /**
     * 收体力/经验盒子
     */
    private suspend fun checkBox() {
        log("Check box")
        val img = screenshot() ?: return
        // 收体力盒子
        if (appearThenClick(I_BOX_AP, img, 2000)) {
            log("Harvested AP box")
            delay(1000)
        }
        // 收经验盒子
        val img2 = screenshot() ?: return
        if (appearThenClick(I_BOX_EXP, img2, 2000)) {
            log("Harvested EXP box")
            delay(1000)
        }
    }

    /**
     * 收取寮资金和体力
     */
    private suspend fun receiveGuildApOrAssets() {
        log("Receive guild AP or assets")
        // 回到寮主界面
        backToRealm()
        gameUi.uiGetCurrentPage()
        gameUi.uiGoto("page_guild")

        var attempts = 0
        while (attempts < 3) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_GUILD_AP, img, 2000)) {
                log("Harvested guild AP")
                delay(1000)
            }
            val img2 = screenshot() ?: continue
            if (appearThenClick(I_GUILD_ASSETS, img2, 2000)) {
                log("Harvested guild assets")
                delay(1000)
            }
            attempts++
            delay(1000)
        }
    }

    /**
     * 返回结界主界面
     */
    private suspend fun backToRealm() {
        var attempts = 0
        while (attempts < 10) {
            val img = screenshot() ?: continue
            if (I_REALM_SHIN.match(img, context).matched || I_SHI_GROWN.match(img, context).matched) {
                return
            }
            if (appearThenClick(I_UI_BACK_RED, img, 1000)) {
                attempts++
                continue
            }
            delay(500)
            attempts++
        }
    }
}

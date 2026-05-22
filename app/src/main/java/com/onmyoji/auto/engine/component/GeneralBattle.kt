package com.onmyoji.auto.engine.component

import android.content.Context
import android.graphics.Bitmap
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 通用战斗组件 — 对应 OAS general_battle.py
 *
 * 使用这个通用的战斗必须要求这个任务的 config 有 GeneralBattleConfig
 * 继承关系: GeneralBattle -> GeneralBuff -> BaseTask
 * Kotlin 中使用组合: GeneralBattle 包含 GeneralBuff 实例
 */
open class GeneralBattle(
    context: Context,
    device: DeviceController,
    config: TaskConfig,
    private val battleConfig: GeneralBattleConfig = GeneralBattleConfig()
) : BaseTask(context, device, config) {

    // ========== 资源定义 (GeneralBattleAssets) ==========

    // Click Rule Assets
    private val C_WIN_1 = RuleClick("win_1", intArrayOf(175, 102, 1054, 99))
    private val C_WIN_2 = RuleClick("win_2", intArrayOf(22, 112, 210, 496))
    private val C_WIN_3 = RuleClick("win_3", intArrayOf(1059, 114, 206, 468))
    private val C_REWARD_1 = RuleClick("reward_1", intArrayOf(606, 603, 325, 87))
    private val C_REWARD_2 = RuleClick("reward_2", intArrayOf(25, 134, 224, 472))
    private val C_REWARD_3 = RuleClick("reward_3", intArrayOf(1092, 156, 168, 437))

    // 预设队伍
    private val C_PRESET_TEAM_1 = RuleClick("preset_team_1", intArrayOf(195, 235, 465, 110))
    private val C_PRESET_TEAM_2 = RuleClick("preset_team_2", intArrayOf(195, 355, 465, 110))
    private val C_PRESET_TEAM_3 = RuleClick("preset_team_3", intArrayOf(195, 475, 465, 110))
    private val C_PRESET_TEAM_4 = RuleClick("preset_team_4", intArrayOf(195, 595, 465, 35))

    // 预设组
    private val C_PRESET_GROUP_1 = RuleClick("preset_group_1", intArrayOf(35, 240, 25, 50))
    private val C_PRESET_GROUP_2 = RuleClick("preset_group_2", intArrayOf(35, 305, 25, 50))
    private val C_PRESET_GROUP_3 = RuleClick("preset_group_3", intArrayOf(35, 365, 25, 50))
    private val C_PRESET_GROUP_4 = RuleClick("preset_group_4", intArrayOf(35, 430, 25, 50))
    private val C_PRESET_GROUP_5 = RuleClick("preset_group_5", intArrayOf(35, 495, 25, 50))
    private val C_PRESET_GROUP_6 = RuleClick("preset_group_6", intArrayOf(35, 555, 25, 50))
    private val C_PRESET_GROUP_7 = RuleClick("preset_group_7", intArrayOf(35, 615, 25, 50))

    // 绿标
    private val C_GREEN_LEFT_1 = RuleClick("green_left_1", intArrayOf(183, 506, 125, 108))
    private val C_GREEN_LEFT_2 = RuleClick("green_left_2", intArrayOf(407, 474, 100, 100))
    private val C_GREEN_LEFT_3 = RuleClick("green_left_3", intArrayOf(608, 446, 64, 44))
    private val C_GREEN_LEFT_4 = RuleClick("green_left_4", intArrayOf(769, 475, 81, 98))
    private val C_GREEN_LEFT_5 = RuleClick("green_left_5", intArrayOf(932, 473, 132, 108))
    private val C_GREEN_MAIN = RuleClick("green_main", intArrayOf(565, 555, 104, 81))
    private val C_RANDOM_CLICK = RuleClick("random_click", intArrayOf(104, 79, 1050, 507))

    // Image Rule Assets
    private val I_REWARD = RuleImage("reward",
        "general_battle/gb/gb_reward.png",
        intArrayOf(547, 518, 172, 96), intArrayOf(547, 518, 172, 96), 0.8f)
    private val I_PRESET = RuleImage("preset",
        "general_battle/gb/gb_preset.png",
        intArrayOf(32, 650, 47, 45), intArrayOf(32, 650, 47, 45), 0.8f)
    private val I_PREPARE_HIGHLIGHT = RuleImage("prepare_highlight",
        "general_battle/gb/gb_prepare_highlight.png",
        intArrayOf(1128, 536, 100, 100), intArrayOf(1110, 500, 169, 200), 0.8f)
    private val I_WIN = RuleImage("win",
        "general_battle/gb/gb_win.png",
        intArrayOf(385, 47, 100, 100), intArrayOf(296, 33, 414, 224), 0.8f)
    private val I_PREPARE_DARK = RuleImage("prepare_dark",
        "general_battle/gb/gb_prepare_dark.png",
        intArrayOf(1131, 538, 100, 100), intArrayOf(1131, 538, 100, 100), 0.8f)
    private val I_FALSE = RuleImage("false",
        "general_battle/gb/gb_false.png",
        intArrayOf(413, 124, 100, 100), intArrayOf(413, 124, 100, 100), 0.8f)
    private val I_PRESET_ENSURE = RuleImage("preset_ensure",
        "general_battle/gb/gb_preset_ensure.png",
        intArrayOf(352, 643, 141, 50), intArrayOf(305, 625, 236, 83), 0.8f)
    private val I_BUFF = RuleImage("buff",
        "general_battle/gb/gb_buff.png",
        intArrayOf(115, 670, 39, 36), intArrayOf(107, 668, 55, 49), 0.7f)
    private val I_LOCAL = RuleImage("local",
        "general_battle/gb/gb_local.png",
        intArrayOf(25, 563, 30, 34), intArrayOf(25, 563, 30, 34), 0.8f)
    private val I_EXIT = RuleImage("exit",
        "general_battle/gb/gb_exit.png",
        intArrayOf(14, 12, 43, 41), intArrayOf(14, 12, 43, 41), 0.8f)
    private val I_EXIT_ENSURE = RuleImage("exit_ensure",
        "general_battle/gb/gb_exit_ensure.png",
        intArrayOf(674, 388, 135, 63), intArrayOf(674, 388, 135, 63), 0.8f)
    private val I_FRIENDS = RuleImage("friends",
        "general_battle/gb/gb_friends.png",
        intArrayOf(89, 14, 36, 36), intArrayOf(89, 14, 36, 36), 0.8f)
    private val I_REWARD_GOLD = RuleImage("reward_gold",
        "general_battle/gb/gb_reward_gold.png",
        intArrayOf(268, 178, 97, 69), intArrayOf(254, 163, 797, 261), 0.8f)
    private val I_DE_WIN = RuleImage("de_win",
        "general_battle/gb/gb_de_win.png",
        intArrayOf(472, 49, 100, 100), intArrayOf(239, 36, 399, 133), 0.8f)
    private val I_PRESENT_LESS_THAN_5 = RuleImage("present_less_than_5",
        "general_battle/gb/gb_present_less_than_5.png",
        intArrayOf(222, 648, 418, 43), intArrayOf(222, 648, 418, 43), 0.8f)
    private val I_BATTLE_INFO = RuleImage("battle_info",
        "general_battle/gb/gb_battle_info.png",
        intArrayOf(230, 12, 37, 39), intArrayOf(210, 1, 147, 72), 0.8f)
    private val I_CONFIRM_CLOSE_DIFF_SOUL = RuleImage("confirm_close_diff_soul",
        "general_battle/gb/gb_confirm_close_diff_soul.png",
        intArrayOf(571, 404, 135, 54), intArrayOf(517, 374, 226, 112), 0.8f)
    private val I_DISABLE_7DAYS_DIFF_SOUL = RuleImage("disable_7days_diff_soul",
        "general_battle/gb/gb_disable_7days_diff_soul.png",
        intArrayOf(547, 342, 27, 36), intArrayOf(524, 325, 70, 71), 0.8f)
    private val I_PRESET_WIT_NUMBER = RuleImage("preset_wit_number",
        "general_battle/gb/gb_preset_wit_number.png",
        intArrayOf(40, 655, 37, 37), intArrayOf(9, 636, 100, 74), 0.8f)

    // Swipe Rule Assets
    private val S_BATTLE_RANDOM_LEFT = RuleSwipe("battle_random_left", 122, 155, 667, 147)
    private val S_BATTLE_RANDOM_RIGHT = RuleSwipe("battle_random_right", 719, 138, 237, 163)

    // Buff 组件（组合方式）
    protected val buffComponent = GeneralBuff(context, device, config)

    // Boss 标记标志
    private var bossMarkFlag = false

    override suspend fun run() {
        // 通用战斗组件不直接 run，由子任务调用
        log("GeneralBattle: 请在子任务中调用 runGeneralBattle()")
    }

    /**
     * 运行通用战斗 — 对应 OAS run_general_battle
     *
     * @param config 战斗配置
     * @param buff 需要开启的 buff 列表
     * @return true: 胜利, false: 失败
     */
    suspend fun runGeneralBattle(
        config: GeneralBattleConfig? = null,
        buff: List<BuffClass>? = null
    ): Boolean {
        val cfg = config ?: GeneralBattleConfig()
        log("General battle start")
        // 本人选择的策略是只要进来了就算一次，不管是不是打完了
        // 战斗统计
        currentCount++
        log("Current count: $currentCount")

        // 战前设置
        battleBefore(buff, cfg)

        // 绿标
        if (isInBattle(false)) {
            greenMark(cfg.greenEnable, cfg.greenMark)
        }

        // 战中设置
        val win = battleWait(cfg.randomClickSwipeEnable)
        return win
    }

    /**
     * 战斗前设置 — 对应 OAS battle_before
     *
     * @return true: 进入战斗或点击了准备按钮且识别不到准备按钮了
     *         false: 超过 timeout 还没有进入战斗且没有点击过准备
     */
    private suspend fun battleBefore(
        buff: List<BuffClass>?,
        config: GeneralBattleConfig,
        timeout: Long = 5000
    ): Boolean {
        val deadline = System.currentTimeMillis() + timeout
        var confed = false
        while (System.currentTimeMillis() < deadline) {
            val img = screenshot() ?: continue
            // 战斗阶段
            if (isInRealBattle(false, img)) return true

            // 关闭御魂不一致提示
            if (appearThenClick(I_DISABLE_7DAYS_DIFF_SOUL, img, 600)) continue
            // 确认关闭御魂不一致提示
            if (appearThenClick(I_CONFIRM_CLOSE_DIFF_SOUL, img, 600)) continue

            // 战斗准备阶段
            if (isInPrepare(false, img)) {
                if (!config.lockTeamEnable) { // 没有锁定阵容
                    if (currentCount == 1 && !confed) { // 第一次战斗且是本次第一次配置
                        switchPresetTeam(config.presetEnable, config.presetGroup, config.presetTeam)
                        checkAndOpenBuff(buff)
                        confed = true
                    }
                }
                // 点击准备(锁定阵容自动点准备,不锁定阵容前面也已经配置完毕需要点准备)
                if (appearThenClick(I_PREPARE_HIGHLIGHT, img, 800)) continue
                continue
            }
            // 未知界面, 既不是准备界面也不是战斗界面
            log("Wait for preparation page")
            delay((400..800).random())
        }
        return false
    }

    /**
     * 进入挑战然后直接返回 — 对应 OAS run_general_battle_back
     */
    suspend fun runGeneralBattleBack(
        config: GeneralBattleConfig? = null,
        exitFour: Boolean = false
    ): Boolean {
        val cfg = config ?: GeneralBattleConfig()

        // 如果没有锁定队伍那么在点击准备后才退出的,退四的话就直接退出
        if (!cfg.lockTeamEnable && !exitFour) {
            // 点击准备按钮
            waitUntilAppear(I_PREPARE_HIGHLIGHT)
            while (true) {
                val img = screenshot() ?: continue
                if (appearThenClick(I_PREPARE_HIGHLIGHT, img, 1500)) continue
                if (!(appear(I_PRESET, img) || appear(I_PRESET_WIT_NUMBER, img))) break
            }
            log("Click prepare_highlight")
        }

        // 点击返回
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_EXIT, img, 1500)) continue
            if (appear(I_EXIT_ENSURE, img)) break
        }
        log("Click exit")

        // 点击返回确认
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_EXIT_ENSURE, img, 1500)) continue
            if (appear(I_FALSE, img)) break
        }
        log("Click exit_ensure")

        // 点击失败确认
        waitUntilAppear(I_FALSE)
        while (true) {
            val img = screenshot() ?: continue
            if (appearThenClick(I_FALSE, img, 1500)) continue
            if (!appear(I_FALSE, img)) break
        }
        log("Click false")

        return true
    }

    /**
     * 在战斗的时候强制退出战斗 — 对应 OAS exit_battle
     */
    suspend fun exitBattle(skipFirst: Boolean = false): Boolean {
        if (skipFirst) screenshot()

        val img = screenshot() ?: return false
        if (!appear(I_EXIT, img)) return false

        // 点击返回
        log("Click exit")
        while (true) {
            val frame = screenshot() ?: continue
            if (appearThenClick(I_EXIT, frame, 1500)) continue
            if (appear(I_EXIT_ENSURE, frame)) break
        }

        // 点击返回确认
        while (true) {
            val frame = screenshot() ?: continue
            if (appearThenClick(I_EXIT_ENSURE, frame, 1500)) continue
            if (appearThenClick(I_FALSE, frame, 1500)) continue
            if (!appear(I_EXIT, frame)) break
        }

        return true
    }

    /**
     * 等待战斗结束 — 对应 OAS battle_wait
     *
     * 很重要 这个函数是原先写的，优化版本在 tasks/Secret/script_task 下。
     * 本着不改动原先的代码的原则，所以就不改了
     */
    private suspend fun battleWait(randomClickSwipeEnable: Boolean): Boolean {
        // 战斗过程 随机点击和滑动 防封
        log("Start battle process")
        var win = false

        while (true) {
            val img = screenshot() ?: continue

            // 如果出现赢 就点击, 第二个是针对封魔的图片
            if (appear(I_WIN, img, 0.8f) || appear(I_DE_WIN, img)) {
                log("Battle result is win")
                if (appear(I_DE_WIN, img)) {
                    uiClickUntilDisappear(I_DE_WIN)
                }
                win = true
                break
            }

            // 如果出现失败 就点击，返回 false
            if (appear(I_FALSE, img, 0.8f)) {
                log("Battle result is false")
                win = false
                break
            }

            // 如果领奖励
            if (appear(I_REWARD, img, 0.6f)) {
                win = true
                break
            }

            // 如果领奖励出现金币
            if (appear(I_REWARD_GOLD, img, 0.8f)) {
                win = true
                break
            }

            // 如果开启战斗过程随机滑动
            if (randomClickSwipeEnable) {
                randomClickSwipe()
            }
        }

        // 再次确认战斗结果
        log("Reconfirm the results of the battle")
        while (true) {
            val img = screenshot() ?: continue
            if (win) {
                // 点击赢了
                val actionClick = listOf(C_WIN_1, C_WIN_2, C_WIN_3).random()
                if (appearThenClick(I_WIN, img, 500, actionClick)) continue
                if (!appear(I_WIN, img)) break
            } else {
                // 如果失败且 点击失败后
                if (appearThenClick(I_FALSE, img, 600f)) continue
                if (!appear(I_FALSE, img, 600f)) return false
            }
        }

        // 最后保证能点击 获得奖励
        if (!waitUntilAppear(I_REWARD)) {
            // 有些的战斗没有下面的奖励，所以直接返回
            log("There is no reward, Exit battle")
            return win
        }

        log("Get reward")
        while (true) {
            val img = screenshot() ?: continue
            // 如果出现领奖励
            val actionClick = listOf(C_REWARD_1, C_REWARD_2, C_REWARD_3).random()
            if (appearThenClick(I_REWARD, img, 1500, actionClick) ||
                appearThenClick(I_REWARD_GOLD, img, 1500, actionClick)) {
                continue
            }
            if (!appear(I_REWARD, img) && !appear(I_REWARD_GOLD, img)) {
                break
            }
        }

        return win
    }

    /**
     * 绿标 — 对应 OAS green_mark
     * 如果不使能就直接返回
     */
    private suspend fun greenMark(
        enable: Boolean = false,
        markMode: GreenMarkType = GreenMarkType.GREEN_MAIN
    ) {
        if (!enable) return

        log("Green is enable")
        val (x, y) = when (markMode) {
            GreenMarkType.GREEN_LEFT1 -> { log("Green left 1"); C_GREEN_LEFT_1.coord() }
            GreenMarkType.GREEN_LEFT2 -> { log("Green left 2"); C_GREEN_LEFT_2.coord() }
            GreenMarkType.GREEN_LEFT3 -> { log("Green left 3"); C_GREEN_LEFT_3.coord() }
            GreenMarkType.GREEN_LEFT4 -> { log("Green left 4"); C_GREEN_LEFT_4.coord() }
            GreenMarkType.GREEN_LEFT5 -> { log("Green left 5"); C_GREEN_LEFT_5.coord() }
            GreenMarkType.GREEN_MAIN -> { log("Green main"); C_GREEN_MAIN.coord() }
        }

        // 等待那个准备的消失
        while (true) {
            val img = screenshot() ?: continue
            if (!appear(I_PREPARE_HIGHLIGHT, img)) break
        }

        // 判断有无坐标的偏移
        appearThenClick(I_LOCAL)
        delay(300)
        // 点击绿标
        device.click(x, y)
    }

    /**
     * 切换预设的队伍 — 对应 OAS switch_preset_team
     * 要求是在不锁定队伍时的情况下
     */
    private suspend fun switchPresetTeam(
        enable: Boolean = false,
        presetGroup: Int = 1,
        presetTeam: Int = 1
    ) {
        if (!enable) {
            log("Preset is disable")
            return
        }

        log("Preset is enable")
        // 点击预设按钮
        while (true) {
            val img = screenshot() ?: continue
            if (appear(I_PRESET_ENSURE, img)) break
            // 首个队伍没有满足5个式神，未出现预设按钮的情况下跳出循环
            if (appear(I_PRESENT_LESS_THAN_5, img)) break
            if (appearThenClick(I_PRESET, img, 1000, threshold = 0.8f)) continue
            if (appearThenClick(I_PRESET_WIT_NUMBER, img, 1000, threshold = 0.8f)) continue
        }
        log("Click preset button")

        // 选择预设组
        val groupClick = getPresetGroupClick(presetGroup)
        // 考虑到有些预设组没有预设，所以这里取一个比较固定的颜色
        // 简化: 点击两次确保选中
        for (i in 0 until 2) {
            val (gx, gy) = groupClick.coord()
            device.click(gx, gy)
            delay(500)
        }
        log("Select preset group")

        // 选择预设的队伍
        delay(500)
        val teamClick = getPresetTeamClick(presetTeam)
        for (i in 0 until 3) {
            delay(800)
            val img = screenshot() ?: continue
            if (appear(I_PRESET_ENSURE, img)) {
                while (true) {
                    val frame = screenshot() ?: break
                    device.click(teamClick.coord().first, teamClick.coord().second)
                    delay(300)
                    if (!appear(I_PRESET_ENSURE, frame)) break
                }
                continue
            }
            val (tx, ty) = teamClick.coord()
            device.click(tx, ty)
            delay(300)
        }

        // 点击预设确认
        waitUntilAppear(I_PRESET_ENSURE, 1000)
        while (true) {
            val img = screenshot() ?: continue
            if (!appear(I_PRESET_ENSURE, img)) break
            if (appearThenClick(I_PRESET_ENSURE, img, 200, threshold = 0.8f)) continue
        }
        log("Click preset ensure")
    }

    /**
     * 随机点击或滑动 — 对应 OAS random_click_swipt
     */
    private suspend fun randomClickSwipe() {
        if ((0..500).random() <= 3) { // 约 0.6% 的概率
            when ((0..2).random()) {
                0 -> {
                    val (cx, cy) = C_RANDOM_CLICK.coord()
                    device.click(cx, cy)
                    delay(20000)
                }
                1 -> {
                    device.swipe(S_BATTLE_RANDOM_LEFT.startX, S_BATTLE_RANDOM_LEFT.startY,
                        S_BATTLE_RANDOM_LEFT.endX, S_BATTLE_RANDOM_LEFT.endY)
                    delay(20000)
                }
                2 -> {
                    device.swipe(S_BATTLE_RANDOM_RIGHT.startX, S_BATTLE_RANDOM_RIGHT.startY,
                        S_BATTLE_RANDOM_RIGHT.endX, S_BATTLE_RANDOM_RIGHT.endY)
                    delay(20000)
                }
            }
        } else {
            delay(400)
        }
    }

    /**
     * 判断是否在战斗中 — 对应 OAS is_in_battle
     * tip: 因为有 friends 判别, 所以即使在准备界面也会识别在战斗中
     */
    fun isInBattle(isScreenshot: Boolean = true, existingImg: Bitmap? = null): Boolean {
        val img = if (isScreenshot) screenshot() else existingImg ?: return false
        return appear(I_BATTLE_INFO, img) ||
                appear(I_FRIENDS, img) ||
                appear(I_WIN, img) ||
                appear(I_FALSE, img) ||
                appear(I_REWARD, img)
    }

    /**
     * 判断是否在真正的战斗中(不是战斗准备界面也不是战斗结束界面)
     */
    fun isInRealBattle(isScreenshot: Boolean = true, existingImg: Bitmap? = null): Boolean {
        val img = if (isScreenshot) screenshot() else existingImg ?: return false
        return appear(I_BATTLE_INFO, img)
    }

    /**
     * 判断是否在准备中 — 对应 OAS is_in_prepare
     */
    fun isInPrepare(isScreenshot: Boolean = true, existingImg: Bitmap? = null): Boolean {
        val img = if (isScreenshot) screenshot() else existingImg ?: return false
        return appear(I_BUFF, img) ||
                appear(I_PREPARE_HIGHLIGHT, img) ||
                appear(I_PREPARE_DARK, img) ||
                appear(I_PRESET, img) ||
                appear(I_PRESET_WIT_NUMBER, img)
    }

    /**
     * 中途接入战斗，并且接管 — 对应 OAS check_take_over_battle
     * @return 赢了返回 true，输了返回 false，不是在战斗中返回 null
     */
    suspend fun checkTakeOverBattle(
        isScreenshot: Boolean,
        config: GeneralBattleConfig? = null
    ): Boolean? {
        val img = if (isScreenshot) screenshot() else screenshot() ?: return null
        if (!isInBattle(false, img)) return null
        return runGeneralBattle(config)
    }

    /**
     * 检测是否锁定队伍 — 对应 OAS check_lock
     */
    suspend fun checkLock(enable: Boolean, lockImage: RuleImage, unlockImage: RuleImage) {
        if (enable) {
            log("Lock team")
            while (true) {
                val img = screenshot() ?: continue
                if (appear(lockImage, img)) break
                if (appearThenClick(unlockImage, img, 1000)) continue
            }
        } else {
            log("Unlock team")
            while (true) {
                val img = screenshot() ?: continue
                if (appear(unlockImage, img)) break
                if (appearThenClick(lockImage, img, 1000)) continue
            }
        }
    }

    /**
     * 检测是否开启 buff — 对应 OAS check_and_open_buff
     */
    private suspend fun checkAndOpenBuff(buff: List<BuffClass>?) {
        if (buff.isNullOrEmpty()) return
        log("Open buff $buff")
        uiClick(I_BUFF, buffComponent.I_CLOUD, 2000)
        for (b in buff) {
            when (b) {
                BuffClass.AWAKE -> buffComponent.awake(true)
                BuffClass.SOUL -> buffComponent.soul(true)
                BuffClass.GOLD_50 -> buffComponent.gold50(true)
                BuffClass.GOLD_100 -> buffComponent.gold100(true)
                BuffClass.EXP_50 -> buffComponent.exp50(true)
                BuffClass.EXP_100 -> buffComponent.exp100(true)
                BuffClass.AWAKE_CLOSE -> buffComponent.awake(false)
                BuffClass.SOUL_CLOSE -> buffComponent.soul(false)
                BuffClass.GOLD_50_CLOSE -> buffComponent.gold50(false)
                BuffClass.GOLD_100_CLOSE -> buffComponent.gold100(false)
                BuffClass.EXP_50_CLOSE -> buffComponent.exp50(false)
                BuffClass.EXP_100_CLOSE -> buffComponent.exp100(false)
            }
            delay(100)
        }
        log("Open buff success")
        while (true) {
            val img = screenshot() ?: continue
            if (!appear(buffComponent.I_CLOUD, img)) break
            if (appearThenClick(I_BUFF, img, 1000)) continue
        }
    }

    /**
     * Boss 标记 — 对应 OAS boss_mark
     */
    suspend fun bossMark(enable: Boolean = true): Boolean {
        if (!enable || bossMarkFlag) return false
        // 简化: 跳过 OCR 检测，直接返回
        bossMarkFlag = true
        log("Boss mark skipped (simplified)")
        return false
    }

    /**
     * 重置 Boss 标记标志
     */
    fun bossMarkReset() {
        bossMarkFlag = false
    }

    // ========== 辅助方法 ==========

    private fun getPresetGroupClick(group: Int): RuleClick = when (group) {
        1 -> C_PRESET_GROUP_1
        2 -> C_PRESET_GROUP_2
        3 -> C_PRESET_GROUP_3
        4 -> C_PRESET_GROUP_4
        5 -> C_PRESET_GROUP_5
        6 -> C_PRESET_GROUP_6
        7 -> C_PRESET_GROUP_7
        else -> C_PRESET_GROUP_1
    }

    private fun getPresetTeamClick(team: Int): RuleClick = when (team) {
        1 -> C_PRESET_TEAM_1
        2 -> C_PRESET_TEAM_2
        3 -> C_PRESET_TEAM_3
        4 -> C_PRESET_TEAM_4
        else -> C_PRESET_TEAM_1
    }

    /**
     * 出现则点击（带 action 点击位置）
     */
    private suspend fun appearThenClick(
        rule: RuleImage,
        screenshot: Bitmap? = null,
        interval: Long = 1000,
        action: RuleClick? = null,
        threshold: Float = 0.8f
    ): Boolean {
        val img = screenshot ?: this.screenshot() ?: return false
        val result = rule.match(img, context)
        if (result.matched) {
            if (action != null) {
                val (ax, ay) = action.coord()
                device.click(ax, ay)
            } else {
                device.click(result.centerX, result.centerY)
            }
            delay(interval)
            return true
        }
        return false
    }

    /**
     * 出现则点击（Float threshold 版本）
     */
    private suspend fun appearThenClick(
        rule: RuleImage,
        screenshot: Bitmap,
        threshold: Float
    ): Boolean {
        val result = rule.match(screenshot, context)
        if (result.matched) {
            device.click(result.centerX, result.centerY)
            delay(500)
            return true
        }
        return false
    }

    /**
     * 判断目标是否出现
     */
    private fun appear(rule: RuleImage, img: Bitmap, threshold: Float = 0.8f): Boolean {
        return rule.match(img, context).matched
    }

    /**
     * 点击直到消失
     */
    private suspend fun uiClickUntilDisappear(rule: RuleImage) {
        while (true) {
            val img = screenshot() ?: break
            if (!rule.match(img, context).matched) break
            appearThenClick(rule, img)
        }
    }

    /**
     * 点击 A 直到出现 B
     */
    private suspend fun uiClick(
        clickRule: RuleImage,
        stopRule: RuleImage,
        interval: Long = 1000
    ) {
        while (true) {
            val img = screenshot() ?: continue
            if (stopRule.match(img, context).matched) return
            appearThenClick(clickRule, img, interval)
        }
    }
}

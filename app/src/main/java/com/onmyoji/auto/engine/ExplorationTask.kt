package com.onmyoji.auto.engine

import kotlinx.coroutines.delay
import android.content.Context
import com.onmyoji.auto.engine.component.GameUi
import com.onmyoji.auto.model.TaskConfig

/**
 * 探索任务 — 修复版
 *
 * 改动点：
 * 1. 继承 GameUi 获得页面导航能力（BFS 最短路径）
 * 2. navigateToExploration() 改为主动导航，而非被动等待
 * 3. 新增 UNKNOWN 场景处理（随机点击恢复）
 * 4. 新增 preProcess() / postProcess()
 */
class ExplorationTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : GameUi(context, device, config) {

    // ========== 资源定义 ==========
    // 场景识别（探索大世界标识 — 含标题栏）
    private val I_CHECK_EXPLORATION_WORLD = RuleImage("check_exploration_world",
        "exploration/res_exploration_title.png",
        intArrayOf(1133, 124, 47, 43), intArrayOf(1100, 100, 180, 100), 0.7f)

    // 探索按钮（章节列表中的"探索"入口）
    private val I_EXPLORATION_CLICK = RuleImage("exploration_click",
        "exploration/res_e_exploration_click.png",
        intArrayOf(1076, 601, 96, 42), intArrayOf(939, 555, 307, 127), 0.8f)

    // 设置按钮
    private val I_SETTINGS_BUTTON = RuleImage("settings_button",
        "exploration/res_e_settings_button.png",
        intArrayOf(37, 692, 53, 26), intArrayOf(37, 692, 53, 26), 0.65f)

    // 自动轮换
    private val I_AUTO_ROTATE_ON = RuleImage("auto_rotate_on",
        "exploration/res_e_auto_rotate_on.png",
        intArrayOf(104, 649, 153, 44), intArrayOf(104, 649, 153, 44), 0.9f)

    private val I_AUTO_ROTATE_OFF = RuleImage("auto_rotate_off",
        "exploration/res_e_auto_rotate_off.png",
        intArrayOf(108, 650, 150, 46), intArrayOf(108, 650, 150, 46), 0.85f)

    // 普通怪
    private val I_NORMAL_BATTLE = RuleImage("normal_battle",
        "exploration/res_normal_battle_button.png",
        intArrayOf(636, 263, 42, 39), intArrayOf(0, 0, 1279, 719), 0.8f)

    // Boss
    private val I_BOSS_BATTLE = RuleImage("boss_battle",
        "exploration/res_boss_battle_button.png",
        intArrayOf(683, 256, 38, 34), intArrayOf(0, 0, 1276, 719), 0.8f)

    // 战后奖励
    private val I_BATTLE_REWARD = RuleImage("battle_reward",
        "exploration/res_battle_reward.png",
        intArrayOf(647, 395, 31, 21), intArrayOf(1, 1, 1278, 718), 0.9f)

    // 突破票数量 OCR 区域
    private val O_REALM_RAID_NUMBER = OcrRegion("realm_raid_number",
        intArrayOf(1050, 20, 60, 30), intArrayOf(1020, 5, 120, 60), "0123456789")

    // 宝箱
    private val I_TREASURE_BOX = RuleImage("treasure_box",
        "exploration/res_treasure_box_click.png",
        intArrayOf(33, 476, 70, 49), intArrayOf(2, 130, 135, 406), 0.7f)

    // 滑动结束标识
    private val I_SWIPE_END = RuleImage("swipe_end",
        "exploration/res_swipe_end.png",
        intArrayOf(994, 234, 119, 100), intArrayOf(968, 196, 311, 165), 0.8f)

    // 退出确认
    private val I_EXIT_CONFIRM = RuleImage("exit_confirm",
        "exploration/res_e_exit_confirm.png",
        intArrayOf(694, 380, 163, 49), intArrayOf(694, 380, 163, 49), 0.8f)

    // 红色关闭
    private val I_RED_CLOSE = RuleImage("red_close",
        "exploration/res_red_close.png",
        intArrayOf(1027, 129, 41, 42), intArrayOf(1021, 121, 54, 55), 0.6f)

    // 队伍表情
    private val I_TEAM_EMOJI = RuleImage("team_emoji",
        "exploration/res_team_emoji.png",
        intArrayOf(36, 437, 44, 46), intArrayOf(4, 407, 100, 100), 0.8f)

    // 箭头
    private val I_ARROW_LEFT = RuleImage("arrow_left",
        "exploration/res_exp_arrow_left.png",
        intArrayOf(1244, 115, 18, 26), intArrayOf(1178, 78, 100, 100), 0.8f)

    private val I_ARROW_RIGHT = RuleImage("arrow_right",
        "exploration/res_exp_arrow_right.png",
        intArrayOf(1240, 117, 24, 21), intArrayOf(1178, 74, 100, 100), 0.8f)

    // 安全随机点击区域（用于 UNKNOWN 场景恢复）
    private val C_SAFE_RANDOM = RuleClick("safe_random",
        intArrayOf(640, 360, 100, 100))

    private var minionsCnt = 0
    private var searchFailCnt = 0
    private var scrollsTriggered = false

    // ========== 场景枚举 ==========
    private enum class Scene {
        WORLD,       // 探索大世界（章节列表）
        ENTRANCE,    // 入口弹窗
        MAIN,        // 探索内部（有小怪/Boss）
        BATTLE,      // 战斗中
        UNKNOWN      // 未知
    }

    // ========== 主入口 ==========

    override suspend fun run() {
        log("=== 探索任务开始 ===")
        log("章节: ${config.explorationLevel}")
        if (config.scrollsEnable) {
            log("绘卷模式: 开启，阈值=${config.scrollsThreshold}")
        }

        // ★ 修复：先做预处理（导航到探索页面）
        preProcess()

        // 执行探索
        runSolo()

        // 后处理
        postProcess()

        log("=== 探索完成，战斗 $minionsCnt 次 ===")
    }

    // ========== 预处理 / 后处理 ==========

    /**
     * 预处理 — 对应 Python 版 pre_process()
     *
     * 核心修复：使用 GameUi 的页面导航系统，主动走到探索页面
     */
    private suspend fun preProcess() {
        log("预处理：导航到探索页面...")

        // ★★★ 关键修复：使用 BFS 路径导航 ★★★
        // 会自动识别当前在哪个页面，然后沿着最短路径点击跳转
        // 例如：主页 → 点击"探索"按钮 → 到达探索页面
        val arrived = uiGotoPage(pageExploration, timeout = 30_000)
        if (!arrived) {
            log("警告：未能通过自动导航到达探索页面，尝试从当前页面继续")
        }
    }

    /**
     * 后处理 — 对应 Python 版 post_process()
     */
    private suspend fun postProcess() {
        // 尝试返回主页
        try {
            uiGoto(pageMain, timeout = 15_000)
        } catch (e: Exception) {
            log("后处理：返回主页失败，忽略")
        }
    }

    // ========== 探索主循环 ==========

    private suspend fun runSolo() {
        log("探索启动")
        searchFailCnt = 0
        var randomClickCnt = 0

        while (true) {
            val img = screenshot() ?: continue
            val scene = detectScene(img)

            when (scene) {
                Scene.WORLD -> {
                    randomClickCnt = 0
                    // 展开箭头
                    appearThenClick(I_ARROW_LEFT, img)
                    // 宝箱
                    if (I_TREASURE_BOX.match(img, context).matched) {
                        log("发现宝箱")
                        appearThenClick(I_TREASURE_BOX, img)
                    }
                    if (checkExit()) return
                    // 绘卷检测
                    if (checkScrolls(img)) return
                    // 选择章节
                    selectChapter()
                }
                Scene.ENTRANCE -> {
                    randomClickCnt = 0
                    if (checkExit()) return
                    appearThenClick(I_EXPLORATION_CLICK, img)
                }
                Scene.MAIN -> {
                    randomClickCnt = 0
                    // 战后奖励
                    if (I_BATTLE_REWARD.match(img, context).matched) {
                        appearThenClick(I_BATTLE_REWARD, img)
                        continue
                    }
                    // Boss
                    if (I_BOSS_BATTLE.match(img, context).matched) {
                        if (fire(I_BOSS_BATTLE, img)) {
                            log("Boss战斗，第 ${minionsCnt} 次")
                        }
                        continue
                    }
                    // 小怪
                    val fightBtn = findUpFight(img)
                    if (fightBtn != null) {
                        if (fire(fightBtn, img)) {
                            log("战斗，第 ${minionsCnt} 次")
                        }
                        continue
                    }
                    // 滑动寻找
                    if (searchFailCnt >= 4) {
                        searchFailCnt = 0
                        if (I_SWIPE_END.match(img, context).matched) {
                            quitExplore()
                            return
                        }
                        device.swipe(1093, 148, 397, 140, 350)
                        delay(2000)
                    } else {
                        searchFailCnt++
                    }
                }
                Scene.BATTLE -> {
                    randomClickCnt = 0
                    log("等待战斗结束...")
                    delay(3000)
                }
                Scene.UNKNOWN -> {
                    // ★ 修复：UNKNOWN 场景处理（原版直接 delay 500 跳过）
                    // 尝试随机点击恢复
                    if (randomClickCnt >= 3) {
                        log("UNKNOWN 场景持续存在，尝试关闭弹窗...")
                        // 尝试点击已知的关闭按钮
                        if (appearThenClick(I_RED_CLOSE, img)) {
                            delay(1000)
                            randomClickCnt = 0
                            continue
                        }
                        // 尝试 GameUi 的页面识别，看能否恢复
                        val currentPage = uiGetCurrentPage()
                        if (currentPage != null) {
                            log("识别到页面: ${currentPage.name}，重新进入主循环")
                            randomClickCnt = 0
                            continue
                        }
                        log("无法恢复，放弃")
                        return
                    }
                    log("未知场景，随机点击尝试恢复 (${randomClickCnt + 1}/3)")
                    val (rx, ry) = C_SAFE_RANDOM.coord()
                    device.click(rx, ry)
                    delay(1500)
                    randomClickCnt++
                }
            }
        }
    }

    // ========== 场景识别 ==========

    private fun detectScene(img: android.graphics.Bitmap): Scene {
        // 探索大世界：有探索标题，但没有设置按钮
        if (I_CHECK_EXPLORATION_WORLD.match(img, context).matched &&
            !I_SETTINGS_BUTTON.match(img, context).matched) {
            return Scene.WORLD
        }
        // 入口弹窗：有"探索"按钮
        if (I_EXPLORATION_CLICK.match(img, context).matched) return Scene.ENTRANCE
        // 探索内部：有设置按钮或自动轮换
        if (I_SETTINGS_BUTTON.match(img, context).matched ||
            I_AUTO_ROTATE_ON.match(img, context).matched ||
            I_AUTO_ROTATE_OFF.match(img, context).matched) {
            return Scene.MAIN
        }
        // 也可以用 GameUi 的页面识别来辅助判断
        if (uiPageAppear(pageExploration, img)) return Scene.WORLD

        return Scene.UNKNOWN
    }

    // ========== 章节选择 ==========

    private suspend fun selectChapter() {
        log("选择章节: ${config.explorationLevel}")
        appearThenClick(I_EXPLORATION_CLICK, interval = 2000)
    }

    // ========== 战斗目标查找 ==========

    private fun findUpFight(img: android.graphics.Bitmap): RuleImage? {
        return if (I_NORMAL_BATTLE.match(img, context).matched) I_NORMAL_BATTLE else null
    }

    // ========== 发起战斗 ==========

    private suspend fun fire(rule: RuleImage, img: android.graphics.Bitmap? = null): Boolean {
        appearThenClick(rule, img)
        delay(3000)
        minionsCnt++
        currentCount = minionsCnt
        return true
    }

    // ========== 退出探索 ==========

    private suspend fun quitExplore() {
        log("退出探索")
        repeat(5) {
            val img = screenshot() ?: return
            if (I_CHECK_EXPLORATION_WORLD.match(img, context).matched) return
            appearThenClick(I_EXIT_CONFIRM, img)
            appearThenClick(I_RED_CLOSE, img)
            delay(1000)
        }
    }

    // ========== 退出条件检查 ==========

    private suspend fun checkExit(): Boolean {
        if (minionsCnt >= config.minionsCount) {
            log("战斗次数达标")
            return true
        }
        if (isTimeUp(config.limitTimeMinutes)) {
            log("时间限制到达")
            return true
        }
        return false
    }

    // ========== 绘卷模式 ==========

    private suspend fun checkScrolls(img: android.graphics.Bitmap): Boolean {
        if (!config.scrollsEnable) return false
        if (scrollsTriggered) return false

        val raidCount = O_REALM_RAID_NUMBER.ocr(img, context)
        val current = raidCount.toIntOrNull() ?: return false

        if (current >= config.scrollsThreshold) {
            log("绘卷模式触发：突破票=$current ≥ 阈值=${config.scrollsThreshold}")
            scrollsTriggered = true

            // 关闭可能存在的弹窗
            appearThenClick(I_RED_CLOSE, img)
            delay(500)

            // 退出探索
            quitExplore()
            delay(1000)

            // 设置探索下次运行：当前时间 + 2 分钟
            val nextRunTime = System.currentTimeMillis() + 2 * 60 * 1000
            setNextRun(nextRunTime)

            // 设置个人突破立即运行
            setRealmRaidNextRun(0)

            log("绘卷模式：已设置突破立即运行，探索2分钟后恢复")
            return true
        }
        return false
    }

    // ========== 辅助方法 ==========

    /**
     * 出现则点击 — 覆盖 BaseTask 版本，保持与原有逻辑兼容
     */
    private suspend fun appearThenClick(
        rule: RuleImage,
        screenshot: android.graphics.Bitmap? = null,
        interval: Long = 1000
    ): Boolean {
        val img = screenshot ?: this.screenshot() ?: return false
        val result = rule.match(img, context)
        if (result.matched) {
            device.click(result.centerX, result.centerY)
            delay(interval)
            return true
        }
        return false
    }
}

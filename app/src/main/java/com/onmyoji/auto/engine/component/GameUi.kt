package com.onmyoji.auto.engine.component

import android.content.Context
import android.graphics.Bitmap
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay
import java.util.LinkedList

/**
 * 页面导航组件 — 对应 OAS game_ui.py
 *
 * 实现页面识别、页面跳转、最短路径规划等功能
 */
class GameUi(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // 当前页面
    var uiCurrent: Page? = null
        private set

    // ========== 资源定义 (GameUiAssets) ==========

    // 商店弹窗红色关闭
    private val I_AD_CLOSE_RED = RuleImage("ad_close_red",
        "game_ui/additional/additional_ad_close_red.png",
        intArrayOf(993, 130, 33, 36), intArrayOf(953, 91, 215, 121), 0.8f)

    // 庭院标识
    private val I_CHECK_MAIN = RuleImage("check_main",
        "game_ui/page/page_check_main.png",
        intArrayOf(807, 108, 76, 45), intArrayOf(49, 98, 1033, 61), 0.8f)

    // 探索标识
    private val I_CHECK_EXPLORATION = RuleImage("check_exploration",
        "game_ui/page/page_check_exploration.png",
        intArrayOf(681, 12, 27, 36), intArrayOf(661, 0, 70, 61), 0.8f)

    // 庭院到探索
    private val I_MAIN_GOTO_EXPLORATION = RuleImage("main_goto_exploration",
        "game_ui/page/page_main_goto_exploration.png",
        intArrayOf(493, 116, 45, 75), intArrayOf(243, 100, 933, 211), 0.8f)

    // 探索返回庭院
    private val I_BACK_YOLLOW = RuleImage("back_yollow",
        "game_ui/page/page_back_yollow.png",
        intArrayOf(24, 16, 48, 55), intArrayOf(0, 0, 100, 100), 0.8f)

    // 觉醒标识
    private val I_CHECK_AWAKE = RuleImage("check_awake",
        "game_ui/page/page_check_awake.png",
        intArrayOf(164, 534, 42, 41), intArrayOf(124, 524, 197, 119), 0.8f)

    // 御魂标识
    private val I_CHECK_SOUL_ZONES = RuleImage("check_soul_zones",
        "game_ui/page/page_check_soul_zones.png",
        intArrayOf(1193, 636, 40, 40), intArrayOf(1131, 591, 149, 128), 0.8f)

    // 结界突破标识
    private val I_CHECK_REALM_RAID = RuleImage("check_realm_raid",
        "game_ui/page/page_check_realm_raid.png",
        intArrayOf(129, 632, 52, 48), intArrayOf(66, 526, 233, 188), 0.7f)

    // 御灵标识
    private val I_CHECK_GORYOU = RuleImage("check_goryou",
        "game_ui/page/page_check_goryou.png",
        intArrayOf(881, 17, 30, 39), intArrayOf(881, 17, 30, 39), 0.8f)

    // 委派标识
    private val I_CHECK_DELEGATION = RuleImage("check_delegation",
        "game_ui/page/page_check_delegation.png",
        intArrayOf(839, 132, 49, 45), intArrayOf(839, 132, 49, 45), 0.8f)

    // 秘闻标识
    private val I_CHECK_SECRET_ZONES = RuleImage("check_secret_zones",
        "game_ui/page/page_check_secret_zones.png",
        intArrayOf(1145, 592, 110, 119), intArrayOf(1145, 592, 110, 119), 0.8f)

    // 地域鬼王标识
    private val I_CHECK_AREA_BOSS = RuleImage("check_area_boss",
        "game_ui/page/page_check_area_boss.png",
        intArrayOf(20, 320, 150, 130), intArrayOf(20, 320, 150, 130), 0.8f)

    // 六道之门标识
    private val I_CHECK_SIX_GATES = RuleImage("check_six_gates",
        "game_ui/page/page_check_six_gates.png",
        intArrayOf(1174, 621, 55, 44), intArrayOf(1174, 621, 55, 44), 0.8f)

    // 探索前往觉醒
    private val I_EXPLORATION_GOTO_AWAKE_ZONE = RuleImage("exploration_goto_awake_zone",
        "game_ui/page/page_exploration_goto_awake_zone.png",
        intArrayOf(65, 632, 50, 41), intArrayOf(33, 619, 1125, 98), 0.65f)

    // 探索前往御魂
    private val I_EXPLORATION_GOTO_SOUL_ZONE = RuleImage("exploration_goto_soul_zone",
        "game_ui/page/page_exploration_goto_soul_zone.png",
        intArrayOf(165, 639, 44, 36), intArrayOf(38, 621, 1106, 98), 0.8f)

    // 探索前往结界突破
    private val I_EXPLORATION_GOTO_REALM_RAID = RuleImage("exploration_goto_realm_raid",
        "game_ui/page/page_exploration_goto_realm_raid.png",
        intArrayOf(263, 636, 51, 46), intArrayOf(42, 618, 1103, 100), 0.65f)

    // 探索前往御灵
    private val I_EXPLORATION_GOTO_GORYOU_REALM = RuleImage("exploration_goto_goryou_realm",
        "game_ui/page/page_exploration_goto_goryou_realm.png",
        intArrayOf(353, 639, 47, 45), intArrayOf(49, 613, 1107, 105), 0.7f)

    // 探索前往委派
    private val I_EXPLORATION_GOTO_DELEGATION = RuleImage("exploration_goto_delegation",
        "game_ui/page/page_exploration_goto_delegation.png",
        intArrayOf(453, 639, 53, 37), intArrayOf(43, 609, 1107, 107), 0.65f)

    // 探索前往秘闻
    private val I_EXPLORATION_GOTO_SECRET_ZONES = RuleImage("exploration_goto_secret_zones",
        "game_ui/page/page_exploration_goto_secret_zones.png",
        intArrayOf(549, 633, 53, 40), intArrayOf(44, 617, 1102, 99), 0.7f)

    // 探索前往地域鬼王
    private val I_EXPLORATION_GOTO_AREA_BOSS = RuleImage("exploration_goto_area_boss",
        "game_ui/page/page_exploration_goto_area_boss.png",
        intArrayOf(640, 638, 51, 45), intArrayOf(33, 622, 1103, 90), 0.65f)

    // 探索前往六道之门
    private val I_EXPLORATION_GOTO_SIX_GATES = RuleImage("exploration_goto_six_gates",
        "game_ui/page/page_exploration_goto_six_gates.png",
        intArrayOf(928, 638, 54, 46), intArrayOf(46, 618, 1090, 100), 0.65f)

    // 结界突破返回探索
    private val I_REALM_RAID_GOTO_EXPLORATION = RuleImage("realm_raid_goto_exploration",
        "game_ui/page/page_realm_raid_goto_exploration.png",
        intArrayOf(1192, 107, 36, 43), intArrayOf(1192, 107, 36, 43), 0.8f)

    // 六道之门返回探索
    private val I_SIX_GATES_GOTO_EXPLORATION = RuleImage("six_gates_goto_exploration",
        "game_ui/page/page_six_gates_goto_exploration.png",
        intArrayOf(18, 19, 52, 55), intArrayOf(18, 19, 52, 55), 0.8f)

    // 庭院相关
    private val I_MAIN_GOTO_SHIKIGAMI_RECORDS = RuleImage("main_goto_shikigami_records",
        "game_ui/page/page_main_goto_shikigami_records.png",
        intArrayOf(1080, 590, 56, 64), intArrayOf(1080, 590, 120, 100), 0.7f)
    private val I_MAIN_GOTO_FRIENDS = RuleImage("main_goto_friends",
        "game_ui/page/page_main_goto_friends.png",
        intArrayOf(845, 590, 55, 55), intArrayOf(845, 590, 120, 100), 0.7f)
    private val I_MAIN_GOTO_GUILD = RuleImage("main_goto_guild",
        "game_ui/page/page_main_goto_guild.png",
        intArrayOf(495, 590, 50, 22), intArrayOf(495, 590, 120, 100), 0.7f)
    private val I_MAIN_GOTO_TEAM = RuleImage("main_goto_team",
        "game_ui/page/page_main_goto_team.png",
        intArrayOf(395, 590, 38, 48), intArrayOf(395, 590, 120, 100), 0.7f)

    // 式神录标识
    private val I_CHECK_RECORDS = RuleImage("check_records",
        "game_ui/page/page_check_records.png",
        intArrayOf(269, 71, 55, 50), intArrayOf(269, 71, 55, 50), 0.8f)

    // 好友标识
    private val I_CHECK_FRIENDS = RuleImage("check_friends",
        "game_ui/page/page_check_friends.png",
        intArrayOf(74, 628, 58, 66), intArrayOf(74, 628, 58, 66), 0.8f)

    // 阴阳寮标识
    private val I_CHECK_GUILD = RuleImage("check_guild",
        "game_ui/page/page_check_guild.png",
        intArrayOf(1072, 630, 49, 46), intArrayOf(1072, 630, 49, 46), 0.8f)

    // 组队标识
    private val I_CHECK_TEAM = RuleImage("check_team",
        "game_ui/page/page_check_team.png",
        intArrayOf(32, 585, 82, 90), intArrayOf(32, 585, 82, 90), 0.8f)

    // 返回按钮
    private val I_BACK_Y = RuleImage("back_y",
        "game_ui/page/page_back_y.png",
        intArrayOf(15, 4, 57, 52), intArrayOf(1, 2, 100, 91), 0.8f)

    private val I_BACK_BLUE = RuleImage("back_blue",
        "game_ui/page/page_back_blue.png",
        intArrayOf(32, 37, 54, 52), intArrayOf(3, 2, 130, 114), 0.8f)

    private val I_BACK_FRIENDS = RuleImage("back_friends",
        "game_ui/page/page_back_friends.png",
        intArrayOf(1152, 87, 53, 52), intArrayOf(1152, 87, 53, 52), 0.8f)

    private val I_BACK_DAILY = RuleImage("back_daily",
        "game_ui/page/page_back_daily.png",
        intArrayOf(33, 13, 39, 50), intArrayOf(33, 13, 39, 50), 0.8f)

    private val I_BACK_MALL = RuleImage("back_mall",
        "game_ui/page/page_back_mall.png",
        intArrayOf(28, 33, 50, 51), intArrayOf(28, 33, 50, 51), 0.8f)

    // 町中
    private val I_CHECK_TOWN = RuleImage("check_town",
        "game_ui/page/page_check_town.png",
        intArrayOf(1026, 106, 68, 82), intArrayOf(765, 98, 402, 96), 0.8f)
    private val I_MAIN_GOTO_TOWN = RuleImage("main_goto_town",
        "game_ui/page/page_main_goto_town.png",
        intArrayOf(706, 249, 61, 57), intArrayOf(200, 120, 951, 298), 0.6f)
    private val I_TOWN_GOTO_MAIN = RuleImage("town_goto_main",
        "game_ui/page/page_town_goto_main.png",
        intArrayOf(1017, 231, 78, 73), intArrayOf(302, 216, 868, 127), 0.8f)

    // 斗技
    private val I_CHECK_DUEL = RuleImage("check_duel",
        "game_ui/page/page_check_duel.png",
        intArrayOf(149, 16, 64, 38), intArrayOf(133, 0, 110, 74), 0.8f)
    private val I_TOWN_GOTO_DUEL = RuleImage("town_goto_duel",
        "game_ui/page/page_town_goto_duel.png",
        intArrayOf(756, 142, 48, 68), intArrayOf(357, 126, 657, 100), 0.8f)

    // 逢魔之时
    private val I_CHECK_DEMON_ENCOUNTER = RuleImage("check_demon_encounter",
        "game_ui/page/page_check_demon_encounter.png",
        intArrayOf(153, 16, 129, 38), intArrayOf(153, 16, 129, 38), 0.8f)
    private val I_TOWN_GOTO_DEMON_ENCOUNTER = RuleImage("town_goto_demon_encounter",
        "game_ui/page/page_town_goto_demon_encounter.png",
        intArrayOf(617, 135, 51, 75), intArrayOf(232, 121, 873, 100), 0.7f)

    // 召唤
    private val I_CHECK_SUMMON = RuleImage("check_summon",
        "game_ui/page/page_check_summon.png",
        intArrayOf(750, 580, 30, 28), intArrayOf(750, 580, 350, 120), 0.8f)
    private val I_MAIN_GOTO_SUMMON = RuleImage("main_goto_summon",
        "game_ui/page/page_main_goto_summon.png",
        intArrayOf(1073, 174, 57, 65), intArrayOf(571, 153, 586, 124), 0.8f)
    private val I_SUMMON_GOTO_MAIN = RuleImage("summon_goto_main",
        "game_ui/page/page_summon_goto_main.png",
        intArrayOf(27, 5, 49, 51), intArrayOf(0, 0, 100, 100), 0.8f)

    // 登录
    private val I_CHECK_LOGIN_FORM = RuleImage("check_login_form",
        "game_ui/page/page_check_login_form.png",
        intArrayOf(178, 572, 53, 60), intArrayOf(1, 500, 400, 150), 0.8f)

    // 可以关闭未知页面的按钮列表
    private val uiClose = listOf(
        I_BACK_MALL, I_BACK_FRIENDS, I_BACK_DAILY,
        I_REALM_RAID_GOTO_EXPLORATION, I_SIX_GATES_GOTO_EXPLORATION,
        I_BACK_BLUE
    )

    // ========== 页面定义 ==========

    // 使用 lazy 初始化页面，避免构造时注册顺序问题
    val pageLogin by lazy { Page(I_CHECK_LOGIN_FORM, "page_login") }
    val pageMain by lazy {
        Page(I_CHECK_MAIN, "page_main").apply {
            additional = listOf(I_AD_CLOSE_RED, I_BACK_FRIENDS)
        }
    }
    val pageSummon by lazy {
        Page(I_CHECK_SUMMON, "page_summon").apply {
            link(I_SUMMON_GOTO_MAIN, pageMain)
        }
    }
    val pageExploration by lazy {
        Page(I_CHECK_EXPLORATION, "page_exploration").apply {
            link(I_BACK_YOLLOW, pageMain)
        }
    }
    val pageTown by lazy {
        Page(I_CHECK_TOWN, "page_town").apply {
            link(I_TOWN_GOTO_MAIN, pageMain)
        }
    }
    val pageAwakeZones by lazy {
        Page(I_CHECK_AWAKE, "page_awake_zones").apply {
            link(I_BACK_YOLLOW, pageExploration)
        }
    }
    val pageSoulZones by lazy {
        Page(I_CHECK_SOUL_ZONES, "page_soul_zones").apply {
            link(I_BACK_YOLLOW, pageExploration)
        }
    }
    val pageRealmRaid by lazy {
        Page(I_CHECK_REALM_RAID, "page_realm_raid").apply {
            link(I_REALM_RAID_GOTO_EXPLORATION, pageExploration)
        }
    }
    val pageGoryouRealm by lazy {
        Page(I_CHECK_GORYOU, "page_goryou_realm").apply {
            link(I_BACK_YOLLOW, pageExploration)
        }
    }
    val pageDelegation by lazy {
        Page(I_CHECK_DELEGATION, "page_delegation").apply {
            link(I_BACK_YOLLOW, pageExploration)
        }
    }
    val pageSecretZones by lazy {
        Page(I_CHECK_SECRET_ZONES, "page_secret_zones").apply {
            link(I_BACK_YOLLOW, pageExploration)
        }
    }
    val pageAreaBoss by lazy {
        Page(I_CHECK_AREA_BOSS, "page_area_boss").apply {
            link(I_BACK_YOLLOW, pageExploration)
        }
    }
    val pageSixGates by lazy {
        Page(I_CHECK_SIX_GATES, "page_six_gates").apply {
            link(I_SIX_GATES_GOTO_EXPLORATION, pageExploration)
        }
    }
    val pageDuel by lazy {
        Page(I_CHECK_DUEL, "page_duel").apply {
            link(I_BACK_YOLLOW, pageTown)
        }
    }
    val pageDemonEncounter by lazy {
        Page(I_CHECK_DEMON_ENCOUNTER, "page_demon_encounter").apply {
            link(I_BACK_YOLLOW, pageTown)
        }
    }
    val pageShikigamiRecords by lazy {
        Page(I_CHECK_RECORDS, "page_shikigami_records").apply {
            link(I_BACK_Y, pageMain)
        }
    }
    val pageFriends by lazy {
        Page(I_CHECK_FRIENDS, "page_friends").apply {
            link(I_BACK_Y, pageMain)
        }
    }
    val pageGuild by lazy {
        Page(I_CHECK_GUILD, "page_guild").apply {
            link(I_BACK_Y, pageMain)
        }
    }
    val pageTeam by lazy {
        Page(I_CHECK_TEAM, "page_team").apply {
            link(I_BACK_Y, pageMain)
        }
    }

    /** 所有已注册页面 */
    val uiPages: List<Page> by lazy {
        listOf(
            pageLogin, pageMain, pageSummon, pageExploration, pageTown,
            pageAwakeZones, pageSoulZones, pageRealmRaid, pageGoryouRealm,
            pageDelegation, pageSecretZones, pageAreaBoss, pageSixGates,
            pageDuel, pageDemonEncounter,
            pageShikigamiRecords, pageFriends, pageGuild, pageTeam
        ).also { pages ->
            // 设置双向链接
            pageMain.link(I_MAIN_GOTO_EXPLORATION, pageExploration)
            pageMain.link(I_MAIN_GOTO_TOWN, pageTown)
            pageMain.link(I_MAIN_GOTO_SUMMON, pageSummon)
            pageMain.link(I_MAIN_GOTO_SHIKIGAMI_RECORDS, pageShikigamiRecords)
            pageMain.link(I_MAIN_GOTO_FRIENDS, pageFriends)
            pageMain.link(I_MAIN_GOTO_GUILD, pageGuild)
            pageMain.link(I_MAIN_GOTO_TEAM, pageTeam)
            pageExploration.link(I_EXPLORATION_GOTO_AWAKE_ZONE, pageAwakeZones)
            pageExploration.link(I_EXPLORATION_GOTO_SOUL_ZONE, pageSoulZones)
            pageExploration.link(I_EXPLORATION_GOTO_REALM_RAID, pageRealmRaid)
            pageExploration.link(I_EXPLORATION_GOTO_GORYOU_REALM, pageGoryouRealm)
            pageExploration.link(I_EXPLORATION_GOTO_DELEGATION, pageDelegation)
            pageExploration.link(I_EXPLORATION_GOTO_SECRET_ZONES, pageSecretZones)
            pageExploration.link(I_EXPLORATION_GOTO_AREA_BOSS, pageAreaBoss)
            pageExploration.link(I_EXPLORATION_GOTO_SIX_GATES, pageSixGates)
            pageTown.link(I_TOWN_GOTO_DUEL, pageDuel)
            pageTown.link(I_TOWN_GOTO_DEMON_ENCOUNTER, pageDemonEncounter)
        }
    }

    override suspend fun run() {
        // GameUi 不直接 run
        log("GameUi: 请在子任务中调用 uiGoto() 等方法")
    }

    // ========== 页面识别 ==========

    /**
     * 判断当前页面是否为指定 page — 对应 OAS ui_page_appear
     */
    fun uiPageAppear(page: Page, img: Bitmap? = null): Boolean {
        val image = img ?: screenshot() ?: return false
        for (btn in page.checkButton) {
            if (btn.match(image, context).matched) return true
        }
        return false
    }

    /**
     * 等待页面出现 — 对应 OAS ui_wait_until_appear
     */
    suspend fun uiWaitUntilAppear(
        page: Page,
        timeoutMs: Long = 5000,
        intervalMs: Long = 500
    ): Boolean {
        log("Waiting for $page")
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (uiPageAppear(page)) return true
            delay(intervalMs)
        }
        return false
    }

    /**
     * 获取当前页面 — 对应 OAS ui_get_current_page
     */
    suspend fun uiGetCurrentPage(): Page? {
        log("UI get current page")
        val timeout = System.currentTimeMillis() + 10_000

        while (System.currentTimeMillis() < timeout) {
            val img = screenshot() ?: continue

            // 遍历所有已知页面
            for (page in uiPages) {
                if (uiPageAppear(page, img)) {
                    log("UI: ${page.name}")
                    uiCurrent = page
                    return page
                }
            }

            // 尝试关闭未知页面
            if (tryCloseUnknownPage(img)) {
                continue
            }

            delay(300)
        }

        log("Unknown ui page")
        return null
    }

    // ========== 页面跳转 ==========

    /**
     * 前往指定 page — 对应 OAS ui_goto_page
     * 自动调用获取当前页面方法
     */
    suspend fun uiGotoPage(destPage: Page, timeout: Long = 60_000): Boolean {
        uiGetCurrentPage()
        return uiGoto(destPage, timeout = timeout)
    }

    /**
     * 前往指定 page — 对应 OAS ui_goto
     *
     * 使用 BFS 构建最短路径，然后逐页跳转
     */
    suspend fun uiGoto(
        destination: Page,
        confirmWait: Long = 0,
        timeout: Long = 60_000
    ): Boolean {
        log("UI goto $destination")
        val timeoutDeadline = System.currentTimeMillis() + timeout
        val closeUnknownTimer = System.currentTimeMillis() + 3_000

        // 构建路径映射
        val pathDict = buildReversePathDict(destination)

        while (System.currentTimeMillis() < timeoutDeadline) {
            val path = pathDict[uiCurrent]
            if (path == null) {
                uiGetCurrentPage()
                continue
            }

            log("Current page: $uiCurrent. Following shortest path:")
            log(path.joinToString(" -> ") { it.name })

            // 执行路径
            val found = executePath(path, timeoutDeadline)
            if (found) {
                if (confirmWait > 0) delay(confirmWait)
                return true
            }

            // 尝试关闭未知页面
            if (System.currentTimeMillis() > closeUnknownTimer) {
                tryCloseUnknownPage()
                uiCurrent = null
            }
        }

        log("Cannot goto page[$destination], timeout reached")
        return false
    }

    /**
     * 构建从每个页面到目标页面的最短路径（反向 BFS）— 对应 OAS build_reverse_path_dict
     */
    private fun buildReversePathDict(destination: Page): Map<Page, List<Page>> {
        val paths = mutableMapOf<Page, MutableList<Page>>()
        paths[destination] = mutableListOf(destination)
        val queue = LinkedList<Page>()
        queue.add(destination)

        while (queue.isNotEmpty()) {
            val cur = queue.poll()
            for (page in uiPages) {
                if (page !in paths && cur in page.links.keys) {
                    // page -> cur
                    val newPath = mutableListOf(page)
                    newPath.addAll(paths[cur]!!)
                    paths[page] = newPath
                    queue.add(page)
                }
            }
        }
        return paths
    }

    /**
     * 执行路径 — 对应 OAS _execute_path
     */
    private suspend fun executePath(path: List<Page>, timeoutDeadline: Long): Boolean {
        for (i in path.indices) {
            if (System.currentTimeMillis() > timeoutDeadline) return false

            val currentPage = path[i]
            if (uiCurrent != currentPage) continue

            // 执行附加操作
            runAdditional(currentPage)

            // 如果已经是最后一页
            if (i == path.size - 1) {
                if (path.size == 1) log("Page arrived $currentPage")
                break
            }

            val nextPage = path[i + 1]
            log("Page switch: $currentPage -> $nextPage")

            // 获取跳转按钮
            val button = currentPage.links[nextPage]
            if (button == null) {
                log("No link from $currentPage to $nextPage")
                continue
            }

            // 跳转页面
            val maxWait = System.currentTimeMillis() + 6_000
            log("Wait appear and operate $button on $currentPage")
            while (System.currentTimeMillis() < maxWait) {
                if (System.currentTimeMillis() > timeoutDeadline) return false
                if (appearThenOperate(button)) break
            }

            // 等待目标页面出现
            val waitTarget = System.currentTimeMillis() + 6_000
            while (System.currentTimeMillis() < waitTarget) {
                if (System.currentTimeMillis() > timeoutDeadline) return false
                if (uiWaitUntilAppear(nextPage, 2500)) {
                    log("Page arrived $nextPage")
                    uiCurrent = nextPage
                    break
                }
            }

            // 如果没到目标页面，重新获取
            if (uiCurrent != nextPage) {
                uiGetCurrentPage()
            }
        }
        return uiCurrent == path.last()
    }

    /**
     * 执行页面附加操作 — 对应 OAS run_additional
     */
    private suspend fun runAdditional(page: Page) {
        val additional = page.additional ?: return
        for (btn in additional) {
            when (btn) {
                is RuleImage -> appearThenClick(btn)
            }
        }
    }

    /**
     * 尝试关闭未知页面 — 对应 OAS try_close_unknown_page
     */
    private suspend fun tryCloseUnknownPage(img: Bitmap? = null): Boolean {
        val image = img ?: screenshot() ?: return false
        for (closeBtn in uiClose) {
            val result = closeBtn.match(image, context)
            if (result.matched) {
                device.click(result.centerX, result.centerY)
                delay(1500)
                log("Trying to switch to supported page")
                return true
            }
        }
        return false
    }

    /**
     * 出现并执行操作 — 对应 OAS appear_then_operate
     */
    private suspend fun appearThenOperate(target: Any): Boolean {
        return when (target) {
            is RuleImage -> {
                val img = screenshot() ?: return false
                val result = target.match(img, context)
                if (result.matched) {
                    device.click(result.centerX, result.centerY)
                    delay(800)
                    true
                } else false
            }
            is List<*> -> {
                // 多个按钮，第一个成功就跳出
                for (btn in target) {
                    if (btn is RuleImage && appearThenOperate(btn)) return true
                }
                false
            }
            else -> false
        }
    }

    // ========== 辅助方法 ==========

    private suspend fun appearThenClick(rule: RuleImage, interval: Long = 1000): Boolean {
        val img = screenshot() ?: return false
        val result = rule.match(img, context)
        if (result.matched) {
            device.click(result.centerX, result.centerY)
            delay(interval)
            return true
        }
        return false
    }
}

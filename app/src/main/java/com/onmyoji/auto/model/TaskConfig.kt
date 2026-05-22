package com.onmyoji.auto.model

/**
 * 任务配置数据类
 */
data class TaskConfig(
    val explorationLevel: String = "第二十八章",
    val limitTimeMinutes: Int = 30,
    val minionsCount: Int = 30,
    val autoRotate: Boolean = true,
    val chooseRarity: String = "N卡",

    // 绘卷模式
    val scrollsEnable: Boolean = true,
    val scrollsThreshold: Int = 25,

    // 个人突破
    val numberAttack: Int = 30,
    val exitFour: Boolean = true,
    val orderAttack: String = "5>4>3>2>1>0",
    val threeRefresh: Boolean = false,

    // 通用战斗
    val lockTeam: Boolean = false,

    // ========== SoulsTidy 御魂整理 ==========
    val soulsTidyEnableGreed: Boolean = true,
    val soulsTidyEnableManeki: Boolean = true,

    // ========== CollectiveMissions 集体任务 ==========
    val collectiveMissionsSelect: String = "觉醒三",
    val collectiveMissionsType: String = "donate", // donate / feed / soul

    // ========== Delegation 委派 ==========
    val delegationNames: List<String> = listOf("画", "鸟羽", "寻找耳环", "猫老大", "接送", "痕迹"),

    // ========== FindJade 找玉 ==========
    // (简化版，无需额外配置)

    // ========== GotoMain 回主页 ==========
    // (无需额外配置)

    // ========== GuildActivityMonitor 寮活动监控 ==========
    val guildActivityMonitorRunDays: String = "1,2,3,4,5,6,7",
    val guildActivityMonitorDuration: Int = 15,
    val guildActivityMonitorInterval: Int = 30,
    val guildActivityMonitorActivities: String = "道馆,狭间,宴会,退治",

    // ========== GuildBanquet 寮宴 ==========
    // (通过时间配置管理，无需额外字段)

    // ========== MemoryScrolls 记忆绘卷 ==========
    val memoryScrollsNumber: Int = 1,
    val memoryScrollsAutoContribute: Boolean = true,

    // ========== Pets 宠物 ==========
    val petsFeast: Boolean = true,

    // ========== TalismanPass 花合战 ==========
    val talismanPassLevelReward: Int = 2, // 1/2/3

    // ========== WeeklyTrifles 每周杂务 ==========
    val weeklyTriflesShareCollect: Boolean = true,
    val weeklyTriflesShareAreaBoss: Boolean = true,
    val weeklyTriflesShareSecret: Boolean = true,
    val weeklyTriflesBrokenAmulet: Int = 100,

    // ========== FloatParade 花车巡游 ==========
    val floatParadeLevelReward1: Int = 3,
    val floatParadeLevelReward2: Int = 1,

    // ========== AutoCheckinBigGod 大神签到 ==========
    // (简化版，无需额外配置)

    // ========== DailyTrifles 每日杂务 ==========
    val dailyTriflesFriendLove: Boolean = false,
    val dailyTriflesLuckMsg: Boolean = false,
    val dailyTriflesStoreSign: Boolean = false,
    val dailyTriflesBuySushiCount: Int = -1,

    // ========== MysteryShop 神秘商店 ==========
    val mysteryShopMysteryAmulet: Boolean = false,
    val mysteryShopBlackDarumaScrap: Boolean = false,
    val mysteryShopTaiko3: Boolean = false,
    val mysteryShopTaiko4: Boolean = false,

    // ========== KittyShop 猫咪商店 ==========
    val kittyShopAttempts: Int = 1,
    val kittyShopQuitWhenFinished: Boolean = false,

    // ========== Orochi 八岐大蛇 ==========
    val orochiLayer: String = "悲",
    val orochiUserStatus: String = "alone", // leader / member / alone / wild
    val orochiSoulBuffEnable: Boolean = false,
    val orochiGreenEnable: Boolean = false,
    val orochiGreenMark: String = "none",

    // ========== AreaBoss 地域鬼王 ==========
    val areaBossNumber: Int = 3,
    val areaBossUseCollect: Boolean = false,
    val areaBossBossReward: Boolean = true,
    val areaBossFloor: String = "day",

    // ========== AbyssShadows 深渊暗影 ==========
    val abyssShadowsTeam: Int = 1,
    val abyssShadowsGreenEnable: Boolean = false,

    // ========== DyeTrials 染色试炼 ==========
    val dyeTrialsLayer: String = "10",

    // ========== GoryouRealm 御灵境 ==========
    val goryouRealmLayer: String = "3",

    // ========== HeroTest 式神试炼 ==========
    val heroTestGreenEnable: Boolean = false,

    // ========== RyouToppa 寮突 ==========
    val ryouToppaNumberAttack: Int = 30,
    val ryouToppaExitFour: Boolean = false,

    // ========== Secret 秘闻 ==========
    val secretLayer: String = "10",

    // ========== Sougenbi 原 ==========
    val sougenbiLayer: String = "10",

    // ========== DemonEncounter 鬼面来袭 ==========
    val demonEncounterAutoBattle: Boolean = true,

    // ========== DemonRetreat 鬼退治 ==========
    val demonRetreatAutoBattle: Boolean = true,

    // ========== Duel 斗技 ==========
    val duelNumberAttack: Int = 5,

    // ========== EvoZone 觉醒 ==========
    val evoZoneLayer: String = "10",
    val evoZoneUserStatus: String = "alone",
    val evoZoneGreenEnable: Boolean = false,

    // ========== ExperienceYoukai 经验妖怪 ==========
    val experienceYoukaiUserStatus: String = "alone",

    // ========== FallenSun 日轮之城 ==========
    val fallenSunLayer: String = "10",
    val fallenSunUserStatus: String = "alone",

    // ========== GoldYoukai 金币妖怪 ==========
    val goldYoukaiUserStatus: String = "alone",

    // ========== Hunt 狩猎战 ==========
    val huntGreenEnable: Boolean = false,

    // ========== Nian 年兽 ==========
    val nianUserStatus: String = "alone",

    // ========== Tako 超鬼王 ==========
    val takoAutoBattle: Boolean = true,

    // ========== BondlingFairyland 契灵之境 ==========
    val bondlingFairylandLayer: String = "10",
    val bondlingFairylandUserStatus: String = "alone",

    // ========== TrueOrochi 真八岐大蛇 ==========
    val trueOrochiLayer: String = "悲",

    // ========== WantedQuests 悬赏封印 ==========
    val wantedQuestsAutoSearch: Boolean = true,

    // ========== EternitySea 永生之海 ==========
    val eternitySeaLayer: String = "10",
    val eternitySeaUserStatus: String = "alone"
)

enum class TaskType {
    EXPLORATION,
    REALM_RAID,
    SOULS_TIDY,
    COLLECTIVE_MISSIONS,
    DELEGATION,
    FIND_JADE,
    GOTO_MAIN,
    GUILD_ACTIVITY_MONITOR,
    GUILD_BANQUET,
    MEMORY_SCROLLS,
    PETS,
    TALISMAN_PASS,
    WEEKLY_TRIFLES,
    FLOAT_PARADE,
    AUTO_CHECKIN_BIG_GOD,
    DAILY_TRIFLES,
    MYSTERY_SHOP,
    KITTY_SHOP,
    // 战斗类任务
    OROCHI,
    AREA_BOSS,
    ABYSS_SHADOWS,
    DYE_TRIALS,
    GORYOU_REALM,
    HERO_TEST,
    RYOUTOPPA,
    SECRET,
    SOUGENBI,
    DEMON_ENCOUNTER,
    DEMON_RETREAT,
    DUEL,
    EVO_ZONE,
    EXPERIENCE_YOUKAI,
    FALLEN_SUN,
    GOLD_YOUKAI,
    HUNT,
    NIAN,
    TAKO,
    BONDLING_FAIRYLAND,
    TRUE_OROCHI,
    WANTED_QUESTS,
    ETERNITY_SEA
}

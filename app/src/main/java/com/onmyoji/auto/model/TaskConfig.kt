package com.onmyoji.auto.model

import com.onmyoji.auto.engine.component.GeneralBattleConfig
import com.onmyoji.auto.engine.component.GeneralInvite

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
    val orochiSwitchSoulEnable: Boolean = false,
    val orochiSwitchGroupTeam: String = "-1,-1",
    val orochiSwitchSoulEnableByName: Boolean = false,
    val orochiGroupName: String = "",
    val orochiTeamName: String = "",
    val orochiLockTeam: Boolean = false,
    val orochiDefaultInvite: Boolean = true,
    val orochiLimitCount: Int = 30,
    val orochiLimitTimeMinutes: Int = 30,
    val orochiWaitTime: Int = 60,
    val orochiInviteConfig: GeneralInvite.InviteConfig = GeneralInvite.InviteConfig(),
    val orochiBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),
    val orochiAutoSwitchSoul: Boolean = false,
    val orochiTenSwitch: String = "-1,-1",
    val orochiElevenSwitch: String = "-1,-1",
    val orochiTwelveSwitch: String = "-1,-1",
    val orochiThirteenSwitch: String = "-1,-1",

    // ========== AreaBoss 地域鬼王 ==========
    val areaBossNumber: Int = 3,
    val areaBossUseCollect: Boolean = false,
    val areaBossBossReward: Boolean = true,
    val areaBossFloor: String = "day",
    val areaBossSwitchSoulEnable: Boolean = false,
    val areaBossSwitchGroupTeam: String = "-1,-1",
    val areaBossSwitchSoulEnableByName: Boolean = false,
    val areaBossGroupName: String = "",
    val areaBossTeamName: String = "",
    val areaBossReward: Boolean = true,
    val areaBossAttack60: Boolean = false,
    val areaBossRewardFloor: String = "1",
    val areaBossBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),

    // ========== AbyssShadows 深渊暗影 ==========
    val abyssShadowsTeam: Int = 1,
    val abyssShadowsGreenEnable: Boolean = false,
    val abyssShadowsSwitchSoulEnable: Boolean = false,
    val abyssShadowsSwitchGroupTeam: String = "-1,-1",
    val abyssShadowsSwitchSoulEnableByName: Boolean = false,
    val abyssShadowsGroupName: String = "",
    val abyssShadowsTeamName: String = "",
    val abyssShadowsCombatTimeEnable: Boolean = false,
    val abyssShadowsBossCombatTime: Int = 60,
    val abyssShadowsGeneralCombatTime: Int = 60,
    val abyssShadowsEliteCombatTime: Int = 60,

    // ========== DyeTrials 染色试炼 ==========
    val dyeTrialsLayer: String = "10",
    val dyeTrialsSwitchSoulEnable: Boolean = false,
    val dyeTrialsSwitchGroupTeam: String = "-1,-1",
    val dyeTrialsSwitchSoulEnableByName: Boolean = false,
    val dyeTrialsGroupName: String = "",
    val dyeTrialsTeamName: String = "",

    // ========== GoryouRealm 御灵境 ==========
    val goryouRealmLayer: String = "3",
    val goryouRealmSwitchSoulEnable: Boolean = false,
    val goryouRealmSwitchGroupTeam: String = "-1,-1",
    val goryouRealmClass: String = "1",
    val goryouRealmLockTeam: Boolean = false,
    val goryouRealmLimitCount: Int = 30,
    val goryouRealmLimitTimeMinutes: Int = 30,
    val goryouRealmBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),

    // ========== HeroTest 式神试炼 ==========
    val heroTestGreenEnable: Boolean = false,
    val heroTestSwitchSoulEnable: Boolean = false,
    val heroTestSwitchGroupTeam: String = "-1,-1",
    val heroTestSwitchSoulEnableByName: Boolean = false,
    val heroTestGroupName: String = "",
    val heroTestTeamName: String = "",
    val heroTestLayer: String = "10",
    val heroTestLimitTimeMinutes: Int = 30,
    val heroTestLimitCount: Int = 30,
    val heroTestBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),
    val heroTestLockTeam: Boolean = false,
    val heroTestExp50Buff: Boolean = false,
    val heroTestExp100Buff: Boolean = false,

    // ========== RyouToppa 寮突 ==========
    val ryouToppaNumberAttack: Int = 30,
    val ryouToppaExitFour: Boolean = false,
    val ryouToppaSwitchSoulEnable: Boolean = false,
    val ryouToppaSwitchGroupTeam: String = "-1,-1",
    val ryouToppaSwitchSoulEnableByName: Boolean = false,
    val ryouToppaGroupName: String = "",
    val ryouToppaTeamName: String = "",
    val ryouToppaAccess: Boolean = false,
    val ryouToppaLockTeam: Boolean = false,
    val ryouToppaLimitCount: Int = 30,
    val ryouToppaLimitTimeMinutes: Int = 30,
    val ryouToppaRandomDelay: Boolean = false,
    val ryouToppaBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),

    // ========== Secret 秘闻 ==========
    val secretLayer: String = "10",
    val secretSwitchSoulEnable: Boolean = false,
    val secretSwitchGroupTeam: String = "-1,-1",
    val secretSwitchSoulEnableByName: Boolean = false,
    val secretGroupName: String = "",
    val secretTeamName: String = "",
    val secretBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),
    val secretLayer10: Boolean = false,
    val secretGold50: Boolean = false,
    val secretGold100: Boolean = false,

    // ========== Sougenbi 原 ==========
    val sougenbiLayer: String = "10",
    val sougenbiSwitchSoulEnable: Boolean = false,
    val sougenbiSwitchGroupTeam: String = "-1,-1",
    val sougenbiSwitchSoulEnableByName: Boolean = false,
    val sougenbiGroupName: String = "",
    val sougenbiTeamName: String = "",
    val sougenbiBuffEnable: Boolean = false,
    val sougenbiBuffGold50: Boolean = false,
    val sougenbiBuffGold100: Boolean = false,
    val sougenbiBuffExp50: Boolean = false,
    val sougenbiBuffExp100: Boolean = false,
    val sougenbiClass: String = "gold",
    val sougenbiLockTeam: Boolean = false,
    val sougenbiLimitCount: Int = 30,
    val sougenbiLimitTimeMinutes: Int = 30,
    val sougenbiBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),

    // ========== DemonEncounter 鬼面来袭 ==========
    val demonEncounterAutoBattle: Boolean = true,
    val deSwitchSoulEnable: Boolean = false,
    val deGroupName: String = "",
    val deTeamName: String = "",

    // ========== DemonRetreat 鬼退治 ==========
    val demonRetreatAutoBattle: Boolean = true,
    val demonRetreatSwitchSoulEnable: Boolean = false,
    val demonRetreatSwitchGroupTeam: String = "-1,-1",
    val demonRetreatSwitchSoulEnableByName: Boolean = false,
    val demonRetreatGroupName: String = "",
    val demonRetreatTeamName: String = "",

    // ========== Duel 斗技 ==========
    val duelNumberAttack: Int = 5,
    val duelSwitchSoulEnable: Boolean = false,
    val duelSwitchGroupTeam: String = "-1,-1",
    val duelSwitchSoulEnableByName: Boolean = false,
    val duelGroupName: String = "",
    val duelTeamName: String = "",
    val duelLimitTimeMinutes: Int = 30,
    val duelTargetScore: Int = 1600,
    val duelGreenEnable: Boolean = false,
    val duelGreenMark: Int = 0,

    // ========== EvoZone 觉醒 ==========
    val evoZoneLayer: String = "10",
    val evoZoneUserStatus: String = "alone",
    val evoZoneGreenEnable: Boolean = false,
    val evoZoneSwitchSoulEnable: Boolean = false,
    val evoZoneSwitchGroupTeam: String = "-1,-1",
    val evoZoneSwitchSoulEnableByName: Boolean = false,
    val evoZoneGroupName: String = "",
    val evoZoneTeamName: String = "",
    val evoZoneSoulBuffEnable: Boolean = false,
    val evoZoneKirinType: String = "1",
    val evoZoneLockTeam: Boolean = false,
    val evoZoneDefaultInvite: Boolean = true,
    val evoZoneLimitCount: Int = 30,
    val evoZoneLimitTimeMinutes: Int = 30,
    val evoZoneWaitTime: Int = 60,
    val evoZoneInviteConfig: GeneralInvite.InviteConfig = GeneralInvite.InviteConfig(),
    val evoZoneBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),

    // ========== ExperienceYoukai 经验妖怪 ==========
    val experienceYoukaiUserStatus: String = "alone",
    val expYoukaiSwitchSoulEnable: Boolean = false,
    val expYoukaiSwitchGroupTeam: String = "-1,-1",
    val expYoukaiSwitchSoulEnableByName: Boolean = false,
    val expYoukaiGroupName: String = "",
    val expYoukaiTeamName: String = "",
    val expYoukaiBuffExp50: Boolean = false,
    val expYoukaiBuffExp100: Boolean = false,

    // ========== FallenSun 日轮之城 ==========
    val fallenSunLayer: String = "10",
    val fallenSunUserStatus: String = "alone",
    val fallenSunSwitchSoulEnable: Boolean = false,
    val fallenSunSwitchGroupTeam: String = "-1,-1",
    val fallenSunSwitchSoulEnableByName: Boolean = false,
    val fallenSunGroupName: String = "",
    val fallenSunTeamName: String = "",
    val fallenSunLockTeam: Boolean = false,
    val fallenSunDefaultInvite: Boolean = true,
    val fallenSunLimitCount: Int = 30,
    val fallenSunLimitTimeMinutes: Int = 30,
    val fallenSunWaitTime: Int = 60,
    val fallenSunInviteConfig: GeneralInvite.InviteConfig = GeneralInvite.InviteConfig(),
    val fallenSunBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),

    // ========== GoldYoukai 金币妖怪 ==========
    val goldYoukaiUserStatus: String = "alone",
    val goldYoukaiSwitchSoulEnable: Boolean = false,
    val goldYoukaiSwitchGroupTeam: String = "-1,-1",
    val goldYoukaiSwitchSoulEnableByName: Boolean = false,
    val goldYoukaiGroupName: String = "",
    val goldYoukaiTeamName: String = "",
    val goldYoukaiBuffGold50: Boolean = false,
    val goldYoukaiBuffGold100: Boolean = false,

    // ========== Hunt 狩猎战 ==========
    val huntGreenEnable: Boolean = false,
    val huntKirinGroupTeam: String = "-1,-1",
    val huntNetherworldGroupTeam: String = "-1,-1",
    val huntKirinBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),
    val huntNetherworldBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),

    // ========== Nian 年兽 ==========
    val nianUserStatus: String = "alone",
    val nianBattleConfig: GeneralBattleConfig = GeneralBattleConfig(),
    val nianBuffGold50: Boolean = false,
    val nianBuffGold100: Boolean = false,

    // ========== Tako 超鬼王 ==========
    val takoAutoBattle: Boolean = true,
    val takoSwitchSoulEnable: Boolean = false,
    val takoSwitchGroupTeam: String = "-1,-1",
    val takoSwitchSoulEnableByName: Boolean = false,
    val takoGroupName: String = "",
    val takoTeamName: String = "",
    val takoBuffEnable: Boolean = false,
    val takoBuffGold50: Boolean = false,
    val takoBuffGold100: Boolean = false,
    val takoBuffExp50: Boolean = false,
    val takoBuffExp100: Boolean = false,

    // ========== BondlingFairyland 契灵之境 ==========
    val bondlingFairylandLayer: String = "10",
    val bondlingFairylandUserStatus: String = "alone",
    val bondlingSwitchSoulEnable: Boolean = false,
    val bondlingSwitchGroupTeam: String = "-1,-1",
    val bondlingSwitchSoulEnableByName: Boolean = false,
    val bondlingGroupName: String = "",
    val bondlingTeamName: String = "",
    val bondlingStoneClassIndex: Int = 0,
    val bondlingMode: String = "mode1",
    val bondlingLimitCount: Int = 30,
    val bondlingUserStatus: String = "alone",
    val bondlingStoneEnable: Boolean = false,
    val bondlingLimitTimeMinutes: Int = 30,
    val bondlingWaitTimeMinutes: Int = 5,

    // ========== TrueOrochi 真八岐大蛇 ==========
    val trueOrochiLayer: String = "悲",
    val trueOrochiCurrentSuccess: Int = 0,
    val trueOrochiSwitchSoulEnable: Boolean = false,
    val trueOrochiSwitchGroupTeam: String = "-1,-1",
    val trueOrochiSwitchSoulEnableByName: Boolean = false,
    val trueOrochiGroupName: String = "",
    val trueOrochiTeamName: String = "",
    val trueOrochiFindTrueOrochi: Boolean = true,

    // ========== WantedQuests 悬赏封印 ==========
    val wantedQuestsAutoSearch: Boolean = true,
    val wqSwitchSoulEnable: Boolean = false,
    val wqSwitchGroupTeam: String = "-1,-1",
    val wqSwitchSoulEnableByName: Boolean = false,
    val wqGroupName: String = "",
    val wqTeamName: String = "",

    // ========== EternitySea 永生之海 ==========
    val eternitySeaLayer: String = "10",
    val eternitySeaUserStatus: String = "alone",
    val eternitySeaSwitchSoul1Enable: Boolean = false,
    val eternitySeaSwitchGroupTeam1: String = "-1,-1",
    val eternitySeaSwitchSoul1EnableByName: Boolean = false,
    val eternitySeaGroupName1: String = "",
    val eternitySeaTeamName1: String = "",
    val eternitySeaSwitchSoul2Enable: Boolean = false,
    val eternitySeaSwitchGroupTeam2: String = "-1,-1",
    val eternitySeaSwitchSoul2EnableByName: Boolean = false,
    val eternitySeaGroupName2: String = "",
    val eternitySeaTeamName2: String = "",
    val eternitySeaLockTeam: Boolean = false,
    val eternitySeaDefaultInvite: Boolean = true,
    val eternitySeaLimitCount: Int = 30,
    val eternitySeaLimitTimeMinutes: Int = 30,
    val eternitySeaWaitTime: Int = 60,
    val eternitySeaInviteConfig: GeneralInvite.InviteConfig = GeneralInvite.InviteConfig(),
    val eternitySeaBattleConfig: GeneralBattleConfig = GeneralBattleConfig()
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

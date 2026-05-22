package com.onmyoji.auto.model

import com.onmyoji.auto.engine.component.GeneralBattleConfig
import com.onmyoji.auto.engine.component.GeneralInvite

/**
 * 任务配置 — 普通 class + var 属性，避免 data class 287 参数构造器导致 DEX 寄存器溢出
 */
class TaskConfig {
    // ========== 探索 ==========
    var explorationLevel: String = "第二十八章"
    var limitTimeMinutes: Int = 30
    var minionsCount: Int = 30
    var autoRotate: Boolean = true
    var chooseRarity: String = "N卡"

    // 绘卷模式
    var scrollsEnable: Boolean = true
    var scrollsThreshold: Int = 25

    // 个人突破
    var numberAttack: Int = 30
    var exitFour: Boolean = true
    var orderAttack: String = "5>4>3>2>1>0"
    var threeRefresh: Boolean = false

    // 通用战斗
    var lockTeam: Boolean = false

    // ========== SoulsTidy 御魂整理 ==========
    var soulsTidyEnableGreed: Boolean = true
    var soulsTidyEnableManeki: Boolean = true

    // ========== CollectiveMissions 集体任务 ==========
    var collectiveMissionsSelect: String = "觉醒三"
    var collectiveMissionsType: String = "donate"

    // ========== Delegation 委派 ==========
    var delegationNames: List<String> = listOf("画", "鸟羽", "寻找耳环", "猫老大", "接送", "痕迹")

    // ========== GuildActivityMonitor 寮活动监控 ==========
    var guildActivityMonitorRunDays: String = "1,2,3,4,5,6,7"
    var guildActivityMonitorDuration: Int = 15
    var guildActivityMonitorInterval: Int = 30
    var guildActivityMonitorActivities: String = "道馆,狭间,宴会,退治"

    // ========== MemoryScrolls 记忆绘卷 ==========
    var memoryScrollsNumber: Int = 1
    var memoryScrollsAutoContribute: Boolean = true

    // ========== Pets 宠物 ==========
    var petsFeast: Boolean = true

    // ========== TalismanPass 花合战 ==========
    var talismanPassLevelReward: Int = 2

    // ========== WeeklyTrifles 每周杂务 ==========
    var weeklyTriflesShareCollect: Boolean = true
    var weeklyTriflesShareAreaBoss: Boolean = true
    var weeklyTriflesShareSecret: Boolean = true
    var weeklyTriflesBrokenAmulet: Int = 100

    // ========== FloatParade 花车巡游 ==========
    var floatParadeLevelReward1: Int = 3
    var floatParadeLevelReward2: Int = 1

    // ========== DailyTrifles 每日杂务 ==========
    var dailyTriflesOneSummon: Boolean = true
    var dailyTriflesFriendLove: Boolean = false
    var dailyTriflesLuckMsg: Boolean = false
    var dailyTriflesStoreSign: Boolean = false
    var dailyTriflesBuySushiCount: Int = -1

    // ========== MysteryShop 神秘商店 ==========
    var mysteryShopMysteryAmulet: Boolean = false
    var mysteryShopBlackDarumaScrap: Boolean = false
    var mysteryShopTaiko3: Boolean = false
    var mysteryShopTaiko4: Boolean = false

    // ========== KittyShop 猫咪商店 ==========
    var kittyShopAttempts: Int = 1
    var kittyShopQuitWhenFinished: Boolean = false

    // ========== Orochi 八岐大蛇 ==========
    var orochiLayer: String = "悲"
    var orochiUserStatus: String = "alone"
    var orochiSoulBuffEnable: Boolean = false
    var orochiGreenEnable: Boolean = false
    var orochiGreenMark: String = "none"
    var orochiSwitchSoulEnable: Boolean = false
    var orochiSwitchGroupTeam: String = "-1,-1"
    var orochiSwitchSoulEnableByName: Boolean = false
    var orochiGroupName: String = ""
    var orochiTeamName: String = ""
    var orochiLockTeam: Boolean = false
    var orochiDefaultInvite: Boolean = true
    var orochiLimitCount: Int = 30
    var orochiLimitTimeMinutes: Int = 30
    var orochiWaitTime: Int = 60
    var orochiInviteConfig: GeneralInvite.InviteConfig = GeneralInvite.InviteConfig()
    var orochiBattleConfig: GeneralBattleConfig = GeneralBattleConfig()
    var orochiAutoSwitchSoul: Boolean = false
    var orochiTenSwitch: String = "-1,-1"
    var orochiElevenSwitch: String = "-1,-1"
    var orochiTwelveSwitch: String = "-1,-1"
    var orochiThirteenSwitch: String = "-1,-1"

    // ========== AreaBoss 地域鬼王 ==========
    var areaBossNumber: Int = 3
    var areaBossUseCollect: Boolean = false
    var areaBossBossReward: Boolean = true
    var areaBossFloor: String = "day"
    var areaBossSwitchSoulEnable: Boolean = false
    var areaBossSwitchGroupTeam: String = "-1,-1"
    var areaBossSwitchSoulEnableByName: Boolean = false
    var areaBossGroupName: String = ""
    var areaBossTeamName: String = ""
    var areaBossReward: Boolean = true
    var areaBossAttack60: Boolean = false
    var areaBossRewardFloor: String = "1"
    var areaBossBattleConfig: GeneralBattleConfig = GeneralBattleConfig()

    // ========== AbyssShadows 深渊暗影 ==========
    var abyssShadowsTeam: Int = 1
    var abyssShadowsGreenEnable: Boolean = false
    var abyssShadowsSwitchSoulEnable: Boolean = false
    var abyssShadowsSwitchGroupTeam: String = "-1,-1"
    var abyssShadowsSwitchSoulEnableByName: Boolean = false
    var abyssShadowsGroupName: String = ""
    var abyssShadowsTeamName: String = ""
    var abyssShadowsCombatTimeEnable: Boolean = false
    var abyssShadowsBossCombatTime: Int = 60
    var abyssShadowsGeneralCombatTime: Int = 60
    var abyssShadowsEliteCombatTime: Int = 60

    // ========== DyeTrials 染色试炼 ==========
    var dyeTrialsLayer: String = "10"
    var dyeTrialsSwitchSoulEnable: Boolean = false
    var dyeTrialsSwitchGroupTeam: String = "-1,-1"
    var dyeTrialsSwitchSoulEnableByName: Boolean = false
    var dyeTrialsGroupName: String = ""
    var dyeTrialsTeamName: String = ""

    // ========== GoryouRealm 御灵境 ==========
    var goryouRealmLayer: String = "3"
    var goryouRealmSwitchSoulEnable: Boolean = false
    var goryouRealmSwitchGroupTeam: String = "-1,-1"
    var goryouRealmClass: String = "1"
    var goryouRealmLockTeam: Boolean = false
    var goryouRealmLimitCount: Int = 30
    var goryouRealmLimitTimeMinutes: Int = 30
    var goryouRealmBattleConfig: GeneralBattleConfig = GeneralBattleConfig()

    // ========== HeroTest 式神试炼 ==========
    var heroTestGreenEnable: Boolean = false
    var heroTestSwitchSoulEnable: Boolean = false
    var heroTestSwitchGroupTeam: String = "-1,-1"
    var heroTestSwitchSoulEnableByName: Boolean = false
    var heroTestGroupName: String = ""
    var heroTestTeamName: String = ""
    var heroTestLayer: String = "10"
    var heroTestLimitTimeMinutes: Int = 30
    var heroTestLimitCount: Int = 30
    var heroTestBattleConfig: GeneralBattleConfig = GeneralBattleConfig()
    var heroTestLockTeam: Boolean = false
    var heroTestExp50Buff: Boolean = false
    var heroTestExp100Buff: Boolean = false

    // ========== RyouToppa 寮突 ==========
    var ryouToppaNumberAttack: Int = 30
    var ryouToppaExitFour: Boolean = false
    var ryouToppaSwitchSoulEnable: Boolean = false
    var ryouToppaSwitchGroupTeam: String = "-1,-1"
    var ryouToppaSwitchSoulEnableByName: Boolean = false
    var ryouToppaGroupName: String = ""
    var ryouToppaTeamName: String = ""
    var ryouToppaAccess: Boolean = false
    var ryouToppaLockTeam: Boolean = false
    var ryouToppaLimitCount: Int = 30
    var ryouToppaLimitTimeMinutes: Int = 30
    var ryouToppaRandomDelay: Boolean = false
    var ryouToppaBattleConfig: GeneralBattleConfig = GeneralBattleConfig()

    // ========== Secret 秘闻 ==========
    var secretLayer: String = "10"
    var secretSwitchSoulEnable: Boolean = false
    var secretSwitchGroupTeam: String = "-1,-1"
    var secretSwitchSoulEnableByName: Boolean = false
    var secretGroupName: String = ""
    var secretTeamName: String = ""
    var secretBattleConfig: GeneralBattleConfig = GeneralBattleConfig()
    var secretLayer10: Boolean = false
    var secretGold50: Boolean = false
    var secretGold100: Boolean = false

    // ========== Sougenbi 原 ==========
    var sougenbiLayer: String = "10"
    var sougenbiSwitchSoulEnable: Boolean = false
    var sougenbiSwitchGroupTeam: String = "-1,-1"
    var sougenbiSwitchSoulEnableByName: Boolean = false
    var sougenbiGroupName: String = ""
    var sougenbiTeamName: String = ""
    var sougenbiBuffEnable: Boolean = false
    var sougenbiBuffGold50: Boolean = false
    var sougenbiBuffGold100: Boolean = false
    var sougenbiBuffExp50: Boolean = false
    var sougenbiBuffExp100: Boolean = false
    var sougenbiClass: String = "gold"
    var sougenbiLockTeam: Boolean = false
    var sougenbiLimitCount: Int = 30
    var sougenbiLimitTimeMinutes: Int = 30
    var sougenbiBattleConfig: GeneralBattleConfig = GeneralBattleConfig()

    // ========== DemonEncounter 鬼面来袭 ==========
    var demonEncounterAutoBattle: Boolean = true
    var deSwitchSoulEnable: Boolean = false
    var deGroupName: String = ""
    var deTeamName: String = ""

    // ========== DemonRetreat 鬼退治 ==========
    var demonRetreatAutoBattle: Boolean = true
    var demonRetreatSwitchSoulEnable: Boolean = false
    var demonRetreatSwitchGroupTeam: String = "-1,-1"
    var demonRetreatSwitchSoulEnableByName: Boolean = false
    var demonRetreatGroupName: String = ""
    var demonRetreatTeamName: String = ""

    // ========== Duel 斗技 ==========
    var duelNumberAttack: Int = 5
    var duelSwitchSoulEnable: Boolean = false
    var duelSwitchGroupTeam: String = "-1,-1"
    var duelSwitchSoulEnableByName: Boolean = false
    var duelGroupName: String = ""
    var duelTeamName: String = ""
    var duelLimitTimeMinutes: Int = 30
    var duelTargetScore: Int = 1600
    var duelGreenEnable: Boolean = false
    var duelGreenMark: Int = 0

    // ========== EvoZone 觉醒 ==========
    var evoZoneLayer: String = "10"
    var evoZoneUserStatus: String = "alone"
    var evoZoneGreenEnable: Boolean = false
    var evoZoneSwitchSoulEnable: Boolean = false
    var evoZoneSwitchGroupTeam: String = "-1,-1"
    var evoZoneSwitchSoulEnableByName: Boolean = false
    var evoZoneGroupName: String = ""
    var evoZoneTeamName: String = ""
    var evoZoneSoulBuffEnable: Boolean = false
    var evoZoneKirinType: String = "1"
    var evoZoneLockTeam: Boolean = false
    var evoZoneDefaultInvite: Boolean = true
    var evoZoneLimitCount: Int = 30
    var evoZoneLimitTimeMinutes: Int = 30
    var evoZoneWaitTime: Int = 60
    var evoZoneInviteConfig: GeneralInvite.InviteConfig = GeneralInvite.InviteConfig()
    var evoZoneBattleConfig: GeneralBattleConfig = GeneralBattleConfig()

    // ========== ExperienceYoukai 经验妖怪 ==========
    var experienceYoukaiUserStatus: String = "alone"
    var expYoukaiSwitchSoulEnable: Boolean = false
    var expYoukaiSwitchGroupTeam: String = "-1,-1"
    var expYoukaiSwitchSoulEnableByName: Boolean = false
    var expYoukaiGroupName: String = ""
    var expYoukaiTeamName: String = ""
    var expYoukaiBuffExp50: Boolean = false
    var expYoukaiBuffExp100: Boolean = false

    // ========== FallenSun 日轮之城 ==========
    var fallenSunLayer: String = "10"
    var fallenSunUserStatus: String = "alone"
    var fallenSunSwitchSoulEnable: Boolean = false
    var fallenSunSwitchGroupTeam: String = "-1,-1"
    var fallenSunSwitchSoulEnableByName: Boolean = false
    var fallenSunGroupName: String = ""
    var fallenSunTeamName: String = ""
    var fallenSunLockTeam: Boolean = false
    var fallenSunDefaultInvite: Boolean = true
    var fallenSunLimitCount: Int = 30
    var fallenSunLimitTimeMinutes: Int = 30
    var fallenSunWaitTime: Int = 60
    var fallenSunInviteConfig: GeneralInvite.InviteConfig = GeneralInvite.InviteConfig()
    var fallenSunBattleConfig: GeneralBattleConfig = GeneralBattleConfig()

    // ========== GoldYoukai 金币妖怪 ==========
    var goldYoukaiUserStatus: String = "alone"
    var goldYoukaiSwitchSoulEnable: Boolean = false
    var goldYoukaiSwitchGroupTeam: String = "-1,-1"
    var goldYoukaiSwitchSoulEnableByName: Boolean = false
    var goldYoukaiGroupName: String = ""
    var goldYoukaiTeamName: String = ""
    var goldYoukaiBuffGold50: Boolean = false
    var goldYoukaiBuffGold100: Boolean = false

    // ========== Hunt 狩猎战 ==========
    var huntGreenEnable: Boolean = false
    var huntKirinGroupTeam: String = "-1,-1"
    var huntNetherworldGroupTeam: String = "-1,-1"
    var huntKirinBattleConfig: GeneralBattleConfig = GeneralBattleConfig()
    var huntNetherworldBattleConfig: GeneralBattleConfig = GeneralBattleConfig()

    // ========== Nian 年兽 ==========
    var nianUserStatus: String = "alone"
    var nianBattleConfig: GeneralBattleConfig = GeneralBattleConfig()
    var nianBuffGold50: Boolean = false
    var nianBuffGold100: Boolean = false

    // ========== Tako 超鬼王 ==========
    var takoAutoBattle: Boolean = true
    var takoSwitchSoulEnable: Boolean = false
    var takoSwitchGroupTeam: String = "-1,-1"
    var takoSwitchSoulEnableByName: Boolean = false
    var takoGroupName: String = ""
    var takoTeamName: String = ""
    var takoBuffEnable: Boolean = false
    var takoBuffGold50: Boolean = false
    var takoBuffGold100: Boolean = false
    var takoBuffExp50: Boolean = false
    var takoBuffExp100: Boolean = false

    // ========== BondlingFairyland 契灵之境 ==========
    var bondlingFairylandLayer: String = "10"
    var bondlingFairylandUserStatus: String = "alone"
    var bondlingSwitchSoulEnable: Boolean = false
    var bondlingSwitchGroupTeam: String = "-1,-1"
    var bondlingSwitchSoulEnableByName: Boolean = false
    var bondlingGroupName: String = ""
    var bondlingTeamName: String = ""
    var bondlingStoneClassIndex: Int = 0
    var bondlingMode: String = "mode1"
    var bondlingLimitCount: Int = 30
    var bondlingUserStatus: String = "alone"
    var bondlingStoneEnable: Boolean = false
    var bondlingLimitTimeMinutes: Int = 30
    var bondlingWaitTimeMinutes: Int = 5

    // ========== TrueOrochi 真八岐大蛇 ==========
    var trueOrochiLayer: String = "悲"
    var trueOrochiCurrentSuccess: Int = 0
    var trueOrochiSwitchSoulEnable: Boolean = false
    var trueOrochiSwitchGroupTeam: String = "-1,-1"
    var trueOrochiSwitchSoulEnableByName: Boolean = false
    var trueOrochiGroupName: String = ""
    var trueOrochiTeamName: String = ""
    var trueOrochiFindTrueOrochi: Boolean = true

    // ========== WantedQuests 悬赏封印 ==========
    var wantedQuestsAutoSearch: Boolean = true
    var wqSwitchSoulEnable: Boolean = false
    var wqSwitchGroupTeam: String = "-1,-1"
    var wqSwitchSoulEnableByName: Boolean = false
    var wqGroupName: String = ""
    var wqTeamName: String = ""

    // ========== EternitySea 永生之海 ==========
    var eternitySeaLayer: String = "10"
    var eternitySeaUserStatus: String = "alone"
    var eternitySeaSwitchSoul1Enable: Boolean = false
    var eternitySeaSwitchGroupTeam1: String = "-1,-1"
    var eternitySeaSwitchSoul1EnableByName: Boolean = false
    var eternitySeaGroupName1: String = ""
    var eternitySeaTeamName1: String = ""
    var eternitySeaSwitchSoul2Enable: Boolean = false
    var eternitySeaSwitchGroupTeam2: String = "-1,-1"
    var eternitySeaSwitchSoul2EnableByName: Boolean = false
    var eternitySeaGroupName2: String = ""
    var eternitySeaTeamName2: String = ""
    var eternitySeaLockTeam: Boolean = false
    var eternitySeaDefaultInvite: Boolean = true
    var eternitySeaLimitCount: Int = 30
    var eternitySeaLimitTimeMinutes: Int = 30
    var eternitySeaWaitTime: Int = 60
    var eternitySeaInviteConfig: GeneralInvite.InviteConfig = GeneralInvite.InviteConfig()
    var eternitySeaBattleConfig: GeneralBattleConfig = GeneralBattleConfig()

    /**
     * 兼容 data class 的 copy() 用法：复制当前对象的所有属性到新实例
     */
    fun copy(): TaskConfig {
        val new = TaskConfig()
        // 通过反射复制所有 var 属性
        for (field in TaskConfig::class.java.declaredFields) {
            if (field.name.startsWith("Companion") || field.name.startsWith("$")) continue
            try {
                field.isAccessible = true
                field.set(new, field.get(this))
            } catch (_: Exception) {}
        }
        return new
    }
}

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

package com.onmyoji.auto.engine.component

/**
 * 通用战斗配置 — 对应 OAS config_general_battle.py
 */
data class GeneralBattleConfig(
    // 是否锁定阵容, 有些的战斗是外边的锁定阵容甚至有些的战斗没有锁定阵容的
    val lockTeamEnable: Boolean = false,

    // 是否启动 预设队伍
    val presetEnable: Boolean = false,
    // 选哪一个预设组 [1-7]
    val presetGroup: Int = 1,
    // 选哪一个队伍 [1-5]
    val presetTeam: Int = 1,

    // 是否开启绿标
    val greenEnable: Boolean = false,
    // 选哪一个绿标
    val greenMark: GreenMarkType = GreenMarkType.GREEN_LEFT1,

    // 是否启动战斗时随机点击或者随机滑动
    val randomClickSwipeEnable: Boolean = false
)

/**
 * 绿标类型 — 对应 OAS GreenMarkType
 */
enum class GreenMarkType {
    GREEN_LEFT1,
    GREEN_LEFT2,
    GREEN_LEFT3,
    GREEN_LEFT4,
    GREEN_LEFT5,
    GREEN_MAIN
}

/**
 * Buff 类型 — 对应 OAS BuffClass
 */
enum class BuffClass {
    AWAKE,          // 觉醒
    SOUL,           // 御魂
    GOLD_50,        // 金币50
    GOLD_100,       // 金币100
    EXP_50,         // 经验50
    EXP_100,        // 经验100
    AWAKE_CLOSE,    // 觉醒关闭
    SOUL_CLOSE,     // 御魂关闭
    GOLD_50_CLOSE,  // 金币50关闭
    GOLD_100_CLOSE, // 金币100关闭
    EXP_50_CLOSE,   // 经验50关闭
    EXP_100_CLOSE   // 经验100关闭
}

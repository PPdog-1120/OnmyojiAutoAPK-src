package com.onmyoji.auto.engine.component

import android.content.Context
import android.graphics.Bitmap
import com.onmyoji.auto.engine.*
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 组队邀请组件 — 对应 OAS general_invite.py
 *
 * 提供邀请好友、等待队员、开启挑战等功能
 */
class GeneralInvite(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 (GeneralInviteAssets) ==========

    // 中间的邀请图片
    private val I_ADD_1 = RuleImage("add_1",
        "general_invite/gi/gi_add_1.png",
        intArrayOf(596, 241, 114, 51), intArrayOf(569, 196, 186, 161), 0.9f)
    // 最右边的邀请
    private val I_ADD_2 = RuleImage("add_2",
        "general_invite/gi/gi_add_2.png",
        intArrayOf(1013, 203, 100, 100), intArrayOf(970, 151, 193, 220), 0.8f)
    // 挑战按钮
    private val I_FIRE = RuleImage("fire",
        "general_invite/gi/gi_fire.png",
        intArrayOf(1179, 602, 81, 74), intArrayOf(1179, 602, 81, 74), 0.8f)
    // 永生之海挑战
    private val I_FIRE_SEA = RuleImage("fire_sea",
        "general_invite/gi/gi_fire_sea.png",
        intArrayOf(1160, 586, 100, 68), intArrayOf(1160, 586, 100, 68), 0.8f)
    // 锁定阵容
    private val I_LOCK = RuleImage("lock",
        "general_invite/gi/gi_lock.png",
        intArrayOf(29, 644, 29, 32), intArrayOf(29, 644, 29, 32), 0.8f)
    // 未锁定阵容
    private val I_UNLOCK = RuleImage("unlock",
        "general_invite/gi/gi_unlock.png",
        intArrayOf(30, 647, 23, 30), intArrayOf(30, 647, 23, 30), 0.8f)
    // 匹配中
    private val I_MATCHING = RuleImage("matching",
        "general_invite/gi/gi_matching.png",
        intArrayOf(51, 574, 52, 114), intArrayOf(51, 574, 52, 114), 0.8f)
    // 永生之海锁定
    private val I_LOCK_SEA = RuleImage("lock_sea",
        "general_invite/gi/gi_lock_sea.png",
        intArrayOf(781, 658, 27, 28), intArrayOf(781, 658, 27, 28), 0.8f)
    // 永生之海未锁定
    private val I_UNLOCK_SEA = RuleImage("unlock_sea",
        "general_invite/gi/gi_unlock_sea.png",
        intArrayOf(781, 656, 27, 30), intArrayOf(781, 656, 27, 30), 0.8f)
    // 五人队伍加号
    private val I_ADD_5_1 = RuleImage("add_5_1",
        "general_invite/gi/gi_add_5_1.png",
        intArrayOf(370, 243, 100, 100), intArrayOf(370, 243, 100, 100), 0.8f)
    private val I_ADD_5_2 = RuleImage("add_5_2",
        "general_invite/gi/gi_add_5_2.png",
        intArrayOf(612, 263, 100, 100), intArrayOf(612, 263, 100, 100), 0.8f)
    private val I_ADD_5_3 = RuleImage("add_5_3",
        "general_invite/gi/gi_add_5_3.png",
        intArrayOf(862, 243, 100, 100), intArrayOf(862, 243, 100, 100), 0.8f)
    private val I_ADD_5_4 = RuleImage("add_5_4",
        "general_invite/gi/gi_add_5_4.png",
        intArrayOf(1118, 228, 100, 100), intArrayOf(1118, 228, 100, 100), 0.8f)
    // 永生之海添加好友
    private val I_ADD_SEA = RuleImage("add_sea",
        "general_invite/gi/gi_add_sea.png",
        intArrayOf(836, 231, 100, 100), intArrayOf(836, 231, 100, 100), 0.8f)
    // 好友加载中
    private val I_LOAD_FRIEND = RuleImage("load_friend",
        "general_invite/gi/gi_load_friend.png",
        intArrayOf(709, 546, 134, 60), intArrayOf(709, 546, 134, 60), 0.8f)
    // 左上角退出
    private val I_BACK_YELLOW = RuleImage("back_yellow",
        "general_invite/gi/gi_back_yellow.png",
        intArrayOf(19, 13, 58, 55), intArrayOf(19, 13, 58, 55), 0.8f)
    // 永生之海退出
    private val I_BACK_YELLOW_SEA = RuleImage("back_yellow_sea",
        "general_invite/gi/gi_back_yellow_sea.png",
        intArrayOf(31, 16, 42, 42), intArrayOf(31, 16, 42, 42), 0.8f)
    // 邀请确认
    private val I_INVITE_ENSURE = RuleImage("invite_ensure",
        "general_invite/gi/gi_invite_ensure.png",
        intArrayOf(710, 544, 132, 60), intArrayOf(710, 544, 132, 60), 0.8f)
    // 选中好友
    private val I_SELECTED = RuleImage("selected",
        "general_invite/gi/gi_selected.png",
        intArrayOf(895, 373, 33, 32), intArrayOf(895, 373, 33, 32), 0.8f)
    // 好友列表标签
    private val I_FLAG_1_ON = RuleImage("flag_1_on",
        "general_invite/gi/gi_flag_1_on.png",
        intArrayOf(354, 126, 62, 21), intArrayOf(354, 126, 62, 21), 0.8f)
    private val I_FLAG_1_OFF = RuleImage("flag_1_off",
        "general_invite/gi/gi_flag_1_off.png",
        intArrayOf(353, 126, 58, 22), intArrayOf(353, 126, 58, 22), 0.8f)
    private val I_FLAG_2_ON = RuleImage("flag_2_on",
        "general_invite/gi/gi_flag_2_on.png",
        intArrayOf(472, 82, 32, 11), intArrayOf(454, 74, 56, 26), 0.8f)
    private val I_FLAG_2_OFF = RuleImage("flag_2_off",
        "general_invite/gi/gi_flag_2_off.png",
        intArrayOf(469, 127, 58, 21), intArrayOf(469, 127, 58, 21), 0.8f)
    private val I_FLAG_3_ON = RuleImage("flag_3_on",
        "general_invite/gi/gi_flag_3_on.png",
        intArrayOf(588, 126, 48, 22), intArrayOf(588, 126, 48, 22), 0.8f)
    private val I_FLAG_3_OFF = RuleImage("flag_3_off",
        "general_invite/gi/gi_flag_3_off.png",
        intArrayOf(590, 126, 41, 22), intArrayOf(590, 126, 41, 22), 0.8f)
    private val I_FLAG_4_ON = RuleImage("flag_4_on",
        "general_invite/gi/gi_flag_4_on.png",
        intArrayOf(713, 128, 34, 21), intArrayOf(713, 128, 34, 21), 0.8f)
    private val I_FLAG_4_OFF = RuleImage("flag_4_off",
        "general_invite/gi/gi_flag_4_off.png",
        intArrayOf(703, 128, 53, 21), intArrayOf(703, 128, 53, 21), 0.8f)
    // 拒绝邀请
    val I_I_REJECT_1 = RuleImage("i_reject_1",
        "general_invite/gi/gi_i_reject.png",
        intArrayOf(5, 210, 110, 95), intArrayOf(5, 210, 110, 95), 0.8f)
    val I_I_REJECT_2 = RuleImage("i_reject_2",
        "general_invite/gi/gi_i_reject.png",
        intArrayOf(5, 320, 110, 95), intArrayOf(5, 320, 110, 95), 0.8f)
    val I_I_REJECT_3 = RuleImage("i_reject_3",
        "general_invite/gi/gi_i_reject.png",
        intArrayOf(5, 430, 110, 95), intArrayOf(5, 430, 110, 95), 0.8f)
    // 接受邀请
    private val I_I_ACCEPT = RuleImage("i_accept",
        "general_invite/gi/gi_i_accept.png",
        intArrayOf(113, 225, 63, 72), intArrayOf(113, 225, 63, 280), 0.8f)
    // 默认接受邀请
    private val I_I_ACCEPT_DEFAULT = RuleImage("i_accept_default",
        "general_invite/gi/gi_i_accept_default.png",
        intArrayOf(205, 223, 61, 68), intArrayOf(205, 223, 61, 297), 0.8f)
    // 不勾选默认邀请
    private val I_I_NO_DEFAULT = RuleImage("i_no_default",
        "general_invite/gi/gi_i_no_default.png",
        intArrayOf(542, 343, 36, 35), intArrayOf(542, 343, 36, 35), 0.8f)
    // 勾选默认邀请
    private val I_I_DEFAULT = RuleImage("i_default",
        "general_invite/gi/gi_i_default.png",
        intArrayOf(541, 342, 41, 39), intArrayOf(541, 342, 41, 39), 0.8f)
    // 队长邀请确定
    private val I_GI_SURE = RuleImage("gi_sure",
        "general_invite/gi/gi_gi_sure.png",
        intArrayOf(670, 402, 175, 60), intArrayOf(670, 402, 175, 60), 0.8f)
    // 左上角协战房间
    private val I_GI_IN_ROOM = RuleImage("gi_in_room",
        "general_invite/gi/gi_gi_in_room.png",
        intArrayOf(92, 17, 213, 64), intArrayOf(92, 17, 213, 64), 0.8f)
    // 表情按钮
    private val I_GI_EMOJI_1 = RuleImage("gi_emoji_1",
        "general_invite/gi/gi_gi_emoji_1.png",
        intArrayOf(27, 526, 55, 51), intArrayOf(27, 526, 55, 51), 0.8f)
    private val I_GI_EMOJI_2 = RuleImage("gi_emoji_2",
        "general_invite/gi/gi_gi_emoji_1.png",
        intArrayOf(27, 622, 55, 51), intArrayOf(27, 622, 55, 51), 0.8f)
    // 庭院标识
    private val I_GI_HOME = RuleImage("gi_home",
        "general_invite/gi/gi_gi_home.png",
        intArrayOf(361, 34, 34, 46), intArrayOf(361, 34, 34, 46), 0.8f)
    // 探索标识
    private val I_GI_EXPLORE = RuleImage("gi_explore",
        "general_invite/gi/gi_gi_explore.png",
        intArrayOf(1138, 119, 41, 48), intArrayOf(1138, 119, 41, 48), 0.8f)

    // ========== 房间类型 ==========

    enum class RoomType {
        NORMAL_2,       // 房间只可以两个人的：探索
        NORMAL_3,       // 房间可以两三个人的：觉醒、御魂、日轮、石距
        ETERNITY_SEA,   // 永生之海
        NORMAL_5        // 经验妖怪和金币妖怪
    }

    enum class InviteNumber { ONE, TWO }
    enum class FindMode { AUTO_FIND, RECENT_FRIEND }

    // 邀请配置
    data class InviteConfig(
        val inviteNumber: InviteNumber = InviteNumber.ONE,
        val friend1: String = "",
        val friend2: String = "",
        val findMode: FindMode = FindMode.AUTO_FIND,
        val waitTimeSeconds: Int = 120, // 默认 2 分钟
        val defaultInvite: Boolean = true
    )

    // 状态
    private var currentRoomType: RoomType? = null
    private var inviteStartTime: Long = 0
    private var waitStartTime: Long = 0
    private var emojiLastTime: Long = 0

    override suspend fun run() {
        // GeneralInvite 不直接 run
        log("GeneralInvite: 请在子任务中调用 runInvite()")
    }

    /**
     * 队长身份在组队界面邀请好友 — 对应 OAS run_invite
     *
     * 请注意，返回的时候成功时是进入战斗了！！！
     * 如果是失败，那就是没有队友进入，然后会退出房间的界面
     */
    suspend fun runInvite(config: InviteConfig, isFirst: Boolean = false): Boolean {
        log("Invite friend")
        if (!ensureEnter()) {
            log("Not enter invite page")
            return false
        }

        if (isFirst) {
            currentRoomType = checkRoomType()
            log("Room type: $currentRoomType")
            inviteStartTime = System.currentTimeMillis()
            ensureRoomType(config.inviteNumber)
            inviteFriends(config)
        } else {
            inviteStartTime = System.currentTimeMillis()
            emojiLastTime = System.currentTimeMillis()
        }

        val waitDeadline = System.currentTimeMillis() + config.waitTimeSeconds * 1000L
        val inviteInterval = if (isFirst) 20_000L else 30_000L

        while (true) {
            val img = screenshot() ?: continue

            // 等待超时
            if (System.currentTimeMillis() > waitDeadline) {
                log("Wait timeout")
                return false
            }
            if (appear(I_MATCHING, img)) {
                log("Timeout, now is no room")
                return false
            }

            if (!isInRoom(false, img)) continue

            // 表情操作防止断线
            if (System.currentTimeMillis() - emojiLastTime > 20_000) {
                emojiLastTime = System.currentTimeMillis()
                appearThenClick(I_GI_EMOJI_1, img)
                appearThenClick(I_GI_EMOJI_2, img)
            }

            var fire = false

            // 判断是否可以开启挑战
            when (currentRoomType) {
                RoomType.NORMAL_2 -> {
                    if (!appear(I_ADD_2, img)) {
                        log("Start challenge, room can only invite one friend")
                        fire = true
                    }
                }
                RoomType.NORMAL_3 -> {
                    if (config.inviteNumber == InviteNumber.ONE && !appear(I_ADD_1, img)) {
                        log("Start challenge, user only invite one friend")
                        fire = true
                    } else if (config.inviteNumber == InviteNumber.TWO && !appear(I_ADD_2, img)) {
                        log("Start challenge, user invite two friends")
                        fire = true
                    }
                }
                RoomType.NORMAL_5 -> {
                    if (config.inviteNumber == InviteNumber.ONE && !appear(I_ADD_5_1, img)) {
                        log("Start challenge, user only invite one friend")
                        fire = true
                    } else if (config.inviteNumber == InviteNumber.TWO && !appear(I_ADD_5_2, img)) {
                        log("Start challenge, user invite two friends")
                        fire = true
                    }
                }
                RoomType.ETERNITY_SEA -> {
                    if (!appear(I_ADD_SEA, img)) {
                        log("Start challenge, this is lock sea")
                        fire = true
                    }
                }
                null -> {}
            }

            // 点击挑战
            if (fire) {
                clickFire()
                return true
            }

            // 定时重新邀请
            if (System.currentTimeMillis() - inviteStartTime > inviteInterval) {
                if (isFirst) {
                    log("Invitation is triggered every 20s")
                    inviteStartTime = System.currentTimeMillis()
                } else {
                    log("Wait for 30s and invite again")
                    inviteStartTime = System.currentTimeMillis() + 999_999_999 // 不再触发
                }
                inviteFriends(config)
            }
        }
    }

    /**
     * 确认是否进入了组队界面 — 对应 OAS ensure_enter
     */
    private suspend fun ensureEnter(): Boolean {
        log("Ensure enter invite page")
        while (true) {
            val img = screenshot() ?: continue
            if (appear(I_ADD_2, img)) return true
            if (appear(I_ADD_5_4, img)) return true
            if (appear(I_LOCK_SEA, img)) return true
            if (appear(I_UNLOCK_SEA, img)) return true
            if (appear(I_GI_IN_ROOM, img)) return true
            if (appear(I_MATCHING, img)) return false
        }
    }

    /**
     * 判断是否在房间里面 — 对应 OAS is_in_room
     */
    fun isInRoom(isScreenshot: Boolean = true, existingImg: Bitmap? = null): Boolean {
        val img = if (isScreenshot) screenshot() else existingImg ?: return false
        return appear(I_GI_EMOJI_1, img) || appear(I_GI_EMOJI_2, img)
    }

    /**
     * 退出房间 — 对应 OAS exit_room
     */
    suspend fun exitRoom(): Boolean {
        if (!isInRoom()) return false
        log("Exit room")
        while (true) {
            val img = screenshot() ?: continue
            if (!isInRoom(false, img) &&
                !appearThenClick(I_GI_SURE, img, 800) &&
                !appear(I_BACK_YELLOW, img)) {
                break
            }
            if (appearThenClick(I_GI_SURE, img, 500)) continue
            if (!appear(I_GI_SURE, img) && appearThenClick(I_BACK_YELLOW, img, 800)) {
                waitUntilAppear(I_GI_SURE, 800)
                continue
            }
            if (!appear(I_GI_SURE, img) && appearThenClick(I_BACK_YELLOW_SEA, img, 800)) {
                waitUntilAppear(I_GI_SURE, 800)
                continue
            }
        }
        return true
    }

    /**
     * 点击挑战 — 对应 OAS click_fire
     */
    private suspend fun clickFire() {
        while (true) {
            val img = screenshot() ?: continue
            if (!isInRoom(false, img)) break
            if (appearThenClick(I_FIRE, img, 1000)) continue
            if (appearThenClick(I_FIRE_SEA, img, 1000)) continue
        }
    }

    /**
     * 检查房间类型 — 对应 OAS check_room_type
     */
    private fun checkRoomType(img: Bitmap? = null): RoomType? {
        val image = img ?: screenshot() ?: return null

        fun check2(): Boolean = !I_ADD_1.match(image, context).matched && I_ADD_2.match(image, context).matched
        fun check3(): Boolean = I_ADD_1.match(image, context).matched && I_ADD_2.match(image, context).matched
        fun check5(): Boolean = I_ADD_5_1.match(image, context).matched && I_ADD_5_2.match(image, context).matched &&
                I_ADD_5_3.match(image, context).matched && I_ADD_5_4.match(image, context).matched
        fun checkSea(): Boolean = I_LOCK_SEA.match(image, context).matched || I_UNLOCK_SEA.match(image, context).matched

        return when {
            check2() -> RoomType.NORMAL_2
            check3() -> RoomType.NORMAL_3
            check5() -> RoomType.NORMAL_5
            checkSea() -> RoomType.ETERNITY_SEA
            else -> null
        }
    }

    /**
     * 确认邀请人数是否超出房间最大值 — 对应 OAS ensure_room_type
     */
    private fun ensureRoomType(inviteNumber: InviteNumber): Boolean {
        val friendNumber = if (inviteNumber == InviteNumber.ONE) 1 else 2
        if (friendNumber == 2) {
            if (currentRoomType == RoomType.NORMAL_2) {
                log("Room can only be one people, but invite two people")
                return false
            }
            if (currentRoomType == RoomType.ETERNITY_SEA) {
                log("Room can only be one people, but invite two people")
                return false
            }
        }
        return true
    }

    /**
     * 邀请好友列表 — 对应 OAS invite_friends
     */
    private suspend fun inviteFriends(config: InviteConfig) {
        val success1 = inviteFriend(config.friend1, config.findMode)
        if (!success1) log("Invite friend 1 failed")
        if (config.inviteNumber == InviteNumber.TWO) {
            val success2 = inviteFriend(config.friend2, config.findMode)
            if (!success2) log("Invite friend 2 failed")
        }
        delay(500)
    }

    /**
     * 邀请单个好友 — 对应 OAS invite_friend
     *
     * 简化实现：点击加号 → 等待好友列表 → 点击确定
     * 完整实现需要 OCR 识别好友名字
     */
    private suspend fun inviteFriend(name: String?, findMode: FindMode): Boolean {
        if (name.isNullOrEmpty()) return false
        log("Click add to invite friend")

        // 点击＋号
        while (true) {
            val img = screenshot() ?: continue
            if (appear(I_LOAD_FRIEND, img)) break
            if (appear(I_INVITE_ENSURE, img)) break
            if (appearThenClick(I_ADD_2, img, 1000)) continue
            if (appearThenClick(I_ADD_5_4, img, 1000)) continue
            if (appearThenClick(I_ADD_SEA, img, 1000)) continue
        }

        // 简化: 等待好友列表加载后直接点击确定
        // 完整实现需要 OCR 匹配好友名
        delay(1000)

        // 点击确定
        log("Click invite ensure")
        while (true) {
            val img = screenshot() ?: continue
            if (!appear(I_INVITE_ENSURE, img)) break
            if (appearThenClick(I_INVITE_ENSURE, img)) continue
        }

        return true
    }

    /**
     * 战斗胜利后再次邀请队友 — 对应 OAS invite_again
     */
    suspend fun inviteAgain(defaultInvite: Boolean = true) {
        log("Invite again")
        // 判断是否进入界面
        while (true) {
            val img = screenshot() ?: continue
            if (appear(I_GI_SURE, img)) break
        }

        if (defaultInvite) {
            log("Click default invite")
            while (true) {
                val img = screenshot() ?: continue
                if (appear(I_I_DEFAULT, img)) break
                if (appearThenClick(I_I_NO_DEFAULT, img, 1000)) continue
            }
        } else {
            log("Click no default invite")
            while (true) {
                val img = screenshot() ?: continue
                if (appear(I_I_NO_DEFAULT, img)) break
                if (appearThenClick(I_I_DEFAULT, img, 1000)) continue
            }
        }

        // 点击确认
        log("Click invite ensure")
        while (true) {
            val img = screenshot() ?: continue
            if (!appear(I_GI_SURE, img)) break
            if (appearThenClick(I_GI_SURE, img)) continue
        }
    }

    /**
     * 队长战斗后邀请队友 — 对应 OAS check_and_invite
     */
    suspend fun checkAndInvite(defaultInvite: Boolean = true): Boolean {
        val img = screenshot() ?: return false
        if (!appear(I_GI_SURE, img)) return false

        if (defaultInvite) {
            // 有可能是挑战失败的
            val frame = screenshot() ?: return false
            if (appear(I_I_DEFAULT, frame) || appear(I_I_NO_DEFAULT, frame)) {
                log("Click default invite")
                while (true) {
                    val f = screenshot() ?: continue
                    if (appear(I_I_DEFAULT, f)) break
                    if (appearThenClick(I_I_NO_DEFAULT, f, 1000)) continue
                }
            }
        }

        // 点击确认
        while (true) {
            val img2 = screenshot() ?: continue
            if (!appear(I_GI_SURE, img2)) break
            if (appearThenClick(I_GI_SURE, img2, 1000)) continue
        }

        return true
    }

    /**
     * 队员接受邀请 — 对应 OAS check_then_accept
     */
    suspend fun checkThenAccept(): Boolean {
        val img = screenshot() ?: return false
        if (!appear(I_I_ACCEPT, img)) return false
        log("Click accept")
        while (true) {
            val frame = screenshot() ?: continue
            if (isInRoom(false, frame)) return true
            // 被秒开
            if (appear(GeneralBattle(context, device, config).let {
                    RuleImage("exit", "general_battle/gb/gb_exit.png",
                        intArrayOf(14, 12, 43, 41), intArrayOf(14, 12, 43, 41), 0.8f)
                }, frame)) return false
            if (appearThenClick(I_I_NO_DEFAULT, frame, 1000)) continue
            if (appearThenClick(I_GI_SURE, frame, 1000)) continue
            if (appearThenClick(I_I_ACCEPT_DEFAULT, frame, 1000)) continue
            if (appearThenClick(I_I_ACCEPT, frame, 1000)) continue
        }
    }

    /**
     * 在房间等待队长开启战斗 — 对应 OAS wait_battle
     *
     * @return 如果成功进入战斗返回 true，如果失败（退出房间）返回 false
     */
    suspend fun waitBattle(waitTimeSeconds: Int): Boolean {
        val emojiTimer = System.currentTimeMillis()
        val waitDeadline = System.currentTimeMillis() + waitTimeSeconds * 1000L
        log("Wait battle $waitTimeSeconds seconds")
        var success = true

        while (true) {
            val img = screenshot() ?: continue

            // 如果自己在探索界面或者是庭院，那就是房间已经被销毁了
            if (appear(I_GI_HOME, img) || appear(I_GI_EXPLORE, img)) {
                log("Room destroyed")
                success = false
                break
            }

            if (System.currentTimeMillis() > waitDeadline) {
                log("Wait battle time out")
                success = false
                break
            }

            // 如果队长跑路了，自己变成了队长
            if (appear(I_FIRE, img) || appear(I_FIRE_SEA, img)) {
                log("Leader run away, become leader now")
                success = false
                break
            }

            // 判断是否进入战斗
            if (isInRoom(false, img)) {
                if (System.currentTimeMillis() - emojiTimer > 15_000) {
                    appearThenClick(I_GI_EMOJI_1, img)
                    appearThenClick(I_GI_EMOJI_2, img)
                }
            } else {
                break // 进入战斗
            }
        }

        if (!success) {
            log("Leave room")
            exitRoom()
        }

        return success
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

package com.onmyoji.auto.engine.tasks

import android.content.Context
import com.onmyoji.auto.engine.BaseTask
import com.onmyoji.auto.engine.DeviceController
import com.onmyoji.auto.engine.RuleImage
import com.onmyoji.auto.model.TaskConfig
import kotlinx.coroutines.delay

/**
 * 宠物任务 — 投喂宠物
 *
 * 流程：进入宠物小屋 → 投喂 → 退出
 */
class PetsTask(
    context: Context,
    device: DeviceController,
    config: TaskConfig
) : BaseTask(context, device, config) {

    // ========== 资源定义 ==========
    // 宠物小屋
    private val I_PET_HOUSE = RuleImage(
        "pet_house",
        "Pets/pet/pet_pet_house.png",
        intArrayOf(1012, 414, 56, 25),
        intArrayOf(952, 412, 144, 60),
        0.7f
    )

    // 爪印
    private val I_PET_CLAW = RuleImage(
        "pet_claw",
        "Pets/pet/pet_pet_claw.png",
        intArrayOf(1171, 625, 55, 56),
        intArrayOf(1171, 625, 55, 56),
        0.8f
    )

    // 其乐融融
    private val I_PET_HAPPY = RuleImage(
        "pet_happy",
        "Pets/pet/pet_pet_happy.png",
        intArrayOf(853, 614, 67, 70),
        intArrayOf(853, 614, 67, 70),
        0.8f
    )

    // 快速喂养
    private val I_PET_FEAST = RuleImage(
        "pet_feast",
        "Pets/pet/pet_pet_feast.png",
        intArrayOf(849, 624, 69, 49),
        intArrayOf(849, 624, 69, 49),
        0.8f
    )

    // 玩耍
    private val I_PET_PLAY = RuleImage(
        "pet_play",
        "Pets/pet/pet_pet_play.png",
        intArrayOf(788, 500, 75, 75),
        intArrayOf(788, 500, 75, 75),
        0.8f
    )

    // 喂食
    private val I_PET_FEED = RuleImage(
        "pet_feed",
        "Pets/pet/pet_pet_feed.png",
        intArrayOf(899, 511, 79, 68),
        intArrayOf(884, 485, 127, 123),
        0.8f
    )

    // 跳过
    private val I_PET_SKIP = RuleImage(
        "pet_skip",
        "Pets/pet/pet_pet_skip.png",
        intArrayOf(1089, 119, 72, 41),
        intArrayOf(999, 42, 242, 147),
        0.65f
    )

    // 退出
    private val I_PET_EXIT = RuleImage(
        "pet_exit",
        "Pets/pet/pet_pet_exit.png",
        intArrayOf(30, 25, 39, 33),
        intArrayOf(30, 25, 39, 33),
        0.8f
    )

    override suspend fun run() {
        log("=== 宠物任务开始 ===")

        // 进入宠物小屋
        while (true) {
            val img = screenshot() ?: continue
            if (I_PET_FEAST.match(img, context).matched) {
                break
            }
            if (appearThenClick(I_PET_HOUSE, img)) continue
            if (appearThenClick(I_PET_CLAW, img)) continue
        }
        log("进入宠物小屋")

        // 投喂
        if (config.petsFeast) {
            feed()
        }

        // 退出
        device.click(30 + 39 / 2, 25 + 33 / 2)
        delay(1000)
        log("=== 宠物任务完成 ===")
    }

    /**
     * 投喂
     */
    private suspend fun feed() {
        log("开始投喂")
        // 点击快速喂养
        while (true) {
            val img = screenshot() ?: continue
            if (I_PET_FEED.match(img, context).matched) break
            appearThenClick(I_PET_FEAST, img)
        }
        // 检查体力
        delay(500)
        log("投喂完成，等待动画")
        // 等待跳过按钮出现并消失
        waitUntilAppear(I_PET_SKIP, 5000)
        waitUntilDisappear(I_PET_SKIP)
    }
}

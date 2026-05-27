# OnmyojiAutoAPK-src 逻辑断层修复报告

**日期**: 2026-05-27  
**对比仓库**: OAS dev2 (Python) → APK-src (Kotlin)  
**修改文件数**: 6

---

## 修复文件清单

### 1. `ExplorationTask.kt` — 探索任务 (857行)

| # | 问题 | 修复 |
|---|------|------|
| 1 | 缺少 `explore_init` 初始化逻辑 | 新增首次进入 MAIN 场景时的自动轮换设置 |
| 2 | `preProcess()` 只做导航 | 新增御魂切换 + buff 开启逻辑 |
| 3 | `postProcess()` 只返回主页 | 新增 buff 关闭 + `set_next_run` 调度 |
| 4 | `findUpFight()` 不支持 UpType | **完整实现** `searchUpFight()`：UP 图标检测 → 缩小 ROI → matchAll → 加权距离排序 |
| 5 | `quitExplore()` 不拾取掉落物 | 新增退出时 `I_BATTLE_REWARD` 检测和拾取 |
| 6 | `checkScrolls()` 不联动 MemoryScrolls | 新增 MemoryScrolls 任务立即运行调度 |
| 7 | `enterSettingsAndDoOperations()` 完全缺失 | 新增候补式神设置界面操作 |
| 8 | 无 leader/member 组队模式 | 新增 `runLeader()` 和 `runMember()` 完整实现 |
| 9 | 场景枚举缺少 TEAM | 新增 TEAM 场景检测和处理 |
| 10 | 组队相关资源缺失 | 新增 I_EXP_CREATE_TEAM, I_ADD_2, I_FIRE 等资源 |
| 11 | 队友离开检测缺失 | 新增 SimpleTimer 和队友离开超时退出逻辑 |
| 12 | UP 图标资源 ROI 不正确 | 更新为 Python 原版的精确 ROI 坐标 |

### 2. `RealmRaidTask.kt` — 个人突破任务 (419行)

| # | 问题 | 修复 |
|---|------|------|
| 1 | 无票数检查 | 新增 `checkTicket()` OCR 票数检测 |
| 2 | 无三胜奖励检测 | 新增 `rewardDetectClick()` 奖励弹窗处理 |
| 3 | `findMedal()` 忽略 `order_attack` 配置 | 改为按 `config.orderAttack` 优先级查找勋章 |
| 4 | `fireAt()` 无弹窗清理 | 新增 `I_FRESH_ENSURE` 弹窗处理 |
| 5 | 退出后不回主页 | 新增 `gameUi.uiGoto(pageMain)` 导航 |
| 6 | 缺少呱太 OCR 检测 | 新增 `checkMedalIsFrog()` 方法 |

### 3. `DemonEncounterTask.kt` — 逢魔之时 (389行)

| # | 问题 | 修复 |
|---|------|------|
| 1 | 时间窗口 17-23 与 Python 17-22 不一致 | 改为 17:00-22:00 |
| 2 | 答题使用随机选择 | **完整实现** OCR 答题系统：OCR 识别题目/选项 → 关键词匹配 → 兜底随机 |
| 3 | 缺少答题 OCR 资源 | 新增 O_LETTER_QUESTION, O_LETTER_ANSWER_1/2/3 |
| 4 | 答题后奖励处理不完善 | 新增答题结束后奖励检测逻辑 |

### 4. `GameUi.kt` — 页面导航系统 (740行)

| # | 问题 | 修复 |
|---|------|------|
| 1 | 缺少 `page_demon_encounter_realworld` | 新增页面定义和注册 |
| 2 | 缺少 `page_bondling_fairyland` | 新增页面定义和注册 |
| 3 | 缺少 `page_hero_test` | 新增页面定义和注册 |
| 4 | 缺少 `page_hunt` / `page_hunt_kirin` | 新增页面定义和注册 |
| 5 | 缺少 `page_kekkai_toppa` | 新增页面定义和注册 |
| 6 | 页面间跳转链接不完整 | 补充探索→契灵、探索→英杰、町中→狩猎、结界→寮突 |

### 5. `BaseTask.kt` — 任务基类 (205行)

| # | 问题 | 修复 |
|---|------|------|
| 1 | `setNextRun()` 只区分 success/fail | 增加 `finish` 参数支持，`!success && !finish` 改为 2 分钟重试 |

### 6. `SwitchSoul.kt` — 御魂切换组件 (新增修复)

| # | 问题 | 修复 |
|---|------|------|
| 1 | `"-1,-1"` 配置导致无效切换 | 新增无效配置跳过逻辑 |
| 2 | `runSwitchSoulByName` 空名称不跳过 | 新增空名称检查 |
| 3 | 无效 pair 不过滤 | 新增 `validPairs` 过滤 |

---

## 修改统计

- **总修改行数**: ~900 行 (新增 + 修改)
- **新增页面**: 6 个
- **新增方法**: 15 个
- **新增资源定义**: 20+ 个 RuleImage / OcrRegion

## 页面完整性验证 ✅

代码中引用的所有 25 个页面均已注册：
`page_login`, `page_main`, `page_summon`, `page_exploration`, `page_town`, `page_awake_zones`, `page_soul_zones`, `page_realm_raid`, `page_goryou_realm`, `page_delegation`, `page_secret_zones`, `page_area_boss`, `page_six_gates`, `page_duel`, `page_demon_encounter`, `page_demon_encounter_realworld`, `page_shikigami_records`, `page_friends`, `page_guild`, `page_team`, `page_bondling_fairyland`, `page_hero_test`, `page_hunt`, `page_hunt_kirin`, `page_kekkai_toppa`

## 括号匹配验证 ✅

所有 6 个修改文件的 `{` / `}` 计数一致。

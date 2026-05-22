# ExplorationTask 修复方案

## 问题根因

`ExplorationTask` 继承 `BaseTask`，没有使用项目中已有的 `GameUi` 页面导航组件。

`navigateToExploration()` 只调用了 `waitUntilAppear(I_CHECK_EXPLORATION, 15000)` — 这是**被动等待**，
不是主动导航。15 秒超时后不管有没有到达探索页面都会继续执行，导致"导航到探索页面"的日志打出来，
实际还在原地。

## 代码变更摘要

### `ExplorationTask.kt` — 唯一需要改的文件

| 位置 | 原代码 | 修改后 |
|------|--------|--------|
| **继承** | `BaseTask(context, device, config)` | `GameUi(context, device, config)` |
| **导航** | `waitUntilAppear(I_CHECK_EXPLORATION, 15000)` | `uiGotoPage(pageExploration)` — BFS 最短路径主动导航 |
| **UNKNOWN** | `delay(500)` 直接跳过 | 随机点击 + 弹窗关闭 + 页面识别恢复 |
| **run()** | 直接 runSolo | preProcess → runSolo → postProcess |

### 不需要改的文件

- `GameUi.kt` — 已有完整的页面导航系统，直接复用
- `Page.kt` — 已有页面注册和 BFS 路径规划
- `TaskManager.kt` — 构造函数签名未变，无需修改
- `BaseTask.kt` — 不变
- `DeviceController.kt` — 不变

## 前置检查

### 1. 图片资源

确认 `app/src/main/assets/game_ui/page/` 目录下存在：

```
page_main_goto_exploration.png   ← 主页上的"探索"按钮截图
page_check_main.png              ← 主页标识
page_check_exploration.png       ← 探索页面标识（章节列表页）
page_back_yollow.png             ← 黄色返回按钮
```

这些文件在 GameUi.kt 中已被引用（`RuleImage` 的 assetPath）。如果 APK 编译时没报资源找不到，
说明已经存在。

### 2. 编译验证

```bash
cd /path/to/OnmyojiAutoAPK-src
./gradlew assembleDebug
```

如果报 `Unresolved reference: GameUi`，检查 import：
```kotlin
import com.onmyoji.auto.engine.component.GameUi
```

### 3. 功能验证

替换后日志应该变成：

```
[HH:mm:ss] === 探索任务开始 ===
[HH:mm:ss] 章节: 第二十八章
[HH:mm:ss] 绘卷模式: 开启，阈值=25
[HH:mm:ss] 预处理：导航到探索页面...
[HH:mm:ss] UI get current page
[HH:mm:ss] UI: page_main                          ← 识别到在主页
[HH:mm:ss] UI goto page_exploration
[HH:mm:ss] page_main -> page_exploration           ← BFS 规划路径
[HH:mm:ss] Page switch: page_main -> page_exploration
[HH:mm:ss] Wait appear and operate ...             ← 点击探索按钮
[HH:mm:ss] Page arrived page_exploration           ← 到达探索页面 ✓
[HH:mm:ss] 探索启动
[HH:mm:ss] 选择章节: 第二十八章
[HH:mm:ss] 战斗，第 1 次
...
```

## 风险评估

| 风险 | 等级 | 说明 |
|------|------|------|
| 编译失败 | 低 | GameUi/Page 组件已存在，只是 ExplorationTask 没接入 |
| 导航失败 | 低 | 30 秒超时保护，失败后会 warn 但从当前页面继续 |
| 资源冲突 | 无 | GameUi 和 ExplorationTask 的 RuleImage 是独立的 private 字段 |
| 行为变化 | 低 | 探索主循环逻辑完全不变，只改了入口导航 |

package com.onmyoji.auto.engine.component

import com.onmyoji.auto.engine.RuleImage

/**
 * 页面定义 — 对应 OAS page.py 中的 Page 类
 *
 * 每个 Page 代表游戏中的一个可识别界面，通过 checkButton 判断当前是否在该页面，
 * 通过 links 记录可以跳转到的目标页面及对应的按钮。
 */
class Page(
    val checkButton: List<RuleImage>,
    val name: String
) {
    // 页面间跳转链接: targetPage -> button
    val links: MutableMap<Page, Any> = mutableMapOf()

    // 附加操作按钮（弹窗关闭等）
    var additional: List<Any>? = null

    constructor(checkButton: RuleImage, name: String) : this(listOf(checkButton), name)

    /**
     * 添加跳转链接
     */
    fun link(button: RuleImage, destination: Page) {
        links[destination] = button
    }

    fun link(buttons: List<RuleImage>, destination: Page) {
        links[destination] = buttons
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Page) return false
        return name == other.name
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = name
}

/**
 * 页面注册表 — 对应 OAS PageRegistry
 */
object PageRegistry {
    private val _registry = mutableListOf<Page>()

    fun register(page: Page) {
        _registry.add(page)
    }

    fun all(): List<Page> = _registry.toList()
}

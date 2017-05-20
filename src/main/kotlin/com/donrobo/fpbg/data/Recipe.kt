package com.donrobo.fpbg.data

data class Recipe @JvmOverloads constructor(val name: String, val enabled: Boolean, val ingredients: List<ItemStack>, val result: List<ItemStack>, val energyRequired: Double = 0.5, private val extra: MutableMap<String, Any>) {
    init {
        this.extra.remove("name")
        this.extra.remove("enabled")
        this.extra.remove("ingredients")
        this.extra.remove("result")
        this.extra.remove("results")
        this.extra.remove("result_count")
        this.extra.remove("energy_required")
        this.extra.remove("type")
        this.extra.remove("normal")
        this.extra.remove("expensive")
    }

    fun isProducingItem(item: Item): Boolean {
        return result.stream().anyMatch { i -> i.item == item }
    }

    fun isProducingItem(item: String): Boolean {
        return result.stream().anyMatch { i -> i.item.name == item }
    }

}

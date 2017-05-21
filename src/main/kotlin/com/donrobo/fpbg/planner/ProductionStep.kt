package com.donrobo.fpbg.planner

import com.donrobo.fpbg.data.Recipe

data class ProductionStep(val recipe: Recipe, val craftingSpeed: Double) {

    val resultPerSecond = FractionalItemStack(recipe.result.count * craftingSpeed / recipe.energyRequired, recipe.result.item)

    val ingredientsPerSecond = recipe.ingredients.map { FractionalItemStack(it.count * craftingSpeed / recipe.energyRequired, it.item) }

    fun requires(input: String) = recipe.ingredients.map { it.item.name }.contains(input)
}

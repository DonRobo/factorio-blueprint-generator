package com.donrobo.fpbg.generator

import com.donrobo.fpbg.data.ItemStack
import com.donrobo.fpbg.data.Recipe
import io.kotlintest.specs.BehaviorSpec

class ProductionStepLayoutTest : BehaviorSpec({

})

fun aRecipe() = Recipe(
        name = "test-recipe",
        enabled = true,
        ingredients = anIngredientList(),
        result = aResultList(), energyRequired = 3.0,
        extra = HashMap())

fun aResultList(): List<ItemStack> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun anIngredientList(): List<ItemStack> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

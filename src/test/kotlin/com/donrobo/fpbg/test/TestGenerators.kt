package com.donrobo.fpbg.test

import com.donrobo.fpbg.data.Item
import com.donrobo.fpbg.data.ItemStack
import com.donrobo.fpbg.data.Recipe
import com.donrobo.fpbg.planner.ProductionStep
import io.kotlintest.properties.Gen


class ItemStackGenerator : Gen<ItemStack> {

    private val stackSizeGenerator = Gen.choose(1, 50)

    override fun generate(): ItemStack {
        return ItemStack(stackSizeGenerator.generate(), Item(nextPrintableString(5)))
    }

    fun generateItemStack(name: String? = null): ItemStack {
        return ItemStack(stackSizeGenerator.generate(), Item(name ?: nextPrintableString(5)))
    }

}

class RecipeGenerator : Gen<Recipe> {

    private val ingredientCountGenerator = Gen.choose(1, 4)
    private val itemStackGenerator = ItemStackGenerator()

    override fun generate(): Recipe = generateRecipe()

    fun generateRecipe(ingredientCount: Int? = null, ingredientPrefix: String? = null): Recipe {
        return Recipe(
                name = nextPrintableString(5),
                enabled = false,
                energyRequired = 0.5,
                extra = HashMap(),
                ingredients = 1.rangeTo(ingredientCount ?: ingredientCountGenerator.generate()).map {
                    itemStackGenerator.generateItemStack(name = ingredientPrefix?.plus(it))
                },
                result = itemStackGenerator.generate())
    }

}

class ProductionStepGenerator : Gen<ProductionStep> {

    private val recipeGenerator = RecipeGenerator()
    private val craftingSpeedGenerator = Gen.choose(1, 100)

    override fun generate(): ProductionStep {
        return ProductionStep(recipe = recipeGenerator.generate(), craftingSpeed = craftingSpeedGenerator.generate() / 10.0)
    }

    fun generateProductionStep(ingredientCount: Int? = null, ingredientPrefix: String? = null): ProductionStep {
        return ProductionStep(recipe = recipeGenerator.generateRecipe(ingredientCount = ingredientCount, ingredientPrefix = ingredientPrefix), craftingSpeed = craftingSpeedGenerator.generate() / 10.0)
    }

}


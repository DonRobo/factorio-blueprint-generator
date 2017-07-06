package com.donrobo.fpbg.generator

import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.data.Item
import com.donrobo.fpbg.data.ItemStack
import com.donrobo.fpbg.data.Recipe
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class AssemblingMachineLayoutTest : StringSpec() {

    init {
        "Input belt assignments" {
            val prod0 = AssemblingMachineLayout(aRecipe(ingredientCount = 0), 1.0)
            prod0.inputs.size shouldBe 0

            val prod1 = AssemblingMachineLayout(aRecipe(ingredientCount = 1), 1.0)
            prod1.inputs.size shouldBe 1
            prod1.inputs[0].beltSide shouldBe BeltSide.BOTH
            prod1.inputs[0].item shouldBe "item-1"

            val prod2 = AssemblingMachineLayout(aRecipe(ingredientCount = 2), 1.0)
            prod2.inputs.size shouldBe 2
            prod2.inputs[0].beltSide shouldBe BeltSide.LEFT
            prod2.inputs[0].item shouldBe "item-1"

            prod2.inputs[1].beltSide shouldBe BeltSide.RIGHT
            prod2.inputs[1].item shouldBe "item-2"

            val prod3 = AssemblingMachineLayout(aRecipe(ingredientCount = 3), 1.0)
            prod3.inputs.size shouldBe 3
            prod3.inputs[0].beltSide shouldBe BeltSide.LEFT
            prod3.inputs[0].item shouldBe "item-1"

            prod3.inputs[1].beltSide shouldBe BeltSide.RIGHT
            prod3.inputs[1].item shouldBe "item-2"

            prod3.inputs[2].beltSide shouldBe BeltSide.BOTH
            prod3.inputs[2].item shouldBe "item-3"

            val prod4 = AssemblingMachineLayout(aRecipe(ingredientCount = 4), 1.0)
            prod4.inputs.size shouldBe 4
            prod4.inputs[0].beltSide shouldBe BeltSide.LEFT
            prod4.inputs[0].item shouldBe "item-1"

            prod4.inputs[1].beltSide shouldBe BeltSide.RIGHT
            prod3.inputs[1].item shouldBe "item-2"

            prod4.inputs[2].beltSide shouldBe BeltSide.LEFT
            prod4.inputs[2].item shouldBe "item-3"

            prod4.inputs[3].beltSide shouldBe BeltSide.RIGHT
            prod4.inputs[3].item shouldBe "item-4"
        }
        "Width calculations" {
            AssemblingMachineLayout(aRecipe(ingredientCount = 1), 1.0).width shouldBe 7
            AssemblingMachineLayout(aRecipe(ingredientCount = 2), 1.0).width shouldBe 7
            AssemblingMachineLayout(aRecipe(ingredientCount = 3), 1.0).width shouldBe 8
            AssemblingMachineLayout(aRecipe(ingredientCount = 4), 1.0).width shouldBe 8
        }
        "Height calculations" {
            for (i in 0..4) {
                AssemblingMachineLayout(aRecipe(i), 1.0).height shouldBe 3
            }
        }
    }

    fun aRecipe(ingredientCount: Int = 3) = Recipe(
            name = "test-recipe",
            enabled = true,
            ingredients = anIngredientList(ingredientCount),
            result = aResult(),
            energyRequired = 3.0,
            extra = HashMap())

    fun aResult() = ItemStack(1, Item("test-item"))

    fun anIngredientList(count: Int) = 0.rangeTo(count - 1).map { ItemStack(item = Item("item-${it + 1}"), count = 3) }
}


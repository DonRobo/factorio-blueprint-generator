package com.donrobo.fpbg.generator

import com.donrobo.fpbg.data.Recipe


enum class BeltSide {
    LEFT, RIGHT, BOTH
}

data class Input(val beltIndex: Int, val beltSide: BeltSide, val type: String)

class ProductionStepLayout(val recipe: Recipe) {
    val width: Int
        get() {
            val belts = when (recipe.ingredients.size) {
                0 -> TODO("No inputs? Does this exist?")
                1, 2 -> 1
                3, 4 -> 2
                else -> TODO("More than 4 inputs not supported yet")
            }
            val assemblingMachineWidth = 3
            val inserterCount = 2

            return assemblingMachineWidth + inserterCount + belts
        }

    val height = 3
    val inputs: List<Input>
        get() {
            val list = ArrayList<Input>()

            recipe.ingredients.indices.mapTo(list) { index ->
                Input(type = recipe.ingredients[index].item.name,
                        beltIndex = index / 2,
                        beltSide = if (index + 1 == recipe.ingredients.size) BeltSide.BOTH else if (index % 2 == 0) BeltSide.LEFT else BeltSide.RIGHT)
            }

            return list
        }
}

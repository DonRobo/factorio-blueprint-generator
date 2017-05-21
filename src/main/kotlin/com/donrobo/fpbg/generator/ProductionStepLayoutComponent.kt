package com.donrobo.fpbg.generator

import com.donrobo.fpbg.data.Recipe
import com.donrobo.fpbg.generator.data.BeltIoType
import com.donrobo.fpbg.generator.data.BeltSide
import com.donrobo.fpbg.generator.data.IndexedBeltIo


class ProductionStepLayoutComponent(val recipe: Recipe) {
    val width: Int
        get() {
            val belts = when (recipe.ingredients.size) {
                0 -> TODO("No inputs? Does this exist?")
                1, 2 -> 1
                3, 4 -> 2
                else -> TODO("More than 4 inputs not supported yet")
            } + 1 //for output
            val assemblingMachineWidth = 3
            val inserterCount = 2

            return assemblingMachineWidth + inserterCount + belts
        }

    val height = 3

    val inputs: List<IndexedBeltIo>
        get() {
            val list = ArrayList<IndexedBeltIo>()

            recipe.ingredients.indices.mapTo(list) { index ->
                IndexedBeltIo(item = recipe.ingredients[index].item.name,
                        beltIndex = index / 2,
                        beltSide = if (index + 1 == recipe.ingredients.size && index % 2 == 0) BeltSide.BOTH else if (index % 2 == 0) BeltSide.LEFT else BeltSide.RIGHT,
                        type = BeltIoType.INPUT)
            }

            return list
        }

    val output = IndexedBeltIo(beltIndex = 0, type = BeltIoType.OUTPUT, beltSide = BeltSide.RIGHT, item = recipe.result.item.name)

    val craftingSpeed = 0.75//TODO

    val resultsPerSecond = craftingSpeed * recipe.result.count
}

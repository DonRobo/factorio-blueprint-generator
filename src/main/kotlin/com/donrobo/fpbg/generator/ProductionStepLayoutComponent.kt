package com.donrobo.fpbg.generator

import com.donrobo.fpbg.data.*


class ProductionStepLayoutComponent(val recipe: Recipe, val maxResultsPerSecond: Double) {
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

    val assemblingMachineType: AssemblingMachine get() {
        val targetedCraftingSpeed = maxResultsPerSecond / recipe.result.count

        return if (targetedCraftingSpeed >= AssemblingMachine.ASSEMBLING_MACHINE_3.craftingSpeed || recipe.ingredients.size > 4)
            AssemblingMachine.ASSEMBLING_MACHINE_3
        else if (targetedCraftingSpeed >= AssemblingMachine.ASSEMBLING_MACHINE_2.craftingSpeed || recipe.ingredients.size > 2)
            AssemblingMachine.ASSEMBLING_MACHINE_2
        else
            AssemblingMachine.ASSEMBLING_MACHINE_1
    }

    val craftingSpeed: Double
        get() {
            return assemblingMachineType.craftingSpeed
        }

    val resultsPerSecond = craftingSpeed * recipe.result.count
}

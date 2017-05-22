package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.building.*
import com.donrobo.fpbg.data.*


class AssemblingMachineLayout(val recipe: Recipe, val maxResultsPerSecond: Double) {
    val width: Int
        get() {
            val inputBelts = if (doubleInputBelts) 2 else 1
            val outputBelts = 1
            val assemblingMachineWidth = 3
            val inserterCount = 2

            return assemblingMachineWidth + inserterCount + inputBelts + outputBelts
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

    val doubleInputBelts = inputs.size > 2

    fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        val beltOffset = if (doubleInputBelts) 2 else 1
        for (y in 0.downTo(-(height - 1))) {
            blueprint.addBuilding(YellowBelt(0, y, UP))
            if (doubleInputBelts) {
                blueprint.addBuilding(YellowBelt(1, y, UP))
            }
            blueprint.addBuilding(YellowBelt(width - 1, y, DOWN))
        }

        blueprint.addBuilding(FastInserter(beltOffset, 0, RIGHT))
        blueprint.addBuilding(FastInserter(beltOffset + 4, 0, RIGHT))
        if (doubleInputBelts)
            blueprint.addBuilding(LongInserter(beltOffset, -1, RIGHT))

        blueprint.addBuilding(when (assemblingMachineType) {
            AssemblingMachine.ASSEMBLING_MACHINE_1 -> AssemblingMachine1(recipe.name, beltOffset + 1, -2)
            AssemblingMachine.ASSEMBLING_MACHINE_2 -> AssemblingMachine2(recipe.name, beltOffset + 1, -2)
            AssemblingMachine.ASSEMBLING_MACHINE_3 -> AssemblingMachine2(recipe.name, beltOffset + 1, -2) //TODO am3
        })

        if (!(blueprint.width == width && blueprint.height == height)) {
            throw RuntimeException("Blueprint generation failed!\n" +
                    "Blueprint is ${blueprint.width}/${blueprint.height} but should be $width/$height\n\n" +
                    blueprint.visualize())
        }
        return blueprint
    }
}
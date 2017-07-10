package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.building.*
import com.donrobo.fpbg.data.*

/**
 * One assembling machine
 */
class AssemblingMachineLayout(val recipe: Recipe, val maxResultsPerSecond: Double) : Layout {
    override val width: Int
        get() {
            val inputBelts = if (doubleInputBelts) 2 else 1
            val outputBelts = 1
            val assemblingMachineWidth = 3
            val inserterCount = 2

            return assemblingMachineWidth + inserterCount + inputBelts + outputBelts
        }

    override val height get() = 3

    override val inputs: List<PositionalBeltIo>
        get() {
            val list = ArrayList<PositionalBeltIo>()

            recipe.ingredients.indices.mapTo(list) { index ->
                PositionalBeltIo(item = recipe.ingredients[index].item.name,
                        position = Int2(if (recipe.ingredients.size > 2) 1 - index / 2 else index / 2, 0),
                        beltSide = if (index + 1 == recipe.ingredients.size && index % 2 == 0) BeltSide.BOTH else if (index % 2 == 0) BeltSide.LEFT else BeltSide.RIGHT,
                        type = BeltIoType.INPUT)
            }

            return list
        }

    override val outputs get() = listOf(
            PositionalBeltIo(position = Int2(width - 1, 0),
                    type = BeltIoType.OUTPUT,
                    direction = DOWN,
                    beltSide = BeltSide.LEFT,
                    item = recipe.result.item.name)
    )

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

    val resultsPerSecond get() = craftingSpeed * recipe.result.count

    val doubleInputBelts get() = inputs.size > 2

    override fun generateBlueprint(): Blueprint {
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
            AssemblingMachine.ASSEMBLING_MACHINE_3 -> AssemblingMachine2(recipe.name, beltOffset + 1, -2) //TODO Assembling Machine 3
        })

        if (!(blueprint.width == width && blueprint.height == height)) {
            throw RuntimeException("Blueprint generation failed!\n" +
                    "Blueprint is ${blueprint.width}/${blueprint.height} but should be $width/$height\n\n" +
                    blueprint.visualizer().visualize())
        }
        return blueprint
    }
}

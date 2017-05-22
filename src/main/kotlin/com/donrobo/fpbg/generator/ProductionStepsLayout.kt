package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.PositionalBeltIo
import com.donrobo.fpbg.planner.ProductionStep

class ProductionStepsLayout(val productionSteps: List<ProductionStep>) {

    val productionStepLayouts = productionSteps.map { AssemblingMachineLineLayout(it.recipe, it.resultPerSecond.count) }

    val width = productionStepLayouts.map { it.width }.sum()

    val height = productionStepLayouts.map { it.height }.max() ?: 0

    val inputs: List<PositionalBeltIo>
        get() {
            var interStepOffset = 0

            return productionStepLayouts.flatMap { psl ->
                val oldInterStepOffset = interStepOffset
                interStepOffset += psl.width
                psl.inputs.map {
                    PositionalBeltIo(
                            position = Pair(oldInterStepOffset + it.beltIndex, 0),
                            type = BeltIoType.INPUT,
                            beltSide = it.beltSide,
                            item = it.item)
                }
            }
        }

    val outputs: List<PositionalBeltIo>
        get() {
            var interStepOffset = 0

            return productionStepLayouts.map {
                interStepOffset += it.width

                PositionalBeltIo(
                        position = Pair(interStepOffset - 1, 0),
                        type = BeltIoType.OUTPUT,
                        beltSide = it.output.beltSide,
                        item = it.output.item)
            }
        }

    fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        var x = 0

        for (subPrint in productionStepLayouts.map { it.generateBlueprint() }) {
            blueprint.addBlueprint(subPrint, x, 0)
            x += subPrint.width
        }

        if (!(blueprint.width == width && blueprint.height == height)) {
            throw RuntimeException("Blueprint generation failed!\n" +
                    "Blueprint is ${blueprint.width}/${blueprint.height} but should be $width/$height\n\n" +
                    blueprint.visualize())
        }
        return blueprint
    }

}

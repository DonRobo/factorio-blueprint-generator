package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo
import com.donrobo.fpbg.planner.ProductionStep

class ProductionStepsLayout(val productionSteps: List<ProductionStep>) {

    val assemblingMachineLineLayouts = productionSteps.map { AssemblingMachineLineLayout(it.recipe, it.resultPerSecond.count) }

    val width = assemblingMachineLineLayouts.map { it.width + 1 }.sum() - 1

    val height = assemblingMachineLineLayouts.map { it.height }.max() ?: 0

    val inputs: List<PositionalBeltIo>
        get() {
            var interStepOffset = 0

            return assemblingMachineLineLayouts.flatMap { psl ->
                val oldInterStepOffset = interStepOffset
                interStepOffset += psl.width + 1
                psl.inputs.map {
                    PositionalBeltIo(
                            position = Int2(oldInterStepOffset + it.beltIndex, 1),
                            type = BeltIoType.INPUT,
                            beltSide = it.beltSide,
                            item = it.item)
                }
            }
        }

    val outputs: List<PositionalBeltIo>
        get() {
            var interStepOffset = 0

            return assemblingMachineLineLayouts.map {
                interStepOffset += it.width + 1

                PositionalBeltIo(
                        position = Int2(interStepOffset - 2, 1),
                        type = BeltIoType.OUTPUT,
                        beltSide = it.output.beltSide,
                        item = it.output.item)
            }
        }

    fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        var x = 0

        for (subPrint in assemblingMachineLineLayouts.map { it.generateBlueprint() }) {
            blueprint.addBlueprint(subPrint, x, 0)
            x += subPrint.width + 1
        }

        if (!(blueprint.width == width && blueprint.height == height)) {
            throw RuntimeException("Blueprint generation failed!\n" +
                    "Blueprint is ${blueprint.width}/${blueprint.height} but should be $width/$height\n\n" +
                    blueprint.visualizer().visualize())
        }

        return blueprint
    }

}

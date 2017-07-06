package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo
import com.donrobo.fpbg.planner.ProductionStep

/**
 * Multiple lines of assembling machines producing various stuff
 */
class ProductionStepsLayout(val productionSteps: List<ProductionStep>) : Layout {

    val assemblingMachineLineLayouts = productionSteps.map { FullBeltWrapperLayout(AssemblingMachineLineLayout(it.recipe, it.resultPerSecond.count)) }

    override val width = generateBlueprint().width

    override val height = generateBlueprint().height

    override val inputs: List<PositionalBeltIo>
        get() {
            var interStepOffset = 0

            return assemblingMachineLineLayouts.flatMap { psl ->
                val oldInterStepOffset = interStepOffset
                interStepOffset += psl.width
                psl.inputs.map {
                    PositionalBeltIo(
                            position = Int2(oldInterStepOffset + it.position.x, 1),
                            type = BeltIoType.INPUT,
                            beltSide = it.beltSide,
                            item = it.item)
                }
            }
        }

    override val outputs: List<PositionalBeltIo>
        get() {
            var interStepOffset = 0

            return assemblingMachineLineLayouts.map {
                interStepOffset += it.width

                assert(it.outputs.size == 1)

                PositionalBeltIo(
                        position = Int2(interStepOffset - 1, 1),
                        type = BeltIoType.OUTPUT,
                        beltSide = it.outputs[0].beltSide,
                        item = it.outputs[0].item)
            }
        }

    override fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        var currentMaxX = 0

        for (subPrint in assemblingMachineLineLayouts.map { it.generateBlueprint() }) {
            blueprint.addBlueprint(subPrint, currentMaxX - subPrint.minimumX, 0)
            currentMaxX = blueprint.maximumX + 1
        }

        return blueprint
    }

}

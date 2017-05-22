package com.donrobo.fpbg.generator

import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.PositionalBeltIo
import com.donrobo.fpbg.planner.ProductionStep

class ProductionStepsLayout(val productionSteps: List<ProductionStep>) {

    val productionStepLayouts = productionSteps.map { ProductionStepLayout(it.recipe, it.resultPerSecond.count) }

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

}

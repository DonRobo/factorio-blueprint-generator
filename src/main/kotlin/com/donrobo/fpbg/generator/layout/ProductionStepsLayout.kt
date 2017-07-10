package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo
import com.donrobo.fpbg.planner.ProductionStep

/**
 * Multiple lines of assembling machines producing various stuff
 */
class ProductionStepsLayout(val productionSteps: List<ProductionStep>) : Layout {

    override val inputs: List<PositionalBeltIo>

    override val outputs: List<PositionalBeltIo>

    private val internalBlueprint: Blueprint

    val assemblingMachineLineLayouts: List<Layout>

    init {
        inputs = ArrayList()
        outputs = ArrayList()
        internalBlueprint = Blueprint()
        assemblingMachineLineLayouts = productionSteps
                .map {
                    buildCombinationLayout(
                            item = it.recipe.result.item.name,
                            layoutToExtend = FullBeltWrapperLayout(AssemblingMachineLineLayout(it.recipe, it.resultPerSecond.count)),
                            outputCount = ingredientCounts[it.recipe.result.item.name] ?: 1)
                }

        var currentMaxX = 0

        for (subLayout in assemblingMachineLineLayouts) {
            val subPrint = subLayout.generateBlueprint()
            val xOffset = currentMaxX - subPrint.minimumX
            internalBlueprint.addBlueprint(subPrint, xOffset, 0)
            currentMaxX = internalBlueprint.maximumX + 1

            inputs.addAll(subLayout.inputs.map { it.move(Int2(xOffset, 0)) })
            outputs.addAll(subLayout.outputs.map { it.move(Int2(xOffset, 0)) })
        }
    }

    private val ingredientCounts: Map<String, Int>
        get() = productionSteps.flatMap { it.ingredientsPerSecond.map { it.item.name } }.groupingBy { it }.eachCount()

    override val width = internalBlueprint.width

    override val height = internalBlueprint.height

    override fun generateBlueprint(): Blueprint {
        return internalBlueprint
    }

}

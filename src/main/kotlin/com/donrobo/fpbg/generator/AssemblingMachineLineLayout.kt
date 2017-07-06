package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.data.Recipe

/**
 * vertical line of multiple assembling machines producing the same recipe
 */
class AssemblingMachineLineLayout(val recipe: Recipe, val resultsPerSecond: Double) : Layout {

    val assemblingMachineLayouts: List<AssemblingMachineLayout>
        get() {
            val components: MutableList<AssemblingMachineLayout> = ArrayList()

            var remainingResultsPerSecond = resultsPerSecond

            while (remainingResultsPerSecond > 0) {
                val component: AssemblingMachineLayout = AssemblingMachineLayout(recipe, remainingResultsPerSecond)
                components.add(component)
                remainingResultsPerSecond -= component.resultsPerSecond
            }

            return components
        }

    override val inputs = if (assemblingMachineLayouts.isNotEmpty()) assemblingMachineLayouts[0].inputs else emptyList()

    override val outputs = if (assemblingMachineLayouts.isNotEmpty()) assemblingMachineLayouts[0].outputs else emptyList()

    override val width = (assemblingMachineLayouts.map { it.width }.max() ?: 0)

    override val height = assemblingMachineLayouts.map { it.height }.sum()

    override fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        var y = 0
        for (subPrint in assemblingMachineLayouts.map { it.generateBlueprint() }) {
            blueprint.addBlueprint(subPrint, 0, y)
            y -= subPrint.height
        }

        if (!(blueprint.width == width && blueprint.height == height)) {
            throw RuntimeException("Blueprint generation failed!\n" +
                    "Blueprint is ${blueprint.width}/${blueprint.height} but should be $width/$height\n\n" +
                    blueprint.visualizer().visualize())
        }

        return blueprint
    }

}
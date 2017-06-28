package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.data.IndexedBeltIo
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

    val inputs = if (assemblingMachineLayouts.isNotEmpty()) assemblingMachineLayouts[0].inputs else emptyList()

    val output = if (assemblingMachineLayouts.isNotEmpty()) assemblingMachineLayouts[0].output else IndexedBeltIo(-1, BeltIoType.OUTPUT, BeltSide.BOTH, "")

    val width = (assemblingMachineLayouts.map { it.width }.max() ?: 0) + when (inputs.size) {
        0, 1 -> 0
        2, 3 -> 1 //TODO probably replace with wrapper layout
        4 -> 2
        else -> throw IllegalArgumentException("Unexpected input size")
    }

    val height = assemblingMachineLayouts.map { it.height }.sum()

    fun generateBlueprint(): Blueprint {
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
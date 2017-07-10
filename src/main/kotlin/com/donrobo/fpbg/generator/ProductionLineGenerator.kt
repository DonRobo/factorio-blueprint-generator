package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.BeltLayer
import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.DirectionalInt2
import com.donrobo.fpbg.blueprint.building.Splitter
import com.donrobo.fpbg.blueprint.building.YellowBelt
import com.donrobo.fpbg.blueprint.move
import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.generator.layout.ProductionStepsLayout
import com.donrobo.fpbg.generator.layout.RawInputLayout
import com.donrobo.fpbg.planner.ProductionLine

class ProductionLineGenerator(val productionLine: ProductionLine) {

    fun generateBlueprint(): Blueprint {
        val stepsLayout = ProductionStepsLayout(productionLine.productionSteps)
        stepsLayout.integrityCheck()
        val blueprint = stepsLayout.generateBlueprint()

        val pathsToGenerate = ArrayList<Pair<DirectionalInt2, DirectionalInt2>>()

        val producedItems = stepsLayout.outputs.map { o -> o.item }.toSet()
        val requiredRawItems = stepsLayout.inputs
                .filterNot { i -> i.item in producedItems }
                .map { it.item }.groupingBy { it }.eachCount()

        val rawInputLayout = RawInputLayout(requiredRawItems)
        blueprint.addBlueprint(rawInputLayout.generateBlueprint())

        val unusedOutputs = ArrayList(stepsLayout.outputs + rawInputLayout.outputs)

        stepsLayout.inputs.forEach { input ->
            val usedOutput = unusedOutputs.filter { it.item == input.item }.sortedBy { it.position.manhattanDistance(input.position) }.first()
            unusedOutputs.remove(usedOutput)

            val start = DirectionalInt2(usedOutput.position.move(usedOutput.direction), usedOutput.direction)
            val end = DirectionalInt2(input.position.move(input.direction.reversed), input.direction)

            pathsToGenerate.add(Pair(start, end))
        }

        BeltLayer.layBelts(blueprint, pathsToGenerate)

        return blueprint
    }

}

private fun ProductionStepsLayout.integrityCheck() {
    val blueprint = generateBlueprint()
    inputs.forEach { input ->
        assert(input.beltSide == BeltSide.BOTH)

        val building = blueprint[input.position]
        if (building is YellowBelt) {
//            assert(building.direction == input.direction)
        } else if (building is Splitter) {
            assert(building.direction == input.direction)
        } else {
            assert(false)
        }
    }
    outputs.forEach { output ->
        val building = blueprint[output.position]
        if (building is YellowBelt) {
            assert(building.direction == output.direction)
        } else if (building is Splitter) {
            assert(building.direction == output.direction)
        } else {
            assert(false)
        }
    }
}

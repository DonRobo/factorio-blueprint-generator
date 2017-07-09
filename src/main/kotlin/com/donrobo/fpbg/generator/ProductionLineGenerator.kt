package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.BeltLayer
import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.DirectionalInt2
import com.donrobo.fpbg.generator.layout.ProductionStepsLayout
import com.donrobo.fpbg.planner.ProductionLine

class ProductionLineGenerator(val productionLine: ProductionLine) {

    fun generateBlueprint(): Blueprint {
        val stepsLayout = ProductionStepsLayout(productionLine.productionSteps)
        val blueprint = stepsLayout.generateBlueprint()

        val pathsToGenerate = ArrayList<Pair<DirectionalInt2, DirectionalInt2>>()

        val producedItems = stepsLayout.outputs.map { o -> o.item }.toSet()
        val requiredRawItems = stepsLayout.inputs
                .filterNot { i -> i.item in producedItems }
                .map { it.item }.groupingBy { it }.eachCount()

        BeltLayer.layBelts(blueprint, pathsToGenerate, { it.pos.x > ProductionLine.materialInputsOffset })

        return blueprint
    }

}

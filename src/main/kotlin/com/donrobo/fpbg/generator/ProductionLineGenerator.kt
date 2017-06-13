package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.BeltLayer
import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.DirectionalInt2
import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.planner.ProductionLine

fun generateBlueprint(productionLine: ProductionLine): Blueprint {
    val stepsLayout = ProductionStepsLayout(productionLine.productionSteps)
    val blueprint = stepsLayout.generateBlueprint()

    val pathsToGenerate = ArrayList<Pair<DirectionalInt2, DirectionalInt2>>()

    stepsLayout.inputs.forEach { input ->
        val output = productionLine.materialInputs.find { it.item == input.item }
                ?: stepsLayout.outputs.find { it.item == input.item }
                ?: throw RuntimeException("Missing output for ${input.item}")

        when (input.beltSide) {
            BeltSide.BOTH -> pathsToGenerate.add(Pair(DirectionalInt2(output.position, output.direction),
                    DirectionalInt2(input.position, Direction.UP)))
            else -> TODO("Not yet implemented")
        }
    }

    BeltLayer.layBelts(blueprint, pathsToGenerate, { it.pos.x > ProductionLine.materialInputsOffset })

    return blueprint
}
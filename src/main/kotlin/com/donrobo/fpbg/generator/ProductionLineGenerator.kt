package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.BeltLayer
import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.DirectionalInt2
import com.donrobo.fpbg.blueprint.building.Splitter
import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo
import com.donrobo.fpbg.planner.ProductionLine

class ProductionLineGenerator(val productionLine: ProductionLine) {

    fun generateBlueprint(): Blueprint {
        val stepsLayout = ProductionStepsLayout(productionLine.productionSteps)
        val blueprint = stepsLayout.generateBlueprint()

        val pathsToGenerate = ArrayList<Pair<DirectionalInt2, DirectionalInt2>>()

        val beltsNeeded = stepsLayout.inputs.groupingBy { it.item }.eachCount()
        val stepsLayoutsInputsMap = stepsLayout.inputs.associateBy { it.item }

        val actualOutputs = ArrayList<PositionalBeltIo>()

        beltsNeeded.forEach { (item, count) ->
            //            val beltIo = stepsLayoutsInputsMap[item] ?: throw RuntimeException("Something got lost somewhere")
            val output = productionLine.materialInputs.find { it.item == item }
                    ?: stepsLayout.outputs.find { it.item == item }
                    ?: throw RuntimeException("Missing output for $item")
            when (count) {
                1 -> actualOutputs.add(output)
                2 -> when (output.direction) {
                    Direction.RIGHT -> run {
                        blueprint.addBuilding(Splitter(output.position.x, output.position.y, Direction.RIGHT))
                        actualOutputs.add(PositionalBeltIo(position = output.position + Int2(1, 0), type = BeltIoType.OUTPUT,
                                direction = Direction.RIGHT, beltSide = output.beltSide, item = output.item))
                    }
                    else -> TODO("Only RIGHT for now")
                }
            }
        }

        BeltLayer.layBelts(blueprint, pathsToGenerate, { it.pos.x > ProductionLine.materialInputsOffset })

        return blueprint
    }

}

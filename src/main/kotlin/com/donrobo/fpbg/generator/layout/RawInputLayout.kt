package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo

class RawInputLayout(requiredItems: Map<String, Int>, xOffset: Int) : Layout {
    private val internalBlueprint: Blueprint = Blueprint()
    override val inputs: List<PositionalBeltIo>
    override val outputs: List<PositionalBeltIo>

    init {
        inputs = ArrayList()
        outputs = ArrayList()

        requiredItems.forEach { (itemName, itemCount) ->
            val combinationLayout = buildCombinationLayout(itemName, 0, 0, Direction.RIGHT, itemCount)
            val combinationBlueprint = combinationLayout.generateBlueprint()
            val yOffset = internalBlueprint.maximumY + 2 - combinationBlueprint.minimumY
            internalBlueprint.addBlueprint(combinationBlueprint, xOffset, yOffset)
            inputs.addAll(combinationLayout.inputs.map { it.move(Int2(xOffset, yOffset)) })
            outputs.addAll(combinationLayout.outputs.map { it.move(Int2(xOffset, yOffset)) })
        }
    }

    override val width: Int
        get() = internalBlueprint.width
    override val height: Int
        get() = internalBlueprint.height

    override fun generateBlueprint(): Blueprint {
        return internalBlueprint
    }

}
package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.data.PositionalBeltIo

class FullBeltWrapperLayout(internalLayout: Layout) : Layout {

    private val internalBlueprint: Blueprint = Blueprint()

    override val width: Int get() = internalBlueprint.width
    override val height: Int get() = internalBlueprint.height
    override val inputs: List<PositionalBeltIo>
    override val outputs: List<PositionalBeltIo>

    override fun generateBlueprint(): Blueprint {
        return internalBlueprint
    }

    init {
        internalBlueprint.addBlueprint(internalLayout.generateBlueprint(), 0, 0)

        val inputs = ArrayList<PositionalBeltIo>()
        val outputs = ArrayList<PositionalBeltIo>(internalLayout.outputs) //TODO überlegen

        val inputsToAnalyze = ArrayList(internalLayout.inputs)
        while (inputsToAnalyze.isNotEmpty()) {
            val currentInputs = inputsToAnalyze
                    .filter { it.position.manhattanDistance(inputsToAnalyze[0].position) <= 1 }
            inputsToAnalyze.removeAll(currentInputs)
            assert(currentInputs.size in 1..4)

            if (currentInputs.any { it.direction != Direction.UP }) TODO("Support other directions")

            val rightBeltInputs = currentInputs.filter { it.position.x == currentInputs.map { it.position.x }.max() }
            val leftBeltInputs = currentInputs.filterNot { it in rightBeltInputs }.filter { it.position.x == currentInputs.map { it.position.x }.min() }

            assert(rightBeltInputs.size in 1..2)
            assert(leftBeltInputs.size in 0..2)
            if (leftBeltInputs.isNotEmpty()) assert(rightBeltInputs.size == 2)

            val item1 = rightBeltInputs.find { it.beltSide == BeltSide.RIGHT || it.beltSide == BeltSide.BOTH }
            val item2 = rightBeltInputs.find { it.beltSide == BeltSide.LEFT }
            val item3 = leftBeltInputs.find { it.beltSide == BeltSide.RIGHT || it.beltSide == BeltSide.BOTH }
            val item4 = leftBeltInputs.find { it.beltSide == BeltSide.LEFT }

            val count =
                    if (item4 != null) 4
                    else if (item3 != null) 3
                    else if (item2 != null) 2
                    else if (item1 != null) 1
                    else throw RuntimeException("Impossible count")

            val layoutToUse = when (count) {
                1 -> null
                2 -> TwoToOneLayout(Direction.UP, item1!!.item, item2!!.item, item1.position.x, item1.position.y + 1)
                3 -> ThreeToTwoLayout(Direction.UP, item1!!.item, item2!!.item, item3!!.item, item1.position.x, item1.position.y + 1)
                4 -> FourToTwoLayout(Direction.UP, item1!!.item, item2!!.item, item3!!.item, item4!!.item, item1.position.x, item1.position.y + 1)
                else -> throw RuntimeException("Impossible count")
            }

            if (layoutToUse != null) {
                internalBlueprint.addBlueprint(layoutToUse.generateBlueprint(), 0, 0)
                inputs.addAll(layoutToUse.inputs)
            } else {
                inputs.add(item1!!)
            }
        }

        this.inputs = inputs
        this.outputs = outputs
    }


}
package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.move
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo

fun buildCombinationLayout(item: String, x: Int, y: Int, direction: Direction, outputCount: Int): CombinationLayout {
    val combinationLayout = CombinationLayout()
    if (outputCount == 1) {
        combinationLayout.internalLayouts.add(SingleBeltLayout(item, x, y, direction))
    } else {
        combinationLayout.internalLayouts.add(OneToTwoLayout(item, x, y, direction, rightHanded = true))
    }

    while (combinationLayout.outputs.size < outputCount) {
        val sortedOutputs = ArrayList(combinationLayout.expandableOutputs.sortedBy { it.position.manhattanDistance(Int2(x, y)) })

        val rightOkay: (PositionalBeltIo) -> Boolean = {
            !combinationLayout.isOccupied(it.position.move(it.direction.rotateCW(1)))
        }
        val leftOkay: (PositionalBeltIo) -> Boolean = {
            !combinationLayout.isOccupied(it.position.move(it.direction.rotateCW(3)))
        }
        val chosenOutput = sortedOutputs.first { leftOkay(it) || rightOkay(it) }

        val startPosition = chosenOutput.position.move(chosenOutput.direction)
        val rightHanded = rightOkay(chosenOutput)
        combinationLayout.internalLayouts.add(OneToTwoLayout(item, startPosition.x - if (rightHanded) 0 else 1, startPosition.y, chosenOutput.direction, rightHanded = rightHanded))
    }

    assert(combinationLayout.inputs.size == 1)
    assert(combinationLayout.outputs.size == outputCount)
    assert(combinationLayout.outputs.none { combinationLayout.isOccupied(it.position.move(it.direction)) })

    return combinationLayout
}

class CombinationLayout(val internalLayouts: MutableList<PositionalLayout> = ArrayList()) : PositionalLayout {

    override val x: Int
        get() = inputs.single().position.x

    override val y: Int
        get() = inputs.single().position.y

    val minimumX: Int
        get() = allOutputs.map { it.position.x }.min() ?: 0

    val maximumX: Int
        get() = allOutputs.map { it.position.x }.max() ?: 0

    val minimumY: Int
        get() = allOutputs.map { it.position.y }.min() ?: 0

    val maximumY: Int
        get() = allOutputs.map { it.position.y }.max() ?: 0

    override val width: Int
        get() = if (internalLayouts.isEmpty()) 0 else maximumX - minimumX + 1

    override val height: Int
        get() = if (internalLayouts.isEmpty()) 0 else maximumY - minimumY + 1

    private val allInputs
        get() = internalLayouts.flatMap { it.inputs }

    private val allOutputs
        get() = internalLayouts.flatMap { it.outputs }

    override val inputs: List<PositionalBeltIo>
        get() = allInputs.filterNot { ai -> ai.position.move(ai.direction.reversed) in allOutputs.map { it.position } }

    override val outputs: List<PositionalBeltIo>
        get() = allOutputs.filterNot { ao -> ao.position.move(ao.direction) in allInputs.map { it.position } }

    val growingY: Boolean
        get() = when (inputs.single().direction) {
            Direction.UP, Direction.DOWN -> true
            Direction.RIGHT, Direction.LEFT -> false
        }

    val expandableOutputs: List<PositionalBeltIo>
        get() = if (growingY)
            outputs.filter { it.position.x == maximumX || it.position.x == minimumX }
        else
            outputs.filter { it.position.y == maximumY || it.position.y == minimumY }

    override fun generateBlueprint(): Blueprint {
        return Blueprint().apply { internalLayouts.forEach { internal -> addBlueprint(internal.generateBlueprint()) } }
    }

    fun isOccupied(position: Int2): Boolean = internalLayouts.any { it.isInside(position) }
}

private fun PositionalLayout.isInside(position: Int2): Boolean = position.x >= x && position.x < x + width && position.y >= y && position.y < y + height

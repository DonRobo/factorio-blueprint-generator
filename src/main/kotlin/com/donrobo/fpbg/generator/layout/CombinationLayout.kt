package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.move
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo

fun buildCombinationLayout(item: String, x: Int, y: Int, direction: Direction, outputCount: Int): CombinationLayout {
    val combinationLayout = CombinationLayout(item)
    if (outputCount == 1) {
        combinationLayout.internalLayouts.add(SingleBeltLayout(item, x, y, direction))
    } else {
        combinationLayout.internalLayouts.add(OneToTwoLayout(item, x, y, direction, rightHanded = true))
    }

    while (combinationLayout.outputs.size < outputCount) {
        val sortedOutputs = ArrayList(combinationLayout.expandableOutputs.sortedBy { it.position.manhattanDistance(Int2(x, y)) })

        val chosenOutput = sortedOutputs.first()

        val rightHanded = chosenOutput in combinationLayout.rightExpandableOutputs
        val startPosition = chosenOutput.position.move(chosenOutput.direction).move(chosenOutput.direction.rotateCW(3), if (rightHanded) 0 else 1)
        combinationLayout.internalLayouts.add(OneToTwoLayout(item, startPosition.x, startPosition.y, chosenOutput.direction, rightHanded = rightHanded))
    }

    assert(combinationLayout.inputs.size == 1)
    assert(combinationLayout.outputs.size == outputCount)
    assert(combinationLayout.outputs.none { combinationLayout.generateBlueprint().isOccupied(it.position.move(it.direction)) })

    return combinationLayout
}

class CombinationLayout(val item: String, val internalLayouts: MutableList<PositionalLayout> = ArrayList()) : PositionalLayout {

    private val origin: PositionalBeltIo
        get() = if (inputs.size == 1)
            inputs.single()
        else internalLayouts.firstOrNull { it.outputs.size == 1 }?.outputs?.single() ?:
                TODO()

    val direction: Direction get() = origin.direction
    override val x: Int
        get() = origin.position.x

    override val y: Int
        get() = origin.position.y

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
        get() = when (origin.direction) {
            UP, DOWN -> true
            RIGHT, LEFT -> false
        }

    val leftExpandableOutputs: List<PositionalBeltIo>
        get() = when (direction) {
            UP -> outputs.filter { it.position.x == minimumX }
            RIGHT -> outputs.filter { it.position.y == minimumY }
            DOWN -> outputs.filter { it.position.x == maximumX }
            LEFT -> outputs.filter { it.position.y == maximumY }
        }

    val rightExpandableOutputs: List<PositionalBeltIo>
        get() = when (direction) {
            UP -> outputs.filter { it.position.x == maximumX }
            RIGHT -> outputs.filter { it.position.y == maximumY }
            DOWN -> outputs.filter { it.position.x == minimumX }
            LEFT -> outputs.filter { it.position.y == minimumY }
        }

    val expandableOutputs: List<PositionalBeltIo>
        get() = leftExpandableOutputs + rightExpandableOutputs

    override fun generateBlueprint(): Blueprint {
        return Blueprint().apply { internalLayouts.forEach { internal -> addBlueprint(internal.generateBlueprint()) } }
    }

}

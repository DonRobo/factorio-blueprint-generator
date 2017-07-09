package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.move
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo

fun buildCombinationLayout(item: String, layoutToExtend: Layout, outputCount: Int): CombinationLayout {
    val oldInputSize = layoutToExtend.inputs.size
    val combinationLayout = CombinationLayout(item, mutableListOf(layoutToExtend))

    if (combinationLayout.outputs.size > outputCount)
        throw RuntimeException("Something probably went wrong")

    while (combinationLayout.outputs.size < outputCount) {
        val sortedOutputs = combinationLayout.expandableOutputs
                .sortedBy { it.position.manhattanDistance(Int2(combinationLayout.x, combinationLayout.y)) } //TODO use minBy

        val chosenOutput = sortedOutputs.first()

        val rightHanded = chosenOutput in combinationLayout.rightExpandableOutputs
        val startPosition = chosenOutput.position.move(chosenOutput.direction).move(chosenOutput.direction.rotateCW(3), if (rightHanded) 0 else 1)
        combinationLayout.internalLayouts.add(OneToTwoLayout(item, startPosition.x, startPosition.y, chosenOutput.direction, rightHanded = rightHanded))
    }

    assert(combinationLayout.inputs.size == oldInputSize)
    assert(combinationLayout.outputs.size == outputCount)
    assert(combinationLayout.outputs.none { combinationLayout.generateBlueprint().isOccupied(it.position.move(it.direction)) })

    return combinationLayout
}

fun buildCombinationLayout(item: String, x: Int, y: Int, direction: Direction, outputCount: Int): CombinationLayout {
    val layout: Layout
    if (outputCount == 1) {
        layout = SingleBeltLayout(item, x, y, direction)
    } else {
        layout = OneToTwoLayout(item, x, y, direction, rightHanded = true)
    }

    return buildCombinationLayout(item, layout, outputCount)
}

class CombinationLayout(val item: String, val internalLayouts: MutableList<Layout> = ArrayList()) : PositionalLayout {

    private val origin: PositionalBeltIo
        get() = internalLayouts.firstOrNull { it.outputs.size == 1 }?.outputs?.single() ?:
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

    val rightExpandableOutputs: List<PositionalBeltIo>
        get() = when (direction) {
            UP -> outputs.filter { it.position.x == maximumX }
            RIGHT -> outputs.filter { it.position.y == maximumY }
            DOWN -> outputs.filter { it.position.x == minimumX }
            LEFT -> outputs.filter { it.position.y == minimumY }
        }

    val leftExpandableOutputs: List<PositionalBeltIo>
        get() = when (direction) {
            UP -> outputs.filter { it.position.x == minimumX }
            RIGHT -> outputs.filter { it.position.y == minimumY }
            DOWN -> outputs.filter { it.position.x == maximumX }
            LEFT -> outputs.filter { it.position.y == maximumY }
        }.filterNot { it in rightExpandableOutputs }

    val expandableOutputs: List<PositionalBeltIo>
        get() = leftExpandableOutputs + rightExpandableOutputs

    override fun generateBlueprint(): Blueprint {
        return Blueprint().apply { internalLayouts.forEach { internal -> addBlueprint(internal.generateBlueprint()) } }
    }

}

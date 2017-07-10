package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.toBeltBlueprint
import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo

class ThreeToTwoLayout(val direction: Direction, val item1: String, val item2: String, val item3: String, override val x: Int, override val y: Int) : PositionalLayout {

    private val Direction.rotationOffset: Int get() = when (this) {
        UP -> 0
        RIGHT -> 1
        DOWN -> 2
        LEFT -> 3
    }

    override val width: Int get() = generateBlueprint().width
    override val height: Int get() = generateBlueprint().height

    override val inputs: List<PositionalBeltIo> get() = listOf(
            PositionalBeltIo(
                    position = Int2(0, 2).rotateCW(count = direction.rotationOffset),
                    item = item1,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.INPUT
            ),
            PositionalBeltIo(
                    position = Int2(-1, 2).rotateCW(count = direction.rotationOffset),
                    item = item2,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.INPUT
            ),
            PositionalBeltIo(
                    position = Int2(-2, 2).rotateCW(count = direction.rotationOffset),
                    item = item3,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.INPUT
            )
    ).map { it.move(Int2(x, y)) }


    override val outputs: List<PositionalBeltIo> get() = listOf(
            PositionalBeltIo(
                    position = Int2(0, 0).rotateCW(count = direction.rotationOffset),
                    item = item1,
                    beltSide = BeltSide.RIGHT,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.OUTPUT
            ),
            PositionalBeltIo(
                    position = Int2(0, 0).rotateCW(count = direction.rotationOffset),
                    item = item2,
                    beltSide = BeltSide.LEFT,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.OUTPUT
            ),
            PositionalBeltIo(
                    position = Int2(-1, 0).rotateCW(count = direction.rotationOffset),
                    item = item3,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.OUTPUT
            )
    ).map { it.move(Int2(x, y)) }

    override fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        /*
         * >^^
         * ^>^<
         * ^^>^
         */
        blueprint.addBlueprint(">^^".toBeltBlueprint(), -2, 0)
        blueprint.addBlueprint("^>^<".toBeltBlueprint(), -2, 1)
        blueprint.addBlueprint("^^>^".toBeltBlueprint(), -2, 2)

        return when (direction) {
            UP -> blueprint
            RIGHT -> blueprint.rotateCW(count = 1)
            DOWN -> blueprint.rotateCW(count = 2)
            LEFT -> blueprint.rotateCW(count = 3)
        }.move(x, y)
    }

}
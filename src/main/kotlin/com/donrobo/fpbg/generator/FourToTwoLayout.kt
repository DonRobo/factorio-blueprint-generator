package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.building.Splitter
import com.donrobo.fpbg.blueprint.toBeltBlueprint
import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo

class FourToTwoLayout(val direction: Direction, val item1: String, val item2: String, val item3: String, val item4: String) : Layout {

    private val Direction.rotationOffset: Int get() = when (this) {
        UP -> 0
        RIGHT -> 1
        DOWN -> 2
        LEFT -> 3
    }

    val inputs: List<PositionalBeltIo> get() = listOf(
            PositionalBeltIo(
                    position = Int2(1, 3).rotateCW(count = direction.rotationOffset),
                    item = item1,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.INPUT
            ),
            PositionalBeltIo(
                    position = Int2(0, 3).rotateCW(count = direction.rotationOffset),
                    item = item2,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.INPUT
            ),
            PositionalBeltIo(
                    position = Int2(-1, 3).rotateCW(count = direction.rotationOffset),
                    item = item3,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.INPUT
            ),
            PositionalBeltIo(
                    position = Int2(-2, 3).rotateCW(count = direction.rotationOffset),
                    item = item4,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.INPUT
            )
    )


    val outputs: List<PositionalBeltIo> get() = listOf(
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
                    beltSide = BeltSide.RIGHT,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.OUTPUT
            ),
            PositionalBeltIo(
                    position = Int2(-1, 0).rotateCW(count = direction.rotationOffset),
                    item = item4,
                    beltSide = BeltSide.LEFT,
                    direction = UP.rotateCW(direction.rotationOffset),
                    type = BeltIoType.OUTPUT
            )
    )

    fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        /*
         *  >^^<
         * >^<>^<
         * ^<^^>^
         *  SSSS
         */
        blueprint.addBlueprint(" >^^< ".toBeltBlueprint(), -3, 0)
        blueprint.addBlueprint(">^<>^<".toBeltBlueprint(), -3, 1)
        blueprint.addBlueprint("^<^^>^".toBeltBlueprint(), -3, 2)

        blueprint.addBuilding(Splitter(-2, 3, UP))
        blueprint.addBuilding(Splitter(0, 3, UP))

        return when (direction) {
            UP -> blueprint
            RIGHT -> blueprint.rotateCW(count = 1)
            DOWN -> blueprint.rotateCW(count = 2)
            LEFT -> blueprint.rotateCW(count = 3)
        }
    }

}
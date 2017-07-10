package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.building.Splitter
import com.donrobo.fpbg.blueprint.building.YellowBelt
import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo

class OneToThreeLayout(val item: String, override val x: Int, override val y: Int, val direction: Direction) : PositionalLayout {

    private val Direction.rotationOffset: Int get() = when (this) {
        UP -> 0
        RIGHT -> 1
        DOWN -> 2
        LEFT -> 3
    }

    override val width: Int
        get() = when (direction) {
            UP, DOWN -> 3
            LEFT, RIGHT -> 2
        }

    override val height: Int
        get() = when (direction) {
            UP, DOWN -> 2
            LEFT, RIGHT -> 3
        }
    override val inputs: List<PositionalBeltIo>
        get() = listOf(PositionalBeltIo(
                position = Int2(x, y),
                beltSide = BeltSide.BOTH,
                item = item,
                type = BeltIoType.INPUT,
                direction = UP
        )).map { it.rotateCW(Int2(x, y), direction.rotationOffset).move(Int2(x, y)) }

    override val outputs: List<PositionalBeltIo>
        get() = listOf(PositionalBeltIo(
                position = Int2(x - 1, y - 1),
                beltSide = BeltSide.BOTH,
                item = item,
                type = BeltIoType.OUTPUT,
                direction = UP
        ), PositionalBeltIo(
                position = Int2(x, y - 1),
                beltSide = BeltSide.BOTH,
                item = item,
                type = BeltIoType.OUTPUT,
                direction = UP
        ), PositionalBeltIo(
                position = Int2(x + 1, y - 1),
                beltSide = BeltSide.BOTH,
                item = item,
                type = BeltIoType.OUTPUT,
                direction = UP
        )).map { it.rotateCW(Int2(x, y), direction.rotationOffset).move(Int2(x, y)) }

    override fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        /*
         * SS^
         *  SS
         */
        blueprint.addBuilding(Splitter(-1, -1, UP))
        blueprint.addBuilding(YellowBelt(1, -1, UP))
        blueprint.addBuilding(Splitter(0, 0, UP))

        val movedAndRotated = when (direction) {
            UP -> blueprint
            RIGHT -> blueprint.rotateCW(count = 1)
            DOWN -> blueprint.rotateCW(count = 2)
            LEFT -> blueprint.rotateCW(count = 3)
        }.move(x, y)

        assert(movedAndRotated.width == width)
        assert(movedAndRotated.height == height)

        return movedAndRotated
    }

}

class SingleBeltLayout(val item: String, override val x: Int, override val y: Int, val direction: Direction) : PositionalLayout {
    override val width: Int = 1
    override val height: Int = 1

    override val inputs: List<PositionalBeltIo> = listOf(PositionalBeltIo(
            position = Int2(x, y),
            direction = direction,
            beltSide = BeltSide.BOTH,
            type = BeltIoType.INPUT,
            item = item))

    override val outputs: List<PositionalBeltIo> = listOf(PositionalBeltIo(
            position = Int2(x, y),
            direction = direction,
            beltSide = BeltSide.BOTH,
            type = BeltIoType.OUTPUT,
            item = item))

    override fun generateBlueprint(): Blueprint {
        return Blueprint().apply { addBuilding(YellowBelt(x, y, direction)) }
    }
}

class OneToTwoLayout(val item: String, override val x: Int, override val y: Int, val direction: Direction, val rightHanded: Boolean) : PositionalLayout {

    private val Direction.rotationOffset: Int get() = when (this) {
        UP -> 0
        RIGHT -> 1
        DOWN -> 2
        LEFT -> 3
    }

    override val width: Int
        get() = when (direction) {
            UP, DOWN -> 2
            LEFT, RIGHT -> 1
        }

    override val height: Int
        get() = when (direction) {
            UP, DOWN -> 1
            LEFT, RIGHT -> 2
        }

    override val inputs: List<PositionalBeltIo>
        get() = listOf(PositionalBeltIo(
                position = Int2(x + if (rightHanded) 0 else 1, y),
                beltSide = BeltSide.BOTH,
                item = item,
                type = BeltIoType.INPUT,
                direction = UP
        )).map { it.rotateCW(Int2(x, y), direction.rotationOffset).move(Int2(x, y)) }

    override val outputs: List<PositionalBeltIo>
        get() = listOf(PositionalBeltIo(
                position = Int2(x, y),
                beltSide = BeltSide.BOTH,
                item = item,
                type = BeltIoType.OUTPUT,
                direction = UP
        ), PositionalBeltIo(
                position = Int2(x + 1, y),
                beltSide = BeltSide.BOTH,
                item = item,
                type = BeltIoType.OUTPUT,
                direction = UP
        )).map { it.rotateCW(Int2(x, y), direction.rotationOffset).move(Int2(x, y)) }

    override fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        /*
         * SS
         */
        blueprint.addBuilding(Splitter(0, 0, UP))

        val movedAndRotated = when (direction) {
            UP -> blueprint
            RIGHT -> blueprint.rotateCW(count = 1)
            DOWN -> blueprint.rotateCW(count = 2)
            LEFT -> blueprint.rotateCW(count = 3)
        }.move(x, y)

        assert(movedAndRotated.width == width)
        assert(movedAndRotated.height == height)

        return movedAndRotated
    }

}

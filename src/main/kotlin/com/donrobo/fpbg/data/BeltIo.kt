package com.donrobo.fpbg.data

import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.Direction.DOWN
import com.donrobo.fpbg.blueprint.Direction.UP
import com.donrobo.fpbg.data.BeltIoType.INPUT
import com.donrobo.fpbg.data.BeltIoType.OUTPUT

enum class BeltSide {
    LEFT, RIGHT, BOTH
}

enum class BeltIoType {
    INPUT,
    OUTPUT
}

/**
 * position is always exactly where the first/last belt is
 */
data class PositionalBeltIo(val position: Int2, val type: BeltIoType, val beltSide: BeltSide, val item: String,
                            val direction: Direction = when (type) {
                                INPUT -> UP
                                OUTPUT -> DOWN
                            }) {
    fun rotateCW(around: Int2 = Int2(0, 0), count: Int): PositionalBeltIo {
        return PositionalBeltIo(
                position = position.rotateCW(around, count),
                direction = direction.rotateCW(count),
                type = type,
                item = item,
                beltSide = beltSide
        )
    }

    fun move(offset: Int2) =
            PositionalBeltIo(
                    position = position + offset,
                    direction = direction,
                    item = item,
                    beltSide = beltSide,
                    type = type
            )
}
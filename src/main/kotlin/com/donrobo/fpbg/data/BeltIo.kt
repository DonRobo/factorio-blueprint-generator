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

data class IndexedBeltIo(val beltIndex: Int, val type: BeltIoType, val beltSide: BeltSide, val item: String)

data class PositionalBeltIo(val position: Int2, val type: BeltIoType, val beltSide: BeltSide, val item: String,
                            val direction: Direction = when (type) {
                                INPUT -> UP
                                OUTPUT -> DOWN
                            })
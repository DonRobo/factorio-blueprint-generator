package com.donrobo.fpbg.generator.data

enum class BeltSide {
    LEFT, RIGHT, BOTH
}

enum class BeltIoType {
    INPUT,
    OUTPUT
}

data class IndexedBeltIo(val beltIndex: Int, val type: BeltIoType, val beltSide: BeltSide, val item: String)

data class PositionalBeltIo(val position: Pair<Int, Int>, val type: BeltIoType, val beltSide: BeltSide, val item: String)

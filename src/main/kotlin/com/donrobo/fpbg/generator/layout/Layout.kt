package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.data.PositionalBeltIo

interface Layout {

    val width: Int
    val height: Int
    val inputs: List<PositionalBeltIo>
    val outputs: List<PositionalBeltIo>

    fun generateBlueprint(): Blueprint
}

interface PositionalLayout : Layout {
    val x: Int
    val y: Int
}
package com.donrobo.fpbg.data

data class Int2(val x: Int, val y: Int) {
    override fun toString(): String {
        return "[$x/$y]"
    }
}
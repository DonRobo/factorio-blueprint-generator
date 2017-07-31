package com.donrobo.fpbg.blueprint

import com.donrobo.fpbg.blueprint.building.Splitter
import com.donrobo.fpbg.blueprint.building.UndergroundBelt
import com.donrobo.fpbg.blueprint.building.YellowBelt
import com.donrobo.fpbg.data.Int2
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

class BeltLayerTest {

    @Test
    fun layOneSimpleBelt() {
        val input = arrayOf(Pair(DirectionalInt2(Int2(0, 0), Direction.RIGHT), DirectionalInt2(Int2(5, 0), Direction.UP)))
        val blueprint = BeltLayer.layBelts(*input)
        assertCorrectBelts(input, blueprint)
    }

    @Test
    fun layOneDiagonalBelt() {
        val input = arrayOf(Pair(DirectionalInt2(Int2(0, 0), Direction.RIGHT), DirectionalInt2(Int2(5, 5), Direction.UP)))
        val blueprint = BeltLayer.layBelts(*input)
        assertCorrectBelts(input, blueprint)
    }

    @Test
    fun layTwoSimpleBelts() {
        val input = arrayOf(
                Pair(DirectionalInt2(Int2(0, 0), Direction.RIGHT), DirectionalInt2(Int2(5, 0), Direction.UP)),
                Pair(DirectionalInt2(Int2(0, 2), Direction.RIGHT), DirectionalInt2(Int2(5, 2), Direction.UP))
        )
        val blueprint = BeltLayer.layBelts(*input)
        assertCorrectBelts(input, blueprint)
    }

    @Test
    fun layTwoDiagonalBelts() {
        val input = arrayOf(
                Pair(DirectionalInt2(Int2(0, 0), Direction.RIGHT), DirectionalInt2(Int2(5, 5), Direction.UP)),
                Pair(DirectionalInt2(Int2(0, 2), Direction.RIGHT), DirectionalInt2(Int2(5, 7), Direction.UP))
        )
        val blueprint = BeltLayer.layBelts(*input)
        assertCorrectBelts(input, blueprint)
    }

    @Ignore("TODO") //TODO
    @Test
    fun layTwoCrossingBelts() {
        val input = arrayOf(
                Pair(DirectionalInt2(Int2(0, 0), Direction.RIGHT), DirectionalInt2(Int2(5, 2), Direction.UP)),
                Pair(DirectionalInt2(Int2(0, 2), Direction.RIGHT), DirectionalInt2(Int2(5, 0), Direction.UP))
        )
        val blueprint = BeltLayer.layBelts(*input)
        assertCorrectBelts(input, blueprint)
    }

    private fun assertCorrectBelts(input: Array<Pair<DirectionalInt2, DirectionalInt2>>, blueprint: Blueprint) {
        try {
            input.forEach {
                assertLeadsTo(blueprint, it.first.pos, it.second)
            }
        } catch(t: Throwable) {
            throw t
        } finally {
            println(blueprint.visualizer().visualize())
        }
    }

    private fun assertLeadsTo(blueprint: Blueprint, start: Int2, destination: DirectionalInt2) {
        if (start == destination.pos) //OKAY
        {
            val building = blueprint[start]!!
            assertTrue(building is YellowBelt)
            assertEquals(destination.direction, (building as YellowBelt).direction)
            return
        }
        val building = blueprint[start]!!
        when (building) {
            is YellowBelt -> assertLeadsTo(blueprint, building.direction.move(start), destination)
            is UndergroundBelt -> fail("TODO")
            is Splitter -> fail("TODO")
            else -> fail("Unexpected building")
        }
    }

}
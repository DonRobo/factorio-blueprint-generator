package com.donrobo.fpbg.blueprint

import com.donrobo.fpbg.blueprint.building.UndergroundBelt
import com.donrobo.fpbg.blueprint.building.YellowBelt
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.util.MapVisualizer

data class AStarNode(val pos: Int2, val target: Int2, val parent: AStarNode?) {
    val f: Int get() = g + h
    val g: Int get() = 10 + (parent?.g ?: 0)
    val h: Int get() = Math.abs(target.x - pos.x) * 10 + Math.abs(target.y - pos.y) * 10
    fun visualizer(blueprint: Blueprint): MapVisualizer {
        val visualizer = parent?.visualizer(blueprint) ?: blueprint.visualizer()

        visualizer[pos] = 'o'

        return visualizer
    }
}

data class DirectionalInt2(val pos: Int2, val direction: Direction)

typealias BeltLayerLimitation = (AStarNode) -> Boolean

object BeltLayer {

    fun layBelts(blueprint: Blueprint, paths: List<Pair<DirectionalInt2, DirectionalInt2>>, vararg limitations: BeltLayerLimitation) {
        paths.forEach { (first, second) ->
            val start = first.pos
            val realStart = first.direction.reversed.move(start)

            val startBuilding = YellowBelt(realStart.x, realStart.y, first.direction)
            if (!blueprint.isOccupied(startBuilding))
                blueprint.addBuilding(startBuilding)

            val destBuilding = YellowBelt(second.pos.x, second.pos.y, second.direction)
            if (!blueprint.isOccupied(destBuilding))
                blueprint.addBuilding(destBuilding)
        }

        paths.forEach { path ->
            try {
                layBelt(blueprint, path, *limitations)
            } catch(t: Throwable) {
                println(blueprint.visualizer().visualize())
                throw t
            }
        }
    }

    private fun layBelt(blueprint: Blueprint, path: Pair<DirectionalInt2, DirectionalInt2>, vararg limitations: BeltLayerLimitation) {
        var currentNode = generatePath(blueprint, path, *limitations)

        while (currentNode.parent != null) {
            val parent = currentNode.parent ?: throw RuntimeException("Literally not possible")

            val calcDirection: Direction

            if (parent.pos.x < currentNode.pos.x) {
                calcDirection = Direction.RIGHT
            } else if (parent.pos.x > currentNode.pos.x) {
                calcDirection = Direction.LEFT
            } else if (parent.pos.y < currentNode.pos.y) {
                calcDirection = Direction.DOWN
            } else if (parent.pos.y > currentNode.pos.y) {
                calcDirection = Direction.UP
            } else {
                throw RuntimeException("Shouldn't happen")
            }

            blueprint.addBuilding(YellowBelt(parent.pos.x, parent.pos.y, calcDirection))
            currentNode = parent
        }
        return
    }

    private fun generatePath(blueprint: Blueprint, path: Pair<DirectionalInt2, DirectionalInt2>, vararg limitations: BeltLayerLimitation): AStarNode {
        val open = mutableListOf(AStarNode(pos = path.first.pos, parent = null, target = path.second.pos))
        val closed = mutableListOf<AStarNode>()

        while (!open.isEmpty()) {
            val current = open.minBy { it.f } ?: throw RuntimeException("Literally not possible")

            assert(!blueprint.isOccupied(current.pos))

            if (current.pos == path.second.pos) {
                return current
            }

            listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT).forEach { dir ->
                val adj = dir.move(current.pos)
                val node = AStarNode(pos = adj, parent = current, target = path.second.pos)
                if (adj == path.second.pos)
                    return node
                addIfValid(node, open, closed, blueprint, *limitations)
            }
            open.remove(current)
            closed.add(current)
        }

        throw RuntimeException("No path found")
    }

    private fun addIfValid(node: AStarNode, open: MutableList<AStarNode>, closed: MutableList<AStarNode>, blueprint: Blueprint, vararg limitations: BeltLayerLimitation) {
        if (limitations.any { limitation -> !limitation(node) }) return

        val existing = open.filter { it.pos == node.pos }.minBy { it.f }
        if (existing != null) {
            if (existing.f < node.f)
                return
            else if (existing.f > node.f)
                open.remove(existing)
        }
        if (closed.find { it.pos == node.pos } != null)
            return

        if (blueprint.isOccupied(node.pos)) {
            return
        }

        listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT).forEach { dir ->
            val buildingLookedAt = blueprint[dir.move(node.pos)]
            when (buildingLookedAt) {
                is YellowBelt -> if (buildingLookedAt.direction.reversed == dir) return
                is UndergroundBelt -> if (buildingLookedAt.isOutput && buildingLookedAt.direction.reversed == dir) return
            }
        }

        open.add(node)
    }

    fun layBelts(vararg paths: Pair<DirectionalInt2, DirectionalInt2>): Blueprint {
        val blueprint = Blueprint()
        layBelts(blueprint, listOf(*paths))
        return blueprint
    }

}
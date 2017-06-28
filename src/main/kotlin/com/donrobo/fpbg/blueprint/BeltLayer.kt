package com.donrobo.fpbg.blueprint

import com.donrobo.fpbg.blueprint.building.Building
import com.donrobo.fpbg.blueprint.building.UndergroundBelt
import com.donrobo.fpbg.blueprint.building.YellowBelt
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.util.MapVisualizer

enum class NodeType {
    BELT, UNDERGROUND_BELT_START, UNDERGROUND_BELT_END
}

data class AStarNode(val pos: Int2, val target: Int2, val parent: AStarNode?, val type: NodeType) {
    val f: Int get() = g + h
    val g: Int get() = when (type) {
        NodeType.BELT -> 10
        NodeType.UNDERGROUND_BELT_START -> 30
        NodeType.UNDERGROUND_BELT_END -> 30
    } + (parent?.g ?: 0)
    val h: Int get() = Math.abs(target.x - pos.x) * 10 + Math.abs(target.y - pos.y) * 10
    fun visualizer(blueprint: Blueprint): MapVisualizer {
        val visualizer = parent?.visualizer(blueprint) ?: blueprint.visualizer()

        visualizer[pos] = 'o'

        return visualizer
    }

    val direction: Direction get() = if (parent != null) Direction.calculateDirection(parent.pos, pos) else TODO("Default direction?")
}

data class DirectionalInt2(val pos: Int2, val direction: Direction) {
    operator fun plus(other: Int2): DirectionalInt2 {
        return DirectionalInt2(pos + other, direction)
    }
}

typealias BeltLayerLimitation = (AStarNode) -> Boolean

object BeltLayer {

    fun layBelts(blueprint: Blueprint, paths: List<Pair<DirectionalInt2, DirectionalInt2>>, vararg limitations: BeltLayerLimitation) {
        paths.forEach { path ->
            try {
                val dontOverwriteStartOrEndLimitation: (AStarNode) -> Boolean = { node ->
                    val illegalPositions = ArrayList<Int2>()

                    paths.forEach { p ->
                        val isOwnPath = p.second.pos == node.target

                        if (!isOwnPath) {
                            illegalPositions.add(p.first.pos) //can't create new node on start; is already created automatically at the start
                            illegalPositions.add(p.first.direction.move(p.first.pos))
                            illegalPositions.add(p.second.pos) //can't place node at goal if it wants to go somewhere else
                            illegalPositions.add(p.second.direction.reversed.move(p.second.pos)) //can't place node at goal if it wants to go somewhere else
                        } else {
                            if (node.type == NodeType.UNDERGROUND_BELT_START) { //can't start underground belt where the goal is supposed to be
                                illegalPositions.add(p.second.pos)
                            } else if (node.type == NodeType.UNDERGROUND_BELT_END && node.direction != p.second.direction) {
                                illegalPositions.add(p.second.pos) //can't end an underground belt in the wrong direction at the goal
                            }
                        }
                    }

                    !illegalPositions.contains(node.pos)
                }
                layBelt(blueprint, Pair(path.first, path.second), *limitations, dontOverwriteStartOrEndLimitation)
            } catch(t: Throwable) {
                println(blueprint.visualizer().visualize())
                throw t
            }
        }
    }

    private fun layBelt(blueprint: Blueprint, path: Pair<DirectionalInt2, DirectionalInt2>, vararg limitations: BeltLayerLimitation) {
        var currentNode = generatePath(blueprint, path, *limitations)

        blueprint.addBuilding(generateBuildingFor(currentNode, AStarNode(
                pos = path.second.direction.move(currentNode.pos),
                type = NodeType.BELT,
                target = path.second.pos,
                parent = currentNode)))

        while (currentNode.parent != null) {
            val parent = currentNode.parent ?: throw RuntimeException("Literally not possible")

            blueprint.addBuilding(generateBuildingFor(parent, currentNode))
            currentNode = parent
        }
        return
    }

    private fun generateBuildingFor(currentNode: AStarNode, nextNode: AStarNode): Building {
        val direction = Direction.calculateDirection(currentNode.pos, nextNode.pos)
        return when (currentNode.type) {
            NodeType.BELT -> YellowBelt(currentNode.pos.x, currentNode.pos.y, direction)
            NodeType.UNDERGROUND_BELT_START -> UndergroundBelt(currentNode.pos.x, currentNode.pos.y, direction, true)
            NodeType.UNDERGROUND_BELT_END -> UndergroundBelt(currentNode.pos.x, currentNode.pos.y, direction, false)
        }
    }

    private fun generatePath(blueprint: Blueprint, path: Pair<DirectionalInt2, DirectionalInt2>, vararg limitations: BeltLayerLimitation): AStarNode {
        val open = mutableListOf(AStarNode(pos = path.first.pos, parent = null, target = path.second.pos, type = NodeType.BELT))
        val closed = mutableListOf<AStarNode>()

        while (!open.isEmpty()) {
            val current = open.minBy { it.f } ?: throw RuntimeException("Literally not possible")

            assert(!blueprint.isOccupied(current.pos))

            if (current.pos == path.second.pos) {
                return current
            }

            when (current.type) {
                NodeType.UNDERGROUND_BELT_START -> for (i in 1..5) {
                    if (current.parent != null) {
                        val underEnd = current.direction.move(current.pos, i)
                        val node = AStarNode(pos = underEnd, parent = current, target = path.second.pos, type = NodeType.UNDERGROUND_BELT_END)
                        addIfValid(node, open, closed, blueprint, *limitations)
                    }
                }
                NodeType.UNDERGROUND_BELT_END -> if (current.parent != null) {
                    val adj = current.direction.move(current.pos)
                    val node = AStarNode(pos = adj, parent = current, target = path.second.pos, type = NodeType.BELT)
                    addIfValid(node, open, closed, blueprint, *limitations)
                }
                NodeType.BELT -> listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT).forEach { dir ->
                    val adj = dir.move(current.pos)
                    val node = AStarNode(pos = adj, parent = current, target = path.second.pos, type = NodeType.BELT)
                    addIfValid(node, open, closed, blueprint, *limitations)
                    addIfValid(AStarNode(pos = adj, parent = current, target = path.second.pos, type = NodeType.UNDERGROUND_BELT_START), open, closed, blueprint, *limitations)
                }
            }

            open.remove(current)
            closed.add(current)

            if (open.isEmpty()) {
                println(current.visualizer(blueprint).visualize())
            }
        }

        throw RuntimeException("No path found")
    }

    private fun addIfValid(node: AStarNode, open: MutableList<AStarNode>, closed: MutableList<AStarNode>, blueprint: Blueprint, vararg limitations: BeltLayerLimitation) {
        if (limitations.any { limitation -> !limitation(node) }) return
        if (node.type == NodeType.UNDERGROUND_BELT_END && node.parent?.type != NodeType.UNDERGROUND_BELT_START) return

        val existing = open.filter { it.pos == node.pos && it.type == node.type }.minBy { it.f }
        if (existing != null) {
            if (existing.f < node.f)
                return
            else if (existing.f > node.f)
                open.remove(existing)
        }
        if (closed.find { it.pos == node.pos && it.type == node.type } != null)
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
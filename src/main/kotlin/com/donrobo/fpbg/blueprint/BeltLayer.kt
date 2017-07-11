package com.donrobo.fpbg.blueprint

import com.donrobo.fpbg.blueprint.building.Building
import com.donrobo.fpbg.blueprint.building.UndergroundBelt
import com.donrobo.fpbg.blueprint.building.YellowBelt
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.util.MapVisualizer

enum class NodeType {
    BELT, UNDERGROUND_BELT_START, UNDERGROUND_BELT_END
}

data class AStarNode(val pos: Int2, val target: Int2, val parent: AStarNode?, val type: NodeType, val blueprint: Blueprint) {
    val f: Int get() = g + h
    val g: Int get() = when (type) {
        NodeType.BELT -> 10
        NodeType.UNDERGROUND_BELT_START -> 30
        NodeType.UNDERGROUND_BELT_END -> 30
    } + isCurved + blockedLeft + blockedRight + (parent?.g ?: 0)

    val h: Int get() = Math.abs(target.x - pos.x) * 11 + Math.abs(target.y - pos.y) * 9

    val isCurved: Int get() = if (parent != null && parent.parent != null && direction != parent.direction) 1 else 0
    val blockedLeft: Int get() = if (blueprint.isOccupied(leftPos)) 3 else 0
    val blockedRight: Int get() = if (blueprint.isOccupied(rightPos)) 3 else 0

    private val leftPos: Int2 get() = pos.move(direction.rotateCW(3))
    private val rightPos: Int2 get() = pos.move(direction.rotateCW(1))

    fun visualizer(blueprint: Blueprint, closed: List<AStarNode> = emptyList(), open: List<AStarNode> = emptyList()): MapVisualizer {
        val visualizer = parent?.visualizer(blueprint, closed, open) ?: blueprint.visualizer().apply {
            closed.map { it.pos }.forEach { set(it, 'x') }
        }.apply {
            open.map { it.pos }.forEach { set(it, '_') }
        }

        visualizer[pos] = 'o'

        return visualizer
    }

    val direction: Direction get() = if (parent != null) Direction.calculateDirection(parent.pos, pos) else Direction.DOWN
}

data class DirectionalInt2(val pos: Int2, val direction: Direction) {
    operator fun plus(other: Int2): DirectionalInt2 {
        return DirectionalInt2(pos + other, direction)
    }
}

typealias BeltLayerLimitation = (AStarNode) -> Boolean

object BeltLayer {

    fun layBelts(blueprint: Blueprint, paths: List<Pair<DirectionalInt2, DirectionalInt2>>, vararg limitations: BeltLayerLimitation) {
        paths.sortedByDescending { it.second.pos.x }.forEach { path ->
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
                println("Path done")
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
                parent = currentNode,
                blueprint = blueprint)))

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
        val open = mutableListOf(AStarNode(pos = path.first.pos, parent = null, target = path.second.pos, type = NodeType.BELT, blueprint = blueprint))
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
                        val node = AStarNode(pos = underEnd, parent = current, target = path.second.pos, type = NodeType.UNDERGROUND_BELT_END, blueprint = blueprint)
                        addIfValid(node, open, closed, blueprint, *limitations)
                    }
                }
                NodeType.UNDERGROUND_BELT_END -> if (current.parent != null) {
                    val adj = current.pos.move(current.direction)
                    addIfValid(AStarNode(pos = adj, parent = current, target = path.second.pos, type = NodeType.BELT, blueprint = blueprint), open, closed, blueprint, *limitations)
                    addIfValid(AStarNode(pos = adj, parent = current, target = path.second.pos, type = NodeType.UNDERGROUND_BELT_START, blueprint = blueprint), open, closed, blueprint, *limitations)
                }
                NodeType.BELT -> listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT).forEach { dir ->
                    val adj = dir.move(current.pos)
                    addIfValid(AStarNode(pos = adj, parent = current, target = path.second.pos, type = NodeType.BELT, blueprint = blueprint), open, closed, blueprint, *limitations)
                    addIfValid(AStarNode(pos = adj, parent = current, target = path.second.pos, type = NodeType.UNDERGROUND_BELT_START, blueprint = blueprint), open, closed, blueprint, *limitations)
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


        val isUndergroundBelt = node.type == NodeType.UNDERGROUND_BELT_START || node.type == NodeType.UNDERGROUND_BELT_END

        if (closed.any {
            it.pos == node.pos
                    && it.type == node.type
                    && (it.direction == node.direction || !isUndergroundBelt)
                    && (it.direction.isSameAxis(node.direction) || isUndergroundBelt)
        })
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

        var parent = node.parent
        while (parent != null) {
            if (node.pos == parent.pos)
                return
            parent = parent.parent
        }

        if (node.type == NodeType.UNDERGROUND_BELT_END) {
            if (node.parent == null) return

            var lookAt = node.parent.pos.move(node.direction)
            while (lookAt != node.pos) {
                val building = blueprint.get(lookAt)
                if (building is UndergroundBelt && node.direction.isSameAxis(building.direction)) {
                    return
                }
                lookAt = lookAt.move(node.direction)
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
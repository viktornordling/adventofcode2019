package d22

import util.Pos
import java.math.BigInteger

object Solution {
    fun solve() {
        val map = mutableMapOf<Pos, Char>()
        val eroMap = mutableMapOf<Pos, BigInteger>()
        val riskMap = mutableMapOf<Pos, Int>()
        val geoMap = mutableMapOf<Pos, BigInteger>()
        val depth = "9171"
//        val depth = "510"

        val cols = 210
        val rows = 4900
//        val cols = 13
//        val rows = 13
        val target = Pos(7,721)
//        val target = Pos(10, 10)
        for (y in 0..rows) {
            for (x in 0..cols) {
                val curPos = Pos(x, y)
                var geoIndex = BigInteger("0")
                if (x == 0 && y == 0) {

                } else if (curPos == target) {
                    // geo = 0
                } else if (x == 0) {
                    geoIndex = BigInteger.valueOf(y.toLong()).multiply(BigInteger("48271"))
                } else if (y == 0) {
                    geoIndex = BigInteger.valueOf(x.toLong()).multiply(BigInteger("16807"))
                } else {
                    val x1 = eroMap.get(Pos(x - 1, y))!!
                    val y1 = eroMap.get(Pos(x, y - 1))!!
                    geoIndex = x1.multiply(y1)
                }
                val erosion = geoIndex.plus(BigInteger(depth)).mod(BigInteger("20183"))
                var type = erosion.mod(BigInteger("3")).toInt()
                var typeC = when (type) {
                    0 -> '.'
                    1 -> '='
                    else -> '|'
                }
                if (curPos == target) {
                    typeC = '.'
                    type = 0
                }
                eroMap[curPos] = erosion
                geoMap[curPos] = geoIndex
                map[curPos] = typeC
                riskMap[curPos] = type
            }
        }
        printMap(map)

        var risk = 0
        for (y in 0..rows) {
            for (x in 0..cols) {
                risk += riskMap.get(Pos(x, y))!!
            }
        }
        println(risk)

        val shortestPath = findShortestPath(map, Node(pos = Pos(0, 0), tool = 'T'), Node(pos = target, tool = 'T'), cols, rows).reversed()
        println(shortestPath)
        println(shortestPath.size)

        var minutes = 0
        var curNode = shortestPath.first()
        for (node in shortestPath.drop(1)) {
            minutes += if (curNode.pos == node.pos) 7 else 1
            curNode = node
        }
        println("Minutes = $minutes")
    }

    fun printMap(map: Map<Pos, Char>) {
        val minX = map.keys.minBy { it.x }!!.x
        val minY = map.keys.minBy { it.y }!!.y
        val maxX = map.keys.maxBy { it.x }!!.x
        val maxY = map.keys.maxBy { it.y }!!.y

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val cur = map.get(Pos(x, y))
                if (cur == null) {
                    print('#')
                } else {
                    print(cur)
                }
            }
            println()
        }
    }

    fun reconstructPath(cameFrom: MutableMap<Node, Node>, current: Node):List<Node> {
        val path = mutableListOf(current)
        var cur = current
        while (cameFrom.containsKey(cur)) {
            cur = cameFrom.get(cur)!!
            path.add(cur)
        }
        return path
    }

    fun h(start: Node, end: Node):Int = Math.abs(start.pos.x - end.pos.x) + Math.abs(start.pos.y - end.pos.y)

    data class Node(val pos: Pos, val tool: Char)

    fun findShortestPath(map: Map<Pos, Char>, start: Node, goal: Node, maxX: Int, maxY: Int):List<Node> {
        val closed = mutableSetOf<Node>()
        val open = mutableSetOf(start)

        val cameFrom: MutableMap<Node, Node> = mutableMapOf()
        val gScore = mutableMapOf<Node, Int>()
        gScore.put(start, 0)
        val fScore = mutableMapOf<Node, Int>()
        fScore.put(start, h(start, goal))

        while (!open.isEmpty()) {
            val current = open.minBy { fScore.getOrDefault(it, 9000000) }!!
            if (current == goal) {
                return reconstructPath(cameFrom, current)
            }
            open.remove(current)
            closed.add(current)

            for (pos in openNeighbours(map, current, maxX, maxY)) {
                if (closed.contains(pos)) {
                    continue
                }
                val cost = if (current.pos == pos.pos) 7 else 1
                val tentScore = gScore.get(current)!! + cost
                if (!open.contains(pos)) {
                    open.add(pos)
                } else if (tentScore >= gScore.getOrDefault(pos, 9000000)) {
                    continue
                }

                cameFrom.put(pos, current)
                gScore.put(pos, tentScore)
                fScore.put(pos, gScore.get(pos)!! + h(pos, goal))
            }
        }
        return emptyList()
    }

    private fun getAllowedToolsForPos(map: Map<Pos, Char>, pos: Pos): Set<Char> {
//        println("cur = $pos")
        val c = map[pos]!!
        return when (c) {
            '.' -> setOf('C', 'T')
            '=' -> setOf('C', 'N')
            else -> setOf('T', 'N')
        }
    }

    private fun openNeighbours(map: Map<Pos, Char>, node: Node, maxX: Int, maxY: Int): Set<Node> {
        val neighbours = setOf(
            node.copy(pos=node.pos.copy(x = node.pos.x+1)),
            node.copy(pos=node.pos.copy(x = node.pos.x-1)),
            node.copy(pos=node.pos.copy(y = node.pos.y+1)),
            node.copy(pos=node.pos.copy(y = node.pos.y-1))
        )
        val inBound = neighbours.filter { it.pos.x >= 0 && it.pos.y >= 0 && it.pos.x < maxX && it.pos.y < maxY }
        val rightTool = inBound.filter { hasRightTool(map, it.pos, node.tool) }

        // Add nodes for changing to the right tool for any neighbours.
        // Try changing to all tools that are allowed in this pos.
        val allowedToolsInThisPos = getAllowedToolsForPos(map, node.pos)

        // Only perform the changes which are actually useful for getting to neighbours.
        val neighbourTools = inBound.flatMap { getAllowedToolsForPos(map, it.pos) }.toSet()
        val usefulTools = allowedToolsInThisPos.filter { neighbourTools.contains(it) }
        val switchedTool = usefulTools.map { node.copy(tool = it) }

        return rightTool.toSet() + switchedTool.toSet()
    }

    private fun hasRightTool(map: Map<Pos, Char>, pos: Pos, tool: Char) = getAllowedToolsForPos(map, pos).contains(tool)

}

fun main(args: Array<String>) {
    Solution.solve()
}
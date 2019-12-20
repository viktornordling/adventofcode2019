package d20

import util.Pos3
import util.Reader
import java.lang.IllegalArgumentException

data class Teleporter(val name: String, val from: Pos3, val to: Pos3)

object Solution {

    val teleporters = mutableListOf<Teleporter>()
    val posToTeleporter = mutableMapOf<Pos3, Teleporter>()
    var minX = -1
    var maxX = 99999
    var minY = -1
    var maxY = 99999

    fun hasTeleporter(pos: Pos3) = posToTeleporter[pos.copy(level = 0)] != null
    fun otherSideOfTeleporter(pos: Pos3, teleporter: Teleporter, map: MutableMap<Pos3, Char>): Pos3 {
        val otherSide = when (pos.copy(level = 0)) {
            teleporter.from -> teleporter.to
            teleporter.to -> teleporter.from
            else -> throw IllegalArgumentException("Pos $pos is not part of this teleporter")
        }
        val isInner = pos.x > minX && pos.x < maxX && pos.y > minY && pos.y < maxY
        val levelChange = if (isInner) 1 else -1
        return otherSide.neighbours().first { map[it.copy(level = 0)] == '.' }.copy(level = pos.level + levelChange)
    }

    fun solve() {
        val input: List<String> = Reader.readInput("easy.txt")
        val map = mutableMapOf<Pos3, Char>()
        var y = 0
        for (line in input) {
            var x = 0
            for (c in line) {
                map[Pos3(x, y)] = c
                x++
            }
            y++
        }
        val walls = map.entries.filter { it.value == '#' }.map { it.key to it.value }.toMap()
        minX = walls.keys.minBy { it.x }!!.x
        minY = walls.keys.minBy { it.y }!!.y
        maxX = walls.keys.maxBy { it.x }!!.x
        maxY = walls.keys.maxBy { it.y }!!.y

        val teleporterPositions = mutableMapOf<String, List<Pos3>>()
        for (entry in map.entries) {
            if (entry.value.toString().matches(Regex("[A-Z]"))) {
                val pos = entry.key
                if (pos.neighbours().map { map[it] }.filter { c -> c == '.' }.isNotEmpty()) {
                    // Find the other letter for this pos, it's one of its neighbors
                    val n = pos.neighbours().map { map[it] }.filterNotNull()
                    val otherLetter: Char = n.filter { c -> c.toString().matches(Regex("[A-Z]")) }.first()
                    val teleporterName = entry.value.toString() + otherLetter.toString()
                    val sorted = teleporterName.toCharArray().sorted().joinToString("")
                    val cur = teleporterPositions[sorted] ?: emptyList()
                    println("Found half a teleporter ($sorted) at pos $pos")
                    teleporterPositions[sorted] = cur + pos
                }
            }
        }

        var start = Pos3(0, 0, 0)
        var end = Pos3(0, 0, 0)
        for (entry in teleporterPositions.entries) {
            val teleporterName = entry.key
            val positions = entry.value
            if (teleporterName == "AA") {
                println("AA is at ${positions[0]}")
                start = positions[0].neighbours().filter { map[it] == '.' }.first()
            } else if (teleporterName == "ZZ") {
                println("ZZ is at ${positions[0]}")
                end = positions[0].neighbours().filter { map[it] == '.' }.first()
            } else {
                if (positions.size != 2) {
                    throw IllegalArgumentException("Teleporter ${entry.key} at pos ${entry.value} seems like a broken teleporter.")
                }
                val teleporter = Teleporter(teleporterName, positions[0], positions[1])
                teleporters.add(teleporter)
                posToTeleporter[teleporter.from] = teleporter
                posToTeleporter[teleporter.to] = teleporter
            }
        }

        println("Teleporters: $teleporters")
        val shortestPath = findShortestPath(start, end, map)
        println("Shortest path is ${shortestPath.size} long")
    }

    fun findShortestPath(start: Pos3, goal: Pos3, map: MutableMap<Pos3, Char>):List<Pos3> {
        println("Finding shortest path from $start to $goal")
        val opens = start.neighbours().filter { isOpen(it, map) }
        var minDist = 9999999
        var minPath = emptyList<Pos3>()

        for (n in opens) {
            if (n == goal) {
                return listOf(goal, start)
            }
        }

        for (n in opens) {
            val s = findShortestPath2(n, goal, map)
            if (s.size in 1..(minDist - 1)) {
                minDist = s.size
                minPath = s
            }
        }
        return minPath
    }

    fun findShortestPath2(start: Pos3, goal: Pos3, map: MutableMap<Pos3, Char>):List<Pos3> {
        val shortestPaths = mutableListOf<List<Pos3>>()
        val closed = mutableSetOf<Pos3>()
        val open = mutableSetOf(start)

        val cameFrom: MutableMap<Pos3, Pos3> = mutableMapOf()
        val gScore = mutableMapOf<Pos3, Int>()
        gScore.put(start, 0)
        val fScore = mutableMapOf<Pos3, Int>()
        fScore.put(start, h(start, goal))

        while (!open.isEmpty()) {
            val current = open.minBy { fScore.getOrDefault(it, 9000000) }!!
            if (current == goal) {
                println("Reached goal!")
                shortestPaths.add(reconstructPath(cameFrom, current).reversed())
                return shortestPaths[0]
            }
            open.remove(current)
            closed.add(current)

            for (pos in openNeighbours(current, map)) {
                if (closed.contains(pos)) {
                    continue
                }
                val tentScore = gScore.get(current)!! + 1
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
        val sizeOfShortestPath = shortestPaths.minBy { it.size }?.size
        if (sizeOfShortestPath == null || sizeOfShortestPath <= 1) {
            return listOf()
        }
        val potShortestPaths = shortestPaths.filter { sizeOfShortestPath == it.size }
        val chosenShortestPath = potShortestPaths.first()
        return chosenShortestPath
    }

    fun openNeighbours(pos: Pos3, map: MutableMap<Pos3, Char>):Set<Pos3> {
        val opens = pos.neighbours().filter { isOpen(it, map) }
        val teleporterNeighbors = pos.neighbours().filter { hasTeleporter(it) }
        val teleporterPositions = mutableListOf<Pos3>()
        for (teleporterNeighbor: Pos3 in teleporterNeighbors) {
            val teleporter = posToTeleporter[teleporterNeighbor.copy(level = 0)]
            if (teleporter != null) {
                val other = otherSideOfTeleporter(teleporterNeighbor, teleporter, map)
                if (other.level >= 0) {
                    teleporterPositions.add(other)
                }
            }
        }

        return opens.toSet() + teleporterPositions.toSet()
    }

    fun h(start: Pos3, end: Pos3):Int = 0

    fun reconstructPath(cameFrom: MutableMap<Pos3, Pos3>, current: Pos3):List<Pos3> {
        val path = mutableListOf(current)
        var cur = current
        while (cameFrom.containsKey(cur)) {
            cur = cameFrom.get(cur)!!
            path.add(cur)
        }
        return path
    }

    private fun isOpen(pos: Pos3, map: MutableMap<Pos3, Char>): Boolean {
        val open = map[pos.copy(level = 0)] == '.'
        return open
    }

}

fun main() {
    val start = System.currentTimeMillis()
    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
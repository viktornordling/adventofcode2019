package d20

import util.Pos
import util.Reader
import util.Surface
import java.lang.IllegalArgumentException

data class Teleporter(val name: String, val from: Pos, val to: Pos)
//data class Cell(val char: Char, val teleporter: Teleporter?)

object Solution {

    val teleporters = mutableListOf<Teleporter>()
    val posToTeleporter = mutableMapOf<Pos, Teleporter>()

    fun hasTeleporter(pos: Pos) = posToTeleporter[pos] != null
    fun otherSideOfTeleporter(pos: Pos, teleporter: Teleporter, map: MutableMap<Pos, Char>): Pos {
        var otherSide = Pos(0, 0)
        if (teleporter.from == pos) {
            otherSide = teleporter.to
        } else if (teleporter.to == pos) {
            otherSide = teleporter.from
        } else {
            throw IllegalArgumentException("Pos $pos is not part of this teleporter")
        }
//        println("Other side of teleporter is ${map[otherSide]}, but really we need to get the open spot.")
        val first = otherSide.neighbours().filter { map[it] == '.' }.first()
        return first
    }

    fun solve() {
        val input: List<String> = Reader.readInput("easy.txt")
        val map = mutableMapOf<Pos, Char>()
        var y = 0
        for (line in input) {
            var x = 0
            for (c in line) {
                map[Pos(x, y)] = c
                x++
            }
            y++
        }

        Surface.printMap(map)

        val teleporterPositions = mutableMapOf<String, List<Pos>>()
        for (entry in map.entries) {
            if (entry.value.toString().matches(Regex("[A-Z]"))) {
                val pos = entry.key
                if (pos.neighbours().map { map[it] }.filter { c -> c == '.' }.isNotEmpty()) {
                    // Find the other letter for this pos, it's one of its neighbors
                    val n = pos.neighbours().map { map[it] }.filterNotNull()
//                    println("n = $n")
                    val otherLetter: Char = n.filter { c -> c.toString().matches(Regex("[A-Z]")) }.first()
                        ?: throw IllegalArgumentException("${entry.value} at pos ${entry.key} seems like a broken teleporter.")
                    val teleporterName = entry.value.toString() + otherLetter.toString()
                    val sorted = teleporterName.toCharArray().sorted().joinToString("")
                    val cur = teleporterPositions[sorted] ?: emptyList()
                    println("Found half a teleporter ($sorted) at pos $pos")
                    teleporterPositions[sorted] = cur + pos
                }
            }
        }

        var start = Pos(0, 0)
        var end = Pos(0, 0)
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

    fun findShortestPath(start: Pos, goal: Pos, map: MutableMap<Pos, Char>):List<Pos> {
        println("Finding shortest path from $start to $goal")
        val opens = start.neighbours().filter { isOpen(it, map) }
        var minDist = 9999999
        var minPath = emptyList<Pos>()

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

    fun findShortestPath2(start: Pos, goal: Pos, map: MutableMap<Pos, Char>):List<Pos> {
        val shortestPaths = mutableListOf<List<Pos>>()
        val closed = mutableSetOf<Pos>()
        val open = mutableSetOf(start)

        val cameFrom: MutableMap<Pos, Pos> = mutableMapOf()
        val gScore = mutableMapOf<Pos, Int>()
        gScore.put(start, 0)
        val fScore = mutableMapOf<Pos, Int>()
        fScore.put(start, h(start, goal))

        while (!open.isEmpty()) {
            val current = open.minBy { fScore.getOrDefault(it, 9000000) }!!
            if (current == goal) {
                println("Reached goal!")
                shortestPaths.add(reconstructPath(cameFrom, current).reversed())
            }
            open.remove(current)
            closed.add(current)

            for (pos in openNeighbours(current, map)) {
                if (closed.contains(pos)) {
                    continue
                }
                val tentScore = gScore.get(current)!! + 1
//                println("Tent score: $tentScore")
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
        if (potShortestPaths.size > 1) {
//            println("Size of potShortestPaths = ${potShortestPaths.size}")
        }
        val chosenShortestPath = potShortestPaths.first()
        return chosenShortestPath
    }

    fun openNeighbours(pos: Pos, map: MutableMap<Pos, Char>):Set<Pos> {
        val opens = pos.neighbours().filter { isOpen(it, map) }
//        println("Open neighbours of $pos is $opens")
        val teleporterNeighbors = pos.neighbours().filter { hasTeleporter(it) }
        val teleporterPositions = mutableListOf<Pos>()
        for (teleporterNeighbor: Pos in teleporterNeighbors) {
            val teleporter = posToTeleporter[teleporterNeighbor]
            if (teleporter != null) {
                teleporterPositions.add(otherSideOfTeleporter(teleporterNeighbor, teleporter, map))
            }
        }
//        println("Teleporter neighbours of $pos is $teleporterPositions")

        return opens.toSet() + teleporterPositions.toSet()
    }

//    fun h(start: Pos, end: Pos):Int = Math.abs(start.x - end.x) + Math.abs(start.y - end.y)
    fun h(start: Pos, end: Pos):Int = 0

    fun reconstructPath(cameFrom: MutableMap<Pos, Pos>, current: Pos):List<Pos> {
        val path = mutableListOf(current)
        var cur = current
        while (cameFrom.containsKey(cur)) {
            cur = cameFrom.get(cur)!!
            path.add(cur)
        }
        return path
    }

    private fun isOpen(pos: Pos, map: MutableMap<Pos, Char>): Boolean {
        // If the pos is a telporter then it's a dot.
        val open = map[pos] == '.'
        return open
    }

}

fun main() {
    val start = System.currentTimeMillis()
    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
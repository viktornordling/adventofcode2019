package d24

import util.Pos
import java.io.File
import java.math.BigInteger

object Solution {

    fun solvePartOne() {
        val lines = File("easy.txt").readLines()
        var map = mutableMapOf<Pos, Char>()
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                map[Pos(x, y)] = char
            }
        }

        val seenRatings = mutableSetOf<BigInteger>()
        while (true) {
            map = evolve(map)
            val bioRating = bioRating(map, 5)
            if (seenRatings.contains(bioRating)) {
                println("Already seen $bioRating!")
                return
            }
            seenRatings.add(bioRating)
        }
    }

    fun solvePartTwo() {
        val lines = File("easy.txt").readLines()
        var maps = mutableMapOf<Int, MutableMap<Pos, Char>>()
        val map = mutableMapOf<Pos, Char>()
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                map[Pos(x, y)] = char
            }
        }
        maps[0] = map

        for (i in 1..200) {
            maps = evolveMaps(maps)
        }

        val bugCount = maps.flatMap { it.value.map { char -> char.value } }.filter { it == '#' }.count()
        println("Bug count after 200 iterations: $bugCount")
    }

    private fun bioRating(map: MutableMap<Pos, Char>, cols: Int): BigInteger {
        var bioRating = BigInteger.ZERO
        for (entry in map.entries) {
            val pos = entry.key
            val char = entry.value
            val index = pos.y * cols + pos.x
            if (char == '#') {
                val cellRating = BigInteger.valueOf(2).pow(index)
                bioRating += cellRating
            }
        }
        return bioRating
    }

    private fun evolve(map: MutableMap<Pos, Char>): MutableMap<Pos, Char> {
        val newMap = mutableMapOf<Pos, Char>()
        for (entry in map.entries) {
            val pos = entry.key
            val char = entry.value

            val neighborCount = pos.neighbours().map { map[it] }.filter { it == '#' }.count()
            if (char == '#' && neighborCount != 1) {
                newMap[pos] = '.'
            } else if (char == '.' && neighborCount == 1 || neighborCount == 2) {
                newMap[pos] = '#'
            } else {
                newMap[pos] = char
            }
        }
        return newMap
    }

    private fun evolveMaps(maps: MutableMap<Int, MutableMap<Pos, Char>>): MutableMap<Int, MutableMap<Pos, Char>> {
        val newMaps = mutableMapOf<Int, MutableMap<Pos, Char>>()
        val lowestLevel = maps.keys.min() ?: 0
        val highestLevel = maps.keys.max() ?: 0
        for (level in (lowestLevel - 1)..(highestLevel + 1)) {
            val map: MutableMap<Pos, Char> = maps[level] ?: createEmptyMap()
            val levelAbove: MutableMap<Pos, Char> = maps[level + 1] ?: mutableMapOf()
            val levelBelow: MutableMap<Pos, Char> = maps[level - 1] ?: mutableMapOf()
            val newMap = mutableMapOf<Pos, Char>()

            for (mapEntry in map.entries) {
                val pos = mapEntry.key
                val char = mapEntry.value
                if (pos == Pos(2, 2)) {
                    continue
                }

                val localNeighbors: List<Char> = pos.neighbours().map { map[it] }.filterNotNull()
                val levelAboveNeighbours: List<Char> = getLevelAboveNeighbors(pos, levelAbove)
                val levelBelowNeighbours: List<Char> = getLevelBelowNeighbors(pos, levelBelow)
                val allNeighbors = levelAboveNeighbours + levelBelowNeighbours + localNeighbors
                val neighborCount = allNeighbors.filter { it == '#' }.count()
                if (char == '#' && neighborCount != 1) {
                    newMap[pos] = '.'
                } else if (char == '.' && neighborCount == 1 || neighborCount == 2) {
                    newMap[pos] = '#'
                } else {
                    newMap[pos] = char
                }
            }
            newMaps[level] = newMap
        }
        return newMaps
    }

    private fun createEmptyMap(): MutableMap<Pos, Char> {
        val map = mutableMapOf<Pos, Char>()
        for (x in 0..4) {
            for (y in 0..4) {
                map[Pos(x, y)] = '.'
            }
        }
        return map
    }

    private fun getLevelAboveNeighbors(pos: Pos, levelAbove: MutableMap<Pos, Char>): List<Char> {
        // Corners:
        if (pos == Pos(0, 0)) {
            return listOf(levelAbove[Pos(2, 1)], levelAbove[Pos(1, 2)]).filterNotNull()
        } else if (pos == Pos(4, 0)) {
            return listOf(levelAbove[Pos(2, 1)], levelAbove[Pos(3, 2)]).filterNotNull()
        } else if (pos == Pos(0, 4)) {
            return listOf(levelAbove[Pos(1, 2)], levelAbove[Pos(2, 3)]).filterNotNull()
        } else if (pos == Pos(4, 4)) {
            return listOf(levelAbove[Pos(2, 3)], levelAbove[Pos(3, 2)]).filterNotNull()
        }

        // Sides:
        if (pos.x == 0) {
            return listOf(levelAbove[Pos(1, 2)]).filterNotNull()
        } else if (pos.x == 4) {
            return listOf(levelAbove[Pos(3, 2)]).filterNotNull()
        } else if (pos.y == 0) {
            return listOf(levelAbove[Pos(2, 1)]).filterNotNull()
        }  else if (pos.y == 4) {
            return listOf(levelAbove[Pos(2, 3)]).filterNotNull()
        }
        return listOf()
    }

    private fun getLevelBelowNeighbors(pos: Pos, levelBelow: MutableMap<Pos, Char>): List<Char> {
        // Only four cases:
        if (pos == Pos(2, 1)) {
            return listOf(levelBelow[Pos(0, 0)], levelBelow[Pos(1, 0)], levelBelow[Pos(2, 0)], levelBelow[Pos(3, 0)], levelBelow[Pos(4, 0)]).filterNotNull()
        } else if (pos == Pos(1, 2)) {
            return listOf(levelBelow[Pos(0, 0)], levelBelow[Pos(0, 1)], levelBelow[Pos(0, 2)], levelBelow[Pos(0, 3)], levelBelow[Pos(0, 4)]).filterNotNull()
        } else if (pos == Pos(3, 2)) {
            return listOf(levelBelow[Pos(4, 0)], levelBelow[Pos(4, 1)], levelBelow[Pos(4, 2)], levelBelow[Pos(4, 3)], levelBelow[Pos(4, 4)]).filterNotNull()
        } else if (pos == Pos(2, 3)) {
            return listOf(levelBelow[Pos(0, 4)], levelBelow[Pos(1, 4)], levelBelow[Pos(2, 4)], levelBelow[Pos(3, 4)], levelBelow[Pos(4, 4)]).filterNotNull()
        }
        return listOf()
    }

}

fun main() {
    val start = System.currentTimeMillis()
    Solution.solvePartOne()
    Solution.solvePartTwo()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
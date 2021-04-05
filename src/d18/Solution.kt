package d18

import util.Pos
import util.Reader
import util.Surface
import java.math.BigInteger

data class Key(val pos: Pos, val key: Char)
data class Door(val pos: Pos, val key: Char)
data class Dependency(val key: Key, val neededKeys: Set<Key>)

object Solution {

    val globalCheapest: MutableMap<Pair<Pos, Set<Key>>, Int> = mutableMapOf()
    var globalCounter = BigInteger.ZERO

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

        val startPos = map.filter { it.value == '@' }.keys.first()
        val keys: Set<Key> = findKeys(map)
        val doors = findDoors(map)

        val dependencies = createDependencies(keys, doors, map, startPos)
        val keyToKeyCost: Map<Pair<Pos, Pos>, Int> = createKeyToKeyCosts(startPos, keys, map)

        val cost = findCheapestCostToGetAllKeys(keyToKeyCost, keys, map, dependencies, startPos, emptyList(), startPos)
        println(cost)
    }

    fun findCheapestCostToGetAllKeys(
        keyToKeyCost: Map<Pair<Pos, Pos>, Int>,
        allKeys: Set<Key>,
        map: MutableMap<Pos, Char>,
        deps: MutableSet<Dependency>,
        start: Pos,
        keysInHand: List<Key>,
        curPos: Pos): Int {
        globalCounter++
        val existingValue = globalCheapest[Pair(curPos, keysInHand.toSet())]
        if (!keysInHand.isEmpty() && existingValue != null) {
            return existingValue
        }
        if (keysInHand.size == allKeys.size) {
            return 0
        }
        var cfsf = 999999
        val keysWithNoDeps: List<Dependency> = findKeysWithNoDeps(deps, map, keysInHand.toSet()).filter { !keysInHand.contains(it.key) }

        for (key in keysWithNoDeps) {
            val costToKey = keyToKeyCost[Pair(curPos, key.key.pos)]!!
            val cost = costToKey + findCheapestCostToGetAllKeys(keyToKeyCost, allKeys, map, deps, start, keysInHand + key.key, key.key.pos)
            if (cost < cfsf) {
                cfsf = cost
            }
        }
        globalCheapest[Pair(curPos, keysInHand.toSet())] = cfsf
        return cfsf
    }

    private fun createKeyToKeyCosts(start: Pos, keys: Set<Key>, map: MutableMap<Pos, Char>): Map<Pair<Pos, Pos>, Int> {
        val costs = mutableMapOf<Pair<Pos, Pos>, Int>()
        for (key in keys) {
            for (otherKey in keys) {
                if (key != otherKey) {
                    val path = findShortestPath(key.pos, otherKey.pos, map)
                    costs[Pair(key.pos, otherKey.pos)] = path.size
                }
            }
        }

        for (key in keys) {
            val path = findShortestPath(start, key.pos, map)
            costs[Pair(start, key.pos)] = path.size
        }

        return costs.toMap()
    }

    fun findKeysWithNoDeps(deps: MutableSet<Dependency>, map: MutableMap<Pos, Char>, keysInHand: Set<Key>): List<Dependency> {
        val depsWithKeyInHandExcluded = deps.map { it: Dependency -> it.copy(neededKeys = it.neededKeys - keysInHand) }
        return depsWithKeyInHandExcluded.filter { it.neededKeys.isEmpty() }
    }

    fun createDependencies(keys: Set<Key>, doors: Set<Door>, map: MutableMap<Pos, Char>, startPos: Pos): MutableSet<Dependency> {
        // for each key, walk from the key to the starting point and see which doors you need to go through
        val posToDoor: Map<Pos, Door> = doors.map { it.pos to it }.toMap()
        val keyNameToKey: Map<Char, Key> = keys.map { it.key to it }.toMap()
        val doorToKeys: Map<Door, Key> = doors.map { it to keyNameToKey[it.key.toLowerCase()]!! }.toMap()
        val deps = mutableSetOf<Dependency>()
        for (key in keys) {
            val shortestPath: List<Pos> = findShortestPath(key.pos, startPos, map)
            val keysNeeded = mutableSetOf<Key>()
            for (pos in shortestPath) {
                if (posToDoor.containsKey(pos)) {
                    keysNeeded.add(doorToKeys[posToDoor[pos]]!!)
                }
            }
            deps.add(Dependency(key, keysNeeded))
        }
        return deps
    }

    fun findKeys(map: MutableMap<Pos, Char>): Set<Key> {
        return map.filter { it.value.toString().matches(Regex("[a-z]"))}
            .map { Key(it.key, it.value) }.toSet()
    }

    fun findDoors(map: MutableMap<Pos, Char>): Set<Door> {
        return map.filter { it.value.toString().matches(Regex("[A-Z]"))}
            .map { Door(it.key, it.value) }.toSet()
    }

    fun findShortestPath(start: Pos, goal: Pos, map: MutableMap<Pos, Char>):List<Pos> {
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
                shortestPaths.add(reconstructPath(cameFrom, current).reversed())
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
        if (potShortestPaths.size > 1) {
//            println("Size of potShortestPaths = ${potShortestPaths.size}")
        }
        val chosenShortestPath = potShortestPaths.first()
        return chosenShortestPath
    }

    fun openNeighbours(pos: Pos, map: MutableMap<Pos, Char>):Set<Pos> {
        val opens = pos.neighbours().filter { isOpen(it, map) }
        return opens.toSet()
    }

    fun h(start: Pos, end: Pos):Int = Math.abs(start.x - end.x) + Math.abs(start.y - end.y)

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
        // Consider a position open if it's not a wall _and_ it has one surrounding known pos
        if (map[pos] == null) {
            val open = !pos.neighbours().map { map[it] }.filterNotNull().isEmpty()
            return open
        }
        val open = map[pos] != '#'
        return open
    }

}

fun main() {
    val start = System.currentTimeMillis()
    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
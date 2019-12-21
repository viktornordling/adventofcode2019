package d18

import util.Pos
import util.Reader
import util.Surface
import java.math.BigInteger

data class Key(val pos: Pos, val key: Char)
data class Door(val pos: Pos, val key: Char)
data class Dependency(val key: Key, val neededKeys: Set<Key>)

object Solution {

    val globalCheapest: MutableMap<Pair<Pos, Set<Key>>, Pair<Int, Int>> = mutableMapOf()
    val globalCheapestKeyCount: MutableMap<Int, Int> = mutableMapOf()
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
        val cost = findCheapestCostToGetAllKeys(keys, map, dependencies, startPos, emptyList(), startPos, 0, 999999)
        println(cost)
    }

    fun findCheapestCostToGetAllKeys(
        allKeys: Set<Key>,
        map: MutableMap<Pos, Char>,
        deps: MutableSet<Dependency>,
        start: Pos,
        keysInHand: List<Key>,
        curPos: Pos, curCost: Int,
        cheapestFoundSoFar: Int): Int {
        globalCounter++
        val keysInHandAsChars = keysInHand.map { it.key }.toSet()
        if (globalCounter % 1000.toBigInteger() == 0.toBigInteger()) {
            println("1000 iterations. Current keys in hand: ${keysInHand.map { it.key }.sorted()}. Current pos: ${curPos} Best so far: ${cheapestFoundSoFar}")
            println("1000 iterations. Current keys in hand in order: ${keysInHand.map { it.key }}. Current pos: ${curPos} Best so far: ${cheapestFoundSoFar}")
        }
        val existingValue = globalCheapest[Pair(curPos, keysInHand.toSet())]
        if (!keysInHand.isEmpty() && existingValue != null) {
            if (existingValue.first <= (curCost)) {
//                println("Reusing existing value: $existingValue for key ${Pair(curPos, keysInHand.map { it.key }.sorted())}")
                return existingValue.second
            } else {
//                println("We've been here before, but last time at a higher cost: last time: ${existingValue.first}, this time: ${curCost}")
            }
        } else {
//            println("Not reusing existing value for key ${Pair(curPos, keysInHand.map { it.key }.sorted())}")
            globalCheapest[Pair(curPos, keysInHand.toSet())] = Pair(curCost, 99999)
            if (curPos == Pos(15, 3)) {
//                println("DUUUDE, putting ${keysInHand.map { it.key }.sorted()} into the map for pos $curPos!")
            }
        }
//        println("Current keys in hand: ${keysInHand.map { it.key }}")
//        if (curCost == 136) {
//            return curCost
//        }
        if (curCost >= cheapestFoundSoFar) {
            return cheapestFoundSoFar
        }
        if (keysInHand.size == allKeys.size) {
//            println("Found all keys! key order: ${keysInHand.map { it.key }}. Total steps: $curCost")
            // Found all keys, mark the path that got us here
            var cp = start
            var steps = 1
            val keys = mutableSetOf<Key>()
            for (key in keysInHand) {
                val p = findShortestPath(cp, key.pos, map)
                for (c in p) {
                    globalCheapest[Pair(c, keys)] = Pair(steps++, curCost)
                }
                keys.add(key)
                cp = key.pos
            }
            return curCost
        }
        var cfsf = cheapestFoundSoFar
        val keysWithNoDeps: List<Dependency> = findKeysWithNoDeps(deps, map, keysInHand.toSet()).filter { !keysInHand.contains(it.key) }

//        val keyToPathLength = mutableMapOf<Key, Int>()
//        for (key in keysWithNoDeps) {
//            val path = findShortestPath(curPos, key.key.pos, map)
//            keyToPathLength[key.key] = path.size
//        }
//        val sorted = keysWithNoDeps.sortedBy { findShortestPath(curPos, it.key.pos, map).size }

//        val keyToShortest = mutableMapOf<Key, Int>()
//        for (key in sorted) {
        println("We have ${keysWithNoDeps.size} keys we can grab")
        for (key in keysWithNoDeps) {
            if (keysInHand.map { it.key }.toSet() == setOf('a', 'b', 'c')) {
                println("breakpoint!")
            }
//            println("Getting key ${key.key.key}")
            // Get the key and then call ourselves recursively
            val path = findShortestPath(curPos, key.key.pos, map)
            val steps = path.size
//            var step = 1
//            println("Checking if path to ${key.key.key} steps over some other key.")
            var steppingOverKey = false
            for (c in path) {
                if (map[c].toString().matches(Regex("[a-z]"))) {
                    val steppedKey = map[c]!!
                    if (steppedKey != key.key.key && !keysInHandAsChars.contains(steppedKey)) {
//                        println("On the way to ${key.key.key} we're stepping over stepped key.")
                        steppingOverKey = true
                    }
                }
            }
            if (steppingOverKey) {
                continue
            }
//            println("Steps to get key ${key.key}: $steps")
            val cost = findCheapestCostToGetAllKeys(allKeys, map, deps, start, keysInHand + key.key, key.key.pos, curCost + steps, cfsf)
            if (cost < cfsf) {
                val p = findShortestPath(curPos, key.key.pos, map)
                globalCheapest[Pair(key.key.pos, keysInHand.toSet() + key.key)] = Pair(curCost + p.size, cost)
//                val p = findShortestPath(curPos, key.key.pos, map)
//                var steps2 = 1
//                for (c in p) {
//                    globalCheapest[Pair(c, keysInHand.toSet())] = Pair(curCost + steps2++, cost)
//                }
                println("Found one path: $cost, key order: ${keysInHand.map { it.key } + key.key.key}")
                cfsf = cost
            }
//            keyToShortest[key.key] = cost
        }
        return cfsf
//        return keyToShortest.minBy { it.value }!!.value
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
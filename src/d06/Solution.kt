package d06

import util.Reader

// a is orbited by b
data class Orbit(val a: String, val b: String)

object Solution {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        val orbits: List<Orbit> = input.map { parse(it) }
        val grouped: Map<String, List<Orbit>> = orbits.groupBy { it.b }
        val planetToParentCount: MutableMap<String, Int> = mutableMapOf()
        val planets: Set<String> = orbits.flatMap { setOf(it.a, it.b) }.toSet()

        for (planet in planets) {
            planetToParentCount[planet] = findAncestors(planet, grouped).size
        }
        println("A: ${planetToParentCount.values.sum()}")

        // Find shortest path from 785 to D1K
        val adjacencyMap = createAdjacencyMap(orbits)
        val shortestPath = findShortestPath("785", "D1K", adjacencyMap)
        println("B: $shortestPath")
    }

    fun findShortestPath(cur: String, end: String, adjacencyMap: Map<String, Set<String>>, curSteps: Int = 0, seen: Set<String> = emptySet()): Int {
        if (cur == end) {
            return curSteps
        }
        val n = adjacencyMap[cur].orEmpty()
        for (s in n) {
            if (!seen.contains(s)) {
                val found = findShortestPath(s, end, adjacencyMap, curSteps + 1, seen + s)
                if (found != -1) {
                    return found
                }
            }
        }
        return -1
    }

    fun createAdjacencyMap(orbits: List<Orbit>): MutableMap<String, Set<String>> {
        val map: MutableMap<String, Set<String>> = mutableMapOf()
        for (orbit in orbits) {
            val left: Set<String> = map[orbit.a].orEmpty()
            val right = map[orbit.b].orEmpty()
            map[orbit.a] = left + orbit.b
            map[orbit.b] = right + orbit.a
        }
        return map
    }

    fun findAncestors(planet: String, orbits: Map<String, List<Orbit>>, parents: Set<String> = emptySet()): Set<String> {
        val orbitedPlanets: List<Orbit> = orbits[planet].orEmpty()
        if (orbitedPlanets.isEmpty()) {
            return parents
        } else {
            val allParents = mutableSetOf<String>()
            for (orbitedPlanet in orbitedPlanets) {
                allParents.addAll(findAncestors(orbitedPlanet.a, orbits, parents + orbitedPlanet.a))
            }
            return allParents
        }
    }

    fun parse(s: String): Orbit {
        val split: List<String> = s.split(")")
        val a: String = split.get(0)
        val b: String = split.get(1)
        return Orbit(a, b)
    }
}

fun main() {
    val start = System.currentTimeMillis()
    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
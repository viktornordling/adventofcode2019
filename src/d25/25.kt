package d25

import util.Reader

object Solution {

    data class Star(val x: Int, val y: Int, val z: Int, val z1: Int)

    fun solve() {

        fun parse(string: String): Star {
            val ints = string.split(",").map { it.trim() }.map { it.toInt() }
            return Star(ints[0], ints[1], ints[2], ints[3])
        }

        fun h(start: Star, end: Star): Int {
            val d = Math.abs(start.x - end.x) + Math.abs(start.y - end.y) + Math.abs(start.z - end.z) + Math.abs(start.z1 - end.z1)
            return d
        }

        fun isNeighbor(a: Star, b: Star): Boolean {
            return h(a, b) <= 3
        }

        fun findNeighbours(star: Star, allStars: List<Star>): Set<Star> {
            return allStars.filter { isNeighbor(star, it) }.toSet()
        }

//        val stars = Reader.readInput("easy1.txt").map { parse(it) }
//        val stars = Reader.readInput("easy2.txt").map { parse(it) }
//        val stars = Reader.readInput("easy3.txt").map { parse(it) }
//        val stars = Reader.readInput("easy4.txt").map { parse(it) }
        val stars = Reader.readInput("input.txt").map { parse(it) }

        val neighbors = stars.associate { it to findNeighbours(it, stars) }

        fun exploreAllNeighbors(star: Star, exploredStars: Set<Star>): Set<Star> {
            val n = neighbors.getOrDefault(star, emptySet()) - exploredStars
            if (n.isEmpty()) {
                return exploredStars
            }
            val newExplored = mutableSetOf<Star>()
            for (n1 in n) {
                val a = exploreAllNeighbors(n1, exploredStars + n1 + newExplored)
                newExplored.addAll(a)
            }
            return newExplored + exploredStars
        }

        fun countConstellations(exploredStars: Set<Star>, unexploredStars: Set<Star>, constellations: Int): Int {
            if (unexploredStars.isEmpty()) {
                return constellations
            }
            val cur = unexploredStars.first()
            val newExplored = exploreAllNeighbors(cur, exploredStars + cur)
            return countConstellations(newExplored + exploredStars, unexploredStars - newExplored, constellations + 1)
        }

        val constellations = countConstellations(emptySet(), stars.toSet(), 0)
        println("Constellations: $constellations")
    }

}

fun main(args: Array<String>) {
    Solution.solve()
}
package d10

import util.AocMath.gcd
import util.Reader
import kotlin.math.abs
import kotlin.math.atan

data class Asteroid(val x: Int, val y: Int)
data class Fraction(val x: Int, val y: Int, val gcd: Boolean)
data class Sighting(val angle: Fraction, val dist: Int, val otherAsteroid: Asteroid, val realAngle:Double)

object Solution {
    fun solve() {
        val input = Reader.readInput("input.txt")
        val asteroids: List<Asteroid> = input.mapIndexed { row, line ->
            line.mapIndexed { col, char -> if (char != '.') Asteroid(col, row) else null }.filterNotNull()
        }.flatten()
        val asteroidToSightings = mutableMapOf<Asteroid, MutableList<Sighting>>()

        // A
        for (asteroid in asteroids) {
            for (otherAsteroid in asteroids) {
                if (asteroid != otherAsteroid) {
                    val angle = calcAngle(asteroid, otherAsteroid)
                    val sightings: MutableList<Sighting> = asteroidToSightings[asteroid].orEmpty().toMutableList()
                    sightings.add(Sighting(angle, dist(asteroid, otherAsteroid), otherAsteroid, getAngle(asteroid, otherAsteroid)))
                    asteroidToSightings[asteroid] = sightings
                }
            }
        }
        val bestAsteroid = asteroids.map { it to countVisibleStars(it, asteroidToSightings) }.toMap().maxBy { it.value }
        println("Best asteroid is $bestAsteroid")

        // B
        val sightingsFromBestAsteroid = asteroidToSightings[bestAsteroid!!.key]!!
        var zapped = 0
        val zappedA = mutableSetOf<Asteroid>()
        while (zapped < 200) {
            val groupedSightings: Map<Double, List<Sighting>> = sightingsFromBestAsteroid.groupBy { it.realAngle }
            val zappable = mutableSetOf<Sighting>()
            for (groupedSighting: Map.Entry<Double, List<Sighting>> in groupedSightings) {
                val filtered = groupedSighting.value.filter { !zappedA.contains(it.otherAsteroid) }
                val first: Sighting? = filtered.minBy { it.dist }

                if (first != null) {
                    zappable.add(first)
                }
            }
            val sorted:List<Sighting> = zappable.sortedBy { it.realAngle }
            for (a in sorted) {
                zappedA.add(a.otherAsteroid)
                zapped++
                if (zapped == 200) {
                    println("Zapped ${a.otherAsteroid} as #{$zapped} (angle = ${a.realAngle})")
                }
            }
        }
    }

    private fun getAngle(a1: Asteroid, a2: Asteroid): Double {
        val xDiff = a2.x - a1.x
        val yDiff = a1.y - a2.y

        return when {
            yDiff < 0 && xDiff >= 0 -> 180 - atan(xDiff.toDouble() / (yDiff.toDouble() * -1.0)) * (180 / Math.PI)
            xDiff < 0 && yDiff <  0 -> 270 - atan(xDiff.toDouble() * -1.0 / (yDiff.toDouble() * -1.0)) * (180 / Math.PI)
            xDiff < 0 && yDiff >= 0 -> 360 - atan(xDiff.toDouble() / (yDiff.toDouble() * -1.0)) * (180 / Math.PI)
            else -> atan(xDiff.toDouble() / yDiff.toDouble()) * (180 / Math.PI)
        }
    }

    private fun countVisibleStars(asteroid: Asteroid, asteroidToSightings: MutableMap<Asteroid, MutableList<Sighting>>): Int {
        val sightings: MutableList<Sighting> = asteroidToSightings[asteroid].orEmpty().toMutableList()
        val groupedSightings: Map<Fraction, List<Sighting>> = sightings.groupBy { it.angle }
        return groupedSightings.keys.size
    }

    private fun dist(a1: Asteroid, a2: Asteroid): Int {
        val xDiff = abs(a1.x - a2.x)
        val yDiff = abs(a1.y - a2.y)
        return xDiff + yDiff
    }

    private fun calcAngle(a1: Asteroid, a2: Asteroid): Fraction {
        val xDiff = a1.x - a2.x
        val yDiff = a1.y - a2.y
        val d: Int = gcd(xDiff, yDiff)
        return when {
            d != 0 -> Fraction(xDiff / d, yDiff / d, d > 0)
            else -> Fraction(xDiff, yDiff, d > 0)
        }
    }
}

fun main() {
    val start = System.currentTimeMillis()

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
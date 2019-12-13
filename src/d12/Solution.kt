package d12

import util.AocMath
import kotlin.math.abs

data class Velocity(val x: Int, val y:Int, val z:Int)
data class Moon(val id: Int, val x: Int, val y:Int, val z:Int, val velocity:Velocity = Velocity(0, 0, 0))

object Solution {
    fun solve() {
        var moons = listOf(Moon(0, 3,3,0), Moon(1, 4,-16,2), Moon(2, -10,-6,5), Moon(3, -3,0,-13))
        val seenXConfigurations = mutableSetOf<List<Int>>()
        val seenYConfigurations = mutableSetOf<List<Int>>()
        val seenZConfigurations = mutableSetOf<List<Int>>()

        var xfound = false
        var yfound = false
        var zfound = false

        var xcycle = -1
        var ycycle = -1
        var zcycle = -1

        for (i in 0..1000000) {
            val xs: List<Int> = moons.map { it.x } + moons.map { it.velocity.x }
            val ys: List<Int> = moons.map { it.y } + moons.map { it.velocity.y }
            val zs: List<Int> = moons.map { it.z } + moons.map { it.velocity.z }
            if (i == 1000) {
                val energy = moons.map { potential(it) * kinetic(it) }.sum()
                println("Energy after 1000 iterations: $energy")
            }
            if (seenXConfigurations.contains(xs) && !xfound) {
                xcycle = i
                xfound = true
            }

            if (seenYConfigurations.contains(ys) && !yfound) {
                ycycle =i
                yfound = true
            }

            if (seenZConfigurations.contains(zs) && !zfound) {
                zcycle = i
                zfound = true
            }

            if (xfound && yfound && zfound) {
                println("First repeat after: ${AocMath.lcm(xcycle, ycycle, zcycle)} iterations")
                return
            }

            seenXConfigurations.add(xs)
            seenYConfigurations.add(ys)
            seenZConfigurations.add(zs)
            moons = updateVelocities(moons)
        }
    }

    fun potential(it: Moon) = abs(it.x) + abs(it.y) + abs(it.z)
    fun kinetic(it: Moon) = abs(it.velocity.x) + abs(it.velocity.y) + abs(it.velocity.z)

    private fun updateVelocities(moons: List<Moon>): List<Moon> {
        val moonsWithNewVelocity = moons.map { it.copy(velocity = newVelocity(it, moons.filter { moon -> moon != it })) }
        return moonsWithNewVelocity.map { it.copy(x = it.x + it.velocity.x, y = it.y + it.velocity.y, z = it.z + it.velocity.z)}
    }

    private fun newVelocity(it: Moon, filter: List<Moon>): Velocity {
        var xDiff = 0
        for (moon in filter) {
            if (moon.x > it.x) {
                xDiff++
            } else if (moon.x < it.x) {
                xDiff--
            }
        }
        var yDiff = 0
        for (moon in filter) {
            if (moon.y > it.y) {
                yDiff++
            } else if (moon.y < it.y) {
                yDiff--
            }
        }
        var zDiff = 0
        for (moon in filter) {
            if (moon.z > it.z) {
                zDiff++
            } else if (moon.z < it.z) {
                zDiff--
            }
        }
        val v = it.velocity
        return it.velocity.copy(x = v.x + xDiff, y = v.y + yDiff, z = v.z + zDiff)
    }
}

fun main() {
    val start = System.currentTimeMillis()

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
package d01

import util.Reader
import kotlin.math.max

object Solution {
    fun solve() {
        val input = Reader.readInput("input.txt")
        val values = input.map { it.toInt() }
        val sumA = values.map { calculateFuel(it) }.sum()
        println("Answer for A: $sumA")
        val sumB = values.map { calcFuelRec(it) }.sum()
        println("Answer for B: $sumB")
    }

    private fun calcFuelRec(mass: Int, sum: Int = 0): Int {
        val fuel = max(calculateFuel(mass), 0)
        if (fuel == 0) {
            return sum
        }
        return calcFuelRec(fuel, sum + fuel)
    }

    private fun calculateFuel(mass: Int) = mass / 3 - 2
}

fun main(args: Array<String>) {
    Solution.solve()
}
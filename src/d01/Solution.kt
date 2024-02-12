package d01

import util.Reader
import kotlin.math.max

object Solution {
    fun solve() {
        val input = Reader.readInput("input.txt")
        val values = input.map { it.toInt() }
        val sumA = values.map { counterUpper(it) }.sum()
        println("Part 1: $sumA")
        val sumB = values.map { calcFuelRec(it) }.sum()
        println("Part 2: $sumB")
    }

    private fun calcFuelRec(mass: Int, sum: Int = 0): Int {
        val fuel = max(counterUpper(mass), 0)
        if (fuel == 0) {
            return sum
        }
        return calcFuelRec(fuel, sum + fuel)
    }

    private fun counterUpper(mass: Int) = mass / 3 - 2
}

fun main(args: Array<String>) {
    Solution.solve()
}
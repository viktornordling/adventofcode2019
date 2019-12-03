package d03

import util.Reader
import kotlin.math.max

object Solution {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        val ints = input.map { it.toInt() }
    }

}

fun main() {
    val start = System.currentTimeMillis()
    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
package d08

import util.Pos
import util.Reader
import util.Surface

object Solution {
    fun solve() {
        val digits = Reader.readInput("input.txt").first()!!.map { it.toString().toInt() }
        val cols = 25
        val rows = 6
        val chunked: List<List<Int>> = digits.chunked(cols * rows)
        val minLayer = chunked.minBy { countDigits(0, it) }
        val ones = countDigits(1, minLayer!!)
        val twos = countDigits(2, minLayer!!)
        println("A: ${ones * twos}")

        val finalImage = mutableMapOf<Pos, Char>()
        for (layer in chunked.reversed()) {
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val color = layer[row * cols + col]!!
                    if (color == 0) {
                        finalImage[Pos(col, row)] = ' '
                    } else if (color == 1) {
                        finalImage[Pos(col, row)] = '#'
                    }
                }
            }
        }
        Surface.printMap(finalImage)
    }

    private fun countDigits(digit: Int, list: List<Int>): Int {
        return list.filter { it == digit }.size
    }
}


fun main() {
    val start = System.currentTimeMillis()

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
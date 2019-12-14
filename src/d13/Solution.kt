package d13

import util.Pos
import util.Reader
import util.Surface

object Solution {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        val values = input.map { it.toInt() }
        println("num values: ${values.size}")
        val inst = values.chunked(3)
        val grid = mutableMapOf<Pos, Char>()
        for (inst in inst) {
            val x = inst[0]
            val y = inst[1]
            val g = inst[2]
            val c = when {
                g == 0 -> '.'
                g == 1 -> 'X'
                g == 2 -> 'B'
                g == 3 -> '_'
                g == 4 -> 'o'
                else -> '?'
            }
            grid[Pos(x, y)] = c
        }
        println(grid.values.filter { it == 'B' }.size)
        Surface.printMap(grid)
    }

}

fun main() {
    val start = System.currentTimeMillis()

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
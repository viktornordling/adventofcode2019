package d03

import util.Pos
import util.Reader
import util.Surface
import kotlin.math.absoluteValue
import kotlin.math.max

data class Steps(val color: Char, val steps: Int)

object Solution {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        var board: MutableMap<Pos, Steps> = mutableMapOf()
        val parts: List<List<String>> = input.map { it.split(",") }
        val s1 = parts[0]
        val s2 = parts[1]
        addWire(s1, board, 'r')
        addWire(s2, board, 'g')
    }

    private fun addWire(wire: List<String>, board: MutableMap<Pos, Steps>, color: Char) {
        var curPos = Pos(0, 0)
        val intersections = mutableSetOf<Int>()
        var stepCount = 1
        for (p in wire) {
            val count = p.drop(1).toInt()-1
            if (p.startsWith("R")) {
                for (i in 0..count) {
                    curPos = curPos.copy(x = curPos.x + 1)
                    if (board[curPos] != null && board[curPos]!!.color != color) {
                        intersections.add(stepCount + board[curPos]!!.steps)
                        stepCount++
                    } else {
                        board[curPos] = Steps(color, stepCount++)
                    }
                }
            } else if (p.startsWith("L")) {
                for (i in 0..count) {
                    curPos = curPos.copy(x = curPos.x - 1)
                    if (board[curPos] != null && board[curPos]!!.color != color) {
                        intersections.add(stepCount + board[curPos]!!.steps)
                        stepCount++
                    } else {
                        board[curPos] = Steps(color, stepCount++)
                    }

                }
            } else if (p.startsWith("U")) {
                for (i in 0..count) {
                    curPos = curPos.copy(y = curPos.y - 1)
                    if (board[curPos] != null && board[curPos]!!.color != color) {
                        intersections.add(stepCount + board[curPos]!!.steps)
                        stepCount++
                    } else {
                        board[curPos] = Steps(color, stepCount++)
                    }
                }
            } else {
                for (i in 0..count) {
                    curPos = curPos.copy(y = curPos.y + 1)
                    if (board[curPos] != null && board[curPos]!!.color != color) {
                        intersections.add(stepCount + board[curPos]!!.steps)
                        stepCount++
                    } else {
                        board[curPos] = Steps(color, stepCount++)
                    }
                }
            }
        }
//        Surface.printMap(board)
        println(intersections.min())
    }

}

fun main() {
    val start = System.currentTimeMillis()
    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
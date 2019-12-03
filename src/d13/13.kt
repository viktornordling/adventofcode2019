package d13

import util.Reader.readInput
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalStateException

object Solution {
    fun solve() {

        val inputStream = FileInputStream(File("input.txt"))
        System.setIn(inputStream)
        val lines = readInput(emptyList())

        val cols = 150
        val rows = 150

        val matrix = CharArray(rows * cols)

        data class Pos(val x: Int, val y: Int)
        class Cart(var pos: Pos, var turns: Int, var direction: Char)
        val cartChars = setOf('>', '<', '^', 'v')
        val turnChars = setOf('\\', '/', '+')
        val carts = mutableMapOf<Pos, Cart>()

        for ((row, line) in lines.withIndex()) {
            for ((col, char) in line.withIndex()) {
                matrix[row * cols + col] = char
                if (cartChars.contains(char)) {
                    val pos = Pos(col, row)
                    val cart = Cart(pos, 0, char)
                    carts[pos] = cart
                    if (char == 'v' || char == '^') {
                        matrix[row * cols + col] = '|'
                    } else {
                        matrix[row * cols + col] = '-'
                    }
                }

            }
        }

        for (row in 0..(rows-1)) {
            for (col in 0..(cols-1)) {
                if (carts.containsKey(Pos(col, row))) {
                    print(carts[Pos(col, row)]?.direction)
                } else {
                    print(matrix[row * cols + col])
                }
            }
            println()
        }

        while (carts.size > 1) {
            val crashes = mutableSetOf<Pos>()
            val sortedByY = carts.values.groupBy { it.pos.y }.toSortedMap()
            for (row in sortedByY) {
                val sortedRow = row.value.sortedBy { it.pos.x }
                for (cart in sortedRow) {
                    if (crashes.contains(cart.pos)) {
                        println("crash on ${cart.pos}, skipping")
                        continue
                    }
                    val curDir = cart.direction
                    val curPos = cart.pos

                    val newPos = when (curDir) {
                        '>' -> Pos(curPos.x + 1, curPos.y)
                        '<' -> Pos(curPos.x-1, curPos.y)
                        '^' -> Pos(curPos.x, curPos.y - 1)
                        'v' -> Pos(curPos.x, curPos.y + 1)
                        else -> throw IllegalStateException()
                    }

                    cart.pos = newPos

                    val mapSegment = matrix[newPos.y * cols + newPos.x]
                    if (turnChars.contains(mapSegment)) {
                        when (mapSegment) {
                            '\\' -> when (cart.direction) {
                                '>' -> cart.direction = 'v'
                                '^' -> cart.direction = '<'
                                '<' -> cart.direction = '^'
                                'v' -> cart.direction = '>'
                            }
                            '/' -> when (cart.direction) {
                                '<' -> cart.direction = 'v'
                                '>' -> cart.direction = '^'
                                '^' -> cart.direction = '>'
                                'v' -> cart.direction = '<'
                            }
                            '+' -> {
                                when (cart.turns % 3){
                                    0 -> // turn left
                                        when {
                                            cart.direction == '>' -> cart.direction = '^'
                                            cart.direction == '^' -> cart.direction = '<'
                                            cart.direction == '<' -> cart.direction = 'v'
                                            else -> cart.direction = '>'
                                        }
                                    1 -> {} // do nothing, keep going straight
                                    2 -> // turn right
                                        when {
                                            cart.direction == '>' -> cart.direction = 'v'
                                            cart.direction == 'v' -> cart.direction = '<'
                                            cart.direction == '<' -> cart.direction = '^'
                                            else -> cart.direction = '>'
                                        }
                                }
                                cart.turns++
                            }
                        }
                    }
                    if (carts.containsKey(newPos)) {
                        println("Crash on pos $newPos")
                        carts.remove(newPos)
                        carts.remove(curPos)
                        crashes.add(newPos)
                    } else {
                        carts.remove(curPos)
                        carts[newPos] = cart
                    }
                }
            }
            println("carts left " + carts.size)
        }
        println(carts)
    }
}

fun main(args: Array<String>) {
    Solution.solve()
}
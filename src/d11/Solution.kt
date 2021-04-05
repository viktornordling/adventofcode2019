package d11

import util.Pos
import util.Reader
import util.Surface
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.util.*


object Solution {
    fun solve() {
        val input = Reader.readInput("input.txt")
        val values = input.flatMap { it.split(",").map { it.toLong() } }
        runProgramWithInput(values, BigInteger.valueOf(2L))
    }

    private fun runProgramWithInput(immutableValues: List<Long>, input: BigInteger): Long {
        var relativeBase = BigInteger.ZERO
        var pos = Pos(0, 0)
        val grid = mutableMapOf<Pos, Char>()
        val values: MutableMap<BigInteger, BigInteger> = immutableValues.toMutableList().mapIndexed { index: Int, it: Long -> BigInteger.valueOf(index.toLong()) to BigInteger.valueOf(it) }.toMap().toMutableMap()

        var first: Int = -1
        var second: Int = -1
        var outputs = 0
        var direction = '^'

        var currentPos = BigInteger.ZERO
        grid[pos] = '1'
        while (true) {
            var jump = false
            var jumps = 4
            val op: BigInteger = values[currentPos]!!
            val opCode = getOpCode(op)
            val mode1 = getMode(op, 1)
            val mode2 = getMode(op, 2)
            val mode3 = getMode(op, 3)
            if (opCode == 1L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                var dest = values[currentPos.add(BigInteger.valueOf(3L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                if (mode3 == 2) {
                    dest = dest + relativeBase
                }
                values[dest] = a.add(b)
            } else if (opCode == 2L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                var dest = values[currentPos.add(BigInteger.valueOf(3L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                if (mode3 == 2) {
                    dest = dest + relativeBase
                }
                values[dest] = a.multiply(b)
            } else if (opCode == 3L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val gridInput: Char = grid[pos] ?: '0'
                val bInput = gridInput.toInt().toBigInteger()
                if (mode1 == 2) {
                    values[relativeBase + addr1] = bInput
                } else if (mode1 == 0) {
                    values[addr1] = bInput
                } else if (mode1 == 1) {
                    println("unexpected")
                }
                jumps = 2
            } else if (opCode == 4L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val output = getValue(addr1, values, mode1, relativeBase)
                if (outputs == 0) {
                    first = output.toInt()
                } else {
                    second = output.toInt()
                }
                outputs++
                if (outputs == 2) {
                    outputs = 0
//                    println("Painting $pos $first")
                    if (first == 0) {
                        grid[pos] = '.'
                    } else {
                        grid[pos] = '#'
                    }
                    val (newPos, newDirection) = getNewPos(direction, second, pos)
                    pos = newPos
                    direction = newDirection
                }
                jumps = 2
            } else if (opCode == 5L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                if (a != BigInteger.valueOf(0L)) {
                    currentPos = b
                    jump = true
                }
                jumps = 3
            } else if (opCode == 6L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                if (a == BigInteger.valueOf(0L)) {
                    currentPos = b
                    jump = true
                }
                jumps = 3
            } else if (opCode == 7L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                val addr3 = values[currentPos.add(BigInteger.valueOf(3L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                var dest = addr3
//                var dest = addr3
                if (mode3 == 2) {
                    dest = dest + relativeBase
                }
                if (a < b) {
                    values[dest] = BigInteger.ONE
                } else {
                    values[dest] = BigInteger.ZERO
                }
            } else if (opCode == 8L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                val addr3 = values[currentPos.add(BigInteger.valueOf(3L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                var dest = addr3
                if (mode3 == 2) {
                    dest = dest + relativeBase
                }
//                var dest = getValue(addr3, values, mode3, relativeBase)
//                if (mode3 == 2) {
//                    dest = dest + relativeBase
//                }
                if (a == b) {
                    values[dest] = BigInteger.ONE
                } else {
                    values[dest] = BigInteger.ZERO
                }
            } else if (opCode == 9L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                relativeBase = relativeBase.add(a)
                jumps = 2
            } else if (opCode == 99L!!) {
                println("Total number of squares painted: ${grid.keys.size}")
                Surface.printMap(grid)
                return 0L
            } else {
                println("Unknown op: $op")
                return -1
            }
            if (!jump) {
                currentPos = currentPos.plus(BigInteger.valueOf(jumps.toLong()))
            }
        }
    }

    private fun getNewPos(direction: Char, turn: Int, curPos: Pos): Pair<Pos, Char> {
        var newDirection = '.'
        if (turn == 0) {
            newDirection = when {
                direction == '^' -> '<'
                direction == '<' -> 'v'
                direction == 'v' -> '>'
                direction == '>' -> '^'
                else -> throw IllegalArgumentException("Gaa")
            }
        } else if (turn == 1) {
            newDirection = when {
                direction == '^' -> '>'
                direction == '>' -> 'v'
                direction == 'v' -> '<'
                direction == '<' -> '^'
                else -> throw IllegalArgumentException("Gaa")
            }
        } else {
            throw IllegalArgumentException("Gaa")
        }
        val newPos = when {
            newDirection == '^' -> curPos.copy(y = curPos.y - 1)
            newDirection == '>' -> curPos.copy(x = curPos.x + 1)
            newDirection == 'v' -> curPos.copy(y = curPos.y + 1)
            newDirection == '<' -> curPos.copy(x = curPos.x - 1)
            else -> throw IllegalArgumentException("Gaa")
        }
        return Pair(newPos, newDirection)
    }

    fun getMode(op: BigInteger, num: Int): Int {
        val chars = op.toString().toCharArray().reversed().drop(2)
        if (num - 1 >= chars.size) {
            return 0
        } else {
            return chars[num-1].toString().toInt()
        }
    }

    private fun getValue(param: BigInteger, values: MutableMap<BigInteger, BigInteger>, mode: Int, relativeBase: BigInteger): BigInteger {
        if (mode == 0) {
            if (values[param] == null) {
                return BigInteger.ZERO
            }
            return values[param]!!
        } else if (mode == 1) {
            return param
        } else {
            if (values[relativeBase + param] == null) {
                return BigInteger.ZERO
            }
            return values[relativeBase + param]!!
        }
    }

    fun getOpCode(pos: BigInteger): Long {
        val chars = pos.toString().toCharArray().reversed().take(2).reversed().toCharArray()
        return String(chars).toLong()
    }
}

fun main() {
    val start = System.currentTimeMillis()

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
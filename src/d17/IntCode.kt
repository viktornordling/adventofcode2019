package d17


import util.Pos
import util.Reader
import util.Surface
import java.math.BigInteger
import java.util.*

data class Move(val direction: Char, val count: Int) {
    override fun toString(): String {
        return "$direction,$count"
    }
}

object IntCode {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        val values = input.flatMap { it.split(",").map { it.toLong() } }
        val solutionA = runProgramWithInput(values, BigInteger.valueOf(2L))
        println("Solution for A: $solutionA")
    }

    private fun runProgramWithInput(immutableValues: List<Long>, input: BigInteger): Long {
        var relativeBase = BigInteger.ZERO
        val values: MutableMap<BigInteger, BigInteger> = immutableValues.toMutableList().mapIndexed { index: Int, it: Long -> BigInteger.valueOf(index.toLong()) to BigInteger.valueOf(it) }.toMap().toMutableMap()
        values[0.toBigInteger()] = 2.toBigInteger()
        val map = mutableMapOf<Pos, Char>()
        var x = 0
        var y = 0
        val outputs = mutableListOf<Int>()
        var currentPos = BigInteger.ZERO

        val input1 = "A,C,A,C,B,C,B,A,B,B".toCharArray().map { it.toInt() } + 10
        val input2 = "R,12,L,10,L,10".toCharArray().map { it.toInt() } + 10
        val input3 = "L,12,R,12,L,6".toCharArray().map { it.toInt() } + 10
        val input4 = "L,6,L,12,R,12,L,4".toCharArray().map { it.toInt() } + 10
        val input5 = "WALK".toCharArray().map { it.toInt() } + 10
        val inputs = input1 + input2 + input3 + input4 + input5
        val inputQueue:Queue<Int> = LinkedList<Int>()
        inputQueue.addAll(inputs)

        while (true) {
            var jump = false
            var jumps = 4
            val op: BigInteger = values[currentPos]!!
            val opCode = getOpCode(op)
//            println("$opCode")
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
                println("reading input")
                var inp = inputQueue.poll()
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val a = getValue(addr1, values, mode1, relativeBase)
                println("providing input $inp")
                if (mode1 == 2) {
                    values[relativeBase + addr1] = inp.toBigInteger()
                } else if (mode1 == 0) {
                    values[addr1] = inp.toBigInteger()
                } else if (mode1 == 1) {
                    println("unexpected")
                }
                jumps = 2
            } else if (opCode == 4L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val output = getValue(addr1, values, mode1, relativeBase)
//                outputs.add(output.toInt())
                if (output > 200.toBigInteger()) {
                    println("Final output: $output")
                } else if (output == 10.toBigInteger()) {
                    println()
                    x = 0
                    y++
                } else {
                    map[Pos(x, y)] = output.toInt().toChar()
                    print(output.toInt().toChar())
                    x++
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
                findAlignmentParameter(map)
                val movements = getMovements(map)
                println(movements.joinToString(separator = ","))
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

    fun findAlignmentParameter(map: MutableMap<Pos, Char>) {
        val intersections = map.entries.filter { it.value == '#' && it.key.neighbours().all { n -> map[n]?.equals('#') ?: false } }
        println(intersections.map { it.key.x * it.key.y }.sum())
    }

    fun getMovements(map: MutableMap<Pos, Char>): List<Move> {
        val startPos = findStartPos(map)
        var direction = 'R'
        var curPos = startPos
        val moves = mutableListOf<Move>()
        var steps = 0
        var turnName = 'R'
        while (direction != '?') {
            map[curPos] = direction
            val nextPos = map[curPos.next(direction)] ?: '.'
            if (nextPos == '.') {
                // Turn!
                moves.add(Move(turnName, steps))
                steps = 0
                val nextPos: Pos? = curPos.neighbours().filter { map[it] == '#' }.firstOrNull()
                if (nextPos == null) {
                    println("Done!")
                    Surface.printMap(map)
                    return moves
                }
                val oldDirection = direction
                direction = getNextDir(curPos, nextPos)
                turnName = getTurnName(oldDirection, direction)
            }
            curPos = curPos.next(direction)
            steps++
        }
        return moves
    }

    fun getTurnName(oldDirection: Char, newDirection: Char): Char {
        return when {
            oldDirection == 'U' && newDirection == 'R' -> 'R'
            oldDirection == 'U' && newDirection == 'L' -> 'L'

            oldDirection == 'D' && newDirection == 'R' -> 'L'
            oldDirection == 'D' && newDirection == 'L' -> 'R'

            oldDirection == 'L' && newDirection == 'U' -> 'R'
            oldDirection == 'L' && newDirection == 'D' -> 'L'

            oldDirection == 'R' && newDirection == 'U' -> 'L'
            oldDirection == 'R' && newDirection == 'D' -> 'R'

            else -> throw IllegalArgumentException("Can't turn from $oldDirection to $newDirection")
        }
    }

    fun getNextDir(curPos: Pos, nextPos: Pos): Char {
        return when {
            nextPos.x > curPos.x -> 'R'
            nextPos.x < curPos.x -> 'L'
            nextPos.y < curPos.y -> 'U'
            nextPos.y > curPos.y -> 'D'
            else -> throw IllegalArgumentException("No will get you to $nextPos from $curPos")
        }
    }

    fun findStartPos(map: MutableMap<Pos, Char>) = map.filter { it.value == '^' }.keys.first()!!

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

    IntCode.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
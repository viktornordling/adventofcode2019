package d19


import util.Pos
import util.Reader
import java.math.BigInteger

object IntCode {

    fun solve() {
        val input = Reader.readInput("easy.txt")
        val values = input.flatMap { it.split(",").map { it.toLong() } }
//        val solutionA = runProgramWithInput(values, BigInteger.valueOf(2L))
//        println("Solution for A: $solutionA")

//        val map = mutableMapOf<Pos, Char>()
//        val inputs = mutableListOf<Int>()
//        for (x in 0..99) {
//            for (y in 0..99) {
//                inputs.add(x)
//                inputs.add(y)
//                runProgramWithInput(values, listOf<Int>(x, y))
//            }
//        }

//        println(map.values.filter { it == '1' }.size)
//        Surface.printMap(map)
        val border = findTopBorder(values)
        val squareSide = 50
        for (p in border) {
            val canFit = canFitSanta(p, values, squareSide)
            if (canFit) {
                println("Found first (top right) pos: $p")
                val answer = (p.x - (squareSide - 1)) * 10000 + p.y
                println("Answer = $answer")
                return
            }
        }
//        findHeightAt(Pos(x=828, y=502), values)
    }

    fun canFitSanta(topRight: Pos, values: List<Long>, squareSide: Int):Boolean {
        val topLeft = topRight.copy(x = topRight.x - (squareSide - 1))
        val bottomLeft = topLeft.copy(y = topLeft.y + (squareSide - 1))
//        println("top right: " + getStateAt(topRight, values))
//        println("top left: " + getStateAt(topLeft, values))
//        println("bottom left: " + getStateAt(bottomLeft, values))
        return getStateAt(topLeft, values) == 1 && getStateAt(bottomLeft, values) == 1
    }

    fun findWidthAt(pos: Pos, values: List<Long>) {
        var count = 0
        var cur = 1
        var curPos = pos
        while (cur == 1) {
            cur = getStateAt(curPos, values)
            count++
            curPos = curPos.copy(x = curPos.x - 1)
        }
        println("Width at $pos = ${count-1}")
    }

    fun findHeightAt(pos: Pos, values: List<Long>) {
        var count = 0
        var cur = 1
        var curPos = pos
        while (cur == 1) {
            cur = getStateAt(curPos, values)
            count++
            curPos = curPos.copy(y = curPos.y + 1)
        }
        println("Height at $pos = ${count-1}")
    }

    private fun findTopBorder(values: List<Long>): MutableList<Pos> {
        val topBorder = mutableListOf<Pos>()
        var curPos = Pos(5, 3)
        while(curPos.x < 2000) {
            curPos = curPos.copy(x = curPos.x + 1)
            val right = getStateAt(curPos, values)
            if (right == 1) {
                topBorder.add(curPos)
            } else {
                curPos = curPos.copy(x = curPos.x - 1, y = curPos.y + 1)
            }
        }
        return topBorder
    }

    fun getStateAt(pos: Pos, values: List<Long>):Int {
        return runProgramWithInput(values, listOf(pos.x, pos.y)).toInt()
    }

    private fun runProgramWithInput(immutableValues: List<Long>, inputs: List<Int>): Long {
        var relativeBase = BigInteger.ZERO
        val values: MutableMap<BigInteger, BigInteger> = immutableValues.toMutableList().mapIndexed { index: Int, it: Long -> BigInteger.valueOf(index.toLong()) to BigInteger.valueOf(it) }.toMap().toMutableMap()
//        values[0.toBigInteger()] = 2.toBigInteger()
        val outputs = mutableListOf<Int>()
        var currentPos = BigInteger.ZERO

        var curInput = 0
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
//                println("reading input")
                var inp = inputs[curInput++]
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val a = getValue(addr1, values, mode1, relativeBase)
//                println("providing input $inp")
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
                return output.toLong()
//                outputs.add(output.toInt())
//                val pos = Pos(inputs[curInput - 2], inputs[curInput - 1])
//                println(output)
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

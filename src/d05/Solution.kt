package d05

import util.Reader

object Solution {
    fun solve() {
        val input = Reader.readInput("input.txt")
        val values = input.flatMap { it.split(",").map { it.toInt() } }
        val solutionA = runProgramWithInput(values, 5)
        println("Solution for A: $solutionA")
    }

    private fun runProgramWithInput(immutableValues: List<Int>, input: Int): Int {
        val values = immutableValues.toMutableList()
        var currentPos = 0
        while (currentPos < values.size) {
            var jump = false
            var jumps = 4
            val op: Int = values[currentPos]
            val opCode = getOpCode(op)
            val mode1 = getMode(op, 1)
            val mode2 = getMode(op, 2)
            val mode3 = getMode(op, 3)
            if (opCode == 1) {
                val addr1 = values[currentPos + 1]
                val addr2 = values[currentPos + 2]
                val dest = values[currentPos + 3]
                val a = getValue(addr1, values, mode1)
                val b = getValue(addr2, values, mode2)
                values[dest] = a + b
            } else if (opCode == 2) {
                val addr1 = values[currentPos + 1]
                val addr2 = values[currentPos + 2]
                val dest = values[currentPos + 3]
                val a = getValue(addr1, values, mode1)
                val b = getValue(addr2, values, mode2)
                values[dest] = a * b
            } else if (opCode == 3) {
                val dest = values[currentPos + 1]
                values[dest] = input
                jumps = 2
            } else if (opCode == 4) {
                val addr1 = values[currentPos + 1]
                println(getValue(addr1, values, mode1))
                jumps = 2
            } else if (opCode == 5) {
                val addr1 = values[currentPos + 1]
                val addr2 = values[currentPos + 2]
                val a = getValue(addr1, values, mode1)
                val b = getValue(addr2, values, mode2)
                if (a != 0) {
                    currentPos = b
                    jump = true
                }
                jumps = 3
            } else if (opCode == 6) {
                val addr1 = values[currentPos + 1]
                val addr2 = values[currentPos + 2]
                val a = getValue(addr1, values, mode1)
                val b = getValue(addr2, values, mode2)
                if (a == 0) {
                    currentPos = b
                    jump = true
                }
                jumps = 3
            } else if (opCode == 7) {
                val addr1 = values[currentPos + 1]
                val addr2 = values[currentPos + 2]
                val addr3 = values[currentPos + 3]
                val a = getValue(addr1, values, mode1)
                val b = getValue(addr2, values, mode2)
//                val dest = values[currentPos + 3]
                val dest = addr3
                if (a < b) {
                    values[dest] = 1
                } else {
                    values[dest] = 0
                }
            } else if (opCode == 8) {
                val addr1 = values[currentPos + 1]
                val addr2 = values[currentPos + 2]
                val addr3 = values[currentPos + 3]
                val a = getValue(addr1, values, mode1)
                val b = getValue(addr2, values, mode2)
//                val dest = values[currentPos + 3]
                val dest = addr3
                if (a == b) {
                    values[dest] = 1
                } else {
                    values[dest] = 0
                }
            } else if (opCode == 99) {
                return values[0]
            } else {
                println("Unknown op: $op")
                return -1
            }
            if (!jump) {
                currentPos += jumps
            }
        }
        return -1
    }

    fun getMode(op: Int, num: Int): Int {
        val chars = op.toString().toCharArray().reversed().drop(2)
        if (num - 1 >= chars.size) {
            return 0
        } else {
            return chars[num-1].toString().toInt()
        }
    }

    private fun getValue(param: Int, values: List<Int>, mode: Int): Int {
        if (mode == 0) {
            return values[param]
        } else {
            return param
        }
    }

    fun getOpCode(pos: Int): Int {
        val chars = pos.toString().toCharArray().reversed().take(2).reversed().toCharArray()
        return String(chars).toInt()
    }
}

fun main() {
    val start = System.currentTimeMillis()
//    println(Solution.getOpCode(199))
//    println(Solution.getOpCode(101))
//    println(Solution.getOpCode(102))
//    println(Solution.getMode(1002, 1)) // => 0
//    println(Solution.getMode(1002, 2)) // => 1
//    println(Solution.getMode(1002, 3)) // => 0
//
//    println(Solution.getMode(11002, 1)) // => 0
//    println(Solution.getMode(11002, 2)) // => 1
//    println(Solution.getMode(11002, 3)) // => 1

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
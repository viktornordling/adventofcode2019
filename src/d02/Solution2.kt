package d02

import util.Reader

object Solution2 {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        val values = input.flatMap { it.split(",").map { it.toInt() } }
        val solutionA = runProgramWithNounAndVerb(values, 12, 2)
        println("Solution for A: $solutionA")
        val solutionB = findInputWhichGivesResult(19690720, values)
        println("Solution for B: $solutionB")
    }

    private fun findInputWhichGivesResult(result: Int, values: List<Int>): Int {
        for (x in 1..100) {
            for (y in 1..100) {
                if (runProgramWithNounAndVerb(values, x, y) == result) {
                    return x * 100 + y
                }
            }
        }
        return -1
    }

    private fun runProgramWithNounAndVerb(values: List<Int>, noun: Int, verb: Int): Int {
        val mutableValues = values.toMutableList()
        mutableValues[1] = noun
        mutableValues[2] = verb
        return runProgram(mutableValues)
    }

    private fun runProgram(immutableValues: List<Int>): Int {
        val values = immutableValues.toMutableList()
        var currentPos = 0
        while (currentPos < values.size) {
            val op = values[currentPos]
            if (op == 1) {
                val addr1 = values[currentPos + 1]
                val addr2 = values[currentPos + 2]
                val dest = values[currentPos + 3]
                val a = values[addr1]
                val b = values[addr2]
                values[dest] = a + b
            } else if (op == 2) {
                val addr1 = values[currentPos + 1]
                val addr2 = values[currentPos + 2]
                val dest = values[currentPos + 3]
                val a = values[addr1]
                val b = values[addr2]
                values[dest] = a * b
            } else if (op == 99) {
                return values[0]
            } else {
                println("Unknown op: $op")
                return -1
            }
            currentPos += 4
        }
        return -1
    }
}

fun main() {
    val start = System.currentTimeMillis()
    Solution2.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
package d07

import util.Reader
import java.util.*

object Solution {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        val values = input.flatMap { it.split(",").map { it.toInt() } }
        val allPermutations = getPermutations()
        val v = allPermutations.map {
            runAllStages(it, values)
        }.max()
        println("max = $v")
    }

    private fun runAllStages(phaseSettings: List<Int>, program: List<Int>): Int {
        var input = 0
        var phaseSetting = phaseSettings[0]

        // run a
        val inputs = LinkedList<Int>()
        inputs.add(phaseSetting)
        inputs.add(input)

        input = runProgramWithInput(program, inputs)

        // run b
        inputs.clear()
        phaseSetting = phaseSettings[1]
        inputs.add(phaseSetting)
        inputs.add(input)
        input = runProgramWithInput(program, inputs)

        // run c
        inputs.clear()
        phaseSetting = phaseSettings[2]
        inputs.add(phaseSetting)
        inputs.add(input)
        input = runProgramWithInput(program, inputs)

        // run d
        inputs.clear()
        phaseSetting = phaseSettings[3]
        inputs.add(phaseSetting)
        inputs.add(input)
        input = runProgramWithInput(program, inputs)

        // run e
        inputs.clear()
        phaseSetting = phaseSettings[4]
        inputs.add(phaseSetting)
        inputs.add(input)
        input = runProgramWithInput(program, inputs)

        return input
    }

    private fun runProgramWithInput(immutableValues: List<Int>, inputs: Queue<Int>): Int {
//        println("running program with inputs: $inputs")
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
                values[dest] = inputs.poll()
                jumps = 2
            } else if (opCode == 4) {
                val addr1 = values[currentPos + 1]
                val output = getValue(addr1, values, mode1)
//                println(output)
                return output
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

    fun getPermutations(): List<List<Int>> {
        val perms = mutableListOf<List<Int>>()
        for (a in 0..4) {
            for (b in 0..4) {
                for (c in 0..4) {
                    for (d in 0..4) {
                        for (e in 0..4) {
                            if (setOf(a,b, c, d, e).size == 5) {
                                perms.add(listOf(a, b, c, d, e))
                            }
                        }
                    }
                }
            }
        }
        return perms
    }
}


fun main() {
    val start = System.currentTimeMillis()

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
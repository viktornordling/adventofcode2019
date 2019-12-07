package d07

import util.Reader
import java.util.*

object SolutionB {
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
        println("phaseSettings: $phaseSettings")
        var input: Triple<Int, Int, Boolean> = Triple(0, 0, false)
        var phaseSetting = phaseSettings[0]

        var aPos = 0
        var bPos = 0
        var cPos = 0
        var dPos = 0
        var ePos = 0
        var firstRun = true
        var eOut = 0

        val aProgram = program.toMutableList()
        val bProgram = program.toMutableList()
        val cProgram = program.toMutableList()
        val dProgram = program.toMutableList()
        val eProgram = program.toMutableList()

        // run a
        while (!input.third) {
            val inputs = LinkedList<Int>()
            if (firstRun) {
                inputs.add(phaseSetting)
            }
            inputs.add(input.first)
            input = runProgramWithInput(aProgram, inputs, aPos)
            aPos = input.second

            // run b
            phaseSetting = phaseSettings[1]
            if (firstRun) {
                inputs.add(phaseSetting)
            }
            inputs.add(input.first)
            input = runProgramWithInput(bProgram, inputs, bPos)
            bPos = input.second

            // run c
            phaseSetting = phaseSettings[2]
            if (firstRun) {
                inputs.add(phaseSetting)
            }
            inputs.add(input.first)
            input = runProgramWithInput(cProgram, inputs, cPos)
            cPos = input.second

            // run d
            phaseSetting = phaseSettings[3]
            if (firstRun) {
                inputs.add(phaseSetting)
            }
            inputs.add(input.first)
            input = runProgramWithInput(dProgram, inputs, dPos)
            dPos = input.second

            // run e
            phaseSetting = phaseSettings[4]
            if (firstRun) {
                inputs.add(phaseSetting)
            }
            inputs.add(input.first)
            input = runProgramWithInput(eProgram, inputs, ePos)
            ePos = input.second
            firstRun = false
            if (!input.third) {
                eOut = input.first
            }
            println("eOut = $eOut")
        }
        return eOut
    }

    private fun runProgramWithInput(values: MutableList<Int>, inputs: Queue<Int>, startPos:Int = 0): Triple<Int,Int,Boolean> {
//        println("running program with inputs: $inputs")
//        val values = immutableValues.toMutableList()
        var currentPos = startPos
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
                currentPos+=2
                return Triple(output, currentPos, false)
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
                return Triple(0, 0, true)
            } else {
                println("Unknown op: $op")
                return Triple(0, 0, true)
            }
            if (!jump) {
                currentPos += jumps
            }
        }
        return Triple(0, 0, true)
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
        for (a in 5..9) {
            for (b in 5..9) {
                for (c in 5..9) {
                    for (d in 5..9) {
                        for (e in 5..9) {
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

    SolutionB.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
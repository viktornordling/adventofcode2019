package d21


import util.Reader
import java.math.BigInteger
import java.util.*

object IntCode {

    fun solve() {
        val input = Reader.readInput("input.txt")
        val values = input.flatMap { it.split(",").map { it.toLong() } }

//        val allPrograms = generateAllPrograms(16, 4)
//        for (p in allPrograms) {
//            println("Running program $p")
//            val inputs = p.flatMap { it.map { it.toInt() } + 10 }
//            val program = inputs + "WALK".map { it.toInt() } + 10
//
//            val inputQueue: Queue<Int> = LinkedList<Int>()
//            inputQueue.addAll(program)
//
//            runProgramWithInput(values, inputQueue)
//        }
//

        val input1 = "NOT A T".map { it.toInt() } + 10
        val input2 = "NOT B J".map { it.toInt() } + 10
        val input3 = "OR T J".map { it.toInt() } + 10
        val input4 = "NOT C T".map { it.toInt() } + 10
        val input5 = "OR T J".map { it.toInt() } + 10
        val input6 = "AND D J".map { it.toInt() } + 10
        val input7 = "NOT E T".map { it.toInt() } + 10
        val input8 = "NOT T T".map { it.toInt() } + 10
        val input9 = "OR H T".map { it.toInt() } + 10
        val input10 = "AND T J".map { it.toInt() } + 10


        // THIS WORKED FOR PART 1!
//        val input1 = "OR A T".map { it.toInt() } + 10
//        val input2 = "AND B T".map { it.toInt() } + 10
//        val input3 = "NOT B J".map { it.toInt() } + 10
//        val input4 = "OR J T".map { it.toInt() } + 10
//        val input5 = "NOT C J".map { it.toInt() } + 10
//        val input6 = "AND T J".map { it.toInt() } + 10
//        val input7 = "AND D J".map { it.toInt() } + 10
//        val input8 = "NOT A T".map { it.toInt() } + 10
//        val input9 = "OR T J".map { it.toInt() } + 10

//        val input10 = "NOT E T".map { it.toInt() } + 10
//        val input11 = "AND F T".map { it.toInt() } + 10
//        val input12 = "OR T J".map { it.toInt() } + 10
//
        val input16 = "RUN".map { it.toInt() } + 10
        val inputs = input1 + input2 + input3 + input4 + input5 + input6 + input7 + input8 + input9 + input10 + input16
//        val inputs = input1 + input2 + input3 + input4 + input5 + input6 + input16
        val inputQueue: Queue<Int> = LinkedList<Int>()
        inputQueue.addAll(inputs)
//
        runProgramWithInput(values, inputQueue)
    }

    private fun generateAllPrograms(max: Int, listLength: Int): MutableList<MutableList<String>> {
        val allPrograms = mutableListOf<MutableList<String>>()
        for (i in 0 until max) {
            val booleans: List<Boolean> = i.toString(2).map { it == '1' }
            val fullList = mutableListOf<Boolean>()
            for (j in 0 until (listLength - booleans.size)) {
                fullList.add(false)
            }
            fullList.addAll(booleans)
            val p = createProgram(fullList)
            println(p)
            allPrograms.add(p)
        }
        return allPrograms
    }

    private fun createProgram(listOf: List<Boolean>): MutableList<String> {
        var first = true
        val instructions = mutableListOf<String>()
        var currentChar = 'A'
        for (b in listOf) {
            if (first) {
                if (b) {
                    instructions.add("OR A J")
                } else {
                    instructions.add("NOT A J")
                }
                first = false
            } else {
                if (b) {
                    instructions.add("AND $currentChar J")
                } else {
                    instructions.add("OR $currentChar T")
                    instructions.add("AND T J")
                }
            }
            currentChar++
        }
        return instructions
    }

    private fun runProgramWithInput(immutableValues: List<Long>, inputQueue: Queue<Int>): Long {
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
                var inp = inputQueue.poll()
//                var inp = inputs[curInput++]
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
                if (output > 200.toBigInteger()) {
                    println("Final output: $output")
                } else if (output == 10.toBigInteger()) {
                    println()
                } else {
                    print(output.toInt().toChar())
                }
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

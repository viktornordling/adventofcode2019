package util

import java.lang.IllegalArgumentException
import java.util.*

class Memory(private val memory: MutableMap<Long, Long>) {
    fun getValue(pos: Long) = memory[pos] ?: 0
    fun setValue(pos: Long, value: Long) {
        memory[pos] = value
    }
}

class IntCodeVm(private val memory: Memory = Memory(emptyMap<Long, Long>().toMutableMap()),
                private val inputs: Queue<Long>,
                private var offset: Long = 0L) {
    private var jumpPos: Long = -1L
    private var currentPos: Long = 0L
    private val operations = mapOf(
        1 to Add(),
        2 to Mul(),
        3 to Read(),
        4 to Write(),
        5 to JumpIfTrue(),
        6 to JumpIfFalse(),
        7 to LessThan(),
        8 to Equals()
    )

    fun getInput(): Long {
        return inputs.poll()
    }

    fun setJumpPos(jumpPos: Long) {
        this.jumpPos = jumpPos
    }

    fun runProgram() {
        val instruction = memory.getValue(currentPos)
        val opCode = getOpCode()
        if (opCode == 99) {
            return
        }
        val operation = operations[opCode]!!
        val modes = getModes(instruction, operation.numArgs)
        val args = getArgs(operation.numArgs, modes)
        operation.apply(args, this)
        if (jumpPos >= 0) {
            currentPos = jumpPos
            jumpPos = -1
        } else {
            currentPos += operation.numArgs + 1
        }
    }

    private fun getModes(instruction: Long, numArgs: Int): List<Int> = (0..numArgs).map { getMode(instruction, it) }

    fun getMode(instruction: Long, index: Int): Int {
        val chars = instruction.toString().toCharArray().reversed().drop(2)
        return when {
            index > chars.size -> 0
            else -> chars[index - 1].toString().toInt()
        }
    }

    private fun getArgs(numArgs: Int, modes:List<Int>): List<Long> = (0..numArgs).zip(modes).map { getArg(it.first, it.second) }

    private fun getArg(argNum: Int, mode: Int): Long {
        val rawArg = memory.getValue(currentPos + argNum)
        return when (mode) {
            0 -> memory.getValue(rawArg)
            1 -> rawArg
            2 -> memory.getValue(offset + rawArg)
            else -> throw IllegalArgumentException("Illegal mode: $mode")
        }
    }

    fun getOpCode(): Int {
//        val chars = pos.toString().toCharArray().reversed().take(2).reversed().toCharArray()
//        return String(chars).toLong()
        return 1
    }

    abstract class Operation(val opCode: Int, val numArgs: Int) {
        abstract fun apply(args: List<Long> , vm: IntCodeVm)
    }

    class Add: Operation(opCode = 1, numArgs = 3) {
        override fun apply(args: List<Long>, vm: IntCodeVm) = vm.memory.setValue(args[2], args[0] + args[1])
    }

    class Mul: Operation(opCode = 2, numArgs = 3) {
        override fun apply(args: List<Long>, vm: IntCodeVm) = vm.memory.setValue(args[2], args[0] * args[1])
    }

    class Read: Operation(opCode = 3, numArgs = 1) {
        override fun apply(args: List<Long>, vm: IntCodeVm) = vm.memory.setValue(args[0], vm.getInput())
    }

    class Write: Operation(opCode = 4, numArgs = 1) {
        override fun apply(args: List<Long>, vm: IntCodeVm) = println(vm.memory.getValue(args[0]))
    }

    class JumpIfTrue: Operation(opCode = 5, numArgs = 2) {
        override fun apply(args: List<Long>, vm: IntCodeVm) {
            if (args[0] != 0L) {
                vm.setJumpPos(args[1])
            }
        }
    }

    class JumpIfFalse: Operation(opCode = 6, numArgs = 2) {
        override fun apply(args: List<Long>, vm: IntCodeVm) {
            if (args[0] == 0L) {
                vm.setJumpPos(args[1])
            }
        }
    }

    class LessThan: Operation(opCode = 7, numArgs = 2) {
        override fun apply(args: List<Long>, vm: IntCodeVm) {
            vm.memory.setValue(args[2], if (args[0] < args[1]) 1L else 0)
        }
    }

    class Equals: Operation(opCode = 8, numArgs = 2) {
        override fun apply(args: List<Long>, vm: IntCodeVm) {
            vm.memory.setValue(args[2], if (args[0] == args[1]) 1L else 0)
        }
    }

    companion object {
        fun fromList(list: List<Int>): IntCodeVm {
            val memory = list.map { it.toLong() }.mapIndexed { index, value -> index.toLong() to value }.toMap()
            return IntCodeVm(Memory(memory.toMutableMap()), LinkedList())
        }
    }
}

fun main() {
    val program = listOf(1,9,10,3,2,3,11,0,99,30,40,50)
    val vm = IntCodeVm.fromList(program)
    vm.runProgram()
}
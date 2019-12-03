package d21

import util.Reader.readInput

import java.io.File
import java.io.FileInputStream
import java.lang.IllegalArgumentException

object Solution {

    data class Opcode(val id: Int, val code: String, val a: Int, val b: Int, val c: Int)

    private fun createOpcode(id: Int, list:List<String>):Opcode {
        return Opcode(id, list[0], list[1].toInt(), list[2].toInt(), list[3].toInt())
    }

    fun solve() {
        val ipReg = 1
        val inputStream = FileInputStream(File("src/twentyone/input.txt"))

        System.setIn(inputStream)

        val lines = readInput(emptyList())

        val program = lines.mapIndexed { index, it -> createOpcode(index, it.split(" ").map { it }) }

        data class Register(val zero: Int, val one: Int, val two: Int, val three: Int, val four: Int, val five: Int) {
            fun getIndex(index:Int):Int {
                return when (index) {
                    0 -> zero
                    1 -> one
                    2 -> two
                    3 -> three
                    4 -> four
                    5 -> five
                    else -> throw IllegalArgumentException("bad index $index")
                }
            }

            fun store(index: Int, value:Int):Register {
                return when (index) {
                    0 -> this.copy(zero = value)
                    1 -> this.copy(one = value)
                    2 -> this.copy(two = value)
                    3 -> this.copy(three = value)
                    4 -> this.copy(four = value)
                    5 -> this.copy(five = value)
                    else -> throw IllegalArgumentException("bad index $index")
                }
            }
        }

        abstract class Instruction(val id:String) {
            abstract fun apply(reg: Register, opcode: Opcode): Register
        }

        class AddR: Instruction("addr") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = reg.getIndex(opcode.b)
                return reg.store(opcode.c, left+right)
            }
        }

        class AddI: Instruction("addi") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = opcode.b
                return reg.store(opcode.c, left+right)
            }
        }

        class MulR: Instruction("mulr") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = reg.getIndex(opcode.b)
                return reg.store(opcode.c, left*right)
            }
        }

        class MulI: Instruction("muli") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = opcode.b
                return reg.store(opcode.c, left*right)
            }
        }

        class BanR: Instruction("banr") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = reg.getIndex(opcode.b)
                return reg.store(opcode.c, left.and(right))
            }
        }

        class BanI: Instruction("bani") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = opcode.b
                return reg.store(opcode.c, left.and(right))
            }
        }

        class BorR: Instruction("borr") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = reg.getIndex(opcode.b)
                return reg.store(opcode.c, left.or(right))
            }
        }

        class BorI: Instruction("bori") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = opcode.b
                return reg.store(opcode.c, left.or(right))
            }
        }

        class SetR: Instruction("setr") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                return reg.store(opcode.c, left)
            }
        }

        class SetI: Instruction("seti") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = opcode.a
                return reg.store(opcode.c, left)
            }
        }

        class GtiR: Instruction("gtir") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = opcode.a
                val right = reg.getIndex(opcode.b)
                return reg.store(opcode.c, if (left > right) 1 else 0)
            }
        }

        class GtrI: Instruction("gri") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = opcode.b
                return reg.store(opcode.c, if (left > right) 1 else 0)
            }
        }

        class GtrR: Instruction("gtrr") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = reg.getIndex(opcode.b)
                return reg.store(opcode.c, if (left > right) 1 else 0)
            }
        }

        class EqiR: Instruction("eqir") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = opcode.a
                val right = reg.getIndex(opcode.b)
                return reg.store(opcode.c, if (left == right) 1 else 0)
            }
        }

        class EqrI: Instruction("eqri") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = opcode.b
                return reg.store(opcode.c, if (left == right) 1 else 0)
            }
        }

        class EqrR: Instruction("eqrr") {
            override fun apply(reg: Register, opcode: Opcode): Register {
                val left = reg.getIndex(opcode.a)
                val right = reg.getIndex(opcode.b)
                return reg.store(opcode.c, if (left == right) 1 else 0)
            }
        }

        val instructions = listOf(AddI(), AddR(), MulI(), MulR(), BanR(), BanI(), BorR(), BorI(),
            SetR(), SetI(), GtiR(), GtrI(), GtrR(), EqiR(), EqrI(), EqrR())

        var reg = Register(0, 0, 0, 0, 0, 0)
        val insts: Map<String, Instruction> = instructions.associateBy { it.id }

        val ops = program.associateBy { it.id }

        var op: Opcode? = ops[0]
        var oldReg = reg
        var counter = 0
        val seen = mutableSetOf<Int>()
        var last = 0

        while (op != null) {
            if (op.id == 29) {
//                println(BigInteger.valueOf(reg.getIndex(5).toLong()).toString(16).padStart(8, '0') + " " + reg)
//                val s = "ip=${reg.getIndex(ipReg)} $reg $op"
                val five = reg.getIndex(5)
                if (seen.contains(five)) {
                    println("Seen!! new = $five last = $last")
                } else {
                    last = five
                    seen.add(five)
                }
//                println(s)
            }
            counter += 1
            oldReg = reg
            val inst = insts[op.code]
            val newReg = inst!!.apply(reg, op)
            reg = newReg

            // bump the ip by one
            val ipPlusOne = reg.getIndex(ipReg) + 1
            reg = reg.store(ipReg, ipPlusOne)

            op = ops[reg.getIndex(ipReg)]
        }
        println(oldReg)
    }
}

fun main(args: Array<String>) {
    Solution.solve()
}
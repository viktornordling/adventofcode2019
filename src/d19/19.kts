import java.io.File
import java.util.Date
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.util.Stack

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList

//val ipReg = 0
//var inputStream = FileInputStream(File("easy.txt"))

val ipReg = 2L
var inputStream = FileInputStream(File("input.txt"))

System.setIn(inputStream);
val lines = readInput(emptyList())

val program = lines.mapIndexed { index, it -> createOpcode(index, it.split(" ").map { it }) }

fun createOpcode(id: Int, list:List<String>):Opcode {
    return Opcode(id, list[0], list[1].toLong(), list[2].toLong(), list[3].toLong())
}

data class Register(val zero: Long, val one: Long, val two: Long, val three: Long, val four: Long, val five: Long) {
    fun getIndex(index:Long):Long {
        if (index == 0L) {
            return zero
        } else if (index == 1L) {
            return one
        } else if (index == 2L) {
            return two
        } else if (index == 3L) {
            return three
        } else if (index == 4L) {
            return four
        } else if (index == 5L) {
            return five
        } else {
            throw IllegalArgumentException("bad index $index")
        }
    }

    fun store(index: Long, value:Long):Register {
        if (index == 0L) {
            println("storing $value in reg 0!!!!")
            return this.copy(zero = value)
        } else if (index == 1L) {
            return this.copy(one = value)
        } else if (index == 2L) {
            return this.copy(two = value)
        } else if (index == 3L) {
            return this.copy(three = value)
        } else if (index == 4L) {
            return this.copy(four = value)
        } else if (index == 5L) {
            return this.copy(five = value)
        } else {
            throw IllegalArgumentException("bad index $index")
        }
    }
}

data class Opcode(val id: Int, val code: String, val a: Long, val b: Long, val c: Long)

abstract class Instruction(val id:String) {
    abstract fun apply(reg: Register, opcode: Opcode): Register
}

class AddR: Instruction("addr") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, left+right)
        return newReg
    }
}

class AddI: Instruction("addi") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, left+right)
        return newReg
    }
}

class MulR: Instruction("mulr") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, left*right)
        return newReg
    }
}

class MulI: Instruction("muli") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, left*right)
        return newReg
    }
}

class BanR: Instruction("banr") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, left.and(right))
        return newReg
    }
}

class BanI: Instruction("bani") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, left.and(right))
        return newReg
    }
}

class BorR: Instruction("borr") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, left.or(right))
        return newReg
    }
}

class BorI: Instruction("bori") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, left.or(right))
        return newReg
    }
}

class SetR: Instruction("setr") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val newReg = reg.store(opcode.c, left)
        return newReg
    }
}

class SetI: Instruction("seti") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = opcode.a
        val newReg = reg.store(opcode.c, left)
        return newReg
    }
}

class GtiR: Instruction("gtir") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = opcode.a
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, if (left > right) 1 else 0)
        return newReg
    }
}

class GtrI: Instruction("gri") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, if (left > right) 1 else 0)
        return newReg
    }
}

class GtrR: Instruction("gtrr") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, if (left > right) 1 else 0)
        return newReg
    }
}

class EqiR: Instruction("eqir") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = opcode.a
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, if (left == right) 1 else 0)
        return newReg
    }
}

class EqrI: Instruction("eqri") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, if (left == right) 1 else 0)
        return newReg
    }
}

class EqrR: Instruction("eqrr") {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, if (left == right) 1 else 0)
        return newReg
    }
}

val instructions = listOf(AddI(), AddR(), MulI(), MulR(), BanR(), BanI(), BorR(), BorI(), SetR(), SetI(), GtiR(), GtrI(), GtrR(), EqiR(), EqrI(), EqrR())

var reg = Register(1L, 0, 0, 0, 0, 0)
val insts: Map<String, Instruction> = instructions.associateBy { it.id }

val ops = program.associateBy { it.id }

var ip = 0
var done = false
var op: Opcode? = ops.get(ip)
var oldReg = reg

var counter = 0

while (op != null) {
    counter = counter + 1
    oldReg = reg
    val ipIndex = reg.getIndex(ipReg)
    if (ipIndex == 9L) {
        val cur5 = reg.getIndex(5L)
        if (cur5 > 2L && cur5 < 441L) {
            reg = reg.store(5L, 441L)
        }
        if (cur5 > 444L && cur5 < 884L) {
            reg = reg.store(5L, 884L)
        }
        if (cur5 > 888L && cur5 < 11907L) {
            reg = reg.store(5L, 11907L)
        }
        if (cur5 > 11910L && cur5 < 23816L) {
            reg = reg.store(5L, 23816L)
        }
        if (cur5 > 23820L && cur5 < 5275685L) {
            reg = reg.store(5L, 5275685L)
        }
        if (cur5 > 5275689L && cur5 < 10551372L) {
            reg = reg.store(5L, 10551372L)
        }

    }
    if (ipIndex == 13L) {
        val cur5 = reg.getIndex(4L)
        if (cur5 > 2L && cur5 < 441L) {
            reg = reg.store(4L, 441L)
        }
        if (cur5 > 444L && cur5 < 884L) {
            reg = reg.store(4L, 884L)
        }
        if (cur5 > 888L && cur5 < 11907L) {
            reg = reg.store(4L, 11907L)
        }
        if (cur5 > 11910L && cur5 < 23816L) {
            reg = reg.store(4L, 23816L)
        }
        if (cur5 > 23820L && cur5 < 5275685L) {
            reg = reg.store(4L, 5275685L)
        }
        if (cur5 > 5275689L && cur5 < 10551372L) {
            reg = reg.store(4L, 10551372L)
        }

    }
//    if (ipIndex == 3L) {
//        println("is ${reg.getIndex(4L)} * ${reg.getIndex(5L)} == ${reg.getIndex(3L)}?")
//        println("${reg.getIndex(4L) * reg.getIndex(5L)} == ${reg.getIndex(3L)}?")
//    }
    if (ipIndex == 7L) {
        println("IP INDEX: $ipIndex")
        println("!!!!!!!!!!!!!!!!!!")
        println("!!!!!!!!!!!!!!!!!!")
        println("!!!!!!!!!!!!!!!!!!")
        println(reg)
        println(counter)
    }
    val inst = insts.get(op!!.code)
//    println("ip=${reg.getIndex(ipReg)} $reg $op")
    val s = "ip=${reg.getIndex(ipReg)} $reg $op"
    val newReg = inst!!.apply(reg, op!!)
    reg = newReg
    // bump the ip by one
    val ipPlusOne = reg.getIndex(ipReg) + 1
    reg = reg.store(ipReg, ipPlusOne)
//    println(reg)
    if (counter % 1000000 == 0) {
        println("$s $reg")
    }
    op = ops.get(reg.getIndex(ipReg).toInt())
//    println(reg.getIndex(ipReg))
}
println(oldReg)
import java.io.File
import java.util.Date
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.util.Stack

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList

//var inputStream = FileInputStream(File("easy.txt"))
//var inputStream = FileInputStream(File("input.txt"))
var inputStream = FileInputStream(File("program.txt"))
System.setIn(inputStream);
val lines = readInput(emptyList())

data class CpuTest(val before: Register, val op: Opcode, val after: Register)
val cpuTests = mutableListOf<CpuTest>()

//var index = 0
//while (index < lines.size) {
//    val before = createRegister(lines.get(index++).substringAfter("Before: [").removeSuffix("]").split(",").map { it.trim().toInt() })
//    val op = createOpcode(lines.get(index++).split(" ").map { it.toInt() })
//    val after = createRegister(lines.get(index++).substringAfter("After:  [").removeSuffix("]").split(",").map { it.trim().toInt() })
//    cpuTests.add(CpuTest(before, op, after))
//    index++
//}

val program = lines.map { createOpcode(it.split(" ").map { it.toInt() }) }

fun createOpcode(list:List<Int>):Opcode {
    return Opcode(list[0], list[1], list[2], list[3])
}

fun createRegister(list:List<Int>):Register {
    return Register(list[0], list[1], list[2], list[3])
}

data class Register(val zero: Int, val one: Int, val two: Int, val three: Int) {
    fun getIndex(index:Int):Int {
        if (index == 0) {
            return zero
        } else if (index == 1) {
            return one
        } else if (index == 2) {
            return two
        } else if (index == 3 ) {
            return three
        } else {
            throw IllegalArgumentException("bad index $index")
        }
    }

    fun store(index: Int, value:Int):Register {
        if (index == 0) {
            return this.copy(zero = value)
        } else if (index == 1) {
            return this.copy(one = value)
        } else if (index == 2) {
            return this.copy(two = value)
        } else if (index == 3 ) {
            return this.copy(three = value)
        } else {
            throw IllegalArgumentException("bad index $index")
        }
    }
}

data class Opcode(val code: Int, val a: Int, val b: Int, val c: Int)

abstract class Instruction(val id:Int) {
    abstract fun apply(reg: Register, opcode: Opcode): Register
}

class AddR: Instruction(15) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, left+right)
        return newReg
    }
}

class AddI: Instruction(4) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, left+right)
        return newReg
    }
}

class MulR: Instruction(6) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, left*right)
        return newReg
    }
}

class MulI: Instruction(5) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, left*right)
        return newReg
    }
}

class BanR: Instruction(11) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, left.and(right))
        return newReg
    }
}

class BanI: Instruction(8) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, left.and(right))
        return newReg
    }
}

class BorR: Instruction(12) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, left.or(right))
        return newReg
    }
}

class BorI: Instruction(10) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, left.or(right))
        return newReg
    }
}

class SetR: Instruction(2) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val newReg = reg.store(opcode.c, left)
        return newReg
    }
}

class SetI: Instruction(0) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = opcode.a
        val newReg = reg.store(opcode.c, left)
        return newReg
    }
}

class GtiR: Instruction(3) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = opcode.a
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, if (left > right) 1 else 0)
        return newReg
    }
}

class GtrI: Instruction(9) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, if (left > right) 1 else 0)
        return newReg
    }
}

class GtrR: Instruction(7) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, if (left > right) 1 else 0)
        return newReg
    }
}

class EqiR: Instruction(1) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = opcode.a
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, if (left == right) 1 else 0)
        return newReg
    }
}

class EqrI: Instruction(13) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = opcode.b
        val newReg = reg.store(opcode.c, if (left == right) 1 else 0)
        return newReg
    }
}

class EqrR: Instruction(14) {
    override fun apply(reg: Register, opcode: Opcode): Register {
        val left = reg.getIndex(opcode.a)
        val right = reg.getIndex(opcode.b)
        val newReg = reg.store(opcode.c, if (left == right) 1 else 0)
        return newReg
    }
}

val instructions = listOf(AddI(), AddR(), MulI(), MulR(), BanR(), BanI(), BorR(), BorI(), SetR(), SetI(), GtiR(), GtrI(), GtrR(), EqiR(), EqrI(), EqrR())

var reg = Register(0, 0, 0, 0)
val insts = instructions.associateBy { it.id }

for (op in program) {
    val inst = insts.get(op.code)
    val newReg = inst!!.apply(reg, op)
    reg = newReg
}
println(reg)
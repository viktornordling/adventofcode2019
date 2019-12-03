package d02

import util.Reader
import kotlin.math.max

data class Op(var op:Long, var a1: Long, var a2: Long, var d1: Long)

object Solution {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        val values = input.flatMap { it.split(",").map { it.toInt() } }

        for (x in 0..100) {
            for (y in 0..100) {
                var counter = 0
                var max = values.size / 4
                val ops = mutableListOf<Op>()
                while (counter < max) {
                    val op = Op(values[counter * 4 + 0].toLong(), values[counter * 4 + 1].toLong(), values[counter * 4 + 2].toLong(), values[counter * 4 + 3].toLong())
                    ops.add(op)
                    counter++
                }

                try {
                    ops[0].a1 = x.toLong()
                    ops[0].a2 = y.toLong()
                    for (op in ops) {
                        if (op.op == 1L) {
                            val first = getValue(op.a1, ops)
                            val second = getValue(op.a2, ops)
                            val sum = first!! + second!!
                            putValue(op.d1, sum, ops)
                        } else if (op.op == 2L) {
                            val first = getValue(op.a1, ops)
                            val second = getValue(op.a2, ops)
                            val product = first!! * second!!
                            putValue(op.d1, product, ops)
                        } else if (op.op == 99L) {
                        }
                    }
                } catch (e: Exception) {
                    println("failed")
                }
                if (ops[0].op == 19690720L) {
                    println("x = $x y = $y")
                }
            }
        }
    }

    private fun putValue(addr: Long, value: Long, ops: List<Op>) {
        val opPos = addr / 4
        val index = addr % 4
        val op = ops.get(opPos.toInt())
        if (index == 0L) {
            op.op = value
        } else if (index == 1L) {
            op.a1 = value
        } else if (index == 2L) {
            op.a2 = value
        } else {
            op.d1 = value
        }
    }

    private fun getValue(addr: Long, ops: List<Op>): Long {
        val opPos = addr / 4
        val index = addr % 4
        val op = ops.get(opPos.toInt())
        if (index == 0L) {
            return op.op
        } else if (index == 1L) {
            return op.a1
        } else if (index == 2L) {
            return op.a2
        }
        return op.d1
    }
}

fun main() {
    val start = System.currentTimeMillis()
    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
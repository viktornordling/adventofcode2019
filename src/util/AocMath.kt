package util

import util.AocMath.gcd
import util.AocMath.lcm

object AocMath {
    fun gcd(a: Long, b: Long): Long {
        if (b == 0L) {
            return a
        }
        return gcd(b, a % b)
    }
    fun gcd(a: Int, b: Int) = gcd(a.toLong(), b.toLong()).toInt()
    fun gcd(values: List<Int>): Long {
        val longs: List<Long> = values.map { it.toLong() }
        return longs.fold(longs.first(), { a, b -> gcd(a, b)})
    }
    fun lcm(a: Long, b: Long): Long = (a * b) / gcd(a, b)
    fun lcm(values: List<Int>): Long {
        val longs: List<Long> = values.map { it.toLong() }
        return longs.fold(values.first().toLong(), { a, b -> lcm(a, b)})
    }
    fun lcm(a: Int, b: Int, c: Int) = lcm(listOf(a, b, c))
}

fun main() {
    println(gcd(listOf(12, 8, 16)))
    println(lcm(12, 8))
    println(lcm(listOf(12, 8, 6)))
}
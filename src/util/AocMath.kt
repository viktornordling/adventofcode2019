package util

object AocMath {
    fun gcd(x: Int, y: Int): Int {
        if (y == 0) {
            return x
        }
        return gcd(y, x % y)
    }
}
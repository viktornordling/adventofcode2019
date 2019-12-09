package d04

object Solution {
    fun solve() {
//        val input = Reader.readInput("easy.txt")
//        val start = 123443
//        val stop = 123445
//        val start = 111121
//        val stop = 111123
        val start = 128392
        val stop = 643281


        var fits = 0
        var fitsKelvin = 0
        for (i in start..stop) {
            if (isValidPasswordK(i)) {
                fitsKelvin++
            }
            if (isValidPasswordV(i)) {
//                println("$i fits")
                fits++
            } else {
//                println("$i doesn't fit")
            }
        }
//        val start = 128392
//        val stop = 643281
//        val parts: List<List<String>> = input.map { it.split(",") }
//        println(parts)
        println("total $fits")
        println("totalKelvin $fitsKelvin")
    }

    fun isValidPasswordK(password:Int): Boolean {
        val ints = password.toString().map { it.toInt() }
        val sizes = ints.groupBy { it }.values.map { it.size }
        val isSorted = ints == ints.sorted()
        val hasGroupOfTwo = sizes.contains(2)
        return isSorted && hasGroupOfTwo
    }

    fun isValidPasswordV(i:Int):Boolean {
        val s = i.toString()
        var oldC = '#'
        var oldCasInt = -1
        var isIncreasing = true
        var doubles = 0
        var repeats = 0
        for (c in s) {
            if (c == oldC) {
                repeats++
            }
            if (c != oldC) {
                if (repeats == 1) {
                    doubles++
                }
                repeats = 0
            }
            val cAsInt = c.toInt()
            if (cAsInt < oldCasInt) {
                isIncreasing = false
            }
            oldCasInt = cAsInt
            oldC = c
        }
        if (repeats == 1) {
            doubles++
        }
        return doubles > 0 && isIncreasing
    }

}

fun main() {
    val start = System.currentTimeMillis()
    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
package d16

data class PhaseAndIndex(val phase: Int, val index: Int)

object Solution {
    fun solve() {
        val initString = "59768092839927758565191298625215106371890118051426250855924764194411528004718709886402903435569627982485301921649240820059827161024631612290005106304724846680415690183371469037418126383450370741078684974598662642956794012825271487329243583117537873565332166744128845006806878717955946534158837370451935919790469815143341599820016469368684893122766857261426799636559525003877090579845725676481276977781270627558901433501565337409716858949203430181103278194428546385063911239478804717744977998841434061688000383456176494210691861957243370245170223862304663932874454624234226361642678259020094801774825694423060700312504286475305674864442250709029812379"
//        val initString = "03036732577212944063491565474664"
//        val offset = "0303673".toInt()
        val offset = 5976809
        println("Generating string")
        val map = mutableMapOf<PhaseAndIndex, Int>()
        for (i in (offset - 1)..(initString.length * 10000)) {
            map[PhaseAndIndex(0, i)] = initString[i % initString.length].toString().toInt()
        }
        println("Done generating string")

//        for (i in 5976808..6500000) {
//            map[PhaseAndIndex(i, 0)] = initString[i % initString.length].toString().toInt()
//        }
//        println("Done generating string")

//        val initString = "12345678"
//        for (i in initString.indices) {
//            map[PhaseAndIndex(0, i)] = initString[i % initString.length].toString().toInt()
//        }

//        val pos = 5976809
//        val offset = 4

//        val pos = value.length - 4
        for (i in 1..100) {
            println("Calculating phase $i")
            calcPhase(offset, map, i, map.keys.maxBy { it.index }!!.index)
            if (i > 2) {
                val keysToDelete = map.filter { it.key.phase < i - 2 }
                for (key in keysToDelete) {
                    map.remove(key.key)
                }
            }
        }

        println("Eight digit message:")
        for (i in 0..7) {
            print(map[PhaseAndIndex(100, offset + i)])
        }
        println()
//        println(map)
//        println(calcNextDigitOptimized(pos, value))
//        println(calcNextDigitOptimized(pos+1, value))
//        println(calcNextDigitOptimized(pos+2, value))
//        println(calcNextDigitOptimized(pos+3, value))
//        println(calcNextDigitOptimized(pos+4, value))
    }

    fun calcPhase(offset: Int, values: MutableMap<PhaseAndIndex, Int>, phase: Int, length: Int) {
        var sum = 0
        // Init the first value for this phase
        println("Generating first value.")
        for (i in offset..length) {
            sum += values[PhaseAndIndex(phase - 1, i)] ?: 0
        }
        values[PhaseAndIndex(phase, offset)] = sum.toString().last().toString().toInt()
        println("Done generating first value.")

        // The rest of the values we can just look up
        for (i in offset+1..length) {
            val next = (values[PhaseAndIndex(phase, i - 1)] ?: 0) - (values[PhaseAndIndex(phase - 1, i - 1)] ?: 0)
            val n = if (next < 0) {
                next + 10
            } else {
                next
            }
            values[PhaseAndIndex(phase, i)] = n
        }
    }

    fun getMultiplier(multiples: Int, index: Int): Int {
        // 0, 1, 0, -1
        // 0, 0, 1, 1, 0, 0, -1, -1
        // 0, 0, 0, 1, 1, 1, 0, 0, 0, -1, -1, -1
        val group = (index+1) / multiples
//        val index = ((index * multiples) + 1)% 4
        return mutableListOf(0, 1, 0, -1)[group % 4]
    }

    fun transform(value:String): String {
        var i = 0
        var curV = value
        var newV = ""
        for (c in curV) {
            val cAsInt = c.toString().toInt()
            val next = calcNextDigit(cAsInt, i, value)
//            println("next for $cAsInt ($i) = $next")
            i++
            newV += next.toString()
        }
        return newV
    }

    fun transformOffset(value:String, start: Int, count: Int): String {
        var i = 0
        var curV = value
        var newV = ""
        for (c in start..(start+count)) {
            val cAsInt = c.toString().toInt()
            val next = calcNextDigit(cAsInt, i, value)
//            println("next for $cAsInt ($i) = $next")
            i++
            newV += next.toString()
        }
        return newV
    }

    fun calcNextDigit(c: Int, i: Int, value: String): Int {
        var i2 = 0
        var cur = 0
        for (c2 in value) {
            val c2I = c2.toString().toInt()
            val multiplier = getMultiplier(i + 1, i2)
            print("$multiplier,")
            cur += c2I * multiplier
            i2++
        }
        println()
        return cur.toString().last().toString().toInt()
    }

    fun calcNextDigitOptimized(pos: Int, value: String): Int {
//        println("pos = $pos")
        var firstOne = pos-1
//        while (getMultiplier(pos, firstOne) == 1) {
//            firstOne--
//        }
//        firstOne++
//        println("First one: $firstOne")
        var lastOne = value.length - 1
//        while (getMultiplier(pos, lastOne) == 1 && lastOne < value.length) {
//            lastOne++
//        }
//        lastOne--
//        println("Last one: $lastOne")
        var nextDigit = 0
        for (a in firstOne..lastOne) {
            val lookup = value[a].toString().toInt()
//            print("$lookup,")
            nextDigit += lookup
        }
//        println()
        return nextDigit.toString().last().toString().toInt()
    }


}

fun main() {
    val start = System.currentTimeMillis()

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
import java.io.File
import java.util.Date
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.util.Stack

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList
val lines = readInput(emptyList())

val initial = "##.##.##..#..#.#.#.#...#...#####.###...#####.##..#####.#..#.##..#..#.#...#...##.##...#.##......####." // input
var plants: MutableMap<Long, Char> = mutableMapOf<Long, Char>()
var ind = 0
for (char in initial) {
    plants[ind.toLong()] = char
    ind = ind+1
}

data class Change(val ll: Char, val l: Char, val c: Char, val r: Char, val rr: Char, val newChar: Char)

fun parseChange(line:String): Change {
    return Change(line[0], line[1], line[2], line[3], line[4], line[line.length - 1])
}

val changes = lines.map { parseChange(it) }

fun samePlant(changePlant:Char, plants: MutableMap<Long, Char>, index:Long):Boolean {
    val cur = plants.getOrDefault(index, '.')
    return changePlant == cur
}

fun applyChange(change:Change, plants: MutableMap<Long, Char>, index:Long):Char {
    if (samePlant(change.ll, plants, index - 2) &&  samePlant(change.l, plants, index - 1) && samePlant(change.c, plants, index)
            && samePlant(change.r, plants, index + 1) && samePlant(change.rr, plants, index + 2)) {
        return change.newChar
    }
    return '-'
}

fun applyAllChanges(plants: MutableMap<Long, Char>, index:Long):Char {
    for (change in changes) {
        var ch = applyChange(change, plants, index)
        if (ch != '-') {
            return ch
        }
    }
    return '-'
}

var last = -1L
for (i in 0..20) {
    val minPos = plants.minBy { it.key }!!.key
    val maxPos = plants.maxBy { it.key }!!.key
    val sum = plants.filterValues { it == '#' }.map { it.key }.sum()
    last = sum

    val newMap = mutableMapOf<Long, Char>()
    for (j in minPos-5..maxPos+5) {
        newMap[j] = applyAllChanges(plants, j)
    }
    plants = newMap
}

val sum = plants.filterValues { it == '#' }.map { it.key }.sum()
println(sum)

val fiftyBillionMinus100: BigInteger = BigInteger("50000000000").minus(BigInteger("200"))
val multiplied: BigInteger = fiftyBillionMinus100.multiply(BigInteger("62"))
val plussed: BigInteger = multiplied.plus(BigInteger("11891"))

println(plussed)

val set22: HashSet<Int> = hashSetOf(1, 2)
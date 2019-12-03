import java.io.File
import java.util.Date
import java.io.FileInputStream

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList

//var inputStream = FileInputStream(File("input6.txt"))
var inputStream = FileInputStream(File("6easy.txt"))
System.setIn(inputStream);
val lines = readInput(emptyList())

var id = 0
fun parseInput(l: String): Star {
    val s = l.split(", ").map { it.toInt() }
    return Star(s.first(), s.last(), id++, -1)
}

data class Star(val x: Int, val y: Int, val id: Int, var totalStarDist:Int)

val input: List<Star> = lines.map { parseInput(it) }

fun findClosest(test:Star, stars:List<Star>):Star? {
    var minDist = 100000000
    var minStar:Star? = null
    var totalStarDist = 0
    for (star in stars) {
        val dist = Math.abs(star.x - test.x) + Math.abs(star.y - test.y)
        if (dist < minDist) {
            minDist = dist
            minStar = star
        }
        totalStarDist += dist
    }
    // Figure out if we have a draw
    var mindists = 0
    for (star in stars) {
        val dist = Math.abs(star.x - test.x) + Math.abs(star.y - test.y)
        if (dist == minDist) {
            mindists++
        }
    }
    if (mindists > 1) {
//        println("Draw!")
        minStar = Star(-1, -1, -1, -1)
    }
    minStar = minStar!!.copy(totalStarDist = totalStarDist)
//    println("Closest star from x=${test.x} y=${test.y} is ${minStar} (dist = $minDist)")
    return minStar
}

val padding = 0
val maxX = input.map { it.x }.max()!! + padding
val maxY = input.map { it.y }.max()!! + padding
val minX = -padding
val minY = -padding
val minStars = mutableListOf<Star?>()
for (y in minY..maxY) {
    for (x in minX..maxX) {
        minStars.add(findClosest(Star(x, y, -1, -1), input))
    }
}

val m = minStars.filterNotNull()

val rows = (maxY - minY + 1)
val cols = (maxX - minX + 1)
println("rows = $rows")
println("cols = $cols")

for (y in 0..rows-1) {
    for (x in 0..cols-1) {
        val s = m.get(y * cols + x)
        if (s.id == -1) {
            print(".")
        } else {
            print("${s.id}")
        }
    }
    println()
}

// find areas on the edge
val edge = mutableSetOf<Int>()
for (x in 0..cols-1) {
    val s1 = m.get(x)
    val s2 = m.get(((rows-1) * cols) + x)
    edge.add(s1.id)
    edge.add(s2.id)
}

for (y in 0..rows-1) {
    val s1 = m.get((y * cols) + 0)
    val s2 = m.get((y * cols) + cols - 1)
    edge.add(s1.id)
    edge.add(s2.id)
}

val starsToKeep = m.filter { !edge.contains(it.id) }
val idToStars: Map<Int, List<Star>> = starsToKeep.groupBy { it.id }
val size = idToStars.mapValues { it.value.size }.maxBy { it.value }
println(size)

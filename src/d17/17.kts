import java.io.File
import java.util.Date
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.util.Stack

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList

//var inputStream = FileInputStream(File("easy.txt"))
var inputStream = FileInputStream(File("input.txt"))
System.setIn(inputStream);
val lines = readInput(emptyList())

data class Spec(val type: Char, val number:Int, val range:Pair<Int,Int>)
data class Pos(val x: Int, val y:Int)
class Cell(val pos: Pos, var state: Char)
val map = mutableMapOf<Pos, Cell>()

fun parse(line:String):Spec {
    val type = line[0]
    val number = line.substringAfter("=").substringBefore(",").toInt()
    val range = line.substringAfter("=").substringAfter("=")
    val rangeParts = range.split("..").map { it.toInt() }
    return Spec(type, number, Pair(rangeParts.get(0), rangeParts.get(1)))
}

val specs = lines.map { parse(it) }

for (spec in specs) {
    if (spec.type == 'x') {
        val x = spec.number
        for (y in spec.range.first..spec.range.second) {
            val pos = Pos(x, y)
            val cell = Cell(pos, '#')
            map.put(pos, cell)
        }
    } else {
        val y = spec.number
        for (x in spec.range.first..spec.range.second) {
            val pos = Pos(x, y)
            val cell = Cell(pos, '#')
            map.put(pos, cell)
        }
    }
}

val minX = map.values.minBy { it.pos.x }!!.pos.x-1
val maxX = map.values.maxBy { it.pos.x }!!.pos.x+1
val minY = map.values.minBy { it.pos.y }!!.pos.y-1
val maxY: Int = map.values.maxBy { it.pos.y }!!.pos.y+1

println("minX = $minX maxX=$maxX minY=$minY maxY=$maxY")

fun printMap() {
    for (y in minY..maxY) {
        for (x in minX..maxX) {
            val cell = map.get(Pos(x, y))
            if (cell == null) {
                print('.')
            } else {
                print(cell.state)
            }
        }
        println()
    }
}

fun isEmpty(pos:Pos): Boolean = !map.containsKey(pos)

fun isWall(pos:Pos?): Boolean = if (pos == null) false else map.containsKey(pos) && map.get(pos)!!.state == '#'

val stable = setOf('~', '#')

fun isEmptyButNotFloating(pos:Pos, from:Pos): Boolean {
    val empty = !map.containsKey(pos)
    val below = map.get(pos.copy(y=pos.y+1))
    val stableBelow = if (below == null) false else stable.contains(below.state)
    // Acckkkshuually, if we're just floating over an edge, we _can_ go out into "the void"
    val belowFrom = from.copy(y=from.y+1)
    val wallBelowFrom = isWall(belowFrom)
    return empty && (stableBelow || wallBelowFrom)
}

fun fillWater(pos:Pos): Boolean {
    println("pos = $pos")
    val cell = map.getOrDefault(pos, Cell(pos, '|'))
    map.put(pos, cell)
    val below = pos.copy(y = pos.y + 1)
    val left = pos.copy(x = pos.x - 1)
    val right = pos.copy(x = pos.x + 1)

    if (below.y < maxY && isEmpty(below)) {
        fillWater(below)
    }

    var hitWallLeft = false
    var hitWallRight = false
    if (isEmptyButNotFloating(left, pos)) {
        hitWallLeft = fillWater(left)
    } else if (isWall(left)) {
        hitWallLeft = true
    }
    if (isEmptyButNotFloating(right, pos)) {
        hitWallRight = fillWater(right)
    } else if (isWall(right)) {
        hitWallRight = true
    }
    if (hitWallLeft && hitWallRight) {
        cell.state = '~'
        println("hit wall both sides")

        // fill with water to the left and right
        var vLeft = left
        while (!isWall(vLeft)) {
            map.get(vLeft)?.state = '~'
            vLeft = vLeft.copy(x = vLeft.x - 1)
        }

        var vRight = right
        while (!isWall(vRight)) {
            map.get(vRight)?.state = '~'
            vRight = vRight.copy(x = vRight.x + 1)
        }
    }
    return hitWallLeft || hitWallRight
}

fillWater(Pos(500, 0))

printMap()

val water = setOf('~')

val result = map.filterValues { it -> water.contains(it.state) && it.pos.y >= minY }.size
println(result)
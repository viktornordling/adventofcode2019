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

data class Pos(val x: Int, val y:Int)
data class Cell(val pos: Pos, val state: Char)
var map = mutableMapOf<Pos, Cell>()


for ((row, line) in lines.withIndex()) {
    for ((col, char) in line.withIndex()) {
        val pos = Pos(col, row)
        print(char)
        map.put(pos, Cell(pos, char))
    }
    println()
}

val minX = map.values.minBy { it.pos.x }!!.pos.x
val maxX = map.values.maxBy { it.pos.x }!!.pos.x
val minY = map.values.minBy { it.pos.y }!!.pos.y
val maxY: Int = map.values.maxBy { it.pos.y }!!.pos.y

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

fun isOpen(pos:Pos): Boolean = !map.containsKey(pos) || map.get(pos)!!.state == '.'

fun isTree(pos:Pos?): Boolean = if (pos == null) false else map.containsKey(pos) && map.get(pos)!!.state == '|'
fun isLumber(pos:Pos?): Boolean = if (pos == null) false else map.containsKey(pos) && map.get(pos)!!.state == '#'

val side = 50

for (i in 0..5000) {
    val minute = i+1
    val newMap = mutableMapOf<Pos, Cell>()
    for (y in 0..side) {
        for (x in 0..side) {
            val pos = Pos(x, y)
            val cell = map.getOrDefault(pos, Cell(pos, '.'))
            val neighborPositions2 = setOf(pos.copy(pos.x - 1, pos.y - 1), pos.copy(pos.x, pos.y - 1), pos.copy(pos.x + 1, pos.y - 1),
                    pos.copy(pos.x - 1, pos.y), pos.copy(pos.x + 1, pos.y),
                    pos.copy(pos.x - 1, pos.y + 1), pos.copy(pos.x, pos.y + 1), pos.copy(pos.x + 1, pos.y + 1))
            val neighborPositions = neighborPositions2.filter { it.x >= 0 && it.x < side && it.y >= 0 && it.y < side }
            if (isOpen(pos)) {
                val treeNeighbours = neighborPositions.map { isTree(it) }.filter { it }.size
                if (treeNeighbours >= 3) {
                    newMap.put(pos, cell.copy(state = '|'))
                } else {
                    newMap.put(pos, cell)
                }
            } else if (isTree(pos)) {
                val lumberNeighbours = neighborPositions.map { isLumber(it) }.filter { it }.size
                if (lumberNeighbours >= 3) {
                    newMap.put(pos, cell.copy(state = '#'))
                } else {
                    newMap.put(pos, cell)
                }
            } else if (isLumber(pos)) {
                val lumberNeighbours = neighborPositions.map { isLumber(it) }.filter { it }.size
                val treeNeighbours = neighborPositions.map { isTree(it) }.filter { it }.size
                if (lumberNeighbours >= 1 && treeNeighbours >= 1) {
                    newMap.put(pos, cell)
                } else {
                    newMap.put(pos, cell.copy(state = '.'))
                }
            }
        }
    }
    map = newMap
    if (minute % 100 == 0) {
        val woods = map.values.filter { it.pos.x >= 0 && it.pos.x < side && it.pos.y >= 0 && it.pos.y < side && it.state == '|' }.size
        val lumbers = map.values.filter { it.pos.x >= 0 && it.pos.x < side && it.pos.y >= 0 && it.pos.y < side && it.state == '#' }.size
        println("minute = $minute woods = $woods lumbers = $lumbers prod=${woods * lumbers}")
    }

}

printMap()
val woods = map.values.filter { it.pos.x >= 0 && it.pos.x < side && it.pos.y >= 0 && it.pos.y < side && it.state == '|'}.size
val lumbers = map.values.filter { it.pos.x >= 0 && it.pos.x < side && it.pos.y >= 0 && it.pos.y < side && it.state == '#'}.size
println("woods = $woods lumbers = $lumbers")
println(woods * lumbers)
println(BigInteger("1000000000").divide(BigInteger("700")))
println(BigInteger("1000000000").minus(BigInteger("1428570").multiply(BigInteger("700"))))
println(BigInteger("1000").plus(BigInteger("1428570").multiply(BigInteger("700"))))
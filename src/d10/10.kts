import java.io.File
import java.util.Date
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.util.Stack

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList

//var inputStream = FileInputStream(File("easy.txt"))
var inputStream = FileInputStream(File("input.txt"))
System.setIn(inputStream);
val lines = readInput(emptyList())

class Point(var x: Int, var y: Int, var xSpeed: Int, var ySpeed: Int)


fun parsePoint(line : String): Point {
    val pos = line.removePrefix("position=<").replace(">", ",").split(",").take(2).map { it.trim().toInt() }
    val velocity = line.substringAfter("velocity=<").replace(">", ",").split(",").take(2).map { it.trim().toInt() }
    return Point(pos[0], pos[1], velocity[0], velocity[1])
}

val points = lines.map { parsePoint(it) }

var lastWidth = 1000000
var lastHeight = 1000000
for (i in 0..10123) {
    for (point in points) {
        point.x += point.xSpeed
        point.y += point.ySpeed
    }
    val width = Math.abs(points.minBy { it.x }!!.x - points.maxBy { it.x }!!.x)
    val height = Math.abs(points.minBy { it.y }!!.y - points.maxBy { it.y }!!.y)

    if (width > lastWidth) {
        println("REACHED TURNING POINT IN X")
    }

    if (height > lastHeight) {
        println("REACHED TURNING POINT IN Y")
    }

    lastWidth = width
    lastHeight = height
}

// subtract minx and miny from all points
val minX = points.minBy { it.x }!!.x
val minY = points.minBy { it.y }!!.y
for (point in points) {
    point.x -= minX
    point.y -= minY
}

// group the points by line
val pointByLine: Map<Int, List<Point>> = points.groupBy { it.y }

val height = Math.abs(points.minBy { it.y }!!.y - points.maxBy { it.y }!!.y)
val width = Math.abs(points.minBy { it.x }!!.x - points.maxBy { it.x }!!.x)

// print each line
for (y in 0..height) {
    val pointsOnLine = pointByLine.get(y)!!.sortedBy { it.x }.associateBy { it.x }
    for (x in 0..width) {
        if (pointsOnLine[x] != null) {
            print("#")
        } else {
            print(".")
        }
    }
    println()
}

println(points.take(2))
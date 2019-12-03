val serial = 8868

val matrix = IntArray(300*300)

fun func(x: Int, y: Int, serial: Int):Int {
    val rack = x + 10
    val powerLevel = rack * y
    val addAndMul = (powerLevel + serial) * rack
    val hundreds = (addAndMul % 1000) / 100
    return hundreds - 5
}

for (x in 1..300) {
    for (y in 1..300) {
        matrix[(y - 1) * 300 + (x - 1)] = func(x, y, serial)
    }
}

// find max 3 by 3
var max = -1000000
var maxX = 0
var maxY = 0

fun get(x: Int, y: Int): Int {
    return matrix[y * 300 + x]
}

for (size in 1..300) {
    println("size = $size, max=$max maxX=${maxX + 1} maxY=${maxY + 1}")
    for (x in 0..297) {
        for (y in 0..297) {
            val total = sumRect(x, y, size)
            if (total > max) {
                max = total
                maxX = x
                maxY = y
            }
        }
    }
}

println("max = $max maxX = $maxX maxY = $maxY")

fun _11.sumRect(curX: Int, curY: Int, size: Int): Int {
    var sum = 0
    for (x in curX..Math.min(299, curX+size)) {
        for (y in curY..Math.min(299, curY+size)) {
            sum += get(x, y)
        }
    }
    return sum
}
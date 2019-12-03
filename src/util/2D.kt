package util

import java.math.BigInteger

data class Pos(val x: Int, val y: Int)

object Surface {
    fun printMap(map: Map<Pos, Char>) {
        val minX = map.keys.minBy { it.x }!!.x - 1
        val minY = map.keys.minBy { it.y }!!.y - 1
        val maxX = map.keys.maxBy { it.x }!!.x + 1
        val maxY = map.keys.maxBy { it.y }!!.y + 1

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val cur = map.get(Pos(x, y))
                if (cur == null) {
                    print('#')
                } else {
                    print(cur)
                }
            }
            println()
        }

    }
}
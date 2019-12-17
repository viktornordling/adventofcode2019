package util

data class Pos(val x: Int, val y: Int) {
    fun neighbours(): Set<Pos> = setOf(this.copy(x=this.x+1), this.copy(x=this.x-1), this.copy(y=this.y+1), this.copy(y=this.y-1))
    fun next(direction: Char): Pos {
        return when (direction) {
            'R' -> return this.copy(x=this.x+1)
            'L' -> this.copy(x=this.x-1)
            'D' -> this.copy(y=this.y+1)
            'U' -> return this.copy(y=this.y-1)
            else -> throw IllegalArgumentException("Bad direction $direction")
        }
    }
}

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
                    print(' ')
                } else {
                    print(cur)
                }
            }
            println()
        }
    }
}
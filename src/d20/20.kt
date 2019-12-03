import util.Pos
import util.Reader
import util.Surface.printMap
import java.util.*

val lines = Reader.readInput("easy.txt")
val line: String = lines.first()

val posStack = Stack<Pos>()
var distStack = Stack<Int>()
var curPos = Pos(0, 0)
var curDist = 0
val map = mutableMapOf<Pos, Char>()
val dist = mutableMapOf<Pos, Int>()

object Solution {
    fun solve() {
        map.put(curPos, '.')

        for (char in line) {
            when (char) {
                '(' -> {
                    posStack.push(curPos)
                    distStack.push(curDist)
                }
                '|' -> {
                    curPos = posStack.peek()
                    curDist = distStack.peek()
                }
                ')' -> {
                    curPos = posStack.pop()
                    curDist = distStack.pop()
                }
                'N' -> {
                    map.put(curPos.copy(y = curPos.y - 1), '-')
                    curPos = curPos.copy(y = curPos.y - 2)
                    curDist = curDist + 1
                }
                'S' -> {
                    map.put(curPos.copy(y = curPos.y + 1), '-')
                    curPos = curPos.copy(y = curPos.y + 2)
                    curDist = curDist + 1
                }
                'W' -> {
                    map.put(curPos.copy(x = curPos.x - 1), '|')
                    curPos = curPos.copy(x = curPos.x - 2)
                    curDist = curDist + 1
                }
                'E' -> {
                    map.put(curPos.copy(x = curPos.x + 1), '|')
                    curPos = curPos.copy(x = curPos.x + 2)
                    curDist += 1
                }
            }

            map.put(curPos, '.')
            if (dist.containsKey(curPos)) {
                val before = dist.get(curPos)!!
                if (curDist < before) {
                    dist.put(curPos, curDist)
                }
            } else {
                dist.put(curPos, curDist)
            }
        }

        printMap(map)

        val maxDist: Int? = dist.values.max()
        val thousandOrMore = dist.values.filter { it >= 1000 }.size
        println("Max dist = $maxDist, >= 1000 = $thousandOrMore")
    }
}

fun main(args: Array<String>) {
    Solution.solve()
}
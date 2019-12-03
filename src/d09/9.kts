class Marble(val id: Long, var left: Marble?, var right: Marble?)

var marble0 = Marble(0, null, null)
var marble1 = Marble(1, null, null)
var marble2 = Marble(2, null, null)

marble0.left = marble1
marble0.right = marble2

marble1.left = marble2
marble1.right = marble0

marble2.left = marble0
marble2.right = marble1

var curMarble = marble2

val points = mutableMapOf<Int, Long>()
val players = 411
val marbles = 72059*100L
var curTurn = 1

for (i in 3..marbles) {
    if (i % 23L != 0L) {
        val new = Marble(i, null, null)
        val next = curMarble.right!!
        val nextNext = next.right!!

        next.right = new
        nextNext.left = new

        new.left = next
        new.right = nextNext

        curMarble = new
    } else {
        val curPoints = points.getOrDefault(curTurn, 0)
        var toRemove = curMarble
        for (c in 0..6) {
            toRemove = toRemove.left!!
        }
        toRemove.left?.right = toRemove.right
        toRemove.right?.left = toRemove.left
        points[curTurn] = curPoints + i + toRemove.id
        curMarble = toRemove.right!!
    }
    curTurn++
    if (curTurn > players) {
        curTurn = 1
    }
}

println(points.maxBy { it.value })
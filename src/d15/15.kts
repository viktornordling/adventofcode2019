import java.io.File
import java.io.FileInputStream

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList

//var inputStream = FileInputStream(File("easy.txt"))
var inputStream = FileInputStream(File("input.txt"))
System.setIn(inputStream);
val lines = readInput(emptyList())

class Cell(var char: Char, val pos: Pos)
data class Pos(val x: Int, val y: Int)
class Unit(var hp: Int, var pos: Pos, val isGoblin: Boolean, val attack:Int) {
    override fun toString(): String {
        return "goblin: $isGoblin pos: $pos hp: $hp"
    }
}

val units: MutableMap<Pos, Unit> = mutableMapOf()

fun parse(y: Int, line:String):List<Cell> {
    return line.mapIndexed { x, it ->
        var c = it
        if (it == 'E') {
            units.put(Pos(x, y), Unit(200, Pos(x, y), false, attack=3))
            c = '.'
        } else if (it == 'G') {
            units.put(Pos(x, y), Unit(200, Pos(x, y), true, attack=3))
            c = '.'
        }
        Cell(c, Pos(x, y))
    }
}

val cells = lines.mapIndexed { index, it -> parse(index, it) }.flatten()

val cols = cells.maxBy { it.pos.x }!!.pos.x
val rows = cells.maxBy { it.pos.y }!!.pos.y
val cellMap: MutableMap<Pos, Cell> = cells.associateBy { it.pos }.toMutableMap()

fun reconstructPath(cameFrom: MutableMap<Pos, Pos>, current: Pos):List<Pos> {
    val path = mutableListOf(current)
    var cur = current
    while (cameFrom.containsKey(cur)) {
        cur = cameFrom.get(cur)!!
        path.add(cur)
    }
    return path
}

fun getIndex(pos: Pos): Int {
    return pos.y + cols + pos.x
}

fun findShortestPath(start: Pos, goal: Pos):List<Pos> {
    val neighbours = setOf(start.copy(y=start.y-1), start.copy(x=start.x-1), start.copy(x=start.x+1), start.copy(y=start.y+1))
    val opens = neighbours.filter { isOpen(it) }
    var minDist = 9999999
    var minPath = emptyList<Pos>()

    for (n in opens) {
        if (n == goal) {
            return listOf(goal, start)
        }
    }

    for (n in opens) {
        val s = findShortestPath2(n, goal)
        if (s.size in 1..(minDist - 1)) {
            minDist = s.size
            minPath = s + listOf(start)
        }
    }
    return minPath
}

fun findShortestPath2(start: Pos, goal: Pos):List<Pos> {
    val shortestPaths = mutableListOf<List<Pos>>()
    val closed = mutableSetOf<Pos>()
    val open = mutableSetOf(start)

    val cameFrom: MutableMap<Pos, Pos> = mutableMapOf()
    val gScore = mutableMapOf<Pos, Int>()
    gScore.put(start, 0)
    val fScore = mutableMapOf<Pos, Int>()
    fScore.put(start, h(start, goal))

    while (!open.isEmpty()) {
        val current = open.minBy { fScore.getOrDefault(it, 9000000) }!!
        if (current == goal) {
            shortestPaths.add(reconstructPath(cameFrom, current))
        }
        open.remove(current)
        closed.add(current)

        for (pos in openNeighbours(current)) {
            if (closed.contains(pos)) {
                continue
            }
            val tentScore = gScore.get(current)!! + 1
            if (!open.contains(pos)) {
                open.add(pos)
            } else if (tentScore >= gScore.getOrDefault(pos, 9000000)) {
                continue
            }

            cameFrom.put(pos, current)
            gScore.put(pos, tentScore)
            fScore.put(pos, gScore.get(pos)!! + h(pos, goal))
        }
    }
    val sizeOfShortestPath = shortestPaths.minBy { it.size }?.size
    if (sizeOfShortestPath == null || sizeOfShortestPath <= 1) {
        return listOf()
    }
    val potShortestPaths = shortestPaths.filter { sizeOfShortestPath == it.size }
    if (potShortestPaths.size > 1) {
        println("Size of potShortestPaths = ${potShortestPaths.size}")
    }
    val chosenShortestPath = potShortestPaths.minBy { getIndex(it.dropLast(1).last()) }!!
    return chosenShortestPath
}

fun isOpen(pos: Pos):Boolean {
    val cellOpen = cellMap.get(pos)?.char?.equals('.') ?: false
    return (cellOpen && !units.containsKey(pos))
}

fun openNeighbours(pos: Pos):Set<Pos> {
    val neighbours = setOf(pos.copy(x=pos.x+1), pos.copy(x=pos.x-1), pos.copy(y=pos.y+1), pos.copy(y=pos.y-1))
    val opens = neighbours.filter { isOpen(it) }
    return opens.toSet()
}

fun h(start: Pos, end: Pos):Int = Math.abs(start.x - end.x) + Math.abs(start.y - end.y)

var round = 0
var done = false
var prematurelyDone = false
do {
    round++
    val sortedByY = units.values.groupBy { it.pos.y }.toSortedMap()
    for (row in sortedByY) {
        val sortedRow = row.value.sortedBy { it.pos.x }
        for (unit in sortedRow) {
            if (unit.hp <= 0) {
                continue
            }
            val remainingGoblins = units.filter { it.value.isGoblin }.size
            val remainingElves = units.filter { !it.value.isGoblin }.size

            if (remainingElves <= 0 || remainingGoblins <= 0) {
                prematurelyDone = true
                val neg = units.values.filter { it.hp < 0 }
                println("neg: $neg")
                val hs = units.values.map { it.hp }.sum()
                println("remaining health = $hs")
                println((round-1) * hs)
                break
            }
            val neighborPositions = setOf(unit.pos.copy(x = unit.pos.x + 1), unit.pos.copy(x = unit.pos.x - 1), unit.pos.copy(y = unit.pos.y + 1), unit.pos.copy(y = unit.pos.y - 1))
            val neighborEnemies = neighborPositions.map { units.get(it) }.filterNotNull().filter { it.isGoblin != unit.isGoblin }.filter { it.hp > 0 }
            // Only move if you don't have any enemies to fight.
            if (neighborEnemies.isEmpty()) {
                val enemies = units.values.filter { it.isGoblin != unit.isGoblin && it.hp > 0 }
                val targetPositions = enemies.flatMap { openNeighbours(it.pos) }
                val pathToTargets = targetPositions.associate { pos -> pos to findShortestPath(unit.pos, pos) }
                val removeImpossible = pathToTargets.filterValues { !it.isEmpty() }
                val minCost = removeImpossible.values.minBy { it.size }?.size
                val potTargets = targetPositions.filter { findShortestPath(unit.pos, it).size == minCost }
                val target = potTargets.minBy { (it.y * cols + it.x) }
                if (target != null && minCost != null && minCost > 1) {
                    val nextStep = findShortestPath(unit.pos, target).dropLast(1).takeLast(1).first()
                    units.remove(unit.pos)
                    unit.pos = nextStep
                    units.put(unit.pos, unit)
                }
            }

            val neighborPositions2 = setOf(unit.pos.copy(x = unit.pos.x + 1), unit.pos.copy(x = unit.pos.x - 1), unit.pos.copy(y = unit.pos.y + 1), unit.pos.copy(y = unit.pos.y - 1))
            val neighborEnemies2 = neighborPositions2.map { units.get(it) }.filterNotNull().filter { it.hp > 0 && it.isGoblin != unit.isGoblin }
            val minhp = neighborEnemies2.map { it.hp }.min()
            if (minhp != null) {
                val picked = neighborEnemies2.filter { it.hp == minhp }.minBy { (it.pos.y * cols + it.pos.x) }
                if (picked != null) {
                    picked.hp -= unit.attack
                    if (picked.hp <= 0) {
                        units.remove(picked.pos)
                    }
                }
            }
        }
    }

    val remainingGoblins = units.filter { it.value.isGoblin }.size
    val remainingElves = units.filter { !it.value.isGoblin }.size
    println(units)
    val hs = units.values.map { it.hp }.sum()
    println("Round = $round, remaining goblins = $remainingGoblins, remaining elves: $remainingElves hp = $hs")
    for (y in 0..rows) {
        for (x in 0..cols) {
            val cell = cellMap.get(Pos(x, y))
            val unit = units.get(Pos(x, y))
            if (unit != null) {
                if (unit.isGoblin) {
                    print('G')
                } else {
                    print('E')
                }
            } else {
                print(cell!!.char)
            }
        }
        println()
    }
    for (unit in units) {
        println(unit)
    }
    if (remainingElves <= 0 || remainingGoblins <= 0 || round == 57) {
        done = true
    }
} while (!done)

println("premature: $prematurelyDone")
if (prematurelyDone) {
    round--
}
println("round = $round")
val hs = units.values.map { it.hp }.sum()
println("remaining health = $hs")
println(round * hs)
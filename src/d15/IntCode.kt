package d15


import util.Pos
import util.Reader
import util.Surface
import java.math.BigInteger

object IntCode {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        val values = input.flatMap { it.split(",").map { it.toLong() } }
        runProgramWithInput(values, BigInteger.valueOf(2L))
    }

    private fun runProgramWithInput(immutableValues: List<Long>, input: BigInteger): Long {
        val frontier = mutableSetOf<Pos>()
        val map = mutableMapOf<Pos, Char>()
        val explored = mutableSetOf<Pos>()
        map[Pos(0, 0)] = '.'
        var curPos = Pos(0, 0)
        frontier.addAll(openNeighbours(curPos, map))
        var newDesiredPos = Pos(0, 0)

        var relativeBase = BigInteger.ZERO
        val values: MutableMap<BigInteger, BigInteger> = immutableValues.toMutableList().mapIndexed { index: Int, it: Long -> BigInteger.valueOf(index.toLong()) to BigInteger.valueOf(it) }.toMap().toMutableMap()
        var currentPos = BigInteger.ZERO

        while (true) {
            var jump = false
            var jumps = 4
            val op: BigInteger = values[currentPos]!!
            val opCode = getOpCode(op)
            val mode1 = getMode(op, 1)
            val mode2 = getMode(op, 2)
            val mode3 = getMode(op, 3)
            if (opCode == 1L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                var dest = values[currentPos.add(BigInteger.valueOf(3L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                if (mode3 == 2) {
                    dest = dest + relativeBase
                }
                values[dest] = a.add(b)
            } else if (opCode == 2L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                var dest = values[currentPos.add(BigInteger.valueOf(3L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                if (mode3 == 2) {
                    dest = dest + relativeBase
                }
                values[dest] = a.multiply(b)
            } else if (opCode == 3L) {
                val next = frontier.minBy { h(curPos, it) }
                if (next == null) {
                    println("Noting more to explore")
                    val oxygen = Pos(-14, -12)
                    val path = findShortestPath(Pos(0,0), oxygen, map)
                    println("Shortest path from start to oxygen is $path")
                    Surface.printMap(map)
                    println("Shortest path has ${path.size} steps.")
                    fillWithOxygen(oxygen, map)
                    return 0
                }
                val shortestPathToNext: List<Pos> = findShortestPath(curPos, next!!, map)
                if (shortestPathToNext.isEmpty()) {
                    Surface.printMap(map)
                    return 0
                }
                newDesiredPos = shortestPathToNext.first()
                val direction = getDirection(curPos, newDesiredPos)
                val inp = direction
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!

                if (mode1 == 2) {
                    values[relativeBase + addr1] = inp.toBigInteger()
                } else if (mode1 == 0) {
                    values[addr1] = inp.toBigInteger()
                } else if (mode1 == 1) {
                    println("unexpected")
                }
                jumps = 2
            } else if (opCode == 4L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val output = getValue(addr1, values, mode1, relativeBase).toInt()
                if (output == 0) {
                    map[newDesiredPos] = '#'
                    explored.add(newDesiredPos)
                    frontier.remove(newDesiredPos)
                } else if (output == 1) {
                    map[newDesiredPos] = '.'
                    curPos = newDesiredPos
                } else if (output == 2) {
                    map[newDesiredPos] = 'o'
                    curPos = newDesiredPos
                    println("Found the oxygen tank! It is at $curPos")
                }
                explored.add(curPos)
                frontier.remove(curPos)
                val neighbors = openNeighbours(curPos, map)
                for (next in neighbors) {
                    if (!explored.contains(next)) {
                        frontier.add(next)
                    }
                }

                jumps = 2
            } else if (opCode == 5L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                if (a != BigInteger.valueOf(0L)) {
                    currentPos = b
                    jump = true
                }
                jumps = 3
            } else if (opCode == 6L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                if (a == BigInteger.valueOf(0L)) {
                    currentPos = b
                    jump = true
                }
                jumps = 3
            } else if (opCode == 7L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                val addr3 = values[currentPos.add(BigInteger.valueOf(3L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                var dest = addr3
                if (mode3 == 2) {
                    dest = dest + relativeBase
                }
                if (a < b) {
                    values[dest] = BigInteger.ONE
                } else {
                    values[dest] = BigInteger.ZERO
                }
            } else if (opCode == 8L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
                val addr3 = values[currentPos.add(BigInteger.valueOf(3L))]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                val b = getValue(addr2, values, mode2, relativeBase)
                var dest = addr3
                if (mode3 == 2) {
                    dest = dest + relativeBase
                }
                if (a == b) {
                    values[dest] = BigInteger.ONE
                } else {
                    values[dest] = BigInteger.ZERO
                }
            } else if (opCode == 9L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val a = getValue(addr1, values, mode1, relativeBase)
                relativeBase = relativeBase.add(a)
                jumps = 2
            } else if (opCode == 99L!!) {
                return 0L
            } else {
                println("Unknown op: $op")
                return -1
            }
            if (!jump) {
                currentPos = currentPos.plus(BigInteger.valueOf(jumps.toLong()))
            }
        }
    }

    fun getDirection(a: Pos, b: Pos): Int {
        if (a.y < b.y) {
            return 1
        } else if (a.y > b.y) {
            return 2
        } else if (a.x < b.x) {
            return 4
        } else {
            return 3
        }
    }

    fun findShortestPath(start: Pos, goal: Pos, map: MutableMap<Pos, Char>):List<Pos> {
        val opens = start.neighbours().filter { isOpen(it, map) }
        var minDist = 9999999
        var minPath = emptyList<Pos>()

        for (n in opens) {
            if (n == goal) {
                return listOf(goal, start)
            }
        }

        for (n in opens) {
            val s = findShortestPath2(n, goal, map)
            if (s.size in 1..(minDist - 1)) {
                minDist = s.size
                minPath = s
            }
        }
        return minPath
    }

    fun findShortestPath2(start: Pos, goal: Pos, map: MutableMap<Pos, Char>):List<Pos> {
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
                shortestPaths.add(reconstructPath(cameFrom, current).reversed())
            }
            open.remove(current)
            closed.add(current)

            for (pos in openNeighbours(current, map)) {
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
//            println("Size of potShortestPaths = ${potShortestPaths.size}")
        }
        val chosenShortestPath = potShortestPaths.first()
        return chosenShortestPath
    }

    fun openNeighbours(pos: Pos, map: MutableMap<Pos, Char>):Set<Pos> {
        val opens = pos.neighbours().filter { isOpen(it, map) }
        return opens.toSet()
    }

    fun h(start: Pos, end: Pos):Int = Math.abs(start.x - end.x) + Math.abs(start.y - end.y)

    fun reconstructPath(cameFrom: MutableMap<Pos, Pos>, current: Pos):List<Pos> {
        val path = mutableListOf(current)
        var cur = current
        while (cameFrom.containsKey(cur)) {
            cur = cameFrom.get(cur)!!
            path.add(cur)
        }
        return path
    }

    private fun isOpen(pos: Pos, map: MutableMap<Pos, Char>): Boolean {
        // Consider a position open if it's not a wall _and_ it has one surrounding known pos
        if (map[pos] == null) {
            val open = !pos.neighbours().map { map[it] }.filterNotNull().isEmpty()
            return open
        }
        val open = map[pos] != '#'
        return open
    }

    fun getMode(op: BigInteger, num: Int): Int {
        val chars = op.toString().toCharArray().reversed().drop(2)
        if (num - 1 >= chars.size) {
            return 0
        } else {
            return chars[num-1].toString().toInt()
        }
    }

    private fun getValue(param: BigInteger, values: MutableMap<BigInteger, BigInteger>, mode: Int, relativeBase: BigInteger): BigInteger {
        if (mode == 0) {
            if (values[param] == null) {
                return BigInteger.ZERO
            }
            return values[param]!!
        } else if (mode == 1) {
            return param
        } else {
            return values[relativeBase + param]!!
        }
    }

    fun getOpCode(pos: BigInteger): Long {
        val chars = pos.toString().toCharArray().reversed().take(2).reversed().toCharArray()
        return String(chars).toLong()
    }

    fun fillWithOxygen(start: Pos, map: MutableMap<Pos, Char>) {
        val frontier = mutableSetOf<Pos>()
        frontier.add(start)
        val visited = mutableSetOf<Pos>()
        visited.add(start)

        var minutes = 0
        while (isNotOxygenEverywhere(map)) {
            minutes++
            val oldFrontier = mutableSetOf<Pos>()
            oldFrontier.addAll(frontier)
            frontier.clear()
            for (p in oldFrontier) {
                for (next in realNeighbours(p, map)) {
                    if (!visited.contains(next)) {
                        frontier.add(next)
                        map[next] = 'o'
                        visited.add(next)
                    }
                }
            }
        }
        println("Filled with oxygen in $minutes minutes.")
    }

    fun isNotOxygenEverywhere(map: MutableMap<Pos, Char>): Boolean {
        return !map.values.none { it == '.' }
    }

    fun realNeighbours(pos: Pos, map: MutableMap<Pos, Char>):Set<Pos> {
        val opens = pos.neighbours().filter { isReallyOpen(it, map) }
        return opens.toSet()
    }

    private fun isReallyOpen(pos: Pos, map: MutableMap<Pos, Char>): Boolean {
        // Consider a position open if it's not a wall _and_ it has one surrounding known pos
        val open = map[pos] != '#' && map[pos] != null
        return open
    }
}

fun main() {
    val start = System.currentTimeMillis()
    IntCode.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
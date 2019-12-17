//package util
//
//import d15.IntCode
//import java.math.BigInteger
//
//class CrapIntCodeVm {
//
//    private fun runProgramWithInput(immutableValues: List<Long>, input: BigInteger): Long {
//        val frontier = mutableSetOf<Pos>()
//        val map = mutableMapOf<Pos, Char>()
//        val explored = mutableSetOf<Pos>()
//        map[Pos(0, 0)] = '.'
//        var curPos = Pos(0, 0)
//        frontier.addAll(IntCode.openNeighbours(curPos, map))
//        var newDesiredPos = Pos(0, 0)
//
//        var relativeBase = BigInteger.ZERO
//        val values: MutableMap<BigInteger, BigInteger> = immutableValues.toMutableList().mapIndexed { index: Int, it: Long -> BigInteger.valueOf(index.toLong()) to BigInteger.valueOf(it) }.toMap().toMutableMap()
//        var currentPos = BigInteger.ZERO
//
//        while (true) {
//            var jump = false
//            var jumps = 4
//            val op: BigInteger = values[currentPos]!!
//            val opCode = IntCode.getOpCode(op)
//            val mode1 = IntCode.getMode(op, 1)
//            val mode2 = IntCode.getMode(op, 2)
//            val mode3 = IntCode.getMode(op, 3)
//            if (opCode == 1L) {
//                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
//                var dest = values[currentPos.add(BigInteger.valueOf(3L))]!!
//                val a = IntCode.getValue(addr1, values, mode1, relativeBase)
//                val b = IntCode.getValue(addr2, values, mode2, relativeBase)
//                if (mode3 == 2) {
//                    dest = dest + relativeBase
//                }
//                values[dest] = a.add(b)
//            } else if (opCode == 2L) {
//                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
//                var dest = values[currentPos.add(BigInteger.valueOf(3L))]!!
//                val a = IntCode.getValue(addr1, values, mode1, relativeBase)
//                val b = IntCode.getValue(addr2, values, mode2, relativeBase)
//                if (mode3 == 2) {
//                    dest = dest + relativeBase
//                }
//                values[dest] = a.multiply(b)
//            } else if (opCode == 3L) {
//                val next = frontier.minBy { IntCode.h(curPos, it) }
//                if (next == null) {
//                    println("Noting more to explore")
//                    val oxygen = Pos(-14, -12)
//                    val path = IntCode.findShortestPath(Pos(0, 0), oxygen, map)
//                    println("Shortest path from start to oxygen is $path")
//                    Surface.printMap(map)
//                    println("Shortest path has ${path.size} steps.")
//                    IntCode.fillWithOxygen(oxygen, map)
//                    return 0
//                }
//                val shortestPathToNext: List<Pos> = IntCode.findShortestPath(curPos, next!!, map)
//                if (shortestPathToNext.isEmpty()) {
//                    Surface.printMap(map)
//                    return 0
//                }
//                newDesiredPos = shortestPathToNext.first()
//                val direction = IntCode.getDirection(curPos, newDesiredPos)
//                val inp = direction
//                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//
//                if (mode1 == 2) {
//                    values[relativeBase + addr1] = inp.toBigInteger()
//                } else if (mode1 == 0) {
//                    values[addr1] = inp.toBigInteger()
//                } else if (mode1 == 1) {
//                    println("unexpected")
//                }
//                jumps = 2
//            } else if (opCode == 4L) {
//                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val output = IntCode.getValue(addr1, values, mode1, relativeBase).toInt()
//                if (output == 0) {
//                    map[newDesiredPos] = '#'
//                    explored.add(newDesiredPos)
//                    frontier.remove(newDesiredPos)
//                } else if (output == 1) {
//                    map[newDesiredPos] = '.'
//                    curPos = newDesiredPos
//                } else if (output == 2) {
//                    map[newDesiredPos] = 'o'
//                    curPos = newDesiredPos
//                    println("Found the oxygen tank! It is at $curPos")
//                }
//                explored.add(curPos)
//                frontier.remove(curPos)
//                val neighbors = IntCode.openNeighbours(curPos, map)
//                for (next in neighbors) {
//                    if (!explored.contains(next)) {
//                        frontier.add(next)
//                    }
//                }
//
//                jumps = 2
//            } else if (opCode == 5L) {
//                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
//                val a = IntCode.getValue(addr1, values, mode1, relativeBase)
//                val b = IntCode.getValue(addr2, values, mode2, relativeBase)
//                if (a != BigInteger.valueOf(0L)) {
//                    currentPos = b
//                    jump = true
//                }
//                jumps = 3
//            } else if (opCode == 6L) {
//                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
//                val a = IntCode.getValue(addr1, values, mode1, relativeBase)
//                val b = IntCode.getValue(addr2, values, mode2, relativeBase)
//                if (a == BigInteger.valueOf(0L)) {
//                    currentPos = b
//                    jump = true
//                }
//                jumps = 3
//            } else if (opCode == 7L) {
//                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
//                val addr3 = values[currentPos.add(BigInteger.valueOf(3L))]!!
//                val a = IntCode.getValue(addr1, values, mode1, relativeBase)
//                val b = IntCode.getValue(addr2, values, mode2, relativeBase)
//                var dest = addr3
//                if (mode3 == 2) {
//                    dest = dest + relativeBase
//                }
//                if (a < b) {
//                    values[dest] = BigInteger.ONE
//                } else {
//                    values[dest] = BigInteger.ZERO
//                }
//            } else if (opCode == 8L) {
//                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val addr2 = values[currentPos.add(BigInteger.valueOf(2L))]!!
//                val addr3 = values[currentPos.add(BigInteger.valueOf(3L))]!!
//                val a = IntCode.getValue(addr1, values, mode1, relativeBase)
//                val b = IntCode.getValue(addr2, values, mode2, relativeBase)
//                var dest = addr3
//                if (mode3 == 2) {
//                    dest = dest + relativeBase
//                }
//                if (a == b) {
//                    values[dest] = BigInteger.ONE
//                } else {
//                    values[dest] = BigInteger.ZERO
//                }
//            } else if (opCode == 9L) {
//                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
//                val a = IntCode.getValue(addr1, values, mode1, relativeBase)
//                relativeBase = relativeBase.add(a)
//                jumps = 2
//            } else if (opCode == 99L!!) {
//                return 0L
//            } else {
//                println("Unknown op: $op")
//                return -1
//            }
//            if (!jump) {
//                currentPos = currentPos.plus(BigInteger.valueOf(jumps.toLong()))
//            }
//        }
//    }
//
//}
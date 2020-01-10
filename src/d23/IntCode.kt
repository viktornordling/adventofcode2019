package d23


import util.Reader
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

data class Packet(val x: BigInteger, val y: BigInteger)

object IntCode {

    fun solve() {
        val input = Reader.readInput("input.txt")
        val values = input.flatMap { it.split(",").map { it.toLong() } }

        val inputs = ConcurrentHashMap<Int, ConcurrentLinkedQueue<Packet>>()

        for (i in 0..49) {
            inputs[i] = ConcurrentLinkedQueue()
        }
        inputs[255] = ConcurrentLinkedQueue()

        for (i in 0..49) {
            thread(start = true) {
                runProgramWithInput(values, i, inputs[i]!!, inputs)
            }
        }

        val seenNatYs = mutableSetOf<BigInteger>()

        thread {
            while (true) {
                Thread.sleep(1000)
                println("NAT is checking if all queues are empty.")
                var allEmpty = true
                for (i in 0..49) {
                    if (!inputs[i]!!.isEmpty()) {
                        allEmpty = false
                    }
                }
                if (allEmpty) {
                    println("ALL QUEUES ARE EMPTY")
                    if (!inputs[255]!!.isEmpty()) {
                        val natPacket = inputs[255]!!.poll()
                        println("NAT is sending $natPacket to id 0")
                        if (seenNatYs.contains(natPacket.y)) {
                            println("ALREADY SEEN Y: ${natPacket.y}")
                        }
                        seenNatYs.add(natPacket.y)
                        inputs[0]!!.add(natPacket)
                    }
                } else {
                    println("There are non empty queues")
                }
            }
        }

    }

    private fun runProgramWithInput(
        immutableValues: List<Long>,
        firstInput: Int,
        inputQueue: ConcurrentLinkedQueue<Packet>,
        outputs: ConcurrentHashMap<Int, ConcurrentLinkedQueue<Packet>>
    ): Long {
        var relativeBase = BigInteger.ZERO
        val values: MutableMap<BigInteger, BigInteger> = immutableValues.toMutableList().mapIndexed { index: Int, it: Long -> BigInteger.valueOf(index.toLong()) to BigInteger.valueOf(it) }.toMap().toMutableMap()
        var currentPos = BigInteger.ZERO

        var curPacket: Packet? = null
        var firstPartOfPacket = true
        var idReceived = false

        var firstOutput = true
        var secondOutput = true
        var thirdOutput = true
        var outId = -1
        var outX = BigInteger("-1")

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
                if (!inputQueue.isEmpty() && curPacket == null) {
                    curPacket = inputQueue.poll()
                }
                var inp: BigInteger
                if (!idReceived) {
                    inp = firstInput.toBigInteger()
                    idReceived = true
                } else if (curPacket != null && firstPartOfPacket) {
                    firstPartOfPacket = false
                    inp = curPacket.x
                } else if (curPacket != null && !firstPartOfPacket) {
                    firstPartOfPacket = true
                    inp = curPacket.y
                    println("Id $firstInput received packet: $curPacket")
                    curPacket = null
                } else {
                    inp = BigInteger("-1")
                }
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                if (mode1 == 2) {
                    values[relativeBase + addr1] = inp
                } else if (mode1 == 0) {
                    values[addr1] = inp
                } else if (mode1 == 1) {
                    println("unexpected")
                }
                jumps = 2
            } else if (opCode == 4L) {
                val addr1 = values[currentPos.add(BigInteger.ONE)]!!
                val output = getValue(addr1, values, mode1, relativeBase)
                if (firstOutput) {
                    outId = output.toInt()
                    firstOutput = false
                    secondOutput = true
                } else if (secondOutput) {
                    outX = output
                    secondOutput = false
                    thirdOutput = true
                } else if (thirdOutput) {
                    val outY = output
                    val packet = Packet(outX, outY)
                    println("Id $firstInput is sending a packet to id $outId: $packet")
                    if (outId == 255) {
                        outputs[255]!!.clear()
                    }
                    outputs[outId]!!.add(packet)
                    println("Queue is now: ${outputs[outId]}")
                    firstOutput = true
                    thirdOutput = false
                } else {
                    throw IllegalArgumentException("Expected to be in one of the output states.")
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
            if (values[relativeBase + param] == null) {
                return BigInteger.ZERO
            }
            return values[relativeBase + param]!!
        }
    }

    fun getOpCode(pos: BigInteger): Long {
        val chars = pos.toString().toCharArray().reversed().take(2).reversed().toCharArray()
        return String(chars).toLong()
    }


}

fun main() {
    val start = System.currentTimeMillis()

    IntCode.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}

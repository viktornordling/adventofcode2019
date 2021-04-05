package d22

import java.math.BigInteger

object Solution {

    fun solve() {
        val input = """
deal into new stack
cut -2732
deal into new stack
deal with increment 57
cut 5974
deal into new stack
deal with increment 32
cut -1725
deal with increment 24
cut 6093
deal with increment 6
cut -2842
deal with increment 14
cut 2609
deal with increment 12
cut -6860
deal with increment 51
cut -6230
deal with increment 61
cut 3152
deal with increment 28
cut 2202
deal into new stack
deal with increment 60
cut 433
deal into new stack
cut -6256
deal with increment 13
deal into new stack
cut 8379
deal into new stack
deal with increment 54
cut 1120
deal with increment 16
cut -5214
deal with increment 63
deal into new stack
cut -8473
deal with increment 11
cut 228
deal with increment 45
cut -6755
deal with increment 50
cut -3391
deal with increment 44
cut -1341
deal with increment 28
cut -6788
deal with increment 52
cut 3062
deal with increment 41
cut 4541
deal with increment 57
cut -7962
deal with increment 56
cut 9621
deal with increment 57
cut 3881
deal with increment 36
deal into new stack
deal with increment 45
cut 522
deal with increment 9
deal into new stack
deal with increment 60
deal into new stack
deal with increment 12
cut -9181
deal with increment 63
deal into new stack
deal with increment 14
cut -2906
deal with increment 10
cut 848
deal with increment 75
cut 798
deal with increment 29
cut 1412
deal with increment 10
deal into new stack
cut -5295
deal into new stack
cut 4432
deal with increment 72
cut -7831
deal into new stack
cut 6216
deal into new stack
deal with increment 7
cut -1720
deal into new stack
cut -5465
deal with increment 70
cut -5173
deal with increment 7
cut 3874
deal with increment 65
cut 921
deal with increment 8
cut -3094
        """.trimIndent()
        val deckSize = BigInteger("119315717514047")
        val shuffles = input.lines()
//        val cardWeAreLookingFor = 2019
//        for (i in 0 until deckSize) {
//        var posWeCareAbout = 2020.toBigInteger()
//        val seenPositions = mutableSetOf<BigInteger>()
        var increment = BigInteger.ONE
        var offset = BigInteger.ZERO
        for (shuffle in shuffles) {
            when {
                shuffle.startsWith("deal with increment") -> {
                    val dealIncrement = shuffle.split(" ").last().toBigInteger()
                    increment *= inv(dealIncrement, deckSize)
                    increment %= deckSize
//                        posWeCareAbout = cardInPositionBeforeDeal(posWeCareAbout, increment, deckSize)
                }
                shuffle.startsWith("deal into new stack") -> {
                    increment *= BigInteger.valueOf(-1)
                    offset += increment
                    increment %= deckSize
                    offset %= deckSize
//                        posWeCareAbout = cardInPositionBeforeDealingIntoNewStack(posWeCareAbout, deckSize)
                }
                shuffle.startsWith("cut") -> {
                    val cutPos = shuffle.split(" ").last().toBigInteger()
                    offset += increment * cutPos
                    offset %= deckSize
//                        posWeCareAbout = cardInPositionBeforeCut(posWeCareAbout, cutPos, deckSize)
                }
            }
        }
        // After one shuffle, the increment and offset are known. After 101741582076661 shuffles, the increment will be
        val numShuffles = BigInteger("101741582076661")
        val oneShuffleIncrement = increment
        val oneShuffleOffset = offset
        increment = oneShuffleIncrement.modPow(numShuffles, deckSize)
        offset = oneShuffleOffset * (BigInteger.ONE - oneShuffleIncrement.modPow(numShuffles, deckSize)) * inv(BigInteger.ONE - oneShuffleIncrement, deckSize)

//            val beforeDeal = cardInPositionBeforeDeal(posWeCareAbout, start, deckSize)
//            println("Start pos: $start, end pos: $posWeCareAbout, diff: ${posWeCareAbout - start}, beforeDeal = $beforeDeal")
//            if (seenPositions.contains(posWeCareAbout)) {
//                println("Already seen $posWeCareAbout. Iterations: $i")
//                return
//            }

//            seenPositions.add(posWeCareAbout)

        if (offset < BigInteger.ZERO) {
            offset += deckSize
        }
        var curCard = offset
        for (i in 0..2020) {
            if (i == 2020) {
                print("Part 2: $curCard")
                return
            }
            curCard += increment
            curCard %= deckSize
            if (curCard < BigInteger.ZERO) {
                curCard += deckSize
            }
        }
    }

    private fun cardInPositionBeforeDealingIntoNewStack(pos: BigInteger, deckSize: BigInteger) = deckSize - pos - BigInteger.ONE

    private fun cardInPositionBeforeCut(pos: BigInteger, cutPos: BigInteger, deckSize: BigInteger): BigInteger {
        return when {
            cutPos > BigInteger.ZERO -> (pos + cutPos) % deckSize
            cutPos < BigInteger.ZERO -> (pos + cutPos + deckSize) % deckSize
            else -> throw IllegalArgumentException("Bad cutPos: $cutPos")
        }
    }

    private fun inv(n: BigInteger, deckSize: BigInteger): BigInteger {
        return n.modInverse(deckSize)
    }

    private fun cardInPositionBeforeDeal(pos: BigInteger, increment: BigInteger, deckSize: BigInteger): BigInteger {
        // (x * a) % b = c
        // 4 * 3 % 10 = 2
        val (s, _) = extendedGcd(increment, deckSize)
        val r = (pos * s) % deckSize
        return when {
            r < 0.toBigInteger() -> r + deckSize
            else -> r
        }
    }

    private fun extendedGcd(a: BigInteger, b: BigInteger): Pair<BigInteger, BigInteger> {
        var s = BigInteger.ZERO
        var t = BigInteger.ONE
        var oldS = BigInteger.ONE
        var oldT = BigInteger.ZERO
        var r = b
        var oldR = a

        while (r != 0.toBigInteger()) {
            val quotient = oldR / r

            val tempOldR = r
            r = oldR - quotient * r
            oldR = tempOldR

            val tempOldS = s
            s = oldS - quotient * s
            oldS = tempOldS

            val tempOldT = t
            t = oldT - quotient * t
            oldT = tempOldT
        }
        return Pair(oldS, oldT)
    }
}

fun main() {
    Solution.solve()
}
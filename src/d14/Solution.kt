package d14

import util.Reader
import java.math.BigInteger
import java.math.RoundingMode

data class Quantity(val element:String, val quantity: Int)
data class Reaction(val inputs: List<Quantity>, val output: Quantity)
data class CheapestReaction(val totalCostInOre: Double, val reaction: Reaction)

object Solution {
    fun solve() {
        val input = Reader.readInput("easy.txt")
        val reactions = input.map { parse(it) }

        // For each element, find the cheapest reaction for producing that element, in terms of total ORE input
        val elements: List<String> = reactions.flatMap { it.inputs.map { it.element }} + "FUEL"
        val elementToReactions = reactions.groupBy { it.output.element }
        val cheapestReactions: Map<String, CheapestReaction> = elements.map { it to findCheapestReaction(it, elementToReactions) }.toMap()
        var low =  3000099.toBigInteger()
        var high = 4000099.toBigInteger()
        val maxOre = BigInteger("1000000000000")
        var count = 0
        while (low < high) {
            val middle = low + (high - low) / 2.toBigInteger()
            val calcElementAmountsNeeded = calcElementAmountsNeeded(cheapestReactions, middle)
            if (calcElementAmountsNeeded > maxOre) {
                high = middle
            } else {
                low = middle
            }
            count++
            if (count == 1000) {
                println("low $low high $high")
                return
            }
        }
    }

    private fun calcElementAmountsNeeded(cheapestReactions: Map<String, CheapestReaction>, fuel: BigInteger): BigInteger {
        val elementsToProduce = mutableMapOf<String, BigInteger>()
        elementsToProduce["FUEL"] = fuel
        val availableElements = mutableMapOf<String, BigInteger>()
        var oreUsed = BigInteger.ZERO
        while (!elementsToProduce.isEmpty()) {
            val element = elementsToProduce.keys.first()
            val reaction = cheapestReactions[element]!!
            val amountAvailable = availableElements[element] ?: 0.toBigInteger()
            val amountWanted = elementsToProduce[element] !!
            val amountToProduce = amountWanted - amountAvailable
            if (amountToProduce <= 0.toBigInteger()) {
                availableElements[element] = amountAvailable - amountWanted
            } else {
                val a = amountToProduce.toBigDecimal()
                val q = reaction.reaction.output.quantity.toBigInteger().toBigDecimal()
                val runsNeeded = a.divide(q, RoundingMode.UP).toBigInteger()
                if (reaction.reaction.inputs.size == 1 && reaction.reaction.inputs[0].element == "ORE") {
                    oreUsed += reaction.reaction.inputs[0].quantity.toBigInteger() * runsNeeded
                } else {
                    for (r in reaction.reaction.inputs) {
                        val curElementToProduce = (elementsToProduce[r.element] ?: 0.toBigInteger())
                        val neededInThisRun = r.quantity.toBigInteger() * runsNeeded
                        elementsToProduce[r.element] = curElementToProduce + neededInThisRun
                    }
                }
                val newAvailable = amountAvailable + reaction.reaction.output.quantity.toBigInteger() * runsNeeded
                availableElements[element] = newAvailable

                val new = (availableElements[element] ?: 0.toBigInteger()) - amountWanted
                availableElements[element] = new
            }
            elementsToProduce.remove(element)
        }
        println("Total ore used $oreUsed")
        return oreUsed
    }

    fun findCheapestReaction(it: String, allReactions: Map<String, List<Reaction>>): CheapestReaction {
        val reactions:List<Reaction> = allReactions[it] ?: emptyList()
        if (reactions.isEmpty()) {
            return CheapestReaction(1.0, Reaction(listOf(Quantity("ORE", 1)), Quantity("ORE", 1)))
        }
        val cheapestReaction = reactions.minBy { costOfReaction(it, allReactions) }!!
        return CheapestReaction(costOfReaction(cheapestReaction, allReactions), cheapestReaction)
    }

    private fun costOfReaction(reaction: Reaction, allReactions: Map<String, List<Reaction>>): Double {
        if (reaction.inputs.size == 1 && reaction.inputs[0].element == "ORE") {
            return reaction.inputs[0].quantity / reaction.inputs[0].quantity.toDouble()
        }
        return reaction.inputs.map { findCheapestReaction(it.element, allReactions).totalCostInOre * it.quantity }.sum()
    }

    fun parse(it: String): Reaction {
        val inOut = it.split("=>")
        val ins = inOut[0].split(",")
        val inQuants: List<Quantity> = ins.map { toQuant(it) }
        val outQuant = toQuant(inOut[1])
        return Reaction(inQuants, outQuant)
    }

    private fun toQuant(it: String): Quantity {
        val nameAmount = it.trim().split(" ")
        return Quantity(nameAmount[1], nameAmount[0].toInt())
    }
}

fun main() {
    val start = System.currentTimeMillis()

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
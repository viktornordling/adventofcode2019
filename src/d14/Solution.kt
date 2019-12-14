package d14

import util.Reader
import java.math.BigInteger

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
        calcElementAmountsNeeded(cheapestReactions)
    }

    private fun calcElementAmountsNeeded(cheapestReactions: Map<String, CheapestReaction>) {
        val elementsToProduce = mutableMapOf<String, BigInteger>()
        elementsToProduce["FUEL"] = 400000.toBigInteger()
        val availableElements = mutableMapOf<String, BigInteger>()
        var oreUsed = BigInteger.ZERO
        while (!elementsToProduce.isEmpty()) {
            val element = elementsToProduce.keys.first()
            val reaction = cheapestReactions[element]!!
            val amountToProduce = elementsToProduce[element]!!
//            println("We need $amountToProduce $element")
            var amountAvailable = availableElements[element] ?: 0.toBigInteger()
            if (amountAvailable >= amountToProduce) {
//                println("Not running reaction, we already have enough")
                availableElements[element] = (amountAvailable - amountToProduce)
            } else {
                while (amountAvailable < amountToProduce) {
//                    println("Running reaction ${reaction.reaction}")
                    if (reaction.reaction.inputs.size == 1 && reaction.reaction.inputs[0].element == "ORE") {
//                        println("Running raw reaction, consuming ${reaction.reaction.inputs[0].quantity} ORE")
                        oreUsed += reaction.reaction.inputs[0].quantity.toBigInteger()
                    } else {
                        for (r in reaction.reaction.inputs) {
                            val new = (elementsToProduce[r.element] ?: 0.toBigInteger()) + r.quantity.toBigInteger()
                            elementsToProduce[r.element] = new
                        }
                    }
                    val new = (availableElements[reaction.reaction.output.element] ?: 0.toBigInteger()) + reaction.reaction.output.quantity.toBigInteger()
                    availableElements[reaction.reaction.output.element] = new
                    amountAvailable += reaction.reaction.output.quantity.toBigInteger()
                }
                val new = (availableElements[element] ?: 0.toBigInteger()) - amountToProduce
                availableElements[element] = new
            }
            elementsToProduce.remove(element)
        }
        println("Total ore used $oreUsed")
    }

    fun findCheapestReaction(it: String, allReactions: Map<String, List<Reaction>>): CheapestReaction {
        val reactions:List<Reaction> = allReactions[it] ?: emptyList()
        if (reactions.isEmpty()) {
            return CheapestReaction(1.0, Reaction(listOf(Quantity("ORE", 1)), Quantity("ORE", 1)))
        }
        val costs = reactions.map { costOfReaction(it, allReactions) }
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
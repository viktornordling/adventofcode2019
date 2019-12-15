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
        var oreNeeded = BigInteger("1000000000000")
        var count = 0
        while (low < high) {
            val middle = low + (high - low) / 2.toBigInteger()
            val calcElementAmountsNeeded = calcElementAmountsNeeded(cheapestReactions, middle)
//            println("Fuel needed for $middle: $calcElementAmountsNeeded")
            if (calcElementAmountsNeeded > oreNeeded) {
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
//            println(elementsToProduce)
            val element = elementsToProduce.keys.first()
            val reaction = cheapestReactions[element]!!
            val amountAvailable = availableElements[element] ?: 0.toBigInteger()
            val amountWanted = elementsToProduce[element] !!
            val amountToProduce = amountWanted - amountAvailable
//            println("We need $amountToProduce $element")
            if (amountToProduce <= 0.toBigInteger()) {
//                println("Not running reaction, we already have enough")
                availableElements[element] = amountAvailable - amountWanted
            } else {
                val a = amountToProduce.toBigDecimal()
                val q = reaction.reaction.output.quantity.toBigInteger().toBigDecimal()
//                println("Calculating $a / $q")
                val runsNeeded = a.divide(q, RoundingMode.UP).toBigInteger()
//                while (amountAvailable < amountToProduce) {
//                println("Running reaction ${reaction.reaction} $runsNeeded times")
                if (reaction.reaction.inputs.size == 1 && reaction.reaction.inputs[0].element == "ORE") {
//                    println("Using ${reaction.reaction.inputs[0].quantity.toBigInteger() * runsNeeded} ore")
                    oreUsed += reaction.reaction.inputs[0].quantity.toBigInteger() * runsNeeded
                } else {
                    for (r in reaction.reaction.inputs) {
                        val curElementToProduce = (elementsToProduce[r.element] ?: 0.toBigInteger())
                        val neededInThisRun = r.quantity.toBigInteger() * runsNeeded
//                            val new = (elementsToProduce[r.element] ?: 0.toBigInteger()) + r.quantity.toBigInteger()
                        elementsToProduce[r.element] = curElementToProduce + neededInThisRun
                    }
//                    val newA = (availableElements[reaction.reaction.output.element] ?: 0.toBigInteger()) + reaction.reaction.output.quantity.toBigInteger() * runsNeeded
//                    amountAvailable += reaction.reaction.output.quantity.toBigInteger()
//                }
                }
                val newAvailable = amountAvailable + reaction.reaction.output.quantity.toBigInteger() * runsNeeded
//                println("Adding ${reaction.reaction.output.quantity.toBigInteger() * runsNeeded} $element")
                availableElements[element] = newAvailable
//                println("Available $availableElements")

//                println("Using $amountWanted $element")
                val new = (availableElements[element] ?: 0.toBigInteger()) - amountWanted
                availableElements[element] = new
//                println("Available: $availableElements")
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
//    val a = 37.toBigDecimal()
//    val q = 5.toBigDecimal()
//    val needed = (a.divide(q, RoundingMode.UP))
//    println("a = $a q = $q needed: $needed")

    Solution.solve()
    println("Millis taken: ${System.currentTimeMillis() - start}")
}
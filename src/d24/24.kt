package d24

import util.Reader.readInput

class NumUnits(var num: Int) {
    override fun toString(): String {
        return "numUnits: $num"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is NumUnits) {
            other.num == num
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return num.hashCode()
    }
}

data class Group(
    val id: Int,
    val numUnits: NumUnits,
    val hitPoints: Int,
    val weaknesses: Set<String>,
    val immuneTo: Set<String>,
    val attackPower: Int,
    val damageType: String,
    val initiative: Int,
    val goodGuys: Boolean
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Group) {
            other.id == id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

object Solution {
    fun solve() {

        fun isAlive(team: Set<Group>) = !team.isEmpty()

        fun extractWeaknesses(string: String): Set<String> {
            if (!string.contains("weak to")) {
                return emptySet()
            }
            val s = string.substringAfter("weak to")
            val endIndex = s.indexOfAny(charArrayOf(';', ')'))
            return s.substring(0, endIndex).split(", ").map { it.trim() }.toSet()
        }

        fun extractImmuneTo(string: String): Set<String> {
            if (!string.contains("immune to")) {
                return emptySet()
            }
            val s = string.substringAfter("immune to")
            val endIndex = s.indexOfAny(charArrayOf(';', ')'))
            return s.substring(0, endIndex).split(", ").map { it.trim() }.toSet()
        }

        fun effectivePower(group: Group) = group.attackPower * group.numUnits.num

        fun damageDealt(group1: Group, group2: Group): Int {
            val effectivePower = effectivePower(group1)
            return when {
                group2.immuneTo.contains(group1.damageType) -> 0
                group2.weaknesses.contains(group1.damageType) -> effectivePower * 2
                else -> effectivePower
            }
        }

        fun opponents(group: Group, team1: Set<Group>, team2: Set<Group>) = if (group.goodGuys) team2 else team1

        fun selectTarget(group: Group, opponents: Set<Group>): Group? {
            val target = opponents.sortedWith(compareBy({ damageDealt(group, it) }, {effectivePower(it)}, {it.initiative})).reversed().firstOrNull()
            if (target == null) {
                return null
            } else if (damageDealt(group, target) == 0) {
                return null
            } else {
                return target
            }
        }

        var count = 0

        fun parse(string: String, goodGuys: Boolean): Group {
            // 479 units each with 3393 hit points (weak to radiation) with an attack that does 66 cold damage at initiative 8
            // 1827 units each with 5107 hit points with an attack that does 24 slashing damage at initiative 18
            val numUnits = string.substringBefore("units").trim().toInt()
            val hitPoints = string.substringAfter("with").substringBefore("hit").trim().toInt()

            val weaknesses = extractWeaknesses(string)
            val immuneTo = extractImmuneTo(string)

            val boost = 42

            val attackPower = string.substringAfter("does ").split(" ").first().trim().toInt()
            val finalAttack =  if (goodGuys) attackPower + boost else attackPower
            val damageType = string.substringBefore(" damage").split(" ").last()
            val initiative = string.split(" ").last().toInt()
            return Group(count++, NumUnits(numUnits), hitPoints, weaknesses, immuneTo, finalAttack, damageType, initiative, goodGuys)
        }

//        val input1 = readInput("easy_immune.txt")
//        val input2 = readInput("easy_infection.txt")

        val input1 = readInput("input_immune.txt")
        val input2 = readInput("input_infection.txt")

        val aTeam = input1.map { parse(it, true) }.toMutableSet()
        val bTeam = input2.map { parse(it, false) }.toMutableSet()

        println(aTeam)
        println(bTeam)

        while (isAlive(aTeam) && isAlive(bTeam)) {
            val selectedTargets = mutableMapOf<Group, Group?>()
            val possibleTargets = (aTeam + bTeam).toMutableSet()
            val aList = aTeam.sortedWith(compareBy( { effectivePower(it) }, { it.initiative } ) ).reversed()
            for (group in aList) {
                val target = selectTarget(group, opponents(group, aTeam, bTeam).intersect(possibleTargets))
                selectedTargets[group] = target
                possibleTargets.remove(target)
            }

            val list = bTeam.sortedWith(compareBy( { effectivePower(it) }, { it.initiative } ) ).reversed()
            for (group in list) {
                val target = selectTarget(group, opponents(group, aTeam, bTeam).intersect(possibleTargets))
                selectedTargets[group] = target
                possibleTargets.remove(target)
            }
//            println("selected targets:")
//            println(selectedTargets.entries.joinToString("\n"))
//            println("DONE selecting targets:")

            for (group in (aTeam + bTeam).sortedBy { it.initiative }.reversed()) {
                if (group.numUnits.num <= 0) {
                    continue
                }
                val target = selectedTargets[group]
                if (target != null) {
                    val damage = damageDealt(group, target)
                    val unitsLost = damage / target.hitPoints
                    target.numUnits.num -= unitsLost
                    if (target.numUnits.num <= 0) {
                        aTeam.remove(target)
                        bTeam.remove(target)
                    }
                }
            }
        }

        val remainingHealth = aTeam.map { it.numUnits.num }.sum() + bTeam.map { it.numUnits.num }.sum()
        println("aTeam: ${aTeam.size}, bTeam: $bTeam")
        println("Remaining health: $remainingHealth")
        //val input = readInput("input.txt")
    }
}

fun main(args: Array<String>) {
    Solution.solve()
}
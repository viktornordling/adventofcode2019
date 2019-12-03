val recipieCount = 9
val recipes = mutableListOf(3, 7)
var elf1Index = 0
var elf2Index = 1
//val targetList = listOf(5, 1, 5, 8, 9)
//val targetList = listOf(5,9,4,1,4)
//val targetList = listOf(9,2,5,1,0)
val targetList = listOf(0,4,7,8,0,1)
var found = false

do {
    val newRecipes = (recipes[elf1Index] + recipes[elf2Index]).toString().map { it.toString().toInt() }
    recipes.addAll(newRecipes)
    val newElf1Index = (elf1Index + recipes.get(elf1Index) + 1) % recipes.size
    val newElf2Index = (elf2Index + recipes.get(elf2Index) + 1) % recipes.size
    elf1Index = newElf1Index
    elf2Index = newElf2Index
    var tailList: List<Int>
    if (recipes.size > 7) {
        tailList = recipes.takeLast(7)
        if (tailList.take(6) == targetList) {
            println("FOUND!")
            println(recipes.size - 7)
            found = true
        } else if (tailList.takeLast(6) == targetList) {
            println("Found!")
            println(recipes.size - 6)
            found = true
        }
    }
} while (!found)

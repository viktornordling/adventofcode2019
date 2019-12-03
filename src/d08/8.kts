import java.io.File
import java.util.Date
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.util.Stack

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList

//var inputStream = FileInputStream(File("easy.txt"))
var inputStream = FileInputStream(File("input.txt"))
System.setIn(inputStream);
val lines = readInput(emptyList())

val tokens = lines.first().split(" ").map { it.toInt() }

data class Node(val children:List<Node>, val metadata:List<Int>)

fun parseChildren(numChildren:Int, tokens:List<Int>): Pair<List<Node>, List<Int>> {
    if (numChildren == 0) {
        return Pair(emptyList(), tokens)
    }
    val numSubChildren = tokens.first()
    val numMeta = tokens.drop(1).first()
    val children = parseChildren(numSubChildren, tokens.drop(2))
    val metadata = children.second.take(numMeta)
    val node = Node(children.first, metadata)
    val moreChildren = parseChildren(numChildren - 1, children.second.drop(numMeta))
    return Pair(listOf(node) + moreChildren.first, moreChildren.second)
}

fun sumMeta(node:Node):Int {
    return node.metadata.sum() + node.children.map { sumMeta(it) }.sum()
}

fun sumMetaPartTwo(node:Node):Int {
    if (node.children.isEmpty()) {
        return node.metadata.sum()
    } else {
        var sum = 0
        for (meta in node.metadata) {
            if (node.children.size >= meta && meta != 0) {
                sum += sumMetaPartTwo(node.children.get(meta - 1))
            }
        }
        return sum
    }
}

val root = parseChildren(1, tokens).first.first()
val sum = sumMeta(root)
println(sum)
println(sumMetaPartTwo(root))
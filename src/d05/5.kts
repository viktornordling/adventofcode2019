import java.io.File
import java.util.Date
import java.io.FileInputStream

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList

var inputStream = FileInputStream(File("input5.txt"))
//var inputStream = FileInputStream(File("easy5.txt"))
System.setIn(inputStream);
val lines = readInput(emptyList())
var line = lines.first()

fun reduce(line: String): String {
    var newString = mutableListOf<Char>()
    var index = 0
    while (index < line.length) {
        val c = line[index]
        if (index == line.length - 1) {
            newString.add(c)
            index++
            continue
        }
        var next = line[index+1]
        var keep = true
        if (c.toLowerCase() == next.toLowerCase()) {
            if ((c.isUpperCase() && !next.isUpperCase()) || (next.isUpperCase() && !c.isUpperCase())) {
                // poof
                keep = false
                index++
            }
        }
        if (keep) {
            newString.add(c)
        }
        index++
    }
    return String(newString.toCharArray())
}

val sizes = mutableListOf<Int>()
val orgLine = line
for (c in 'a'..'z') {
    var cleanedLine = orgLine.replace(c.toString(), "", true)
    do {
        val oldLine = cleanedLine
        cleanedLine = reduce(cleanedLine)
    //    println("new " + line)
    } while (oldLine != cleanedLine)
    println("char removed: " + c)
    println("Sie: " + cleanedLine.length)
    sizes.add(cleanedLine.length)
}

println(sizes.min())

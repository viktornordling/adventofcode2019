import java.io.File
import java.util.Date
import java.io.FileInputStream
import java.lang.IllegalArgumentException

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList

var inputStream = FileInputStream(File("input.txt"))
System.setIn(inputStream);
val lines = readInput(emptyList())

data class Dep(val from:Char, val to:Char, val children:List<Char>, val parents:List<Char>)

fun getDep(line:String):Dep {
    val from = line.drop(5).take(1).toCharArray().first()
    val to = line.drop(36).take(1).toCharArray().first()
    return Dep(from, to, emptyList(), emptyList())
}

fun findRoots(deps:List<Dep>):List<Char> {
    val allChars = deps.flatMap { dep -> setOf(dep.from, dep.to)}.toSet()
    val allDependedOn = deps.map { dep -> dep.to }.toSet()
    val single = allChars - allDependedOn
    return single.sorted()
}

val deps: List<Dep> = lines.map { getDep(it) }
val roots = findRoots(deps)

val depsWithChildren = mutableMapOf<Char, Dep>()
for (dep in deps) {
    depsWithChildren.put(dep.from, dep)
}

for (dep in deps) {
    if (!depsWithChildren.containsKey(dep.to)) {
        depsWithChildren.put(dep.to, dep)
    }
}

for (dep in deps) {
    val fromDep = depsWithChildren.get(dep.from)!!
    val depList = fromDep.children
    val newDepList = depList + dep.to
    depsWithChildren.put(fromDep.from, fromDep.copy(children = newDepList))
}

for (dep in deps) {
    val toDep = depsWithChildren.get(dep.to)
    if (toDep == null) {
        println("No to dep for " + dep.to)
        continue
    }
    val depList = toDep.parents
    val newDepList = depList + dep.from
    depsWithChildren.put(dep.to, toDep.copy(parents = newDepList))
}


println(depsWithChildren)
data class FinishTime(val time:Int, val char:Char)

fun printDepOrder() {
    var curTime = 0
    val finishTimes = mutableListOf<FinishTime>()
    val available = mutableSetOf<Char>()
    val done = mutableSetOf<Char>()
    available.addAll(roots)
    while (!available.isEmpty()) {
        val reallAvailable = available - finishTimes.map { it.char }
        val doNow = reallAvailable.sorted().take(5-finishTimes.size)
        for (c in doNow) {
            val finishTime = FinishTime(curTime + 60 + (c - 'A').toInt(), c)
            finishTimes.add(finishTime)
        }
        println("finish times: " + finishTimes)
        println("Doing $doNow")
        // get the lowest time, it will be done now
        val lowestTime = finishTimes.minBy { it.time }!!.time
        val next = finishTimes.filter { it.time == lowestTime }
        println("next = " + next)
        finishTimes.removeAll(next)
        done.addAll(next.map { it.char })
        curTime = lowestTime + 1
        for (finishTime in next) {
            val dependency = depsWithChildren.get(finishTime.char)
            if (dependency != null) {
                for (char in dependency.children) {
                    val subDependency = depsWithChildren.get(char)
                    if (subDependency != null) {
                        if (done.containsAll(subDependency.parents)) {
                            available.add(char)
                        }
                    }
                }
            }
            available.remove(finishTime.char)
        }
    }
    print("all done, curtime = $curTime")
    print(finishTimes)
}
printDepOrder()
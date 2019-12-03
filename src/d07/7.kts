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

fun printDepOrder() {
    val available = mutableSetOf<Char>()
    val done = mutableSetOf<Char>()
    available.addAll(roots)
    while (!available.isEmpty()) {
        val doNow = available.sorted().first()
        done.add(doNow)
        print(doNow)
        val dependency = depsWithChildren.get(doNow)
        if (dependency != null) {
            for (c in dependency.children) {
                val dd = depsWithChildren.get(c)
                if (dd != null) {
                    if (done.containsAll(dd.parents)) {
                        available.add(c)
                    }
                }
            }
        }
        available.remove(doNow)
    }
}
printDepOrder()
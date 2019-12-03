import java.io.File
import java.util.Date
import java.io.FileInputStream

fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList
data class SleepTime(val date: Date, val month: Int, val day: Int, val hour: Int, val minute: Int, val message: String, var guardId: Int)

fun parseLine(line: String):SleepTime {
    //[1518-11-01 00:00] Guard #10 begins shift
    //[1518-11-01 00:05] falls asleep
    //[1518-11-01 00:25] wakes up
    //[1518-11-01 00:30] falls asleep
    val datePart = line.take(18).removeSurrounding("[]")
    val dateString = datePart.split(" ")[0]
    val month = dateString.split("-")[1]
    val day = dateString.split("-")[2]
    val hourMinutePart = datePart.split(" ")[1].removeSuffix("]")
    val hour = hourMinutePart.split(":")[0]
    val minute = hourMinutePart.split(":")[1]
    val message = line.drop(19)
    val date = Date(1508, month.toInt(), day.toInt(), hour.toInt(), minute.toInt())
    val s = SleepTime(date, month.toInt(), day.toInt(), hour.toInt(), minute.toInt(), message, -1)
    //println(s)
    return s
}

data class TotalSleep(val guardId:Int, val totalSleep:Int, val mostAsleepMinute:Int, val mostAsleepMinCount:Int)

fun sleepingInMinute(minute:Int, sleepList:List<BooleanArray>):Int {
    if (minute == -1) {
        return 0
    }
    var count = 0
    for (s in sleepList) {
        if (s[minute]) {
            count++
        }
    }
    return count
}

fun mostAsleepMinute(sleepList:List<BooleanArray>):Int {
    var maxSleep = 0
    var maxMinute = -1
    for (minute in 0 .. 59) {
        val timesSleeping = sleepingInMinute(minute, sleepList)
        if (timesSleeping > maxSleep) {
            maxSleep = timesSleeping
            maxMinute = minute
        }
    }
    return maxMinute
}

fun totalSleepTime(things: List<SleepTime>):TotalSleep {
    var thangs = things.sortedBy { it.date }
    var totalSleep:Int = 0
    var fell:Int = -1
    var asleep = false
    var guardId:Int = 0

    val sleepLists = mutableListOf<BooleanArray>()

    // As we go through the sleeps, for each day, mark all asleep minutes
    var asleepMinutes = BooleanArray(60)
    var curDayAndMonth = "0_0"
    for (s in thangs) {
//        println("Sleeptime: $s")
        val dayAndMoth = "${s.month}_${s.day}"
        if (curDayAndMonth != dayAndMoth) {
            if (curDayAndMonth != "0_0") {
//                println(s.date)
//                println(s.guardId)
//                println(asleepMinutes.map { if (it) "#" else "." }.joinToString())
                sleepLists.add(asleepMinutes)
            }
            curDayAndMonth = dayAndMoth
            asleepMinutes = BooleanArray(60)
        }
        guardId = s.guardId
        if (s.message.startsWith("falls")) {
            asleep = true
            fell = s.minute
        } else if (s.message.startsWith("wakes")) {
            asleep = false
            val sleep = s.minute - fell
            totalSleep += sleep
            for (index in fell .. s.minute-1) {
                asleepMinutes[index] = true
            }
        }
    }
//    println(asleepMinutes.map { if (it) "#" else "." }.joinToString())
    sleepLists.add(asleepMinutes)

    val mostAsleepMinute = mostAsleepMinute(sleepLists)
    val minCount = sleepingInMinute(mostAsleepMinute, sleepLists)

    return TotalSleep(guardId, totalSleep, mostAsleepMinute, mostAsleepMinCount = minCount)
}

var inputStream = FileInputStream(File("input2.txt"))
System.setIn(inputStream);
val lines = readInput(emptyList())
val sleepTimes = lines.map { parseLine(it) }.sortedBy { it.date }
var curGuardId = -1
val sleepTimesWithGuards = sleepTimes.map { st ->
    if (st.message.contains("#")) {
        val guardId = st.message.split(" ").filter { it.contains("#") }.first().removePrefix("#").toInt()
        curGuardId = guardId
    }
    SleepTime(date = st.date, month = st.month, day = st.day, hour = st.hour, minute = st.minute, message = st.message, guardId = curGuardId)
}


val byGuard = sleepTimesWithGuards.groupBy { it.guardId }
val totalSleeps: Map<Int, TotalSleep> = byGuard.mapValues { totalSleepTime(it.value.sortedBy { it.date }) }
val mostAsleep: TotalSleep? = totalSleeps.values.maxBy { it.totalSleep }
val mostAsleepMinute: TotalSleep? = totalSleeps.values.maxBy { it.mostAsleepMinCount }
println(mostAsleep)
println(mostAsleepMinute!!.guardId * mostAsleepMinute!!.mostAsleepMinute)



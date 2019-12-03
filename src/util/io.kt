package util

import java.io.File
import java.io.FileInputStream

object Reader {
    fun readInput(file: String): List<String> {
        val inputStream = FileInputStream(File(file))
        System.setIn(inputStream)
        return readInput(emptyList())
    }
    fun readInput(curList:List<String>): List<String> = readLine()?.let { readInput(curList + it) } ?: curList
}


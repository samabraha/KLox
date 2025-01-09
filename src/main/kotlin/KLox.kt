package com.klox

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

object KLox {
    private var hadError = false

    @JvmStatic
    fun main(args: Array<String>) {
        println("KLox 0.0.0 ${countRuns()}")

        if (args.size > 1) {
            println("Usage: klox [script]")
            exitProcess(64)
        } else if (args.size == 1) {
            runFile(args[0])
        } else {
            runPrompt()
        }
    }

    fun runFile(fileName: String) {
        val path = Paths.get(fileName)
        val string = Files.readString(path, Charset.defaultCharset())
        runCode(string)
        if (hadError) exitProcess(65)
    }

    fun runPrompt() {
        val input = InputStreamReader(System.`in`)
        val reader = BufferedReader(input)

        while (true) {
            print("> ")
            val line = reader.readLine() ?: break
            runCode(line)
            hadError = false
        }
    }

    fun runCode(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        tokens.forEach { token ->
            println(token)
        }
    }

    fun error(line: Int, message: String) {
        report(line, "", message)
    }

    fun report(line: Int, where: String, message: String) {
        System.err.println("[line $line] Error $where: $message")
        hadError = true
    }

    private fun countRuns(): Int {
        val filePath = Path.of("launch_count")
        val incrementedCount = Files.readString(filePath).toInt() + 1
        Files.writeString(filePath, incrementedCount.toString())
        return incrementedCount
    }

}

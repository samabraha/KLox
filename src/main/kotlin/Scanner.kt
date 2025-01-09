package com.klox

import com.klox.TokenType.*
import java.lang.Double.parseDouble

class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token> {
        while (isAtEnd().not()) {
            start = current
            scanToken()
        }

        tokens.add(Token(type = EOF, lexeme = "", literal = null, line = line))
        return tokens
    }

    private fun scanToken() {
        val c: Char = advance()

        when (c) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && isAtEnd().not()) advance()
                } else {
                    addToken(SLASH)
                }
            }

            ' ', '\r', '\t' -> {}
            '\n' -> line++

            '"' -> string()

//            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> number()

            else -> {
                if (isDigit(c)) number()
                else if (isAlpha(c)) identifier()
                else KLox.error(line, "Unexpected character.")
            }
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val text = source.substring(start, current)
        var type = keywords[text]
        if (type == null) type = IDENTIFIER
        addToken(type)
    }

    private fun number() {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peekNext())) {
            advance()

            while (isDigit(peek())) advance()
        }

        addToken(NUMBER, parseDouble(source.substring(start, current)))
    }

    private fun string() {
        while (peek() != '"' && isAtEnd().not()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            KLox.error(line, "Unterminated string.")
            return
        }

        // The closing quote
        advance()

        val value = source.substring(start + 1, current - 1)
        addToken(STRING, value)
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd()) return Char(0)
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return Char(0)
        return source[current + 1]
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z'
                && c in 'A'..'Z'
                && c == '_'
    }

    private fun isAlphaNumeric(c: Char) = isAlpha(c) || isDigit(c)

    private fun isDigit(c: Char) = c in '0'..'9'

    private fun isAtEnd() = current >= source.length

    private fun advance() = source[current++]

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens += Token(type = type, lexeme = text, literal = literal, line = line)
    }

    companion object {
        val keywords = mapOf(
            "and" to AND,
            "class" to CLASS,
            "else" to ELSE,
            "false" to FALSE,
            "for" to FOR,
            "fun" to FUN,
            "if" to IF,
            "nil" to NIL,
            "or" to OR,
            "print" to PRINT,
            "return" to RETURN,
            "super" to SUPER,
            "this" to THIS,
            "true" to TRUE,
            "var" to VAR,
            "while" to WHILE,
        )
    }
}
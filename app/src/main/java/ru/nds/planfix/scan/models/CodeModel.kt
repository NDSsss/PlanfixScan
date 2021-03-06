package ru.nds.planfix.scan.models

import okhttp3.ResponseBody

sealed class CodeState {
    object Undefined : CodeState()
    data class Parsed(val description: String) : CodeState()
    object UnParsed : CodeState()
}

data class CodeModel(val code: String, val state: CodeState, var price: Int = 0) {

    fun toParsedResult(): String {
        return when (state) {
            is CodeState.Parsed -> "${state.description} Цена - $price"
            CodeState.Undefined, CodeState.UnParsed -> "Not Found. Code - $code Цена - $price"
        }
    }
}

fun generateFakeCodes(): List<CodeModel> = listOf(
    CodeModel(code = "12345", state = CodeState.Undefined),
    CodeModel(code = "23456", state = CodeState.Parsed(description = "23456 description")),
    CodeModel(code = "34567", state = CodeState.Parsed(description = "34567 description")),
)

fun ResponseBody.toCodeModel(code: String): CodeModel {
    val rawPage = this.byteStream().readBytes().decodeToString()
    return rawPage.toCodeModel(code)
}

fun String.toCodeModel(code: String): CodeModel {
    val regex = "<title>(.*?)</title>".toRegex()
    val state = try {
        val title = regex.find(this)
        title?.groupValues?.getOrNull(1)
            ?.let { parsedTitle -> CodeState.Parsed(description = parsedTitle) }
            ?: CodeState.UnParsed
    } catch (e: Exception) {
        CodeState.UnParsed
    }
    return CodeModel(code = code, state = state)
}
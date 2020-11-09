package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(query: String, ignoreCase: Boolean = true): List<Int> {
    return this?.let {
        val regex = if (ignoreCase) Regex(query, RegexOption.IGNORE_CASE) else Regex(query)
        regex.findAll(this).map { it.range.first }.toList()
    } ?: emptyList()
}
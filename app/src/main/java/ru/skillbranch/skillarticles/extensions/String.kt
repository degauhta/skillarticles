package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(query: String): List<Int> {
    return this?.let {
        Regex(query, RegexOption.IGNORE_CASE).findAll(this).map { it.range.first }.toList()
    } ?: emptyList()
}
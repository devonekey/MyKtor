package com.example.model

import kotlinx.serialization.Serializable

enum class Priority {
    Low, Medium, High, Vital
}

@Serializable
data class Task(
    val name: String,
    val description: String,
    val priority: Priority
)

fun Task.taskAsRow() = """
    <tr>
        <td>$name</td><td>$description</td><td>$priority</td>
    </tr>
""".trimIndent()

fun List<Task>.taskAsTable() = joinToString(
    prefix = "<table rules=\"all\">",
    postfix = "</table>",
    separator = "\n",
    transform = Task::taskAsRow
)

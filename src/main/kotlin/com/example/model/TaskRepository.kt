package com.example.model

interface TaskRepository {
    suspend fun allTask(): List<Task>
    suspend fun taskByPriority(priority: Priority): List<Task>
    suspend fun taskByName(name: String): Task?
    suspend fun addTask(task: Task)
    suspend fun removeTask(name: String): Boolean
}

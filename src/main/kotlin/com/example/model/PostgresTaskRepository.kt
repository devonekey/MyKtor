package com.example.model

class PostgresTaskRepository : TaskRepository {
    override suspend fun allTask(): List<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun taskByPriority(priority: Priority): List<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun taskByName(name: String): Task? {
        TODO("Not yet implemented")
    }

    override suspend fun addTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun removeTask(name: String): Boolean {
        TODO("Not yet implemented")
    }
}

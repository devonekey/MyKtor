package com.example.model

import com.example.db.TaskDAO
import com.example.db.TaskTable
import com.example.db.daoToModel
import com.example.db.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class PostgresTaskRepository : TaskRepository {
    override suspend fun allTask(): List<Task> = suspendTransaction {
        TaskDAO
            .all()
            .map(::daoToModel)
    }

    override suspend fun taskByPriority(priority: Priority): List<Task> = suspendTransaction {
        TaskDAO
            .find { TaskTable.priority eq priority.toString() }
            .map(::daoToModel)
    }

    override suspend fun taskByName(name: String): Task? = suspendTransaction {
        TaskDAO
            .find { TaskTable.name eq name }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addTask(task: Task): Unit = suspendTransaction {
        TaskDAO.new {
            name = task.name
            description = task.description
            priority = task.priority.toString()
        }
    }

    override suspend fun removeTask(name: String): Boolean = suspendTransaction {
        val deleted = TaskTable.deleteWhere {
            TaskTable.name eq name
        }

        deleted == 1
    }
}

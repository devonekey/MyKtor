package com.example.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TaskRepositoryTest {
    @BeforeEach
    fun setup() {
        with(TaskRepository.allTask() as MutableList) {
            clear()
            addAll(
                mutableListOf(
                    Task("cleaning", "Clean the house", Priority.Low),
                    Task("gardening", "Mow the lawn", Priority.Medium),
                    Task("shopping", "Buy the groceries", Priority.High),
                    Task("painting", "Paint the fence", Priority.Medium),
                    Task("swimming", "Go to the beach", Priority.Low),
                    Task("cooking", "Cook the dinner", Priority.High)
                )
            )
        }
    }

    @Test
    fun `TaskRepository로부터 모든 Task들을 가져올 수 있다`() {
        val tasks = TaskRepository.allTask()

        assertEquals(expected = 6, actual = tasks.size)
    }

    @Test
    fun `TaskRepository로부터 특정 우선 순위의 Task들을 가져올 수 있다`() {
        val lowTasks = TaskRepository.taskByPriority(Priority.Low)
        val vitalTasks = TaskRepository.taskByPriority(Priority.Vital)

        assertEquals(expected = 2, actual = lowTasks.size)
        assertEquals(expected = 0, actual = vitalTasks.size)
    }

    @Test
    fun `TaskRepository로부터 특정 이름을 가진 Task를 가져올 수 있다`() {
        val task = TaskRepository.taskByName("swimming")

        assertNotNull(task)
        assertEquals(expected = "Go to the beach", actual = task.description)
    }

    @Test
    fun `TaskRepository로부터 Task를 추가할 수 있다`() {
        Assertions.assertThrows(IllegalStateException::class.java) {
            TaskRepository.addTask(Task("cleaning", "Clean the code", Priority.Low))
        }
        TaskRepository.addTask(Task("repairing", "Repair the car", Priority.Low))
        assertEquals(expected = 7, actual = TaskRepository.allTask().size)
    }
}

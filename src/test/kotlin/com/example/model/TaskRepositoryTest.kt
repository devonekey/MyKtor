package com.example.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TaskRepositoryTest {
    @BeforeEach
    fun setup() {
        with(FakeTaskRepository.allTask() as MutableList) {
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
        val tasks = FakeTaskRepository.allTask()

        assertEquals(expected = 6, actual = tasks.size)
    }

    @Test
    fun `TaskRepository로부터 특정 우선 순위의 Task들을 가져올 수 있다`() {
        val lowTasks = FakeTaskRepository.taskByPriority(Priority.Low)
        val vitalTasks = FakeTaskRepository.taskByPriority(Priority.Vital)

        assertEquals(expected = 2, actual = lowTasks.size)
        assertEquals(expected = 0, actual = vitalTasks.size)
    }

    @Test
    fun `TaskRepository로부터 특정 이름을 가진 Task를 가져올 수 있다`() {
        val task = FakeTaskRepository.taskByName("swimming")

        assertNotNull(task)
        assertEquals(expected = "Go to the beach", actual = task.description)
    }

    @Test
    fun `TaskRepository로부터 Task를 추가할 수 있다`() {
        Assertions.assertThrows(IllegalStateException::class.java) {
            FakeTaskRepository.addTask(Task("cleaning", "Clean the code", Priority.Low))
        }
        FakeTaskRepository.addTask(Task("repairing", "Repair the car", Priority.Low))
        assertEquals(expected = 7, actual = FakeTaskRepository.allTask().size)
    }

    @Test
    fun `TaskReposiroty로부터 Task를 제거할 수 있다`() {
        assertTrue(FakeTaskRepository.removeTask("cooking"))
        assertEquals(expected = 5, actual = FakeTaskRepository.allTask().size)
        assertNull(FakeTaskRepository.taskByName("cooking"))
    }
}

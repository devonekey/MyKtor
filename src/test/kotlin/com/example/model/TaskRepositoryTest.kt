package com.example.model

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TaskRepositoryTest {
    lateinit var repository: TaskRepository

    @BeforeEach
    fun setup() {
        repository = FakeTaskRepository()

        runBlocking {
            with(repository.allTask() as MutableList) {
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
    }

    @Test
    fun `TaskRepository로부터 모든 Task들을 가져올 수 있다`() {
        runBlocking {
            val tasks = async { repository.allTask() }.await()

            assertEquals(expected = 6, actual = tasks.size)
        }
    }

    @Test
    fun `TaskRepository로부터 특정 우선 순위의 Task들을 가져올 수 있다`() {
        runBlocking {
            val deferredLowTasks = async { repository.taskByPriority(Priority.Low) }
            val deferredVitalTasks = async { repository.taskByPriority(Priority.Vital) }

            assertEquals(expected = 2, actual = deferredLowTasks.await().size)
            assertEquals(expected = 0, actual = deferredVitalTasks.await().size)
        }
    }

    @Test
    fun `TaskRepository로부터 특정 이름을 가진 Task를 가져올 수 있다`() {
        runBlocking {
            val task = async { repository.taskByName("swimming") }.await()

            assertNotNull(task)
            assertEquals(expected = "Go to the beach", actual = task.description)
        }
    }

    @Test
    fun `TaskRepository로부터 Task를 추가할 수 있다`() {
        runBlocking {
            Assertions.assertThrows(IllegalStateException::class.java) {
                runBlocking { repository.addTask(Task("cleaning", "Clean the code", Priority.Low)) }
            }
            launch { repository.addTask(Task("repairing", "Repair the car", Priority.Low)) }.join()
            assertEquals(expected = 7, actual = repository.allTask().size)
        }
    }

    @Test
    fun `TaskReposiroty로부터 Task를 제거할 수 있다`() {
        runBlocking {
            launch { assertTrue(repository.removeTask("cooking")) }.join()

            assertEquals(expected = 5, actual = repository.allTask().size)
            assertNull(repository.taskByName("cooking"))
        }
    }
}

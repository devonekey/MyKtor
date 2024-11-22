package com.example.model

import org.junit.jupiter.api.BeforeEach

class TaskRepositoryTest {
    @BeforeEach
    fun setup() {
        with(TaskRepository.tasks) {
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

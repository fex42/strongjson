package org.example

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MainTest {
    @Test
    fun `add two numbers`() {
        val a = 1
        val b = 2

        assertEquals(3, a + b)
    }
}
package com.example

import com.example.ui.TrafficViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testAuthFlow() {
    val viewModel = TrafficViewModel()

    // Invalid login
    val failed = viewModel.login("wrong", "credentials")
    assertFalse(failed)
    assertFalse(viewModel.isLoggedIn.value)
    assertEquals("Invalid username or password", viewModel.loginError.value)

    // Valid admin login matching Flask app
    val success = viewModel.login("admin", "1234")
    assertTrue(success)
    assertTrue(viewModel.isLoggedIn.value)
    assertEquals(null, viewModel.loginError.value)
  }

  @Test
  fun testViolationsSeedData() {
    val viewModel = TrafficViewModel()
    val initialViolations = viewModel.violationsList.value
    assertTrue("Should contain initial violations", initialViolations.isNotEmpty())

    val firstViolation = initialViolations.first()
    viewModel.issueChallan(firstViolation.id)
    val updated = viewModel.violationsList.value.first { it.id == firstViolation.id }
    assertEquals(com.example.data.model.ChallanStatus.ISSUED, updated.status)
  }
}

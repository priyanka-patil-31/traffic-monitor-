package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AnprPlateRecord
import com.example.data.model.CameraFeed
import com.example.data.model.DetectedVehicle
import com.example.data.model.TrafficDensity
import com.example.data.model.TrafficSignalState
import com.example.data.model.ViolationRecord
import com.example.data.repository.TrafficRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

enum class TrafficScreenTab(val title: String) {
  DASHBOARD("Live Monitor"),
  VIOLATIONS("Violations"),
  ANPR("Number Plates"),
  SIGNALS("Traffic Signals"),
  SETTINGS("System / Git")
}

data class SnapshotEvent(
  val id: String,
  val cameraName: String,
  val timestamp: String,
  val vehicleCount: Int,
  val violationsCaptured: Int
)

class TrafficViewModel : ViewModel() {

  val repository = TrafficRepository(viewModelScope)

  // Auth State (matching Flask admin / 1234)
  private val _isLoggedIn = MutableStateFlow(false)
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  private val _loginError = MutableStateFlow<String?>(null)
  val loginError: StateFlow<String?> = _loginError.asStateFlow()

  private val _activeTab = MutableStateFlow(TrafficScreenTab.DASHBOARD)
  val activeTab: StateFlow<TrafficScreenTab> = _activeTab.asStateFlow()

  private val _currentSnapshot = MutableStateFlow<SnapshotEvent?>(null)
  val currentSnapshot: StateFlow<SnapshotEvent?> = _currentSnapshot.asStateFlow()

  val selectedCamera: StateFlow<CameraFeed> = repository.selectedCamera
  val isStreaming: StateFlow<Boolean> = repository.isStreaming
  val activeVehicles: StateFlow<List<DetectedVehicle>> = repository.activeVehicles
  val totalVehiclesCount: StateFlow<Int> = repository.totalVehiclesCount
  val currentDensity: StateFlow<TrafficDensity> = repository.currentDensity
  val totalViolationsCount: StateFlow<Int> = repository.totalViolationsCount
  val totalPlatesCount: StateFlow<Int> = repository.totalPlatesCount
  val violationsList: StateFlow<List<ViolationRecord>> = repository.violationsList
  val anprPlatesList: StateFlow<List<AnprPlateRecord>> = repository.anprPlatesList
  val signalState: StateFlow<TrafficSignalState> = repository.signalState
  val latestAlert: StateFlow<String?> = repository.latestAlert

  fun login(user: String, pass: String): Boolean {
    if (user.trim() == "admin" && pass.trim() == "1234") {
      _isLoggedIn.value = true
      _loginError.value = null
      return true
    } else {
      _loginError.value = "Invalid username or password"
      return false
    }
  }

  fun logout() {
    _isLoggedIn.value = false
    _loginError.value = null
    _activeTab.value = TrafficScreenTab.DASHBOARD
  }

  fun clearLoginError() {
    _loginError.value = null
  }

  fun selectTab(tab: TrafficScreenTab) {
    _activeTab.value = tab
  }

  fun switchCamera(camera: CameraFeed) {
    repository.switchCamera(camera)
  }

  fun toggleStreaming() {
    repository.toggleStreaming()
  }

  fun triggerTestViolation() {
    repository.triggerManualTestViolation()
  }

  fun issueChallan(id: String) {
    repository.issueChallan(id)
  }

  fun dismissViolation(id: String) {
    repository.dismissViolation(id)
  }

  fun markPaid(id: String) {
    repository.markPaid(id)
  }

  fun toggleEmergencyCorridor() {
    repository.toggleEmergencyCorridor()
  }

  fun clearAlert() {
    repository.clearAlert()
  }

  fun captureSnapshot() {
    val cam = selectedCamera.value
    val vehicles = activeVehicles.value
    val violations = vehicles.count { it.isViolation }
    _currentSnapshot.value = SnapshotEvent(
      id = "SNAP-${System.currentTimeMillis() % 100000}",
      cameraName = cam.name,
      timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
      vehicleCount = vehicles.size,
      violationsCaptured = violations
    )
  }

  fun dismissSnapshotDialog() {
    _currentSnapshot.value = null
  }
}

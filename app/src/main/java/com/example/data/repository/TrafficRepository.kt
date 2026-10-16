package com.example.data.repository

import com.example.data.model.AnprPlateRecord
import com.example.data.model.CameraFeed
import com.example.data.model.ChallanStatus
import com.example.data.model.DetectedVehicle
import com.example.data.model.SignalPhase
import com.example.data.model.TrafficDensity
import com.example.data.model.TrafficSignalState
import com.example.data.model.VehicleType
import com.example.data.model.ViolationRecord
import com.example.data.model.ViolationType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrafficRepository(private val scope: CoroutineScope) {

  private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

  val availableCameras = listOf(
    CameraFeed("cam_1", "CAM-01: MG Road Junction", "CBD Center Lane", 30, 50),
    CameraFeed("cam_2", "CAM-02: Express Highway North", "Sector 14 Corridor", 60, 80),
    CameraFeed("cam_3", "CAM-03: Central Metro Ring", "Gate 4 Intersection", 30, 40),
    CameraFeed("cam_4", "CAM-04: River Bridge Approach", "South Toll Gate", 30, 60)
  )

  private val _selectedCamera = MutableStateFlow(availableCameras[0])
  val selectedCamera: StateFlow<CameraFeed> = _selectedCamera.asStateFlow()

  private val _isStreaming = MutableStateFlow(true)
  val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

  // Detection objects for canvas rendering
  private val _activeVehicles = MutableStateFlow<List<DetectedVehicle>>(emptyList())
  val activeVehicles: StateFlow<List<DetectedVehicle>> = _activeVehicles.asStateFlow()

  // Cumulative metrics
  private val _totalVehiclesCount = MutableStateFlow(142)
  val totalVehiclesCount: StateFlow<Int> = _totalVehiclesCount.asStateFlow()

  private val _currentDensity = MutableStateFlow(TrafficDensity.MEDIUM)
  val currentDensity: StateFlow<TrafficDensity> = _currentDensity.asStateFlow()

  private val _totalViolationsCount = MutableStateFlow(12)
  val totalViolationsCount: StateFlow<Int> = _totalViolationsCount.asStateFlow()

  private val _totalPlatesCount = MutableStateFlow(118)
  val totalPlatesCount: StateFlow<Int> = _totalPlatesCount.asStateFlow()

  private val _violationsList = MutableStateFlow<List<ViolationRecord>>(emptyList())
  val violationsList: StateFlow<List<ViolationRecord>> = _violationsList.asStateFlow()

  private val _anprPlatesList = MutableStateFlow<List<AnprPlateRecord>>(emptyList())
  val anprPlatesList: StateFlow<List<AnprPlateRecord>> = _anprPlatesList.asStateFlow()

  private val _signalState = MutableStateFlow(
    TrafficSignalState(
      junctionName = "MG Road & 4th Avenue",
      mainPhase = SignalPhase.GREEN,
      crossPhase = SignalPhase.RED,
      countdownSeconds = 24,
      emergencyCorridorActive = false
    )
  )
  val signalState: StateFlow<TrafficSignalState> = _signalState.asStateFlow()

  private val _latestAlert = MutableStateFlow<String?>(null)
  val latestAlert: StateFlow<String?> = _latestAlert.asStateFlow()

  private val states = listOf("KA", "MH", "DL", "TN", "TS", "GJ", "KL")
  private val randomOwners = listOf("A. Sharma", "R. Verma", "S. Patil", "P. Kumar", "K. Nair", "M. Reddy", "V. Singh")

  init {
    seedInitialData()
    startTrafficSimulation()
    startSignalCycle()
  }

  private fun seedInitialData() {
    val initialViolations = listOf(
      ViolationRecord(
        id = "VIO-101",
        timestamp = "09:42:15",
        cameraName = "CAM-01: MG Road Junction",
        vehicleType = VehicleType.CAR,
        plateNumber = "KA-01-MJ-8821",
        violationType = ViolationType.RED_LIGHT,
        recordedSpeedKmh = 54,
        speedLimitKmh = 50,
        fineAmount = 2000,
        status = ChallanStatus.ISSUED,
        snapshotNotes = "Crossed stop line 2.4s after red signal light."
      ),
      ViolationRecord(
        id = "VIO-102",
        timestamp = "09:45:33",
        cameraName = "CAM-02: Express Highway North",
        vehicleType = VehicleType.CAR,
        plateNumber = "MH-12-PQ-4519",
        violationType = ViolationType.SPEEDING,
        recordedSpeedKmh = 108,
        speedLimitKmh = 80,
        fineAmount = 1500,
        status = ChallanStatus.PENDING,
        snapshotNotes = "Over speed limit by +28 km/h on fast lane."
      ),
      ViolationRecord(
        id = "VIO-103",
        timestamp = "09:48:02",
        cameraName = "CAM-01: MG Road Junction",
        vehicleType = VehicleType.MOTORCYCLE,
        plateNumber = "DL-03-TC-9102",
        violationType = ViolationType.NO_HELMET,
        recordedSpeedKmh = 38,
        speedLimitKmh = 50,
        fineAmount = 1000,
        status = ChallanStatus.ISSUED,
        snapshotNotes = "Rider detected without standard ISI helmet."
      ),
      ViolationRecord(
        id = "VIO-104",
        timestamp = "09:51:19",
        cameraName = "CAM-03: Central Metro Ring",
        vehicleType = VehicleType.AUTO,
        plateNumber = "KA-05-AA-3124",
        violationType = ViolationType.WRONG_WAY,
        recordedSpeedKmh = 28,
        speedLimitKmh = 40,
        fineAmount = 2500,
        status = ChallanStatus.PENDING,
        snapshotNotes = "One-way street wrong direction intrusion."
      )
    )
    _violationsList.value = initialViolations

    val initialPlates = listOf(
      AnprPlateRecord("KA-01-MJ-8821", VehicleType.CAR, "09:42:15", "CAM-01", 98, "R. Verma", "Karnataka"),
      AnprPlateRecord("MH-12-PQ-4519", VehicleType.CAR, "09:45:33", "CAM-02", 96, "A. Sharma", "Maharashtra"),
      AnprPlateRecord("DL-03-TC-9102", VehicleType.MOTORCYCLE, "09:48:02", "CAM-01", 94, "K. Nair", "Delhi"),
      AnprPlateRecord("KA-05-AA-3124", VehicleType.AUTO, "09:51:19", "CAM-03", 97, "V. Singh", "Karnataka"),
      AnprPlateRecord("TS-09-FB-7744", VehicleType.BUS, "09:52:40", "CAM-02", 99, "State Transport Corp", "Telangana"),
      AnprPlateRecord("KA-03-X-9901", VehicleType.CAR, "09:53:11", "CAM-01", 95, "S. Patil", "Karnataka", isStolenOrBlacklisted = true)
    )
    _anprPlatesList.value = initialPlates

    // Seed 4-6 initial vehicles on camera
    spawnVehicles(5)
  }

  private fun generatePlate(): String {
    val st = states.random()
    val rto = Random.nextInt(1, 99).toString().padStart(2, '0')
    val chars = ('A'..'Z').toList()
    val alpha = "${chars.random()}${chars.random()}"
    val num = Random.nextInt(1000, 9999)
    return "$st-$rto-$alpha-$num"
  }

  private fun spawnVehicles(count: Int) {
    val list = mutableListOf<DetectedVehicle>()
    val cam = _selectedCamera.value
    for (i in 0 until count) {
      val type = VehicleType.values().filter { it != VehicleType.AMBULANCE }.random()
      val baseSpeed = cam.speedLimitKmh + Random.nextInt(-15, 10)
      list.add(
        DetectedVehicle(
          id = UUID.randomUUID().toString(),
          type = type,
          plateNumber = generatePlate(),
          speedKmh = baseSpeed.coerceAtLeast(15),
          speedLimitKmh = cam.speedLimitKmh,
          violationType = ViolationType.NONE,
          confidence = 0.88f + (Random.nextFloat() * 0.11f),
          posX = 0.1f + (i * 0.18f),
          posY = Random.nextFloat() * 0.6f + 0.2f,
          directionX = if (Random.nextBoolean()) 0.008f else -0.008f,
          directionY = if (Random.nextBoolean()) 0.004f else -0.004f
        )
      )
    }
    _activeVehicles.value = list
  }

  private fun startTrafficSimulation() {
    scope.launch(Dispatchers.Default) {
      while (true) {
        delay(60) // ~16 fps physics tick
        if (!_isStreaming.value) continue

        val currentList = _activeVehicles.value.toMutableList()
        val cam = _selectedCamera.value

        // Move existing vehicles
        for (v in currentList) {
          v.posX += v.directionX
          v.posY += v.directionY

          // Wrap around edges
          if (v.posX > 1.05f) v.posX = -0.05f
          if (v.posX < -0.05f) v.posX = 1.05f
          if (v.posY > 0.95f) v.posY = 0.15f
          if (v.posY < 0.10f) v.posY = 0.90f
        }

        // Randomly spawn/cycle vehicles occasionally
        if (Random.nextInt(100) < 2 && currentList.size < 8) {
          val newType = if (Random.nextInt(20) == 0) VehicleType.AMBULANCE else VehicleType.values().random()
          val speedOffset = Random.nextInt(-15, 25)
          val speed = (cam.speedLimitKmh + speedOffset).coerceAtLeast(20)
          val isSpeeding = speed > cam.speedLimitKmh + 10
          val violation = when {
            isSpeeding -> ViolationType.SPEEDING
            Random.nextInt(35) == 0 -> ViolationType.RED_LIGHT
            newType == VehicleType.MOTORCYCLE && Random.nextInt(15) == 0 -> ViolationType.NO_HELMET
            else -> ViolationType.NONE
          }

          val plate = generatePlate()
          val newVehicle = DetectedVehicle(
            id = UUID.randomUUID().toString(),
            type = newType,
            plateNumber = plate,
            speedKmh = speed,
            speedLimitKmh = cam.speedLimitKmh,
            violationType = violation,
            confidence = 0.91f + (Random.nextFloat() * 0.08f),
            posX = if (Random.nextBoolean()) 0.0f else 0.95f,
            posY = 0.25f + Random.nextFloat() * 0.5f,
            directionX = if (Random.nextBoolean()) 0.009f else -0.009f,
            directionY = (Random.nextFloat() - 0.5f) * 0.005f
          )
          currentList.add(newVehicle)
          _totalVehiclesCount.value += 1
          _totalPlatesCount.value += 1

          // Log to ANPR
          val anpr = AnprPlateRecord(
            plateNumber = plate,
            vehicleType = newType,
            timestamp = dateFormat.format(Date()),
            cameraName = cam.name.substringBefore(":"),
            accuracyScore = (newVehicle.confidence * 100).toInt(),
            registeredOwner = randomOwners.random(),
            registrationState = states.random(),
            isStolenOrBlacklisted = Random.nextInt(50) == 0
          )
          _anprPlatesList.value = listOf(anpr) + _anprPlatesList.value.take(40)

          // If it is a violation, register record
          if (violation != ViolationType.NONE) {
            recordViolation(newVehicle, cam)
          }
        }

        // Keep vehicle count bounded
        if (currentList.size > 8) {
          currentList.removeAt(0)
        }

        _activeVehicles.value = currentList

        // Recalculate density
        val density = when {
          currentList.size <= 2 -> TrafficDensity.LOW
          currentList.size <= 5 -> TrafficDensity.MEDIUM
          currentList.size <= 7 -> TrafficDensity.HIGH
          else -> TrafficDensity.CONGESTED
        }
        _currentDensity.value = density
      }
    }
  }

  private fun startSignalCycle() {
    scope.launch(Dispatchers.Default) {
      while (true) {
        delay(1000)
        val s = _signalState.value
        if (s.emergencyCorridorActive) {
          // Keep main green indefinitely during emergency corridor
          continue
        }

        val newCount = s.countdownSeconds - 1
        if (newCount <= 0) {
          when (s.mainPhase) {
            SignalPhase.GREEN -> {
              _signalState.value = s.copy(mainPhase = SignalPhase.YELLOW, countdownSeconds = 4)
            }
            SignalPhase.YELLOW -> {
              _signalState.value = s.copy(
                mainPhase = SignalPhase.RED,
                crossPhase = SignalPhase.GREEN,
                countdownSeconds = 25
              )
            }
            SignalPhase.RED -> {
              _signalState.value = s.copy(
                mainPhase = SignalPhase.GREEN,
                crossPhase = SignalPhase.RED,
                countdownSeconds = 25
              )
            }
          }
        } else {
          _signalState.value = s.copy(countdownSeconds = newCount)
        }
      }
    }
  }

  private fun recordViolation(vehicle: DetectedVehicle, cam: CameraFeed) {
    val fine = vehicle.violationType.defaultFine
    val record = ViolationRecord(
      id = "VIO-${Random.nextInt(1000, 9999)}",
      timestamp = dateFormat.format(Date()),
      cameraName = cam.name,
      vehicleType = vehicle.type,
      plateNumber = vehicle.plateNumber,
      violationType = vehicle.violationType,
      recordedSpeedKmh = vehicle.speedKmh,
      speedLimitKmh = vehicle.speedLimitKmh,
      fineAmount = fine,
      status = ChallanStatus.PENDING,
      snapshotNotes = "Auto AI detected: ${vehicle.violationType.title} (${vehicle.speedKmh} km/h)"
    )
    _violationsList.value = listOf(record) + _violationsList.value
    _totalViolationsCount.value += 1
    _latestAlert.value = "🚨 ${vehicle.violationType.title} detected! Plate: ${vehicle.plateNumber}"
  }

  fun switchCamera(camera: CameraFeed) {
    _selectedCamera.value = camera
    spawnVehicles(Random.nextInt(3, 6))
  }

  fun toggleStreaming() {
    _isStreaming.value = !_isStreaming.value
  }

  fun triggerManualTestViolation() {
    val cam = _selectedCamera.value
    val plate = generatePlate()
    val types = listOf(ViolationType.RED_LIGHT, ViolationType.SPEEDING, ViolationType.WRONG_WAY, ViolationType.NO_HELMET)
    val violation = types.random()
    val speed = if (violation == ViolationType.SPEEDING) cam.speedLimitKmh + 32 else cam.speedLimitKmh + 4

    val testVehicle = DetectedVehicle(
      id = UUID.randomUUID().toString(),
      type = VehicleType.CAR,
      plateNumber = plate,
      speedKmh = speed,
      speedLimitKmh = cam.speedLimitKmh,
      violationType = violation,
      confidence = 0.97f,
      posX = 0.5f,
      posY = 0.5f,
      directionX = 0.006f,
      directionY = -0.003f
    )

    val current = _activeVehicles.value.toMutableList()
    current.add(testVehicle)
    _activeVehicles.value = current
    _totalVehiclesCount.value += 1
    _totalPlatesCount.value += 1

    recordViolation(testVehicle, cam)
  }

  fun issueChallan(id: String) {
    _violationsList.value = _violationsList.value.map {
      if (it.id == id) it.copy(status = ChallanStatus.ISSUED) else it
    }
  }

  fun dismissViolation(id: String) {
    _violationsList.value = _violationsList.value.map {
      if (it.id == id) it.copy(status = ChallanStatus.DISMISSED) else it
    }
  }

  fun markPaid(id: String) {
    _violationsList.value = _violationsList.value.map {
      if (it.id == id) it.copy(status = ChallanStatus.PAID) else it
    }
  }

  fun clearAlert() {
    _latestAlert.value = null
  }

  fun toggleEmergencyCorridor() {
    val current = _signalState.value
    val newStatus = !current.emergencyCorridorActive
    _signalState.value = current.copy(
      emergencyCorridorActive = newStatus,
      mainPhase = if (newStatus) SignalPhase.GREEN else SignalPhase.GREEN,
      crossPhase = if (newStatus) SignalPhase.RED else SignalPhase.RED,
      countdownSeconds = if (newStatus) 99 else 25
    )
    if (newStatus) {
      _latestAlert.value = "🚑 Emergency Green Corridor ACTIVE: MG Road prioritised!"
    } else {
      _latestAlert.value = "Normal traffic signal cycle restored."
    }
  }
}

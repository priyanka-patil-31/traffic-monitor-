package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.TrafficCyan
import com.example.ui.theme.TrafficGreen
import com.example.ui.theme.TrafficRed
import com.example.ui.theme.TrafficYellow

enum class TrafficDensity(val label: String, val color: Color) {
  LOW("Low", TrafficGreen),
  MEDIUM("Medium", TrafficYellow),
  HIGH("High", Color(0xFFFB923C)),
  CONGESTED("Congested", TrafficRed)
}

enum class ViolationType(val title: String, val defaultFine: Int) {
  SPEEDING("Overspeeding", 1500),
  RED_LIGHT("Red Light Jumping", 2000),
  NO_HELMET("No Helmet", 1000),
  WRONG_WAY("Wrong Way Entry", 2500),
  NONE("Normal Traffic", 0)
}

enum class ChallanStatus(val label: String, val color: Color) {
  PENDING("Pending Review", TrafficYellow),
  ISSUED("Challan Issued", TrafficCyan),
  PAID("Fine Settled", TrafficGreen),
  DISMISSED("Dismissed", Color(0xFF94A3B8))
}

enum class VehicleType(val displayName: String) {
  CAR("Sedan / Car"),
  MOTORCYCLE("Motorcycle"),
  BUS("Transit Bus"),
  TRUCK("Heavy Truck"),
  AUTO("Auto Rickshaw"),
  AMBULANCE("Ambulance (Emergency)")
}

data class CameraFeed(
  val id: String,
  val name: String,
  val location: String,
  val fps: Int = 30,
  val speedLimitKmh: Int = 50,
  val isLive: Boolean = true
)

data class DetectedVehicle(
  val id: String,
  val type: VehicleType,
  val plateNumber: String,
  val speedKmh: Int,
  val speedLimitKmh: Int,
  val violationType: ViolationType,
  val confidence: Float,
  var posX: Float, // 0.0 to 1.0 (relative canvas coord)
  var posY: Float,
  val directionX: Float,
  val directionY: Float
) {
  val isViolation: Boolean get() = violationType != ViolationType.NONE
}

data class ViolationRecord(
  val id: String,
  val timestamp: String,
  val cameraName: String,
  val vehicleType: VehicleType,
  val plateNumber: String,
  val violationType: ViolationType,
  val recordedSpeedKmh: Int,
  val speedLimitKmh: Int,
  val fineAmount: Int,
  val status: ChallanStatus = ChallanStatus.PENDING,
  val snapshotNotes: String = ""
)

data class AnprPlateRecord(
  val plateNumber: String,
  val vehicleType: VehicleType,
  val timestamp: String,
  val cameraName: String,
  val accuracyScore: Int, // e.g. 96%
  val registeredOwner: String,
  val registrationState: String,
  val isStolenOrBlacklisted: Boolean = false
)

enum class SignalPhase(val color: Color) {
  RED(TrafficRed),
  YELLOW(TrafficYellow),
  GREEN(TrafficGreen)
}

data class TrafficSignalState(
  val junctionName: String,
  val mainPhase: SignalPhase,
  val crossPhase: SignalPhase,
  val countdownSeconds: Int,
  val emergencyCorridorActive: Boolean = false
)

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CameraFeed
import com.example.data.model.DetectedVehicle
import com.example.data.model.VehicleType
import com.example.data.model.ViolationType
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TrafficBlue
import com.example.ui.theme.TrafficCyan
import com.example.ui.theme.TrafficGreen
import com.example.ui.theme.TrafficRed
import com.example.ui.theme.TrafficYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun CctvVideoBox(
  cameraFeed: CameraFeed,
  vehicles: List<DetectedVehicle>,
  isStreaming: Boolean,
  onToggleStreaming: () -> Unit,
  onTriggerViolation: () -> Unit,
  onCaptureSnapshot: () -> Unit,
  modifier: Modifier = Modifier
) {
  var currentTimeString by remember { mutableStateOf("00:00:00") }
  val timeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

  // Tick clock for CCTV on-screen display
  LaunchedEffect(Unit) {
    while (true) {
      currentTimeString = timeFormat.format(Date())
      delay(1000)
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(340.dp)
      .testTag("cctv_video_box"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF141A22)),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF334455))
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      // 1. CCTV Road Backdrop Image
      Image(
        painter = painterResource(id = R.drawable.cctv_road_backdrop),
        contentDescription = "CCTV camera feed backdrop",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )

      // 2. Surveillance Dark & Scanline overlay
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0xCC0B1017),
                Color(0x99101820),
                Color(0xEE0B1017)
              )
            )
          )
      )

      // 3. Canvas rendering Lane grid and AI bounding boxes
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Simulated lane tracking lines (Perspective grid)
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
        drawLine(
          color = Color(0x4400E5FF),
          start = Offset(w * 0.35f, h * 0.15f),
          end = Offset(w * 0.15f, h * 0.95f),
          strokeWidth = 2f,
          pathEffect = dashEffect
        )
        drawLine(
          color = Color(0x4400E5FF),
          start = Offset(w * 0.50f, h * 0.15f),
          end = Offset(w * 0.50f, h * 0.95f),
          strokeWidth = 2f,
          pathEffect = dashEffect
        )
        drawLine(
          color = Color(0x4400E5FF),
          start = Offset(w * 0.65f, h * 0.15f),
          end = Offset(w * 0.85f, h * 0.95f),
          strokeWidth = 2f,
          pathEffect = dashEffect
        )

        // Draw bounding boxes for detected vehicles
        for (v in vehicles) {
          val centerX = v.posX * w
          val centerY = v.posY * h
          val boxW = if (v.type == VehicleType.BUS || v.type == VehicleType.TRUCK) 90f else 65f
          val boxH = if (v.type == VehicleType.MOTORCYCLE) 45f else 55f

          val topLeftX = (centerX - boxW / 2).coerceIn(10f, w - boxW - 10f)
          val topLeftY = (centerY - boxH / 2).coerceIn(30f, h - boxH - 25f)

          val boxColor = if (v.isViolation) TrafficRed else TrafficCyan

          // Bounding Box Rectangle
          drawRect(
            color = boxColor,
            topLeft = Offset(topLeftX, topLeftY),
            size = Size(boxW, boxH),
            style = Stroke(width = if (v.isViolation) 3f else 1.8f)
          )

          // Corner brackets for AI vision look
          val cornerLen = 10f
          // Top-Left
          drawLine(boxColor, Offset(topLeftX, topLeftY), Offset(topLeftX + cornerLen, topLeftY), strokeWidth = 3f)
          drawLine(boxColor, Offset(topLeftX, topLeftY), Offset(topLeftX, topLeftY + cornerLen), strokeWidth = 3f)
          // Top-Right
          drawLine(boxColor, Offset(topLeftX + boxW, topLeftY), Offset(topLeftX + boxW - cornerLen, topLeftY), strokeWidth = 3f)
          drawLine(boxColor, Offset(topLeftX + boxW, topLeftY), Offset(topLeftX + boxW, topLeftY + cornerLen), strokeWidth = 3f)

          // Center crosshair dot
          drawCircle(
            color = boxColor,
            radius = 3f,
            center = Offset(topLeftX + boxW / 2, topLeftY + boxH / 2)
          )
        }
      }

      // 4. Vehicle HTML/CSS-style Tag Badges over canvas
      for (v in vehicles) {
        val isViol = v.isViolation
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(
              start = (v.posX * 260).dp.coerceIn(8.dp, 230.dp),
              top = (v.posY * 220).dp.coerceIn(36.dp, 250.dp)
            )
        ) {
          Column {
            // AI Classification Badge
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isViol) TrafficRed else Color(0xCC0B1A2A))
                .border(0.5.dp, if (isViol) TrafficRed else TrafficCyan, RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isViol) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
              }
              Text(
                text = if (isViol) "VIOLATION ${v.speedKmh} km/h" else "${v.type.name} ${v.speedKmh}km/h",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }

            // Plate Tag
            Row(
              modifier = Modifier
                .padding(top = 1.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xE6000000))
                .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
              Text(
                text = v.plateNumber,
                color = TrafficYellow,
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      // 5. OSD Top Bar (Header HUD)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xCC0A0F16))
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(if (isStreaming) TrafficRed else Color.Gray)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isStreaming) "LIVE REC" else "PAUSED",
            color = if (isStreaming) TrafficRed else Color.LightGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = cameraFeed.name,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Text(
          text = currentTimeString,
          color = TrafficCyan,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      // 6. HUD Bottom Control Overlay
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .background(Color(0xD90A0F16))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Play / Pause Stream
          IconButton(
            onClick = onToggleStreaming,
            modifier = Modifier
              .size(32.dp)
              .testTag("toggle_stream_button")
          ) {
            Icon(
              imageVector = if (isStreaming) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isStreaming) "Pause stream" else "Resume stream",
              tint = Color.White
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Snapshot button
          IconButton(
            onClick = onCaptureSnapshot,
            modifier = Modifier
              .size(32.dp)
              .testTag("capture_snapshot_button")
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "Capture frame",
              tint = TrafficCyan
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Text(
            text = "AI: YOLOv8-Traffic + OCR ANPR (FPS: ${cameraFeed.fps})",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        // Trigger manual test violation
        Button(
          onClick = onTriggerViolation,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = TrafficRed),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier
            .height(28.dp)
            .testTag("test_violation_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Simulate Violation", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

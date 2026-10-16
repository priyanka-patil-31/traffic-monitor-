package com.example.ui.screens.signals

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SignalPhase
import com.example.data.model.TrafficSignalState
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrafficBlue
import com.example.ui.theme.TrafficCyan
import com.example.ui.theme.TrafficDarkBg
import com.example.ui.theme.TrafficGreen
import com.example.ui.theme.TrafficRed
import com.example.ui.theme.TrafficSurface
import com.example.ui.theme.TrafficSurfaceElevated
import com.example.ui.theme.TrafficYellow

@Composable
fun SignalControlScreen(
  signalState: TrafficSignalState,
  onToggleEmergencyCorridor: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TrafficDarkBg)
      .verticalScroll(scrollState)
      .padding(16.dp)
  ) {
    Text(
      text = "Traffic Signal Control",
      fontSize = 22.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )
    Text(
      text = "AI Adaptive Traffic Timing & Emergency Corridor Override",
      fontSize = 13.sp,
      color = TextSecondary
    )

    Spacer(modifier = Modifier.height(18.dp))

    // Emergency Green Corridor Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("emergency_corridor_card"),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (signalState.emergencyCorridorActive) TrafficGreen.copy(alpha = 0.18f) else TrafficSurface
      ),
      border = androidx.compose.foundation.BorderStroke(
        1.5.dp,
        if (signalState.emergencyCorridorActive) TrafficGreen else Color(0xFF2C3949)
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (signalState.emergencyCorridorActive) TrafficGreen else TrafficSurfaceElevated),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.LocalHospital,
              contentDescription = null,
              tint = if (signalState.emergencyCorridorActive) Color.Black else TrafficGreen,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = "Emergency Green Corridor",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = if (signalState.emergencyCorridorActive)
                "ACTIVE: All lights locked green for Ambulance passage"
              else
                "Clear path for ambulances & emergency responders",
              fontSize = 11.sp,
              color = if (signalState.emergencyCorridorActive) TrafficGreen else TextSecondary
            )
          }
        }

        Switch(
          checked = signalState.emergencyCorridorActive,
          onCheckedChange = { onToggleEmergencyCorridor() },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = TrafficGreen
          ),
          modifier = Modifier.testTag("emergency_switch")
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Interactive Traffic Light Simulator Box
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = TrafficSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949))
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = signalState.junctionName,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = "Intersection ID: JUNC-402-MG",
          fontSize = 11.sp,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Main Road Traffic Light
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Main Road (North-South)",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            TrafficLightHead(phase = signalState.mainPhase)
          }

          // Countdown Timer Display
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F172A))
                .border(2.dp, TrafficBlue, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${signalState.countdownSeconds}s",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TrafficCyan,
                fontFamily = FontFamily.Monospace
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Phase Change",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }

          // Cross Road Traffic Light
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Cross Road (East-West)",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            TrafficLightHead(phase = signalState.crossPhase)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // AI Adaptive Cycle telemetry card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = TrafficSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text("AI Signal Automation Engine", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Queue Density Estimation", fontSize = 12.sp, color = TextSecondary)
          Text("32 vehicles / lane", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Green Window Optimization", fontSize = 12.sp, color = TextSecondary)
          Text("+8s allocated to North-South", fontSize = 12.sp, color = TrafficGreen, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Pollution & Idle Reduction", fontSize = 12.sp, color = TextSecondary)
          Text("18% reduced wait time", fontSize = 12.sp, color = TrafficCyan, fontWeight = FontWeight.Medium)
        }
      }
    }
  }
}

@Composable
fun TrafficLightHead(phase: SignalPhase) {
  Box(
    modifier = Modifier
      .width(54.dp)
      .height(130.dp)
      .clip(RoundedCornerShape(27.dp))
      .background(Color(0xFF0F172A))
      .border(2.dp, Color(0xFF334155), RoundedCornerShape(27.dp))
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()
    ) {
      // Red
      SignalLamp(
        isActive = phase == SignalPhase.RED,
        activeColor = TrafficRed
      )
      // Yellow
      SignalLamp(
        isActive = phase == SignalPhase.YELLOW,
        activeColor = TrafficYellow
      )
      // Green
      SignalLamp(
        isActive = phase == SignalPhase.GREEN,
        activeColor = TrafficGreen
      )
    }
  }
}

@Composable
fun SignalLamp(
  isActive: Boolean,
  activeColor: Color
) {
  val color by animateColorAsState(
    targetValue = if (isActive) activeColor else activeColor.copy(alpha = 0.15f),
    label = "lamp_color"
  )

  Box(
    modifier = Modifier
      .size(32.dp)
      .clip(CircleShape)
      .background(color)
      .border(
        width = if (isActive) 2.dp else 1.dp,
        color = if (isActive) Color.White.copy(alpha = 0.8f) else Color(0x33FFFFFF),
        shape = CircleShape
      )
  )
}

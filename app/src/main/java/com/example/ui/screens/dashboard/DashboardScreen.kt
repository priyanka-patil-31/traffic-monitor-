package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameraFeed
import com.example.data.model.DetectedVehicle
import com.example.data.model.TrafficDensity
import com.example.ui.TrafficScreenTab
import com.example.ui.components.CctvVideoBox
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
fun DashboardScreen(
  cameras: List<CameraFeed>,
  selectedCamera: CameraFeed,
  vehicles: List<DetectedVehicle>,
  totalVehicles: Int,
  trafficDensity: TrafficDensity,
  totalViolations: Int,
  totalPlates: Int,
  isStreaming: Boolean,
  latestAlert: String?,
  onSwitchCamera: (CameraFeed) -> Unit,
  onToggleStreaming: () -> Unit,
  onTriggerViolation: () -> Unit,
  onCaptureSnapshot: () -> Unit,
  onClearAlert: () -> Unit,
  onNavigateToTab: (TrafficScreenTab) -> Unit,
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
    // 1. Dashboard Header (matching user HTML h1 and p)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "🚦 Traffic Monitoring Dashboard",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "AI-Based Real-Time Traffic Monitoring and Violation Detection",
          fontSize = 13.sp,
          color = TextSecondary
        )
      }

      // Live status badge
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(TrafficSurfaceElevated)
          .border(1.dp, TrafficGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(TrafficGreen)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "SYSTEM ONLINE",
          color = TrafficGreen,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Camera Selector Chips
    Text(
      text = "Active Surveillance Feed:",
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      color = TextSecondary
    )
    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      cameras.forEach { cam ->
        val isSelected = cam.id == selectedCamera.id
        FilterChip(
          selected = isSelected,
          onClick = { onSwitchCamera(cam) },
          label = {
            Text(
              cam.name.substringBefore(":"),
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Videocam,
              contentDescription = null,
              tint = if (isSelected) TrafficBlue else TextSecondary,
              modifier = Modifier.size(16.dp)
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TrafficBlue.copy(alpha = 0.2f),
            selectedLabelColor = TrafficBlue,
            containerColor = TrafficSurface,
            labelColor = TextPrimary
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) TrafficBlue else Color(0xFF334455),
            selectedBorderColor = TrafficBlue
          ),
          modifier = Modifier.testTag("camera_chip_${cam.id}")
        )
      }
    }

    // Latest Violation Alert Bar
    AnimatedVisibility(
      visible = latestAlert != null,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      if (latestAlert != null) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .testTag("alert_banner"),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = TrafficRed.copy(alpha = 0.18f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, TrafficRed.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alert",
                tint = TrafficRed,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = latestAlert,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
            IconButton(
              onClick = onClearAlert,
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 2. The 4 Metric Cards (Matching user's HTML: Vehicles, Traffic Density, Violations, Number Plates)
    // Displayed in a responsive 2x2 grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Card 1: Vehicles
      MetricDashboardCard(
        title = "Vehicles",
        value = totalVehicles.toString(),
        subtitle = "${vehicles.size} currently in view",
        icon = Icons.Default.DirectionsCar,
        accentColor = TrafficBlue,
        modifier = Modifier
          .weight(1f)
          .testTag("metric_card_vehicles"),
        onClick = { }
      )

      // Card 2: Traffic Density
      MetricDashboardCard(
        title = "Traffic Density",
        value = trafficDensity.label,
        subtitle = "Flow: ${selectedCamera.speedLimitKmh} km/h zone",
        icon = Icons.Default.Speed,
        accentColor = trafficDensity.color,
        modifier = Modifier
          .weight(1f)
          .testTag("metric_card_density"),
        onClick = { }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Card 3: Violations
      MetricDashboardCard(
        title = "Violations",
        value = totalViolations.toString(),
        subtitle = "Click to inspect logs",
        icon = Icons.Default.Warning,
        accentColor = TrafficRed,
        modifier = Modifier
          .weight(1f)
          .testTag("metric_card_violations"),
        onClick = { onNavigateToTab(TrafficScreenTab.VIOLATIONS) }
      )

      // Card 4: Number Plates
      MetricDashboardCard(
        title = "Number Plates",
        value = totalPlates.toString(),
        subtitle = "ANPR OCR active",
        icon = Icons.Default.LocalPolice,
        accentColor = TrafficYellow,
        modifier = Modifier
          .weight(1f)
          .testTag("metric_card_plates"),
        onClick = { onNavigateToTab(TrafficScreenTab.ANPR) }
      )
    }

    Spacer(modifier = Modifier.height(22.dp))

    // 3. Live Traffic Monitoring CCTV Box (matching user's h2 and video-box)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Live Traffic Monitoring",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Text(
        text = "4-Lane HD Optical Feed",
        fontSize = 12.sp,
        color = TextSecondary
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // CCTV Video Stream Canvas Composable
    CctvVideoBox(
      cameraFeed = selectedCamera,
      vehicles = vehicles,
      isStreaming = isStreaming,
      onToggleStreaming = onToggleStreaming,
      onTriggerViolation = onTriggerViolation,
      onCaptureSnapshot = onCaptureSnapshot
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Quick Action Bar
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = TrafficSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Control Center Shortcuts",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ActionShortcutButton(
            title = "Violations Hub",
            icon = Icons.Default.Warning,
            color = TrafficRed,
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToTab(TrafficScreenTab.VIOLATIONS) }
          )
          ActionShortcutButton(
            title = "ANPR Registry",
            icon = Icons.Default.LocalPolice,
            color = TrafficYellow,
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToTab(TrafficScreenTab.ANPR) }
          )
          ActionShortcutButton(
            title = "Traffic Signals",
            icon = Icons.Default.Traffic,
            color = TrafficGreen,
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToTab(TrafficScreenTab.SIGNALS) }
          )
        }
      }
    }
  }
}

@Composable
fun MetricDashboardCard(
  title: String,
  value: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = TrafficSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondary
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = value,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = accentColor
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = TextSecondary
      )
    }
  }
}

@Composable
fun ActionShortcutButton(
  title: String,
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = TrafficSurfaceElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextPrimary
      )
    }
  }
}

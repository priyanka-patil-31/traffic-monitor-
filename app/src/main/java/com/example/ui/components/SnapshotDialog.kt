package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.SnapshotEvent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrafficBlue
import com.example.ui.theme.TrafficCyan
import com.example.ui.theme.TrafficGreen
import com.example.ui.theme.TrafficRed
import com.example.ui.theme.TrafficSurface

@Composable
fun SnapshotDialog(
  snapshot: SnapshotEvent,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("snapshot_dialog"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = TrafficSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, TrafficCyan.copy(alpha = 0.5f))
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = null,
              tint = TrafficCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CCTV Frame Snapshot",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
          }
          Text(
            text = snapshot.id,
            fontSize = 11.sp,
            color = TrafficCyan,
            fontFamily = FontFamily.Monospace
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Frame preview
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF334455), RoundedCornerShape(8.dp))
        ) {
          Image(
            painter = painterResource(id = R.drawable.cctv_road_backdrop),
            contentDescription = "Snapshot Frame",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
          )
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .background(Color(0xCC000000))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${snapshot.cameraName} | ${snapshot.timestamp}",
              color = Color.White,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Vehicles Detected", fontSize = 11.sp, color = TextSecondary)
            Text("${snapshot.vehicleCount}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          }
          Column {
            Text("Violations in Frame", fontSize = 11.sp, color = TextSecondary)
            Text(
              text = "${snapshot.violationsCaptured}",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = if (snapshot.violationsCaptured > 0) TrafficRed else TrafficGreen
            )
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("Evidence Status", fontSize = 11.sp, color = TextSecondary)
            Text("Archived Locally", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TrafficCyan)
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("dismiss_snapshot_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = TrafficBlue)
        ) {
          Text("Done", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

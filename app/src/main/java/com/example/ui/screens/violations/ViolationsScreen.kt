package com.example.ui.screens.violations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChallanStatus
import com.example.data.model.ViolationRecord
import com.example.data.model.ViolationType
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
fun ViolationsScreen(
  violations: List<ViolationRecord>,
  onIssueChallan: (String) -> Unit,
  onDismissViolation: (String) -> Unit,
  onMarkPaid: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTypeFilter by remember { mutableStateOf<ViolationType?>(null) }

  val filteredViolations = remember(violations, selectedTypeFilter) {
    if (selectedTypeFilter == null) violations
    else violations.filter { it.violationType == selectedTypeFilter }
  }

  val totalPending = violations.count { it.status == ChallanStatus.PENDING }
  val totalFines = violations.filter { it.status != ChallanStatus.DISMISSED }.sumOf { it.fineAmount }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TrafficDarkBg)
      .padding(16.dp)
  ) {
    // Header
    Text(
      text = "Traffic Violations Hub",
      fontSize = 22.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )
    Text(
      text = "AI automated detection log & electronic challan management",
      fontSize = 13.sp,
      color = TextSecondary
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Summary Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = TrafficSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Recorded", fontSize = 11.sp, color = TextSecondary)
          Text("${violations.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFF2C3949)))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Pending Review", fontSize = 11.sp, color = TextSecondary)
          Text("$totalPending", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TrafficYellow)
        }
        Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFF2C3949)))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Total Fines", fontSize = 11.sp, color = TextSecondary)
          Text("₹$totalFines", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TrafficRed)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = selectedTypeFilter == null,
        onClick = { selectedTypeFilter = null },
        label = { Text("All (${violations.size})") },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = TrafficBlue.copy(alpha = 0.2f),
          selectedLabelColor = TrafficBlue,
          containerColor = TrafficSurface,
          labelColor = TextPrimary
        ),
        modifier = Modifier.testTag("filter_all_violations")
      )

      listOf(ViolationType.SPEEDING, ViolationType.RED_LIGHT, ViolationType.NO_HELMET, ViolationType.WRONG_WAY).forEach { type ->
        val isSelected = selectedTypeFilter == type
        val count = violations.count { it.violationType == type }
        FilterChip(
          selected = isSelected,
          onClick = { selectedTypeFilter = if (isSelected) null else type },
          label = { Text("${type.title} ($count)") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TrafficRed.copy(alpha = 0.2f),
            selectedLabelColor = TrafficRed,
            containerColor = TrafficSurface,
            labelColor = TextPrimary
          ),
          modifier = Modifier.testTag("filter_violation_${type.name}")
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // List of Violations
    if (filteredViolations.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Text("No violations matching selected filter.", color = TextSecondary, fontSize = 14.sp)
      }
    } else {
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredViolations, key = { it.id }) { record ->
          ViolationItemCard(
            record = record,
            onIssueChallan = { onIssueChallan(record.id) },
            onDismiss = { onDismissViolation(record.id) },
            onMarkPaid = { onMarkPaid(record.id) }
          )
        }
      }
    }
  }
}

@Composable
fun ViolationItemCard(
  record: ViolationRecord,
  onIssueChallan: () -> Unit,
  onDismiss: () -> Unit,
  onMarkPaid: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("violation_item_${record.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = TrafficSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // License Plate badge
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, TrafficYellow, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = record.plateNumber,
            color = TrafficYellow,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        // Status badge
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(record.status.color.copy(alpha = 0.15f))
            .border(1.dp, record.status.color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(record.status.color)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = record.status.label,
            color = record.status.color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Violation Title & Fine
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = record.violationType.title,
            color = TrafficRed,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${record.vehicleType.displayName} • ${record.cameraName}",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }
        Text(
          text = "Fine: ₹${record.fineAmount}",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = record.snapshotNotes,
        color = TextSecondary,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Action row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = record.timestamp,
          color = TextSecondary,
          fontSize = 11.sp,
          modifier = Modifier.weight(1f)
        )

        when (record.status) {
          ChallanStatus.PENDING -> {
            OutlinedButton(
              onClick = onDismiss,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text("Dismiss", fontSize = 11.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = onIssueChallan,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = TrafficBlue),
              modifier = Modifier.height(34.dp)
            ) {
              Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Issue E-Challan", fontSize = 11.sp)
            }
          }
          ChallanStatus.ISSUED -> {
            Button(
              onClick = onMarkPaid,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = TrafficGreen),
              modifier = Modifier.height(34.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Record Payment", fontSize = 11.sp)
            }
          }
          ChallanStatus.PAID -> {
            Text("Settled via Online Portal", color = TrafficGreen, fontSize = 11.sp, fontWeight = FontWeight.Medium)
          }
          ChallanStatus.DISMISSED -> {
            Text("Marked as false positive", color = TextSecondary, fontSize = 11.sp)
          }
        }
      }
    }
  }
}

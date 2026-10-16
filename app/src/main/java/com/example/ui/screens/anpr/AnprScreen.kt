package com.example.ui.screens.anpr

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.AnprPlateRecord
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrafficBlue
import com.example.ui.theme.TrafficCyan
import com.example.ui.theme.TrafficDarkBg
import com.example.ui.theme.TrafficGreen
import com.example.ui.theme.TrafficRed
import com.example.ui.theme.TrafficSurface
import com.example.ui.theme.TrafficYellow

@Composable
fun AnprScreen(
  plates: List<AnprPlateRecord>,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredPlates = remember(plates, searchQuery) {
    if (searchQuery.isBlank()) plates
    else plates.filter {
      it.plateNumber.contains(searchQuery, ignoreCase = true) ||
          it.registeredOwner.contains(searchQuery, ignoreCase = true) ||
          it.registrationState.contains(searchQuery, ignoreCase = true)
    }
  }

  val flaggedCount = plates.count { it.isStolenOrBlacklisted }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TrafficDarkBg)
      .padding(16.dp)
  ) {
    // Header
    Text(
      text = "ANPR Registry",
      fontSize = 22.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )
    Text(
      text = "Automatic Number Plate Recognition & VAHAN Database Lookup",
      fontSize = 13.sp,
      color = TextSecondary
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Summary statistics
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = TrafficSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("Plates Scanned", fontSize = 11.sp, color = TextSecondary)
          Text("${plates.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TrafficBlue)
        }
      }

      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = TrafficSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("Flagged / Blacklist", fontSize = 11.sp, color = TextSecondary)
          Text("$flaggedCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (flaggedCount > 0) TrafficRed else TrafficGreen)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      label = { Text("Search by plate number, owner, or state") },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = TrafficBlue)
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
          }
        }
      },
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("anpr_search_input"),
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = TrafficBlue,
        unfocusedBorderColor = Color(0xFF334155),
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = TrafficBlue
      )
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Plates list
    if (filteredPlates.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Text("No plates matching '$searchQuery'", color = TextSecondary, fontSize = 14.sp)
      }
    } else {
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredPlates) { item ->
          AnprPlateItemCard(item = item)
        }
      }
    }
  }
}

@Composable
fun AnprPlateItemCard(item: AnprPlateRecord) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("anpr_item_${item.plateNumber}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = TrafficSurface),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (item.isStolenOrBlacklisted) TrafficRed.copy(alpha = 0.8f) else Color(0xFF2C3949)
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // High contrast License Plate styling
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F172A))
            .border(1.5.dp, if (item.isStolenOrBlacklisted) TrafficRed else TrafficYellow, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(TrafficBlue)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = item.plateNumber,
              color = if (item.isStolenOrBlacklisted) TrafficRed else TrafficYellow,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        // Confidence badge
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TrafficGreen.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "AI: ${item.accuracyScore}% match",
            color = TrafficGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      if (item.isStolenOrBlacklisted) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(TrafficRed.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = TrafficRed, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "HOTLIST ALERT: Vehicle flagged in police database!",
            color = TrafficRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text("Owner", fontSize = 10.sp, color = TextSecondary)
          Text(item.registeredOwner, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
        }
        Column {
          Text("Type", fontSize = 10.sp, color = TextSecondary)
          Text(item.vehicleType.displayName, fontSize = 12.sp, color = TextPrimary)
        }
        Column {
          Text("State", fontSize = 10.sp, color = TextSecondary)
          Text(item.registrationState, fontSize = 12.sp, color = TextPrimary)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("Time / Camera", fontSize = 10.sp, color = TextSecondary)
          Text("${item.timestamp} (${item.cameraName})", fontSize = 12.sp, color = TextSecondary)
        }
      }
    }
  }
}

package com.example.ui.screens.settings

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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SystemSettingsScreen(
  onLogout: () -> Unit,
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
      text = "System & Repository",
      fontSize = 22.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )
    Text(
      text = "Operator session, GitHub repository export, and build info",
      fontSize = 13.sp,
      color = TextSecondary
    )

    Spacer(modifier = Modifier.height(18.dp))

    // Operator Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = TrafficSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(TrafficBlue.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = TrafficBlue,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text("Admin Operator", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          Text("Username: admin (Traffic Control Desk)", fontSize = 12.sp, color = TextSecondary)
          Text("Role: Senior Traffic Enforcement Officer", fontSize = 11.sp, color = TrafficGreen)
        }

        OutlinedButton(
          onClick = onLogout,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TrafficRed),
          modifier = Modifier.testTag("logout_button")
        ) {
          Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Logout", fontSize = 12.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // GitHub & Export ZIP instructions card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = TrafficSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, TrafficCyan.copy(alpha = 0.4f))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.FolderZip,
            contentDescription = null,
            tint = TrafficCyan,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Export Project & Push to GitHub",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "You requested to create a ZIP file or folder to push this app to GitHub. Follow either method below:",
          fontSize = 12.sp,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Step 1: Export ZIP in AI Studio
        Text(
          text = "Option A: 1-Click ZIP Download (AI Studio UI)",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TrafficBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "1. Click the Project Settings / Overflow menu in the top bar of AI Studio.\n2. Select 'Export as ZIP' to instantly download the full Android project directory.\n3. Extract the ZIP on your PC and open in Android Studio.",
          fontSize = 11.sp,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Step 2: Push to GitHub terminal commands
        Text(
          text = "Option B: Push to GitHub via Git CLI",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TrafficGreen
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .padding(12.dp)
        ) {
          Text(
            text = "git init\n" +
                "git add .\n" +
                "git commit -m \"feat: Traffic Monitoring and Violation Detection System\"\n" +
                "git branch -M main\n" +
                "git remote add origin https://github.com/<your-username>/<repo>.git\n" +
                "git push -u origin main",
            color = Color(0xFF38BDF8),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Build & Run APK:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .padding(10.dp)
        ) {
          Text(
            text = "gradle assembleDebug",
            color = TrafficYellow,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // System Information
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = TrafficSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3949))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("System Architecture", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("• Framework: Kotlin + Jetpack Compose Material 3", fontSize = 11.sp, color = TextSecondary)
        Text("• Vision Simulation: Real-time multi-lane vehicle tracker & OCR ANPR", fontSize = 11.sp, color = TextSecondary)
        Text("• Original Web Conversion: Flask app + HTML/CSS styled dashboard", fontSize = 11.sp, color = TextSecondary)
        Text("• Status: Production Ready APK", fontSize = 11.sp, color = TrafficGreen)
      }
    }
  }
}

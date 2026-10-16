package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TrafficScreenTab
import com.example.ui.TrafficViewModel
import com.example.ui.components.SnapshotDialog
import com.example.ui.screens.anpr.AnprScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.login.LoginScreen
import com.example.ui.screens.settings.SystemSettingsScreen
import com.example.ui.screens.signals.SignalControlScreen
import com.example.ui.screens.violations.ViolationsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrafficBlue
import com.example.ui.theme.TrafficDarkBg
import com.example.ui.theme.TrafficRed
import com.example.ui.theme.TrafficSurface
import com.example.ui.theme.TrafficSurfaceElevated

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        TrafficApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficApp(viewModel: TrafficViewModel = viewModel()) {
  val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
  val loginError by viewModel.loginError.collectAsStateWithLifecycle()
  val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

  val selectedCamera by viewModel.selectedCamera.collectAsStateWithLifecycle()
  val isStreaming by viewModel.isStreaming.collectAsStateWithLifecycle()
  val activeVehicles by viewModel.activeVehicles.collectAsStateWithLifecycle()
  val totalVehicles by viewModel.totalVehiclesCount.collectAsStateWithLifecycle()
  val currentDensity by viewModel.currentDensity.collectAsStateWithLifecycle()
  val totalViolations by viewModel.totalViolationsCount.collectAsStateWithLifecycle()
  val totalPlates by viewModel.totalPlatesCount.collectAsStateWithLifecycle()
  val violationsList by viewModel.violationsList.collectAsStateWithLifecycle()
  val anprPlatesList by viewModel.anprPlatesList.collectAsStateWithLifecycle()
  val signalState by viewModel.signalState.collectAsStateWithLifecycle()
  val latestAlert by viewModel.latestAlert.collectAsStateWithLifecycle()
  val currentSnapshot by viewModel.currentSnapshot.collectAsStateWithLifecycle()

  if (!isLoggedIn) {
    LoginScreen(
      errorMessage = loginError,
      onLoginSubmitted = { user, pass ->
        viewModel.login(user, pass)
      },
      onClearError = {
        viewModel.clearLoginError()
      }
    )
  } else {
    // BackHandler: return to dashboard if on sub-tab
    BackHandler(enabled = activeTab != TrafficScreenTab.DASHBOARD) {
      viewModel.selectTab(TrafficScreenTab.DASHBOARD)
    }

    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .background(TrafficDarkBg),
      containerColor = TrafficDarkBg,
      contentWindowInsets = WindowInsets(0.dp),
      topBar = {
        TopAppBar(
          title = {
            Text(
              text = "Traffic Eye • ${activeTab.title}",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
          },
          actions = {
            // Quick button to view Git & Export settings
            IconButton(
              onClick = { viewModel.selectTab(TrafficScreenTab.SETTINGS) },
              modifier = Modifier.testTag("topbar_git_button")
            ) {
              Icon(
                imageVector = Icons.Default.FolderZip,
                contentDescription = "Export & Git",
                tint = TrafficBlue
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TrafficSurface,
            titleContentColor = TextPrimary
          )
        )
      },
      bottomBar = {
        NavigationBar(
          containerColor = TrafficSurface,
          contentColor = TextPrimary,
          tonalElevation = 8.dp
        ) {
          NavigationBarItem(
            selected = activeTab == TrafficScreenTab.DASHBOARD,
            onClick = { viewModel.selectTab(TrafficScreenTab.DASHBOARD) },
            icon = {
              Icon(Icons.Default.Videocam, contentDescription = "Monitor")
            },
            label = { Text("Monitor", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = TrafficBlue,
              selectedTextColor = TrafficBlue,
              indicatorColor = TrafficSurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_dashboard")
          )

          NavigationBarItem(
            selected = activeTab == TrafficScreenTab.VIOLATIONS,
            onClick = { viewModel.selectTab(TrafficScreenTab.VIOLATIONS) },
            icon = {
              val pendingCount = violationsList.count { it.status == com.example.data.model.ChallanStatus.PENDING }
              if (pendingCount > 0) {
                BadgedBox(badge = {
                  Badge(containerColor = TrafficRed) { Text("$pendingCount") }
                }) {
                  Icon(Icons.Default.Warning, contentDescription = "Violations")
                }
              } else {
                Icon(Icons.Default.Warning, contentDescription = "Violations")
              }
            },
            label = { Text("Violations", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = TrafficRed,
              selectedTextColor = TrafficRed,
              indicatorColor = TrafficSurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_violations")
          )

          NavigationBarItem(
            selected = activeTab == TrafficScreenTab.ANPR,
            onClick = { viewModel.selectTab(TrafficScreenTab.ANPR) },
            icon = {
              Icon(Icons.Default.LocalPolice, contentDescription = "ANPR Plates")
            },
            label = { Text("ANPR", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = TrafficBlue,
              selectedTextColor = TrafficBlue,
              indicatorColor = TrafficSurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_anpr")
          )

          NavigationBarItem(
            selected = activeTab == TrafficScreenTab.SIGNALS,
            onClick = { viewModel.selectTab(TrafficScreenTab.SIGNALS) },
            icon = {
              Icon(Icons.Default.Traffic, contentDescription = "Signals")
            },
            label = { Text("Signals", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = TrafficBlue,
              selectedTextColor = TrafficBlue,
              indicatorColor = TrafficSurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_signals")
          )

          NavigationBarItem(
            selected = activeTab == TrafficScreenTab.SETTINGS,
            onClick = { viewModel.selectTab(TrafficScreenTab.SETTINGS) },
            icon = {
              Icon(Icons.Default.Settings, contentDescription = "Settings")
            },
            label = { Text("System", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = TrafficBlue,
              selectedTextColor = TrafficBlue,
              indicatorColor = TrafficSurfaceElevated,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_settings")
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (activeTab) {
          TrafficScreenTab.DASHBOARD -> {
            DashboardScreen(
              cameras = viewModel.repository.availableCameras,
              selectedCamera = selectedCamera,
              vehicles = activeVehicles,
              totalVehicles = totalVehicles,
              trafficDensity = currentDensity,
              totalViolations = totalViolations,
              totalPlates = totalPlates,
              isStreaming = isStreaming,
              latestAlert = latestAlert,
              onSwitchCamera = { viewModel.switchCamera(it) },
              onToggleStreaming = { viewModel.toggleStreaming() },
              onTriggerViolation = { viewModel.triggerTestViolation() },
              onCaptureSnapshot = { viewModel.captureSnapshot() },
              onClearAlert = { viewModel.clearAlert() },
              onNavigateToTab = { viewModel.selectTab(it) }
            )
          }

          TrafficScreenTab.VIOLATIONS -> {
            ViolationsScreen(
              violations = violationsList,
              onIssueChallan = { viewModel.issueChallan(it) },
              onDismissViolation = { viewModel.dismissViolation(it) },
              onMarkPaid = { viewModel.markPaid(it) }
            )
          }

          TrafficScreenTab.ANPR -> {
            AnprScreen(
              plates = anprPlatesList
            )
          }

          TrafficScreenTab.SIGNALS -> {
            SignalControlScreen(
              signalState = signalState,
              onToggleEmergencyCorridor = { viewModel.toggleEmergencyCorridor() }
            )
          }

          TrafficScreenTab.SETTINGS -> {
            SystemSettingsScreen(
              onLogout = { viewModel.logout() }
            )
          }
        }

        // Snapshot modal
        currentSnapshot?.let { snap ->
          SnapshotDialog(
            snapshot = snap,
            onDismiss = { viewModel.dismissSnapshotDialog() }
          )
        }
      }
    }
  }
}

package com.example.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrafficBlue
import com.example.ui.theme.TrafficDarkBg
import com.example.ui.theme.TrafficRed
import com.example.ui.theme.TrafficSurface
import com.example.ui.theme.TrafficSurfaceVariant

@Composable
fun LoginScreen(
  errorMessage: String?,
  onLoginSubmitted: (String, String) -> Unit,
  onClearError: () -> Unit,
  modifier: Modifier = Modifier
) {
  var username by remember { mutableStateOf("admin") }
  var password by remember { mutableStateOf("1234") }
  var isPasswordVisible by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(TrafficDarkBg)
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .widthIn(max = 420.dp)
        .fillMaxWidth()
        .testTag("login_card"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = TrafficSurface),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .padding(28.dp)
          .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App / CCTV Emblem
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(TrafficSurfaceVariant)
            .border(2.dp, TrafficBlue.copy(alpha = 0.5f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.traffic_app_icon),
            contentDescription = "Traffic System Logo",
            modifier = Modifier.size(60.dp)
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
          text = "🚦 Traffic Monitoring System",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "AI-Based Real-Time Traffic Monitoring & Violation Detection",
          fontSize = 13.sp,
          color = TextSecondary,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Username Field
        OutlinedTextField(
          value = username,
          onValueChange = {
            username = it
            onClearError()
          },
          label = { Text("Username") },
          placeholder = { Text("Enter username") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = "Username",
              tint = TrafficBlue
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("username_input"),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TrafficBlue,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = TrafficBlue
          ),
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password Field
        OutlinedTextField(
          value = password,
          onValueChange = {
            password = it
            onClearError()
          },
          label = { Text("Password") },
          placeholder = { Text("Enter password") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Password",
              tint = TrafficBlue
            )
          },
          trailingIcon = {
            IconButton(
              onClick = { isPasswordVisible = !isPasswordVisible },
              modifier = Modifier.testTag("toggle_password_visibility")
            ) {
              Icon(
                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                tint = TextSecondary
              )
            }
          },
          visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("password_input"),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TrafficBlue,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = TrafficBlue
          ),
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
          ),
          keyboardActions = KeyboardActions(
            onDone = {
              onLoginSubmitted(username, password)
            }
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Demo credentials quick-fill helper
        OutlinedButton(
          onClick = {
            username = "admin"
            password = "1234"
            onClearError()
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("fill_demo_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TrafficBlue
          )
        ) {
          Text("Fill Demo Credentials (admin / 1234)", fontSize = 12.sp)
        }

        // Error message container matching Flask {% if error %}
        AnimatedVisibility(
          visible = errorMessage != null,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          if (errorMessage != null) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(TrafficRed.copy(alpha = 0.15f))
                  .border(1.dp, TrafficRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = "Error",
                  tint = TrafficRed,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = errorMessage,
                  color = TrafficRed,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.testTag("login_error_text")
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Submit Button (#1677ff)
        Button(
          onClick = { onLoginSubmitted(username, password) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("login_submit_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = TrafficBlue)
        ) {
          Text(
            text = "Login to Dashboard",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Authorized Traffic Police & Highway Control Access Only",
          fontSize = 11.sp,
          color = TextSecondary.copy(alpha = 0.8f),
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

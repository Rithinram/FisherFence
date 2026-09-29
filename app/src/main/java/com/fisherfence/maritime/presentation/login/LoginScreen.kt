package com.fisherfence.maritime.presentation.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fisherfence.maritime.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    role: String, // "fisherman", "coastguard", "family"
    onLoginSuccess: (role: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Prepopulate credentials for ease of testing/demo
    LaunchedEffect(role) {
        when (role) {
            "fisherman" -> {
                email = "fisherman@sea.com"
                password = "fisher123"
            }
            "coastguard" -> {
                email = "coastguard@gov.in"
                password = "coast123"
            }
            "family" -> {
                email = "family@home.com"
                password = "family123"
            }
        }
    }

    val roleDisplayName = when (role) {
        "fisherman" -> "Fisherman"
        "coastguard" -> "Coast Guard"
        "family" -> "Family Member"
        else -> "User"
    }

    // Wave animation at the bottom
    val waveTransition = rememberInfiniteTransition(label = "loginWave")
    val waveOffset by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loginWaveOffset"
    )

    val validateAndLogin = {
        val isValid = when (role) {
            "fisherman" -> email == "fisherman@sea.com" && password == "fisher123"
            "coastguard" -> email == "coastguard@gov.in" && password == "coast123"
            "family" -> email == "family@home.com" && password == "family123"
            else -> false
        }

        if (isValid) {
            isLoading = true
            coroutineScope.launch {
                delay(1000) // Simulating network/auth call
                isLoading = false
                onLoginSuccess(role)
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Invalid credentials. Please try again.")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        // Bottom animated wave
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .align(Alignment.BottomCenter)
        ) {
            val width = size.width
            val height = size.height
            val path = androidx.compose.ui.graphics.Path()
            path.moveTo(0f, height * 0.5f)
            for (x in 0..width.toInt() step 10) {
                val y = height * 0.5f + 15f * kotlin.math.sin((x + waveOffset) * 0.015f)
                path.lineTo(x.toFloat(), y.toFloat())
            }
            path.lineTo(width, height)
            path.lineTo(0f, height)
            path.close()
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(SecondaryBlue.copy(alpha = 0.4f), DarkCardSurface)
                )
            )
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Icon(
                    imageVector = Icons.Default.Anchor,
                    contentDescription = "App Logo",
                    tint = PrimaryTeal,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "FisherFence",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Role Chip Badge
                SuggestionChip(
                    onClick = { },
                    label = {
                        Text(
                            text = "$roleDisplayName Login",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = PrimaryTeal
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = null
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Input Fields Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, BorderBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Email Text Field
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address", color = TextGrey) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = PrimaryTeal,
                                unfocusedIndicatorColor = BorderBlue,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        // Password Text Field
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = TextGrey) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = TextGrey)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = PrimaryTeal,
                                unfocusedIndicatorColor = BorderBlue,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button / Loading state
                if (isLoading) {
                    CircularProgressIndicator(color = PrimaryTeal)
                } else {
                    Button(
                        onClick = { validateAndLogin() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Login",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onNavigateBack) {
                    Text(
                        text = "Back to Role Selection",
                        color = TextGrey,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

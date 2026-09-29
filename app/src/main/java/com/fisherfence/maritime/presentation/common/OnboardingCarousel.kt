package com.fisherfence.maritime.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fisherfence.maritime.presentation.theme.DarkCardSurface
import com.fisherfence.maritime.presentation.theme.PrimaryTeal
import com.fisherfence.maritime.presentation.theme.TextGrey

data class OnboardingStep(
    val emoji: String,
    val title: String,
    val description: String
)

@Composable
fun OnboardingCarousel(
    role: String,
    onFinish: () -> Unit
) {
    val steps = remember(role) {
        when (role) {
            "fisherman" -> listOf(
                OnboardingStep("⚓", "Safety Tracking", "Track your vessel coordinates in real-time. Visually monitor your distance from the IMBL boundary on the map."),
                OnboardingStep("🚨", "Emergency SOS", "Trigger instant distress broadcasts to both Coast Guard and Family in one tap. Help is always within reach."),
                OnboardingStep("📋", "Pre-Departure Checklist", "Verify checklist items before venturing. The trip starts only when your safety parameters are complete.")
            )
            "coastguard" -> listOf(
                OnboardingStep("🛡", "Patrol Sectors", "Oversee multiple sea zones, monitor vessel statuses, and dynamically restrict zones during bad weather."),
                OnboardingStep("🚨", "SOS Dispatch Hub", "Take charge of emergency alerts. Track dispatcher response times and coordinates with Leaflet routing."),
                OnboardingStep("📂", "Vessel Registry DB", "Search profiles of all active vessels, check history logs, and review previous border incidents.")
            )
            else -> listOf( // family
                OnboardingStep("📍", "Voyage Tracking", "Keep a safe eye on your fisherman at sea. Monitor their distance from the shore and expected return countdowns."),
                OnboardingStep("🔔", "Custom Preference Alerts", "Decide which warnings you want to subscribe to. Get notified of cyclone advisories and border caution updates."),
                OnboardingStep("🎉", "Safe Return Confetti", "Confirm their safe landing. Celebrate with confetti animations and log completion states.")
            )
        }
    }

    var currentStep by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(24.dp)
    ) {
        // Skip Button (Top Right)
        Text(
            text = "Skip",
            color = PrimaryTeal,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable { onFinish() }
                .padding(8.dp)
        )

        // Carousel Content (Center)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val step = steps[currentStep]
            
            // Emoji Illustration
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = step.emoji, fontSize = 72.sp)
            }
            
            Spacer(modifier = Modifier.height(36.dp))

            // Text content
            Text(
                text = step.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = step.description,
                color = TextGrey,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Bottom Controls: Page indicator dots and Next button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            // Dots (Left/Center)
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentStep) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (index == currentStep) PrimaryTeal else TextGrey.copy(alpha = 0.5f))
                    )
                }
            }

            // Next / Finish Button (Right)
            Button(
                onClick = {
                    if (currentStep < steps.size - 1) {
                        currentStep++
                    } else {
                        onFinish()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(
                    text = if (currentStep == steps.size - 1) "GET STARTED" else "NEXT",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

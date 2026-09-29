package com.fisherfence.maritime.presentation.common

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.fisherfence.maritime.data.local.SystemNotification
import com.fisherfence.maritime.presentation.theme.BorderBlue
import com.fisherfence.maritime.presentation.theme.DarkCardSurface
import com.fisherfence.maritime.presentation.theme.PrimaryTeal
import com.fisherfence.maritime.presentation.theme.TextGrey
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationDrawer(
    isOpen: Boolean,
    notifications: List<SystemNotification>,
    onMarkAllAsRead: () -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "System Log Notifications",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    TextButton(onClick = onMarkAllAsRead) {
                        Text("Mark all as read", color = PrimaryTeal, fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                if (notifications.isEmpty()) {
                    Text(
                        text = "No notifications logged recently.",
                        color = TextGrey,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 240.dp)
                    ) {
                        items(notifications) { item ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (!item.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(PrimaryTeal, CircleShape)
                                        )
                                    }
                                    Text(
                                        text = item.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = item.message,
                                    color = TextGrey,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = SimpleDateFormat("dd MMM hh:mm a", Locale.ENGLISH).format(Date(item.timestamp)),
                                    color = TextGrey.copy(alpha = 0.5f),
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Divider(
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = BorderBlue.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = BorderBlue),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CLOSE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

package com.example.Roomdb.ui.view.employer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.Roomdb.data.model.Worker
import com.example.Roomdb.ui.theme.*


@Composable
fun WorkerListCard(
    worker: Worker,
    onMessage: () -> Unit,
    onHire: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, KKBorder)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val profilePicUrl = worker.profilePictureUrl
                    if (!profilePicUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = profilePicUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(KKBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                worker.fullName.take(2).uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = KKBlue
                            )
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(worker.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = KKTextPrimary)
                    Text(worker.category, fontSize = 12.sp, color = KKTextMuted)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = KKTextMuted, modifier = Modifier.size(12.dp))
                        Text(worker.location, fontSize = 11.sp, color = KKTextMuted)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (worker.isOnline) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(KKGreenLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(KKGreen))
                            Spacer(Modifier.width(4.dp))
                            Text("Online", fontSize = 9.sp, color = KKGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "KSh ${worker.hourlyRate.toInt()}/hr",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = KKBlue
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(5) { i ->
                    Icon(
                        if (i < worker.averageRating.toInt()) Icons.Outlined.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = Color(0xFFE89B2A),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text("${worker.averageRating} (${worker.reviewCount} reviews)", fontSize = 11.sp, color = KKTextMuted)
            }

            if (worker.bio.isNotBlank()) {
                Text(
                    worker.bio,
                    fontSize = 12.sp,
                    color = KKTextMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
            }

            if (worker.skills.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(worker.skills.take(4)) { skill ->
                        Text(
                            skill,
                            fontSize = 10.sp,
                            color = KKBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(KKBlueLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onMessage,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, KKBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KKBlue)
                ) {
                    Icon(Icons.Outlined.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Message", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = onHire,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KKBlue)
                ) {
                    Icon(Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Hire", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
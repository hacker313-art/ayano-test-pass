package com.example.ui.screens

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AyanoRepository
import com.example.model.LeaderboardEntry
import com.example.model.TestModel
import com.example.model.User
import com.example.ui.components.AyanoTopBar
import com.example.ui.theme.AyanoBackgroundDark
import com.example.ui.theme.AyanoBorder
import com.example.ui.theme.AyanoGold
import com.example.ui.theme.AyanoPrimaryBlue
import com.example.ui.theme.AyanoPrimaryCyan
import com.example.ui.theme.AyanoSurfaceCard
import com.example.ui.theme.AyanoSurfaceCardElevated
import com.example.ui.theme.AyanoTextMuted
import com.example.ui.theme.AyanoTextPrimary
import com.example.ui.theme.AyanoTextSecondary
import com.example.ui.theme.CbtAnsweredGreen
import java.util.Locale

@Composable
fun LeaderboardScreen(
    user: User,
    test: TestModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { AyanoRepository.getInstance(context) }
    val leaderboardEntries by repository.observeLeaderboard(test.testId).collectAsState(initial = emptyList())

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AyanoBackgroundDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AyanoTopBar(
                title = "All India Rank (AIR)",
                subtitle = test.name,
                showBack = true,
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Note
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = AyanoSurfaceCardElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = AyanoGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Live Leaderboard • Tie-breaker: In equal score, lower time taken gets higher rank.",
                                color = AyanoTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Top 3 Podium
                if (leaderboardEntries.size >= 3) {
                    item {
                        PodiumView(entries = leaderboardEntries.take(3))
                    }
                }

                item {
                    Text(
                        text = "STUDENT RANKINGS",
                        color = AyanoTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                if (leaderboardEntries.isEmpty()) {
                    item {
                        Surface(
                            color = AyanoSurfaceCard,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No test submissions yet. Be the first to attempt!",
                                    color = AyanoTextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(leaderboardEntries) { entry ->
                        val isCurrentUser = entry.userId == user.userId
                        LeaderboardRow(entry = entry, isCurrentUser = isCurrentUser)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
private fun PodiumView(entries: List<LeaderboardEntry>) {
    val rank1 = entries.getOrNull(0)
    val rank2 = entries.getOrNull(1)
    val rank3 = entries.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // Rank 2 (Silver)
        if (rank2 != null) {
            PodiumItem(entry = rank2, rank = 2, color = Color(0xFFC0C0C0), height = 110.dp)
        }

        // Rank 1 (Gold)
        if (rank1 != null) {
            PodiumItem(entry = rank1, rank = 1, color = AyanoGold, height = 135.dp)
        }

        // Rank 3 (Bronze)
        if (rank3 != null) {
            PodiumItem(entry = rank3, rank = 3, color = Color(0xFFCD7F32), height = 95.dp)
        }
    }
}

@Composable
private fun PodiumItem(entry: LeaderboardEntry, rank: Int, color: Color, height: androidx.compose.ui.unit.Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(95.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = entry.userName,
            color = AyanoTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Text(
            text = "${entry.score} pts",
            color = AyanoPrimaryCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            color = color.copy(alpha = 0.15f),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (rank == 1) "🏆 1st" else if (rank == 2) "🥈 2nd" else "🥉 3rd",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry, isCurrentUser: Boolean) {
    val minutes = (entry.timeTaken % 3600) / 60
    val seconds = entry.timeTaken % 60
    val timeStr = String.format(Locale.US, "%dm %ds", minutes, seconds)

    Surface(
        color = if (isCurrentUser) AyanoPrimaryBlue.copy(alpha = 0.25f) else AyanoSurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isCurrentUser) 1.5.dp else 1.dp,
            if (isCurrentUser) AyanoPrimaryCyan else AyanoBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_${entry.rank}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (entry.rank) {
                            1 -> AyanoGold.copy(alpha = 0.2f)
                            2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                            3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                            else -> AyanoSurfaceCardElevated
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${entry.rank}",
                    color = when (entry.rank) {
                        1 -> AyanoGold
                        2 -> Color(0xFFC0C0C0)
                        3 -> Color(0xFFCD7F32)
                        else -> AyanoTextSecondary
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Student Name & Class
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.userName,
                        color = if (isCurrentUser) AyanoPrimaryCyan else AyanoTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 1
                    )
                    if (isCurrentUser) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = AyanoPrimaryCyan,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "YOU",
                                color = AyanoBackgroundDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                if (entry.userClass.isNotBlank()) {
                    Text(
                        text = "Class: ${entry.userClass}",
                        color = AyanoTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Score & Time
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.score} pts",
                    color = CbtAnsweredGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = AyanoTextMuted,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = timeStr,
                        color = AyanoTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

package com.mnhyim.myusic.ui.feature.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.mnhyim.myusic.domain.model.MusicFile
import com.mnhyim.myusic.util.MusicControlAction
import com.mnhyim.myusic.util.formatMilliToMinutes
import dev.vivvvek.seeker.Seeker
import dev.vivvvek.seeker.SeekerDefaults

@Composable
fun Player(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()

    PlayerScreen(
        isPlaying = isPlaying,
        currentSong = currentSong,
        currentPosition = currentPosition,
        onAction = viewModel::onAction,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerScreen(
    isPlaying: Boolean,
    currentSong: MusicFile?,
    currentPosition: Long,
    onAction: (MusicControlAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val (playbackIcon, playbackAction) = remember(isPlaying) {
        if (isPlaying) Icons.Outlined.Pause to MusicControlAction.PauseAction
        else Icons.Outlined.PlayArrow to MusicControlAction.ResumeAction
    }
    val duration = currentSong?.duration ?: 0L
    val seekerValue = remember(currentPosition, duration) {
        currentPosition.toFloat() / duration.toFloat()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Now Playing",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
//                        IconButton(
//                            onClick = {  },
//                            modifier = Modifier.padding(horizontal = 8.dp)
//                        ) {
//                            Icon(Icons.Outlined.Search, "")
//                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                currentSong?.albumArtUri == null -> {
                    Icon(
                        imageVector = Icons.Outlined.MusicNote,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                            .fillMaxWidth(0.85f)
                            .padding(72.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                else -> {
                    Image(
                        painter = rememberAsyncImagePainter(currentSong.albumArtUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
            currentSong?.let {
                Text(
                    text = currentSong.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = currentSong.artist,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }

            Box {
                Text("$seekerValue")
                Seeker(
                    value = seekerValue,
                    onValueChange = { },
                    dimensions = SeekerDefaults.seekerDimensions(thumbRadius = 4.dp),
                    modifier = Modifier.padding(bottom = 0.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = formatMilliToMinutes(currentPosition),
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        text = formatMilliToMinutes(duration),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp, 0.dp, 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.SkipPrevious,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAction(MusicControlAction.PreviousSongAction) }
                        .size(48.dp)
                        .padding(8.dp)
                )
                Icon(
                    imageVector = playbackIcon,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(CircleShape)
                        .clickable { onAction(playbackAction) }
                        .size(56.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                        .padding(8.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.SkipNext,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAction(MusicControlAction.NextSongAction) }
                        .size(48.dp)
                        .padding(8.dp)
                )
            }
        }
    }
}
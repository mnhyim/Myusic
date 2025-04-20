package com.mnhyim.myusic.ui.feature.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forward5
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay5
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@Composable
fun Player(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()

    PlayerScreen(
        isPlaying = isPlaying,
        music = currentSong,
        onAction = viewModel::onAction,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun PlayerScreen(
    isPlaying: Boolean,
    music: MusicFile?,
    onAction: (MusicControlAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val (playbackIcon, playbackAction) = remember(isPlaying) {
        if (isPlaying) Icons.Outlined.Pause to MusicControlAction.PauseAction
        else Icons.Outlined.PlayArrow to MusicControlAction.ResumeAction
    }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(music?.albumArtUri),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(0.75f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            )
            music?.let {
                Text(
                    text = music.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = music.artist,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                IconButton(
                    onClick = { onAction(MusicControlAction.PreviousSongAction) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SkipPrevious,
                        contentDescription = "",
                    )
                }
                IconButton(
                    onClick = { onAction(MusicControlAction.BackwardAction) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Replay5,
                        contentDescription = "",
                    )
                }
                IconButton(
                    onClick = { onAction(playbackAction) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = playbackIcon,
                        contentDescription = "",
                    )
                }
                IconButton(
                    onClick = { onAction(MusicControlAction.ForwardAction) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Forward5,
                        contentDescription = "",
                    )
                }
                IconButton(
                    onClick = { onAction(MusicControlAction.NextSongAction) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SkipNext,
                        contentDescription = "",
                    )
                }
            }
        }
    }
}
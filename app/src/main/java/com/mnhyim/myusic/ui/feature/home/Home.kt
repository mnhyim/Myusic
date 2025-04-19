package com.mnhyim.myusic.ui.feature.home

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forward5
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay5
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.mnhyim.myusic.domain.model.MusicFile
import com.mnhyim.myusic.ui.components.MusicItem
import com.mnhyim.myusic.ui.theme.MyusicTheme
import com.mnhyim.myusic.util.MusicControlAction

@Composable
fun Home(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val musicList by viewModel.musicList.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    RequestPermissionScreen(
        onPermissionGranted = { viewModel.fetchMusic() }
    )
    HomeScreen(
        isPlaying = isPlaying,
        musicList = musicList,
        onAction = viewModel::onAction,
        onPlay = viewModel::playTrack,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    isPlaying: Boolean,
    musicList: List<MusicFile>,
    onAction: (MusicControlAction) -> Unit,
    onPlay: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMusic: MusicFile? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {},
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Outlined.Search, "")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            MusicScreen(
                music = currentMusic,
                isPlaying = isPlaying,
                onAction = onAction,
                modifier = Modifier
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = musicList,
                    key = { it.id },
                ) { music ->
                    MusicItem(
                        item = music,
                        onClick = {
                            onPlay(music.uri.toUri())
                            currentMusic = music
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(
    music: MusicFile?,
    isPlaying: Boolean,
    onAction: (MusicControlAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val (playbackIcon, playbackAction) = remember(isPlaying) {
        if (isPlaying) Icons.Outlined.Pause to MusicControlAction.PauseAction
        else Icons.Outlined.PlayArrow to MusicControlAction.ResumeAction
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = rememberAsyncImagePainter(music?.albumArtUri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .border(8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            IconButton(
                onClick = { onAction(MusicControlAction.BackwardAction) }
            ) {
                Icon(imageVector = Icons.Outlined.Replay5, contentDescription = "")
            }
            IconButton(
                onClick = { onAction(playbackAction) }
            ) {
                Icon(imageVector = playbackIcon, contentDescription = "")
            }
            IconButton(
                onClick = { onAction(MusicControlAction.ForwardAction) }
            ) {
                Icon(imageVector = Icons.Outlined.Forward5, contentDescription = "")
            }
        }
    }
}

@Composable
fun RequestPermissionScreen(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val permission = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> Manifest.permission.READ_MEDIA_AUDIO
        else -> Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> onPermissionGranted()
            else -> Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        when (ContextCompat.checkSelfPermission(context, permission)) {
            PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted()
            }

            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Requesting permission...")
    }
}

@Preview(name = "Dark Mode", showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun HomeScreenPreview() {
    MyusicTheme {
//        HomeScreen(emptyList())
    }
}
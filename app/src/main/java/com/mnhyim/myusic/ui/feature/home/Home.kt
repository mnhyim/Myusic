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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mnhyim.myusic.domain.model.MusicFile
import com.mnhyim.myusic.ui.components.MusicItem
import com.mnhyim.myusic.ui.navigation.Routes
import com.mnhyim.myusic.ui.theme.MyusicTheme

@Composable
fun Home(
    onNavigate: (Routes) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val musicList by viewModel.musicList.collectAsStateWithLifecycle()

    RequestPermissionScreen(
        onPermissionGranted = { viewModel.fetchMusic() }
    )
    HomeScreen(
        musicList = musicList,
        onPlay = { uri, musicFile -> viewModel.playTrack(uri, musicFile) },
        onAddToQueue = { uri, musicFile -> viewModel.addToQueue(uri, musicFile) },
        onNavigate = onNavigate,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    musicList: List<MusicFile>,
    onPlay: (Uri, MusicFile) -> Unit,
    onAddToQueue: (Uri, MusicFile) -> Unit,
    onNavigate: (Routes) -> Unit,
    modifier: Modifier = Modifier
) {
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
                            onClick = { onNavigate(Routes.Player) },
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = musicList,
                    key = { it.id },
                ) { music ->
                    MusicItem(
                        item = music,
                        onClick = { onPlay(music.uri.toUri(), music) },
                        onAddToQueue = { onAddToQueue(music.uri.toUri(), music) }
                    )
                }
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
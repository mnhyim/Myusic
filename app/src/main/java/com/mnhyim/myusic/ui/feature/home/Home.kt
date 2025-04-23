package com.mnhyim.myusic.ui.feature.home

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    RequestPermissionScreen(
        content = {
            val musicList by viewModel.musicList.collectAsStateWithLifecycle()
            HomeScreen(
                musicList = musicList,
                onPlay = { uri, musicFile -> viewModel.playTrack(uri, musicFile) },
                onAddToQueue = { uri, musicFile -> viewModel.addToQueue(uri, musicFile) },
                onNavigate = onNavigate,
                modifier = Modifier
            )
        }
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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = modifier.padding(innerPadding)
        ) {
            items(
                items = musicList,
                key = { it.id },
            ) { music ->
                MusicItem(
                    item = music,
                    onClick = { onPlay(music.uri.toUri(), music) },
                    onAddToQueue = { onAddToQueue(music.uri.toUri(), music) },
                    modifier = Modifier
                )
            }
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun HomeScreenPreview() {
    MyusicTheme {
        HomeScreen(
            musicList = emptyList(),
            onPlay = { uri, musicFile -> },
            onAddToQueue = { uri, musicFile -> },
            onNavigate = { },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
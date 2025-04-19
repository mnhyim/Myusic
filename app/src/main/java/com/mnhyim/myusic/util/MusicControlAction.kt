package com.mnhyim.myusic.util

sealed class MusicControlAction {
    data object PauseAction : MusicControlAction()
    data object ResumeAction : MusicControlAction()
    data object ForwardAction : MusicControlAction()
    data object BackwardAction : MusicControlAction()
}
package com.example.smartscreenguard.model

data class VideoPair(
    val id: Int,
    val title: String,
    val subtitle: String,
    val inputUri: String,
    val outputUri: String? = null
)

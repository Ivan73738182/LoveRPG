package com.ivangames.loverpg

data class Choice(
    val text: String,
    val nextSceneId: String,
    val affectionDelta: Int = 0,
    val affectionTarget: String = ""
)

data class Scene(
    val id: String,
    val text: String,
    val choices: List<Choice>,
    val location: String = "",
    val day: Int = 1
)

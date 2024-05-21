package model

import java.io.Serializable

data class MessageModel(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val imageUrl: String? = null
):Serializable
{
    // No-argument constructor
    constructor() : this("", "", 0L, null)
}

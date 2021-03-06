package com.enl

import kotlinx.serialization.Serializable

@Serializable
data class Config(val token: String, val xqCookie: String, val template: String, val channelId: String, val webhookUrl: String, val funds: List<Fund>)

@Serializable
data class Fund(val id: String, val name: String)
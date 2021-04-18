package com.enl.news

data class Feed(
    val html: String,
    val list: List<News>,
    val max_id: Int,
    val min_id: Int,
    val page_info: PageInfo,
    val survey_id: List<Any>
)
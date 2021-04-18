package com.enl.news

data class CommentList(
    val list: List<Comment>,
    val qreply: Int,
    val qreply_show: Int,
    val show: Int,
    val thread_show: Int,
    val total: Int
)
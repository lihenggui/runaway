package com.enl.news

data class PageInfo(
    val firstPage: Int,
    val lastPage: Int,
    val nextPage: Int,
    val pName: String,
    val page: Int,
    val pageSize: Int,
    val paramStr: String,
    val prePage: Int,
    val totalNum: Int,
    val totalPage: Int
)
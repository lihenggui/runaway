package com.enl.day

data class Type(
    val name: String,
    val type: Int, // enum(0, 1, 2, 3), 节假日类型，分别表示 工作日、周末、节日、调休。
    val week: Int
)
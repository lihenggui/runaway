package com.enl.news

data class Tag(
    val id: String,
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Tag) return false
        return other.id == this.id
    }
}
package com.back.standard.dto.page

data class PageableDto(
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Long,
    val totalElements: Long,
    val totalPages: Int,
    val numberOfElements: Int,
    val paged: Boolean,
)

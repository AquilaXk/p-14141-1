package com.back.boundedContexts.member.dto

import com.back.boundedContexts.member.domain.shared.Member
import java.time.Instant

data class MemberWithUsernameDto(
    val id: Int,
    val createdAt: Instant,
    val modifiedAt: Instant,
    val username: String,
    val nickname: String,
) {
    constructor(member: Member) : this(
        id = member.id,
        createdAt = member.createdAt,
        modifiedAt = member.modifiedAt,
        username = member.username,
        nickname = member.nickname,
    )
}

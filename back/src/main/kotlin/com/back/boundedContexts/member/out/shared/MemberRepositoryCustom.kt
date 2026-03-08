package com.back.boundedContexts.member.out.shared

import com.back.boundedContexts.member.domain.shared.Member

interface MemberRepositoryCustom {
    fun findByUsername(username: String): Member?
}
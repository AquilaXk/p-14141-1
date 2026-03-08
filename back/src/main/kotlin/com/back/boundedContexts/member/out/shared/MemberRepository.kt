package com.back.boundedContexts.member.out.shared

import com.back.boundedContexts.member.domain.shared.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Int>, MemberRepositoryCustom {
    override fun findAll(pageable: Pageable): Page<Member>
}

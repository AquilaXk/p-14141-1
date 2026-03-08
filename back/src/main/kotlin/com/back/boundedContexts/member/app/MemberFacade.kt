package com.back.boundedContexts.member.app

import com.back.boundedContexts.member.domain.shared.Member
import com.back.boundedContexts.member.out.shared.MemberRepository
import com.back.global.exception.app.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MemberFacade(
    private val memberRepository: MemberRepository
) {
    @Transactional(readOnly = true)
    fun count(): Long = memberRepository.count()

    @Transactional
    fun join(username: String, password: String?, nickname: String): Member {
        memberRepository.findByUsername(username)?.let {
            throw BusinessException("409-1", "이미 존재하는 회원 아이디입니다.")
        }

        val member = memberRepository.save(
            Member(
                0,
                username,
                password,
                nickname,
                UUID.randomUUID().toString()
            )
        )

        return member
    }

    @Transactional(readOnly = true)
    fun findByUsername(username: String): Member? = memberRepository.findByUsername(username)
}
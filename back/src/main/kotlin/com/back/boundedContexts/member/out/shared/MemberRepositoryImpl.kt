package com.back.boundedContexts.member.out.shared

import com.back.boundedContexts.member.domain.shared.Member
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.Session

class MemberRepositoryImpl : MemberRepositoryCustom {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findByUsername(username: String): Member? =
        entityManager.unwrap(Session::class.java)
            .byNaturalId(Member::class.java)
            .using("username", username)
            .load()
}
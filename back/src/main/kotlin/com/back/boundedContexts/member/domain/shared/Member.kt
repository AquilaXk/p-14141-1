package com.back.boundedContexts.member.domain.shared

import com.back.global.jpa.domain.AfterDDL
import com.back.global.jpa.domain.BaseTime
import jakarta.persistence.*
import jakarta.persistence.GenerationType.SEQUENCE
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NaturalId

@Entity
@DynamicUpdate
@AfterDDL(
    """
    CREATE INDEX IF NOT EXISTS member_idx_created_at_desc
    ON member (created_at DESC)
"""
)
@AfterDDL(
    """
        CREATE INDEX IF NOT EXISTS member_idx_modified_at_desc
        ON member (modified_at DESC)
"""
)
@AfterDDL(
    """
    CREATE INDEX IF NOT EXISTS member_idx_pgroonga_username_nickname
    ON member USING pgroonga ((ARRAY["username"::text, "nickname"::text])
    pgroonga_text_array_full_text_search_ops_v2) WITH (tokenizer = 'TokenBigram')
    """
)
class Member(
    @field:Id
    @field:SequenceGenerator(name = "member_seq_gen", sequenceName = "member_seq", allocationSize = 50)
    @field:GeneratedValue(strategy = SEQUENCE, generator = "member_seq")
    override val id: Int = 0,

    @field:NaturalId
    @field:Column(unique = true, nullable = false)
    val username: String,

    @field:Column(nullable = true)
    var password: String? = null,

    @field:Column(nullable = false)
    var nickname: String,

    @field:Column(unique = true, nullable = false)
    var apiKey: String,
) : BaseTime(id) {
    fun modifyApiKey(apiKey: String) {
        this.apiKey = apiKey
    }
}
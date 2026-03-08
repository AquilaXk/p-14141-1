package com.back.boundedContexts.member.`in`

import com.back.boundedContexts.member.app.MemberFacade
import com.back.boundedContexts.member.dto.MemberWithUsernameDto
import com.back.standard.dto.member.type1.MemberSearchSortType1
import com.back.standard.dto.page.PageDto
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member/api/v1/adm/members")
class ApiV1AdmMemberController(
    private val memberFacade: MemberFacade,
) {
    @GetMapping
    @Transactional(readOnly = true)
    fun getItems(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "30") pageSize: Int,
        @RequestParam(defaultValue = "") kw: String,
        @RequestParam(defaultValue = "CREATED_AT") sort: MemberSearchSortType1,
    ): PageDto<MemberWithUsernameDto> {
        val normalizedPage = if (page >= 1) page else 1
        val normalizedPageSize = if (pageSize in 1..30) pageSize else 30

        return PageDto(
            memberFacade.findPagedByKw(
                kw = kw,
                sort = sort,
                page = normalizedPage,
                pageSize = normalizedPageSize,
            ).map(::MemberWithUsernameDto)
        )
    }
}

package com.back.boundedContexts.member.`in`

import com.back.boundedContexts.member.app.MemberFacade
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiV1AdmMemberControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var memberFacade: MemberFacade

    @Test
    fun `기본 목록 조회가 된다`() {
        mvc.perform(get("/member/api/v1/adm/members"))
            .andExpect(status().isOk)
            .andExpect(handler().handlerType(ApiV1AdmMemberController::class.java))
            .andExpect(handler().methodName("getItems"))
            .andExpect(jsonPath("$.content.length()").value(memberFacade.count().toInt()))
            .andExpect(jsonPath("$.pageable.pageNumber").value(1))
            .andExpect(jsonPath("$.pageable.pageSize").value(30))
    }

    @Test
    fun `username과 nickname을 통합 검색한다`() {
        memberFacade.join("android-a", "1234", "안드로이드 가이드")
        memberFacade.join("guide-search", "1234", "안드로이드 레시피")
        memberFacade.join("dev-guide", "1234", "개발 가이드")
        memberFacade.join("android-guide", "1234", "일반 사용자")

        mvc.perform(
            get("/member/api/v1/adm/members")
                .param("page", "1")
                .param("pageSize", "10")
                .param("kw", "android")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[*].username").value(containsInAnyOrder("android-a", "android-guide")))
    }

    @Test
    fun `공백 검색어는 검색을 적용하지 않는다`() {
        mvc.perform(
            get("/member/api/v1/adm/members")
                .param("kw", "   ")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(memberFacade.count().toInt()))
    }
}

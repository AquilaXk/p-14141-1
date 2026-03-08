package com.back.boundedContexts.member.out.shared

import com.back.boundedContexts.member.app.MemberFacade
import com.back.boundedContexts.member.domain.shared.Member
import com.back.standard.dto.member.type1.MemberSearchSortType1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var memberFacade: MemberFacade

    private fun join(username: String, nickname: String): Member =
        memberFacade.join(username, "1234", nickname)

    private fun search(kw: String): Page<Member> =
        memberRepository.findQPagedByKw(
            kw,
            PageRequest.of(0, 100, MemberSearchSortType1.CREATED_AT.sortBy)
        )

    @Nested
    inner class `1 기본 검색` {
        @Test
        fun `단일 키워드 - username에 있으면 매칭된다`() {
            join("zzalpha-user", "평범한닉네임")
            join("zzbeta-user", "다른닉네임")

            val result = search("zzalpha")

            assertThat(result.content.map { it.username }).contains("zzalpha-user")
            assertThat(result.content.map { it.username }).doesNotContain("zzbeta-user")
        }

        @Test
        fun `단일 키워드 - nickname에 있으면 매칭된다`() {
            join("plain-user-a", "망고 연구회")
            join("plain-user-b", "사과 연구회")

            val result = search("망고")

            assertThat(result.content.map { it.nickname }).contains("망고 연구회")
            assertThat(result.content.map { it.nickname }).doesNotContain("사과 연구회")
        }

        @Test
        fun `단일 키워드 - username과 nickname 중 한쪽에만 있어도 매칭된다`() {
            join("papaya-user", "여름모임")
            join("summer-user", "papaya club")

            val result = search("papaya")

            assertThat(result.content.map { it.username }).contains("summer-user")
            assertThat(result.content.map { it.username }).contains("papaya-user")
        }
    }

    @Nested
    inner class `2 AND OR 검색` {
        @Test
        fun `공백 AND - 두 키워드를 모두 포함한 회원만 매칭된다`() {
            join("android-a", "안드로이드 가이드")
            join("android-only", "안드로이드 레시피")
            join("guide-only", "개발 가이드")

            val result = search("안드로이드 가이드")

            assertThat(result.content.map { it.username }).containsExactly("android-a")
        }

        @Test
        fun `플러스 AND - 명시적 AND도 동작한다`() {
            join("cloud-aws", "클러스터 운영")
            join("cloud-only", "클라우드 운영")
            join("cluster-only", "클러스터 운영팀")

            val result = search("+cloud +클러스터")

            assertThat(result.content.map { it.username }).containsExactly("cloud-aws")
        }

        @Test
        fun `OR - 둘 중 하나라도 있으면 매칭된다`() {
            join("python-user", "데이터 분석")
            join("javascript-user", "웹 개발")
            join("java-user", "백엔드 개발")

            val result = search("python OR javascript")

            assertThat(result.content.map { it.username })
                .containsExactlyInAnyOrder("python-user", "javascript-user")
        }
    }

    @Nested
    inner class `3 NOT 검색` {
        @Test
        fun `마이너스 검색어 - 제외 단어를 포함한 회원은 빠진다`() {
            join("apple-banana", "과일 연구회")
            join("apple-only", "과일 스터디")

            val result = search("apple -banana")

            assertThat(result.content.map { it.username }).containsExactly("apple-only")
        }

        @Test
        fun `크로스 컬럼 제외 - nickname에 제외어가 있으면 username이 매칭돼도 빠진다`() {
            join("search-hit", "제외단어 포함")
            join("search-pass", "정상 회원")

            val result = search("search -제외단어")

            assertThat(result.content.map { it.username }).containsExactly("search-pass")
        }

        @Test
        fun `복수 제외 - 여러 제외 단어를 동시에 적용한다`() {
            join("fruit-apple", "바나나 동호회")
            join("fruit-orange", "오렌지 동호회")
            join("fruit-kiwi", "키위 동호회")

            val result = search("fruit -바나나 -오렌지")

            assertThat(result.content.map { it.username }).containsExactly("fruit-kiwi")
        }
    }

    @Nested
    inner class `4 구문 전위 그룹 검색` {
        @Test
        fun `구문 검색 - 따옴표 안 표현이 연속으로 있어야 한다`() {
            join("phrase-hit", "빠른 정렬 연구회")
            join("phrase-miss", "빠른 알고리즘 정렬 연구회")

            val result = search("\"빠른 정렬\"")

            assertThat(result.content.map { it.username }).containsExactly("phrase-hit")
        }

        @Test
        fun `전위 검색 - 영문 prefix가 동작한다`() {
            join("spring-guide", "프레임워크")
            join("springboot-lab", "자동설정")
            join("hibernate-lab", "ORM")

            val result = search("spring*")

            assertThat(result.content.map { it.username })
                .containsExactlyInAnyOrder("spring-guide", "springboot-lab")
        }

        @Test
        fun `그룹 OR 와 AND 조합 - 괄호가 적용된다`() {
            join("mysql-designer", "스키마 설계")
            join("postgres-designer", "데이터 설계")
            join("mysql-backup", "백업 운영")

            val result = search("(mysql OR postgres) 설계")

            assertThat(result.content.map { it.username })
                .containsExactlyInAnyOrder("mysql-designer", "postgres-designer")
        }

        @Test
        fun `그룹 OR 와 NOT 조합 - 괄호 결과에서 제외어를 적용한다`() {
            join("python-crawler", "데이터 수집")
            join("r-visualizer", "데이터 시각화")
            join("python-ml", "머신러닝 실험실")
            join("r-ml", "통계 머신러닝")

            val result = search("(python OR r) -머신러닝")

            assertThat(result.content.map { it.username })
                .containsExactlyInAnyOrder("python-crawler", "r-visualizer")
        }
    }
}

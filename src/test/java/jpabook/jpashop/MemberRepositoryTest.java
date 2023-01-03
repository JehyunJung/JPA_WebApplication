package jpabook.jpashop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 객체를 저장하고 검색하는 기능 테스트")
    @Transactional
    @Rollback(false)
    void testMember() {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long savedId = memberRepository.save(member);
        Member savedMember = memberRepository.find(savedId);

        //then
        assertThat(savedId).isEqualTo(member.getId());
        assertThat(savedMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(savedMember).isEqualTo(member);
    }
}
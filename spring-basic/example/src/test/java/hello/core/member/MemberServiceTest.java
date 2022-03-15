package hello.core.member;

import static org.assertj.core.api.Assertions.assertThat;

import hello.core.AppConfig;
import hello.core.domain.member.Grade;
import hello.core.domain.member.Member;
import hello.core.service.MemberService;
import hello.core.service.MemberServiceImpl;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

  AppConfig appConfig = new AppConfig();
  private final MemberService memberService = appConfig.memberService();

  @Test
  void join() {
    Member member = new Member(1L, "member1", Grade.BASIC);
    memberService.join(member);

    Member findMember = memberService.findMember(member.getId());

    assertThat(member.getId()).isEqualTo(findMember.getId());
  }
}

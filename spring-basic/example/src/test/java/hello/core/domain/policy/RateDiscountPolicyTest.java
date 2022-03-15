package hello.core.domain.policy;

import static org.junit.jupiter.api.Assertions.*;

import hello.core.domain.member.Grade;
import hello.core.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RateDiscountPolicyTest {

  DiscountPolicy discountPolicy = new RateDiscountPolicy();

  @Test
  void vip_o() {
    Member member = new Member(1L, "memberVIP", Grade.VIP);

    int discount = discountPolicy.discount(member, 10000);

    Assertions.assertThat(discount).isEqualTo(1000);
  }

  @Test
  void vip_x() {
    Member member = new Member(1L, "memberBASIC", Grade.BASIC);

    int discount = discountPolicy.discount(member, 10000);

    Assertions.assertThat(discount).isEqualTo(0);
  }

}
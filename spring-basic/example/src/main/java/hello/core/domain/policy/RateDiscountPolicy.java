package hello.core.domain.policy;

import hello.core.domain.member.Grade;
import hello.core.domain.member.Member;
import org.springframework.stereotype.Component;

@Component
public class RateDiscountPolicy implements DiscountPolicy{

  private final int discountRate = 10;

  @Override
  public int discount(Member member, int price) {
    return member.getGrade() == Grade.VIP ? price * discountRate / 100 : 0;
  }
}

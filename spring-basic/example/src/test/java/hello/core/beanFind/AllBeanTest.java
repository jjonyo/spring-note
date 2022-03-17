package hello.core.beanFind;

import hello.core.AutoAppConfig;
import hello.core.domain.member.Grade;
import hello.core.domain.member.Member;
import hello.core.domain.policy.DiscountPolicy;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AllBeanTest {

  @Test
  void findAllBean() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class,
        DiscountService.class);

    DiscountService discountService = ac.getBean(DiscountService.class);
    Member member = new Member(1L, "member1", Grade.VIP);
    int fixDiscountPrice = discountService.discount(member, 1000, "rateDiscountPolicy");

    Assertions.assertThat(fixDiscountPrice).isEqualTo(100);
  }


  static class DiscountService {
    private final Map<String, DiscountPolicy> policyMap;

    public DiscountService(
        Map<String, DiscountPolicy> policyMap) {
      this.policyMap = policyMap;
    }

    public int discount(Member member, int price, String discountCode) {
      DiscountPolicy discountPolicy = policyMap.get(discountCode);

      return discountPolicy.discount(member, price);
    }
  }
}

package hello.core.order;

import hello.core.AppConfig;
import hello.core.domain.member.Grade;
import hello.core.domain.member.Member;
import hello.core.domain.order.Order;
import hello.core.repository.MemberRepository;
import hello.core.repository.MemoryMemberRepository;
import hello.core.service.MemberService;
import hello.core.service.MemberServiceImpl;
import hello.core.service.OrderService;
import hello.core.service.OrderServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;

public class OrderServiceTest {

  AppConfig appConfig = new AppConfig();

  MemberService memberService = appConfig.memberService();
  OrderService orderService = appConfig.orderService();

  @Test
  void order() {
    //given
    Member member = new Member(1L, "memberA", Grade.VIP);
    memberService.join(member);

    //when
    Order order = orderService.createOrder(1L, "item1", 2000);

    //then
    Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
  }

}

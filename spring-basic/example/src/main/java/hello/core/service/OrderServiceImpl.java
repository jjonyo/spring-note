package hello.core.service;

import hello.core.domain.member.Member;
import hello.core.domain.order.Order;
import hello.core.domain.policy.DiscountPolicy;
import hello.core.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

  private final MemberRepository memberRepository;
  private final DiscountPolicy discountPolicy;

  @Override
  public Order createOrder(Long memberId, String itemName, int itemPrice) {
    Member member = memberRepository.findById(memberId);
    System.out.println(member);
    int discountAmount = discountPolicy.discount(member, itemPrice);

    return new Order(memberId, itemName, itemPrice, discountAmount);
  }
}

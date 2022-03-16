package hello.core.service;

import hello.core.domain.member.Member;
import hello.core.domain.order.Order;
import hello.core.domain.policy.DiscountPolicy;
import hello.core.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceImpl implements OrderService{

  private MemberRepository memberRepository;
  private DiscountPolicy discountPolicy;

  @Autowired
  public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
  }

  @Override
  public Order createOrder(Long memberId, String itemName, int itemPrice) {
    Member member = memberRepository.findById(memberId);
    System.out.println(member);
    int discountAmount = discountPolicy.discount(member, itemPrice);

    return new Order(memberId, itemName, itemPrice, discountAmount);
  }
}

package hello.core.service;

import hello.core.domain.member.Member;
import hello.core.domain.order.Order;
import hello.core.domain.policy.DiscountPolicy;
import hello.core.domain.policy.FixDiscountPolicy;
import hello.core.repository.MemberRepository;
import hello.core.repository.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService{

  MemberRepository memberRepository = new MemoryMemberRepository();
  DiscountPolicy discountPolicy = new FixDiscountPolicy();

  @Override
  public Order createOrder(Long memberId, String itemName, int itemPrice) {
    Member member = memberRepository.findById(memberId);
    System.out.println(member);
    int discountAmount = discountPolicy.discount(member, itemPrice);

    return new Order(memberId, itemName, itemPrice, discountAmount);
  }
}

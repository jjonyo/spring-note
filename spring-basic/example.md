# 스프링 핵심 원리 이해-1

## 비즈니스 요구사항과 설계

### 회원

- 가입과 조회가 가능해야한다.
- 일반과 VIP 두가지 등급이 존재한다.
- 데이터는 자체 DB를 구축하거나, 외부 시스템과 연동할 수 있다.(확정 전)

### 주문과 할인 정책

- 상품을 주문할 수 잇다.
- 등급에 따라 할인정책 적용할수 있다.
- VIP는 1000원을 할인해주는 고정 금액 할인 적용(정책은 변경 가능)
- 회사의 기본 할인 정책을 정하지 못했고 오픈 직전까지 미룰 수 있으며 최악의 경우에는 할인을 적용하지 않을 수 있다. (확정 전)

## 회원 도메인 설계

데이터 저장방법이 확정되지 않았으므로 우선은 `메모리 회원 저장소` 를 통해 데이터를 저장하자.

## 새로운 할인 정책 개발

고정 금액 할인이 아니라 주문 금액당 할인하는 정률% 할인으로 변경하고 싶다고 요구사항이 왔다. 해당 기능에 맞게 기능을 추가해야한다.

기존의 `DiscountPolicy` 역할에 해당하는 구현체를 `FixDiscountPolicy` 에서 `RateDiscountPolicy` 로 변경하면 된다.

 

`FixDiscouontPolicy` 를 구현했을 때, 우리는 역할과 구현을 충분히 분리했으며 다형성도 활용하고 인터페이스와 구현 객체를 분리했다.
하지만 OCP, DIP와 같은 객체지향 설계를 완전히 만족하지는 않았다.

`OrderServiceImpl` 을 보면 `DiscountPolicy` 의 구현체를 의존하고 있다.
이는 항상 추상화를 의존해야 하는 DIP를 위반 하는 것이다.
결국 할인정책을 변경하기 위해서는 `OrderServiceImpl` 의 소스코드를 변경해야 한다. 이는 결국 OCP원칙을 위반하는 것이다.

## 어떻게 해결할 수 있을까?

DIP를 위반하지 않도록 인터페이스만 의존하도록 코드를 변경해야 한다.

`private final DiscountPolicy discountPolicy` 와 같이 선언해보자.
이렇게 하면 인터페이스에만 의존할 수 있지만 구현체가 없으므로 에러가 발생한다.

따라서, 이 문제를 해결하기 위해서는 누군가 `OrderServiceImpl` 에 `DiscountPolicy` 의 구현체를 직접 주입해줘야 한다. 이를 `의존성 주입` 이라고 한다.

## 관심사 분리

`로미오와 줄리엣` 공연을 생각해보자.
로미오와 줄리엣에 해당하는 배우를 결정하는 것은 기획자가 해야 할 일이다.
로미오 역을 맡은 배우가 줄리엣 역을 맡은 배우를 결정하는 것은 말이 안된다. 이는 로미오 역할의 배우에게 너무 많은 책임을 지는 것이다.

이 예제 코드에서 `OrderServiceImpl` 이 특정 인터페이스의 구현체를 결정하는 것은 로미오 역할의 배우가 줄리엣 역할의 배우를 결정하는 것과 같은 의미이다.

각각의 배우는 자신의 역할을 수행하는 것에만 집중해야 한다. 로미오 역할의 배우는 줄리엣 역할의 배우가 누구이건간에 자신의 역할에 집중해야 한다.

그리고 로미오와 줄리엣의 역할에 알맞은 배우를 지정하는 책임은 `기획자` 가 결정해야 하는 것이다.

마찬가지로, 프로젝트에서 구현체를 결정하는 것은 `프로그래머` 가 해야 하는 일이다.
따라서 상황에 맞게 알맞은 구현체를 직접 주입해야한다! 이것이 의존성 주입이다.

## AppConfig

애플리케이션의 전체 동작 방식을 구성하기 위해 구현 객체를 생성하고 연결 하는 책임을 가지는 별도의 설정 클래스를 만든다.

AppConfig는 애플리케이션 실제 동작에 필요한 `구현 객체` 를 생성한다.
그리고 AppConfig는 객체 인스턴스의 참조를 `생성자` 를 통해 주입한다.

이렇게 하면 OrderService, MemberService와 같은 구현객체들은 이제 의존 관계에 대한 고민은 외부에 맡기고 실행에만 집중하면 된다.

```java
package hello.core;

import hello.core.domain.policy.RateDiscountPolicy;
import hello.core.repository.MemoryMemberRepository;
import hello.core.service.MemberService;
import hello.core.service.MemberServiceImpl;
import hello.core.service.OrderService;
import hello.core.service.OrderServiceImpl;

public class AppConfig {

  public MemberService memberService() {
    return new MemberServiceImpl(new MemoryMemberRepository());
  }

  public OrderService orderService() {
    return new OrderServiceImpl(new MemoryMemberRepository(), new RateDiscountPolicy());
  }
  
}
```

이렇게 `AppConfig` 를 구성하고 이후 구현객체가 필요할때는
`AppConfig appConfig = new AppConfig();`

`MemberService memberService = appConfig.memberService()`

이렇게 사용하면 된다.

## AppConfig 리팩토링

기존 AppConfig는 중복코드가 존재하며, 역할에 따른 구현이 직관적으로 보이지 않는다.

```java
package hello.core;

import hello.core.domain.policy.FixDiscountPolicy;
import hello.core.repository.MemoryMemberRepository;
import hello.core.service.MemberService;
import hello.core.service.MemberServiceImpl;
import hello.core.service.OrderService;
import hello.core.service.OrderServiceImpl;

public class AppConfig {

  public MemberService memberService() {
    return new MemberServiceImpl(memberRepository());
  }

  public MemoryMemberRepository memberRepository() {
    return new MemoryMemberRepository();
  }

  public OrderService orderService() {
    return new OrderServiceImpl(memberRepository(), discountPolicy());
  }

  public FixDiscountPolicy discountPolicy() {
    return new FixDiscountPolicy();
  }

}
```

이렇게 리팩토링 해보았다. 이렇게하면 각각의 역할이 쉽게 보인다.
따라서, 만약 구현 객체를 변경해야 할 경우 이곳에서 구현체만 변경하면 다른 서비스의 수정없이 변경이 가능하다.

## 새로운 할인정책 적용

이제 `FixDiscountPolicy` 에서 `RateDiscountPolicy` 로 변경해보자.
이제는 `AppConfig` 만 변경하면 다른 사용 영역의 코드는 전혀 손댈 필요가 없다.
구성영역이 변경되는 것은 어쩔 수 없다. 하지만 사용영역의 변경 없이 요구사항을 반영할 수 있다는 점이 DI의 장점이다.

결과적으로 이를 통해 객체지향 설계의 SRP, DIP, OCP원칙을 적용하게 되었다.

SRP

- 기존 서비스는 집적 객체를 생성하고, 연결하고, 실행하는 등 많은 책임을 가졌다.
- SRP원칙에 따라 관심사를 분리했다.
- 구현 객체를 생성하고 연결은 `AppConfig` 가 하며 클라이언트 객체는 실행하는 책임만 담당한다.

DIP

- 이제 클라이언트 코드에서 구현 객체를 의존하지 않고 인터페이스를 의존하게 되었다.
- 인터페이스만으로는 코드를 실행할 수 없지만 AppConfig를 통해 의존관계를 주입받아 사용할 수 있다.

OCP

- 클라이언트의 코드를 변경하지 않고 기능을 확장할 수 있게 되었다.

## IOC, DI 그리고 컨테이너

제어역전(IOC)란 프레임워크의 프로그램의 제어 흐름을 맡기는 것을 말한다.

의존관계는 `정적인 클래스 의존관계`와, 실행 시점에 결정되는 `동적인 객체 의존 관계` 둘을 분리해서 생각해야 한다.

정적인 클래스 의존관계는 애플리케이션을 실행하지 않아도 분석할 수 있다. 
반면, 동적인 객체 인스턴스 의존 관계는 애플리케이션 실행 시점에 실제 생성된 객체 인스턴스의 참조가 연결된 의존 관계다.

애플리케이션 런타임 시점에 외부에서 실제 구현객체를 생성하고 클라이언트에 전달하여 서버의 실제 의존관계가 되는것을 의존관계 주입이라고 한다. 의존관계 주입을 사용하면 클라이언트 코드를 변경하지 않고, 클라이언트가 호출하는 대상의 타입 인스턴스를 변경할 수 있다. 또한, 의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인슽턴스 의존관계를 쉽게 변경할 수 있다.

위에서 만든 AppConfig와 같이 객체를 생성하고 관리하며 의존관계를 연결해주는 것을 `IOC컨테이너` 또는 `DI컨테이너` 라고 한다.
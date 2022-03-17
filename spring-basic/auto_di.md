# 의존관계 자동 주입

## 다양한 의존관계 주입 방법

의존관계의 주입에는 크게 4가지 방법이 있다.

- 생성자 주입
- 수정자 주입(setter 주입)
- 필드 주입
- 일반 메서드 주입

### 생성자 주입

지금까지 했던 방법이다. 생성자를 통해서 의존관계를 주입 받는다.
생성자 호출 시점에 딱 1번만 호출되는 것이 보장되며 `불변, 필수` 의존관계에 사용된다.

만약, 생성자가 단 1개만 있따면 `@Autowired` 를 생략해도 자동주입 된다.
또한 역할에 해당하는 클래스를 `final` 로 선언하면 생성자가 호출되는 시점에 반드시 해당 클래스가 주입되어야 한다.

가장 추천되는 방법이다.

### 수정자 주입(setter 주입)

수정자 메서드를 통해 의존관계를 주입하는 방법이다.

주로 선택,변경 가능성이 있는 의존관계에 사용된다.

```java
private MemberRepository memberRepository;
private DiscountPolicy discountPolicy;

@Autowired
public void setMemberRepository(MemberRepository memberRepository) {
	this.memberRepository = memberRepository;
}

@Autowired
public void setDiscountPolicy(DiscountPolicy discountPolicy) {
	this.discountPolicy = discountPolicy;
}
```

> 무조건 getter, setter를 통해 클래스의 필드 값을 접근하는 것을 자바빈 프로퍼티 규약 이라고 한다.
> 

### 필드 주입

필드에 바로 주입하는 방법이다.

코드가 간결하지만 테스트하기가 힘들다는 단점이 있다.
왜냐하면, 애초에 객체가 생성되는 시점에 주입이 되기 때문이다.
그러면 테스트코드에서 mock db를 만들어서 넣고 싶어도 넣을 수 있는 방법이 없다.

DI프레임워크가 없다면 아무것도 할 수 없다.

```java
@Autowired
private MemberRepository memberRepository;

@Autowired
private DiscountPolicy discountPolicy;
```

최대한 사용하지 말자!

### 일반 메서드 주입

일반 메서드를 통해 주입받을 수 있다.

한번에 여러 필드를 주입받을 수 있다. 하지만, 일반적으로 잘 사용하지 않는다.

당연하지만, 의존관계 자동 주입은 스프링 컨테이너가 관리하는 `스프링 빈` 이여야 동작한다. 스프링 빈이 아닌 곳에서 `@Autowired` 를 적는다고해도 자동 의존관계 주입은 되지 않는다.

## 옵션처리

주입할 스프링 빈이 없어도 동작해야 할 때가 있다.
`@Autowired` 만 사용하면 `required` 의 기본값이 true이기에 자동 주입대상이 없으면 오류가 발생한다.

총 세가지 방식이 있다.

```java
//호출 안됨
@Autowired(required = false)
public void setNoBean1(Member member) {
 System.out.println("setNoBean1 = " + member);
}

//null 호출
@Autowired
public void setNoBean2(@Nullable Member member) {
 System.out.println("setNoBean2 = " + member);
}

//Optional.empty 호출
@Autowired(required = false)
public void setNoBean3(Optional<Member> member) {
 System.out.println("setNoBean3 = " + member);
}
```

첫번째는 required를 false옵션을 주는 것이다. 자동 주입할 대상이 없으면 아예 메소드 호출이 안된다.
두번째는 만약 없을 경우 null이 member에 들어가게 된다.
세번째는 Optional값이 들어오게 된다.

## 생성자 주입을 선택하는 이유

과거에는 수정자 주입과 필드수입을 많이 사용했지만 최근에는 스프링을 포함한 대부분의 DI프레임워크는 생성자 주입을 권장한다.

그 이유는 `불변` 에 있다.
대부분의 애플리케이션은 의존관계 주입이 한번 일어나면 종료시점까지 의존관계를 변경할 일이 없다. 오히려 변하면 안된다.

만약 수정자 주입을 사용한다면 메소드를 public으로 열어두어야하고 이는 누군가의 실수로 인해 접근될 수 있다.
따라서 애초에 불변되게 설계하는 것이 가장 좋은 방법이다.

그 다음 이유는 `누락` 이다.

프레임워크가 없이 순수한 자바 코드로 단위 테스트를 하는 경우가 있다.
이때 프레임워크가 없으므로, 의존관계가 없을 때 오류가 발생하지 않고 런타임에 오류가 발생하게 된다.
하지만, 생성자 주입을 사용하게되면 순수 자바코드로 테스트를 할 지라도 컴파일 오류가 발생하여 필요한 의존관계를 바로 파악할 수 있다.

다음은 `final` 키워드이다.

생성자 주입을 사용하면 필드에 final을 사용할 수 있다.
이는 혹시라도, 생성자를 통해 값이 설정되지 않는 오류를 컴파일 시점에 막아줄 수 있다.

결과적으로 다음과 같은 이유때문에 생성자 주입을 선택해야 한다.

- 프레임워크에 의존하지 않고 순수한 자바 언어의 특성을 살릴 수 있다.
- 필수값이 아닌 경우에만 수정자 주입방식을 옵션으로 부여하자
- 그리고 그 외의 상황은 항상 생성자 주입을 사용하자. 필드 주입은 사용하지 않는게 좋다.

## 롬복과 최신 트렌드

자동 의존관계 주입 방법 중 생성자 주입방법이 가장 추천되는 방법인 것을 알았다.

하지만, 생성자 주입을 받을 때는 생성자도 만들어야하고, 주입 받을 값을 대입하는 코드도 만들어야한다.
개발자들은 귀찮은 것을 못참는다. 그래서 더 의존관계 주입을 더 편리하게 해주는 것들이 있다.

`롬복` 이라는 라이브러리를 사용해보자.
롬복라이브러리가 제공하는 `@RequireArgsConstructor` 기능을 사용하면 final이 붙은 필드를 모아서 생성자를 자동으로 만들어 준다.

`build.gradle` 에 롬복과 관련된 코드들을 추가하고, 인텔리제이 플러그인도 설치하자.
그다음 설정에서 `Annotaion Processors` 에서 `Enable annotaion processing` 을 체크해줘야 한다.

`@RequireArgsConstructor` 를 클래스위에 붙어주면 require되는(final) 생성자를 자동으로 만들어준다.
또한 생성자가 자동으로 만들어지기에 의존관계 자동 주입도 가능하다.

## 조회 빈이 2개 이상일때의 문제

의존관계 자동주입을 할 때, 스프링빈에 동일한 타입이 두개 이상 있으면 어떤 걸 선택해야 하는지모른다.

이때는 하위타입으로 지정하거나, 수동으로 빈 등록해서 해결할 수 있겠지만 자동 주입에서도 해결하는 방법이 있다.

첫번째는 `@Autowired` 에 필드명을 매칭시키는 방법이다.
만약 타입이 여러개가 있으면 필드이름, 파라미터 이름으로 빈 이름을 추가 매칭하는 기능이 있다.

```java
@Autowired
private DiscountPolicy discountPolicy
```

이렇게하면, 스프링 빈에 있는 두개의 구현체중 어떤 것을 선택해야 하는지 모른다.

```java
@Autowired
private DiscountPolicy rateDiscountPolicy
```

하지만 이렇게 필드명을 구현체의 이름으로 작성하면 정상적으로 자동 주입이 가능하다.

두번째 방법은 `@Qualifier` 를 사용하는 것이다. 이는 구분자를 추가하여 추가적인 방법을 제공하는 것이다. 빈 이름을 변경하는 것은 아니다.

빈 등록시 `@Qualifier("mainDiscountPolicy")` 이렇게 어노테이션을 붙여놓은 다음
이후에 자동 주입을 받고자 할 때 `@Qualifier("mainDiscountPolicy") DiscountPolicy` 이렇게 사용하면 된다.

마지막 방법은 `@Primary` 를 사용하는 방법이다.

이것은 우선순위를 지정할 수 있는 방법으로 동일한 타입의 빈이 여러개 발견될 시 `@Primary` 가 붙은 클래스가 우선순위를 가진다.

스프링은 자동보다는 수동이, 넓은 범위보다는 좁은 범위의 선택권이 우선순위를 가진다.
따라서, `@Primary` 와 `@Qualifier` 가 두개 있다면 `@Qualifier` 가 우선순위가 높다.

## 어노테이션 만들기

`@Qualifier("mainDiscountPolicy")` 이런식으로 사용하면 문자가 들어가므로 컴파일 시점에 타입 체크가 되지 않는다. 이럴 때는 어노테이션을 만들어 문제를 해결할 수 있다.

```java
package hello.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Qualifier;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
    ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier("mainDiscountPolicy")
public @interface MainDiscountPolicy {

}
```

이렇게 어노테이션을 만들고, 사용하고자 하는 곳에 `@MainDiscountPolicy` 를 사용하면 혹시나 오타가 발생해도 컴파일 시점에 알 수 있으며, 사용하고자 하는 곳에서도 이 어노테이션을 붙여 사용하면 된다.

## 조회한 빈이 모두 필요할 때

의도적으로 해당 타입의 스프링 빈이 다 필요한 경우도 있다.

예를들어, 할인 정책이 여러개가 있는데 클라이언트가 할인정책을 선택하게 하는 경우이다.
스프링을 사용하면 전략 패턴을 매우 간단하게 구현할 수 있다.

```java
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
```

이렇게 전체 빈을 Map에 담고, 필요에따라 원하는 빈을 사용하도록 할 수 있다.
이런것을 전략패턴 이라고 한다.

## 자동, 수동 올바른 실무 운영 기준

편리한 자동 기능을 기본으로 사용하는 것이 좋다.
빈이 많아지면 설정 정보를 관리하는 것 자체가 부담이 될 수 있다.
최근 스프링 부트도 기본적으로 컴포넌트 스캔 방식을 지원하며, 최대한 자동 등록을 사용하는 것이 좋다.

그렇다면 언제 수동 빈 등록을 하면 좋을까?

애플리케이션은 크게 업무 로직과 기술 지원 로직으로 나눌 수 있다.

- 업무로직 : 웹을 지원하는 컨트롤러, 서비스 등 핵심 비즈니스 로직이다. 보통 비즈니스 요구사항을 개발할때 추가되거나 변경된다.
- 기술 지원 : 기술적인 문제나 공통관심사를 처리할 때 주로 사용된다. 데이터베이스 연결이나 공통 로그 처럼 업무 로직을 지원하기 위한 하부 기술이나 공통 기술이다.

업무로직은 숫자도 많고 한번 개발해야하면 유사한 패턴을 사용하게 된다. 따라서 이런 경우 자동 기능을 적극 사용하는 것이 좋다. 

기술지원 로직은 업무로직과 비교하여 그 수가 적다. 기술지원 로직들은 집적 수동 빈 등록하여 명확하게 들어내는 것이 좋다.

> 애플리케이션에 광범위하게 영향을 미치는 기술지원 객체는 수동 빈으로 등록해서 설정 정보에 바로 나타나게 하는 것이 유지보수에 좋다.
> 

다만, 비즈니스 로직 중에서도 다형성을 적극 활용할 때는 수동빈 등록을 사용하는 것이 좋을 때가 있다.

예를들어 위의 예시에서 두개의 할인정책을 전략패턴으로 구현할 때, 처음 코드를 본사람은 어떤 구현체들이 있는지 바로 파악하기가 힘들다.
이런 부분을 수동빈 등록부분으로 바꿔놓으면 바로 어떤 구현객체들이 있는지 직관적으로 확인할 수 있다.

정리

- 편리한 자동 기능을 기본으로 사용하자
- 집적 등록하는 기술 지원 객체는 수동 등록을 사용해보자.
- 다형성을 적극 활용하는 비즈니스 로직은 수동 등록을 고민해보자.(자동 등록을 사용해도 상관 없다.)
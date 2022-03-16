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
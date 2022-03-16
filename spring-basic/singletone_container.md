# 싱글톤 컨테이너

## 웹 애플리케이션과 싱글톤

스프링은 기업용 온라인 서비스 기술을 지원하기 위해 탄생했다.
대부분의 스프링 애플리케이션은 앱 애플리케이션이다. 물론, 웹이 아닌 애플리케이션 개발도 가능하다.

웹 애플리케이션은 보통 여러 고객이 동시에 요청을 한다.
그렇다면, 매 요청이 올때마다 매번 `Service` 객체가 새롭게 생성되어야 할까?
우리가 이전에 만들었던 `AppConfig` 를 보면 요청이 올 때 마다 서비스 객체를 새롭게 생성한다.
즉, 1초에 100번의 요청이 왔다면 1초에 100개의 서비스 객체가 생성되는것이며 이는 메모리 낭비다.
어쩌피 하는 역할은 같으므로 객체는 딱 1개만 생성하고, 그것을 공유하도록 설계하는 것이 싱글톤 패턴이다.

## 싱글톤 패턴

싱글톤패턴은 클래스 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴이다.
객체 인스턴스를 2개이상 생성하지 못하도록 생성자를 `private` 로 선언하여 막을 수 있다.

```java
public class Singletone {
	private static final Singletone instance = new Singletone();

	private Singletone() {}

	public static Singletone getInstance() {return instance}
}
```

이렇게 싱글톤 클래스를 설계하면

`Singletone singletone = Singletone.getInstance();` 와 같이 사용이 가능하다.

이렇게하면 여러번 해당 클래스를 호출해도, 단 하나의 객체만 생성되어 존재하게 된다.

하지만 싱글톤 패턴의 문제점도 존재한다.

- 싱글톤 패턴을 구현하는 코드 자체가 많이 필요함
- 의존 관계상 클라이언트가 구체 클레스에 의존함 (DIP 위반)
- 테스트하기 어려움
- 내부 속성을 변경하거나 초기화하기 어려움
- private 생성자로 자식 클래스를 만들기 어려움.
- 결론적으로 유연성이 떨어진다.

## 싱글톤 컨테이너

스프링은 싱글톤 패턴의 문제를 해결하며, 객체 인스턴스를 싱글톤으로 관리한다.

스프링 컨테이너는 싱글톤 패턴을 직접 적용하지 않아도 객체를 싱글톤으로 관리한다. 이 기능을 싱글톤 레지스트리 라고 한다.
스프링 컨테이너에서 지원하는 이러한 기능 덕분에 싱글톤 패턴의 단점을 해결하며 객체를 싱글톤으로 유지할 수 있다.

스프링에서 빈 등록할 때 기본적으로 싱글톤 방식으로 동작하지만, 싱글톤 방식 외에 새롭게 객체를 생성하여 반환하는 기능도 제공하고 있다.

## 싱글톤 방식의 주의점

객체인슽턴스를 하나만 생성해서 공유하는 싱글톤 방식은 여러 클라이언트가 객체 인스턴스를 공유하기 ㄸ문에 싱글톤 객체는 상태를 유지(stateful) 하게 설계하면 안된다.
즉, 싱글톤 객체는 무상태(stateless)로 설계해야 한다.

- 특정 클라이언트에 의존적인 픽드가 있으면 안된다.
- 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다.
- 가급적 읽기만 가능해야 한다.
- 필드 대신 자바에서 공유되지 않는 지역변수, 파라미터 등을 사용해야 한다.
- 스프링 빈의 필드에 공유 값을 설정하면 큰 장애가 발생할 수 있다.

## @Configuration과 싱글톤

이전에 만들어둔 AppConfig 코드를 보면 다음과 같은 코드가 있다.

```java
@Configuration
public class AppConfig {
 @Bean
 public MemberService memberService() {
	 return new MemberServiceImpl(memberRepository());
 }

 @Bean
 public OrderService orderService() {
	 return new OrderServiceImpl(
		 memberRepository(),
		 discountPolicy());
 }

 @Bean
 public MemberRepository memberRepository() {
	 return new MemoryMemberRepository();
 }
}
```

잘 보면 `MemberService` 와 `OrderService` 에서 각각 `memberRepository()` 를 호출하고 있고 이는 새로운 객체를 리턴하여 반환하고 있다.
결국 이 코드만 보았을 때는 새로운 객체가 2개가 생성이되어 싱글톤이 적용되지 않는 것 처럼 보인다.

하지만 집적 테스트하여 결과를 보면 두개의 멤버레포지토리는 하나의 객체 인스턴스를 가르키고 있다.

코드로만 보았을때는 `new MemoryMemberRepository()` 가 두번 일어나야 하지만, 실제로 결과를 볼때는 그렇지 않았다.
그 이유는 스프링이 지원하는 `@Configuration` 에 있다.

`@Configuration` 은 스프링이 스프링 빈을 싱글톤으로 되도록 보장해주는 것이다.
실제로 `ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);` 로 스프링 컨테이너를 만들고 `AppConfig bean = ac.getBean(AppConfig.class);` 로 빈을 조회해보면 `bean = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$bd479d70` 이런 결과가 나타난다.

잘 보면 AppConfig뒤에 `xxxCGLIB` 가 붙으며 이름이 복잡해졌다.
이는 바로, 스프링이 CGLIB라는 바이트코드 조작 라이브러리를 이용하여 AppConfig를 상속받은 다른 임의의 클래스를 만들고 그 클래스를 스프링 빈으로 등록한 것이다.
그리고 이 임의의 클래스가 스프링 빈을 싱글톤으로 유지되도록 보장해준다.

따라서, `@Bean` 이 붙은 메소드마다, 이미 스프링빈이 존재하는지 확인하고 이미 있다면 존재하는 빈을 반환하고 스프링 빈이 없으면 생성해서 반환하는 코드가 동적으로 만들어진다. 이런 역할을 AppConfig 상속받은 클래스가 수행하는 것이다.

만약 `@Configuration` 을 적용하지 않으면 스프링에서 CGLIB라이브러리를 사용하지 않는다.
따라서, `@Bean` 이 있는 메소드가 순수하게 스프링 빈에 등록된다. 이 경우에는 여러개의 객체 인스턴스가 생성되게 된다.

즉, 정리하면

- `@Bean` 만 사용해도 스프링 빈에 등록이 되지만 싱글톤을 보장하지는 않는다. (의존관계 주입 필요시)
- `@Bean` 을 하더라도, 그 내부에서 의존관계를 집적 `new` 를 통해 할당하면 이는 스프링컨테이너에 등록되지 않는다.
- 따라서, 스프링 설정 정보는 반드시 `@Configuration` 을 사용하여 싱글톤을 보장하도록 하자.
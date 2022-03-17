# 빈 스코프

## 빈 스코프란?

스코프는 빈이 존재할 수 있는 범위를 뜻한다.

스프링은 다음과 같은 스코프를 지원한다.

- 싱글톤 : 기본 스코프, 스프링 컨테이너 시작과 종료까지 유지되는 가장 넓은 범위의 스코프
- 프로토타입 : 프로토타입 빈의 생성과 의존관계 주입까지만 관여하고 더는 관리하지 않는 짧은 범위의 스코프
- 웹관련 스코프 :
    - request : 웹 요청이 들어오고 나갈때 까지 유지되는 스코프
    - session : 웹 세션이 생성되고 종료될 때 까지 유지되는 스코프
    - application : 웹의 서블릿 컨텍스와 같은 범위로 유지되는 스코프
    

빈 스코프는 다음과 같이 지정할 수 있다.

`@Scope("prototype")`

싱글톤 스코프의 경우 여러 클라이언트가 요청해도 동일한 빈을 공유하게 된다.
그러나 프로토타입 빈은 클라이언트가 요청을 하면 그때 프르토타입 빈을 생성한다. 이후, 그것을 클라이언트에게 반환하고 더이상 스프링 컨테이너가 해당 빈을 관리하지 않는다.

정리

스프링 컨테이너는 프로토타입 빈을 생성하고, 의존관계 주입, 초기화 까지만 처리한다.
클라이언트에게 빈을 반환하면 그 후에는 빈을 관리할 책임은 프로토타입 빈을 호출한 클라이언트에 있다.
따라서, 스프링컨테이너가 종료되어도 스프링 빈의 종료함수가 실행되지 않는다.

프로토타입 빈의 특징 정리

- 스프링 컨테이너에 요청할 때 마다 새로 생성된다.
- 스프링 컨테이너는 프로토타입 빈의 생성과 의존관계 주입 그리고 초기화까지만 관여한다.
종료 메서드가 호출되지 않는다.
- 그래서 프로토타입 빈은 프로토타입 빈을 조회한 클라이언트가 관리해야 한다. 종료 메서드에 대한 호출도
클라이언트가 직접 해야한다.

## 프로토타입 스코프를 싱글톤 빈과 함께 사용시 문제점

싱글톤은 단 하나의 객체만을 생성하여 사용하게 된다.
하지만 만약 싱글톤 내부에서 프로토타입 빈을 주입받는다면 어떻게 될까?
싱글톤은 초기에 생성되며, 의존 관계를 주입받고 이 때 프로토타입 빈을 주입받게 된다.

따라서, 만약 여러개의 클라이언트가 이 싱글톤 객체를 요청했을 때, 이때 싱글톤 내부에 있는 프로토타입 빈은 요청할 때마다 생성되는 것이 아닌, 처음 싱글톤을 만들때 주입받은 프로토타입 빈이 계속 사용되게 된다.

프로토타입 빈을 만드는 이유는 매 요청때마다 새로운 객체를 할당받고 싶어서이다. 하지만 이렇게 되면, 프로토타입 빈의 장점을 살릴 수 없다. 따라서 이럴때는 다른 해결책이 필요하다. 이것을 해결하기 위해서는, 스프링컨테이너에서 집적 빈을 요청하는 방법이 있다. 하지만 그보다 더 쉬운 방법도 존재한다.

## Provider로 문제 해결

스프링은 다음과 같은 기능을 제공해준다.

`ObjectFactory` , `ObjectProvider`

집적 필요한 의존관계를 찾는 것을 Dependency Lookup(DL)이라고 한다.
스프링에서는 빈을 컨테이너에서 찾아주는 DL서비스를 제공하고 있다. 기존에 `ObjectFactory` 가 있었는데 여기에 기능이 더 추가되어 `ObjectProvider` 가 만들어졌다.

`PrototypeBean prototypeBean = prototypeBeanProvider.getObject();` 이렇게 사용하면 된다.

이렇게하면 내부에서는 스프링 컨테이너를 통해 해당 빈을 찾아 반환한다.

- ObjectFactory: 기능이 단순, 별도의 라이브러리 필요 없음, 스프링에 의존
- ObjectProvider: ObjectFactory 상속, 옵션, 스트림 처리등 편의 기능이 많고, 별도의 라이브러리 필요
없음, 스프링에 의존

## 웹 스코프

웹 스코프는 웹 환경에서만 동작한다.
프로토타입과 다르게 스프링이 해당 스코프의 종료시점까지 관리한다. 따라서 종료 메소드가 호출된다.

웹 스코프 종류

- request: HTTP 요청 하나가 들어오고 나갈 때 까지 유지되는 스코프, 각각의 HTTP 요청마다 별도의 빈
인스턴스가 생성되고, 관리된다.
- session: HTTP Session과 동일한 생명주기를 가지는 스코프
- application: 서블릿 컨텍스트( ServletContext )와 동일한 생명주기를 가지는 스코프
- websocket: 웹 소켓과 동일한 생명주기를 가지는 스코프

### request 스코프 예제

웹 스코프는 웹 환경에서만 동작하므로 web 환경이 동작하도록 라이브러리를 추가해야 한다.

`implementation 'org.springframework.boot:spring-boot-starter-web'`

위 라이브러리를 추가하면 스프링부트는 내장 톰캣서버를 활용하여 웹서버와 스프링을 함께 실행시킨다.

동시에 여러 HTTP요청이 오면 정확히 어떤 요청이 남긴 로그인지 구분이 어렵다.
이럴때 request스코프를 이용하면 좋다.

```java
[d06b992f...] request scope bean create
[d06b992f...][http://localhost:8080/log-demo] controller test
[d06b992f...][http://localhost:8080/log-demo] service id = testId
[d06b992f...] request scope bean close
```

이런식으로 식별자를 통해 어떤 요청이 남긴 로그인지 확인할 수 있다.

```java
@Controller
@RequiredArgsConstructor
public class LogDemoController {
 private final LogDemoService logDemoService;
 private final MyLogger myLogger;
 @RequestMapping("log-demo")
 @ResponseBody
 public String logDemo(HttpServletRequest request) {
 String requestURL = request.getRequestURL().toString();
 myLogger.setRequestURL(requestURL);
 myLogger.log("controller test");
 logDemoService.logic("testId");
 return "OK";
 }
}
```

이렇게 컨트롤러를 만들고 실행시키면 오류가 발생한다.

그 이유는 이 컨트롤러가 등록되는 시점에 MyLogger가 requset scope를 가지기는데 request정보가 없기때문에 해당 스코프 빈을 등록하지 못해서 생기는 문제이다. 실제 요청이 와야 해당 스코프 빈을 생성할 수 있다.
이 문제를 해결하기 위해서는 위에 사용한 Provider를 사용하여 해결할 수 있다.

> 여기선 예시를 위해 컨트롤러에서 로깅을 했지만 실제로는 인터셉터나 서블릿 필터같은곳을 활용하는 것이 좋다.
> 

첫번째 해결방법은 Provider를 사용하는 것이다.

`private final MyLogger myLogger;` 를 `private final ObjectProvider<MyLogger> myLoggerProvider` 로 프로바이더로 주입받자. 그럼 실행시점에 주입받을 수 있으므로 오류가 발생하지 않는다.

그리고 비즈니스 로직 내부에서 `MyLogger myLogger = myLoggerProvider.getObject()` 와 같이 사용하면 된다.

이렇게 request 스코프를 사용하면 고객의 요청에대한 정보를 유지하며 같은 HTTP 요청에 대한 정보를 저장할 수 있다.

## 스코프와 프록시

위 방식으로도 문제를 해결할 수 있지만 더 깔끔하게 해결하는 방법이 있다. 그것이 바로 프록시 방식이다.

`@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)` 이 어노테이션을 추가하자. 적용대상이 클래스면 TARGET_CLASS를 인터페이스면 TARGET_INTERFACES를 추가하자.

이렇게하면 MyLogger의 가짜 프록시 클래스를 만들어두고 HTTP request와 상관없이 가짜 프록시 클래스를 미리 다른빈에 주입해둘 수 있다. 이렇게하면 프로바이더에서 DL하는 코드를 추가하지 않아도 된다.

이것이 작동하는 원리는 CGLIB라는 라이브러리를 통해 가짜 프록시 객체를 만들어 주입하기 때문이다.
따라서 로그를 찍어보면 우리는 순수 객체가 아닌 CGLIB가 만든 객체가 등록된 것을 확인할 수 있다.

가짜 프록시 객체는 요청이 오면 그때 내부에서 진짜 빈을 요청하는 `위임 로직` 이 들어있다.
따라서, 가짜 프록시 객체 내부에서 진짜 객체를 찾는 방법을 알고 있는 것이다.
만약 `myLogger.logic()` 을 호출하면 가짜 프록시 객체의 함수를 호출하는 것이지만, 그 내부동작은 실제 객체의 `logic()` 함수를 호출한다. 결국 이 객체를 사용하는 입장에서는 이게 원본인지 아닌지 모르게 동일하게 사용할 수 있다.

가짜 프록시 객체는 request scope와 관계없다. 그냥 가짜일뿐이고 싱글톤으로 동작하며 내부의 단순한 위임로직을 통해 작동한다.

즉, 가짜 프록시 객체를 통해 진짜 객체 조회를 꼭 필요한 시점까지 지연처리 한다는 점이다. 꼭 웹스코프뿐만 아니라 다른 곳에서도 프록시는 사용할 수 있다.

! 주의점

- 마치 싱글톤을 사용하는 것 처럼 사용하지만 실제로는 다르게 동작하므로 결국 주의해서 사용해야 한다.
- 특별한 scope는 필요한 곳에서만 최소화 하여 사용해야한다. 무분별하게 사용하면 유지보수가 어렵다.
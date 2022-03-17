# 빈 생명주기 콜백

## 빈 생명주기 콜백

데이터베이스 커넥션 풀이나, 네트워크 소켓처럼 애플리케이션 시작 시점에 필요한 연결을 미리 해두고, 애플리케이션 종료 시점에 연결을 모두 종료하는 작업을 진행하려면 객체의 초기화와 종료 작업이 필요하다.

스프링을 통해 이러한 초기화 작업과 종료작업을 어떻게 하는지 알아보자.

외부의 네트워크에 미리 연결하는 객체를 하나 생성한다고 가정해보자.
이 `NetworkClient` 는 시작 시점에 `connect()` 로 호출해 연결을 맺고, 애플리케이션이 종료되면 `disConnect()` 를 호출에 연결을 끊어야 한다.

스프링은 다음고 같은 라이프사이클을 가진다. “객체 생성 → 의존관계 주입”

스프링 빈은 객체를 생성하고 의존관계 주입이 다 끝난 다음에야 필요한 데이터를 사용할 수 있는 준비가 완료된다.
초기화 작업은 의존관계 주입이 모두 완료되고 난 다음에 호출해야 한다. 개발자가 의존관계 주입이 모두 완료된 시점을 어떻게 알 수 있을까?

스프링은 의존관계 주입이 완료되면 스프링 빈에게 콜백 메서드를 통해 초기화 시점을 알려주는 다양한 기능을 제공하며, 스프링 컨테이너가 종료되기 직전에 소멸 콜백을 준다. 

따라서 스프링 빈의 이벤트 라이프사이클은 다음과 같다.

`스프링 컨테이너 생성 -> 스프링 빈 생성 -> 의존관계 주입 -> 초기화 콜백 -> 사용 -> 소멸전 콜백 -> 스프링종료`

> 객체의 생성과 초기화를 분리하자.
객체의 생성은 필수 정보(파라미터)를 받고 메모리를 할당하여 객체를 생성하는 책임을 가진다.
반면 초기화는 이렇게 생성된 값을 활용해 외부 커넥션을 연결하는 등 무거운 동작을 수행한다.
따라서, 생성자 안에서 이 작업들을 한번에 수행하는 것 보다는 두가지 부분을 명확하게 나누는 것이 유지보수 관점에서 좋다.
> 

스프링은 세가지 방법으로 빈 생명주기 콜백을 지원한다.

- 인터페이스(InitializingBean, DisposableBean)
- 설정 정보에 초기화 메서드, 종료 메서드 지정
- @PostConstruct, @PreDestroy 애노테이션 지원

## 인터페이스 방법

`InitializingBean, DisposableBean` 인터페이스를 구현하자.
그럼 다음과 같은 메소드를 오버라이드 할 수 있다.

`afterPropertiesSet()` : 생성이 된 후 초기화 작업을 하면 된다.

`destroy()` : 소멸 작업을 진행하면 된다.

결과적으로 다음과 같은 코드가 된다.

```java
package hello.core.lifecylce;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NetworkClient implements InitializingBean, DisposableBean {
  private String url;

  public NetworkClient() {
    System.out.println("url" + url);
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void connect() {
    System.out.println("url = " + url);
  }

  public void call(String message) {
    System.out.println("call" + url + " message = " + message);
  }

  public void disconnect() {
    System.out.println("close" + url);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    connect();
    call("초기화 연결 메세지");
  }

  @Override
  public void destroy() throws Exception {
    disconnect();
  }
}
```

이렇게하면 빈의 생성과 초기화 시점을 분리할 수 있으며, 종료전에 사용해야 하는 함수도 지정할 수 있다.

다만 이 인터페이스는 스프링 전용 인터페이스로 해당 코드가 스프링 전용 인터페이스에 의존한다.
또한 초기화, 소멸 메소드의 이름을 변경할 수 없다. 그리고 지금은 더 나은 방법들이 있어 거의 사용하지 않는다.
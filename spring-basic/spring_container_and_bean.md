# 스프링 컨테이너와 스프링 빈

[소스코드](example)

## 스프링 컨테이너 생성

스프링 컨테이너는 다음과 같이 사용했다.

```java
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```

`ApplicationContext` 를 스프링 컨테이너라고 하며 ApplicationContext역시 인터페이스이며 `AnnotationConfigApplicationContext` 는 해당 인터페이스를 구현한 구현 객체이다.

스프링 컨테이너는 XML을 기반으로 만들 수 있으며 어노테이션 기반의 자바 설정 클래스로도 만들 수 있다.

스프링 컨테이너 생성 과정

- `new AnnotationConfigApplicationContext(AppConfig.class)`
- 스프링 컨테이너를 생성할 때는 구성 정보(AppConfig)를 지정해주어야함.
- 스프링 컨테이너는 구성정보를 바탕으로 `빈 이름` , `빈 객체` 로 `스프링 빈 저장소` 에 저장된다.
- 빈 이름이 중복되는 것은 주의해야한다 !
- 스프링 컨테이너는 설정 정보를 참고하여 의존관계를 주입한다.
- 단순히 자바 코드를 호출하는 것 처럼 보이지만 조금 차이가 있다. (싱글톤 컨테이너 관련 개념)

## 컨테이너에 등록된 모든 빈 조회

컨테이너에 등록된 빈들을 확인해보자.

```java
package hello.core.beanFind;

import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextInfoTest {

  AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

  @Test
  @DisplayName("모든 빈 출력하기")
  void findAllBean() {
    String[] beanDefinitionNames = ac.getBeanDefinitionNames();
    for (String beanDefinitionName : beanDefinitionNames) {
      Object bean = ac.getBean(beanDefinitionName);
      System.out.println("bean = " + bean);
    }
  }

  @Test
  @DisplayName("애플리케이션 빈 출력하기")
  void findApplicationBean() {
    String[] beanDefinitionNames = ac.getBeanDefinitionNames();
    for (String beanDefinitionName : beanDefinitionNames) {
      BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

      //Role ROLE_APPLICATION: 집적 등록한 애플리케이션
      //Role ROLE_INFRASTRUCTURE: 스프링 내부에서 사용하는 빈
      if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
        Object bean = ac.getBean(beanDefinitionName);
        System.out.println("bean = " + bean);
      }
    }
  }
}
```

## 기본적인 스프링 빈 조회방법

스프링 빈을 조회하는 가장 기본적인 방법은 `getBean()` 메소드를 사용하는 것이다.
만약 조회대상 스프링 빈이 없으면 예외가 발생한다.
getBean함수는 `getBean(빈이름, 타입)` 과 `getBean(타입)` 으로 사용하는 방법이 있다.

동일한 타입이 둘 일때는 오류가 발생한다. 이때는 빈 이름을 지정해야 한다.

만약, 동일한 타입의 빈을 모두 조회하고 싶다면 `ac.getBeansOfType()` 를 사용하면 된다. 
String, Bean 형식의 리스트로 조회할 수 있다.

## 스프링 빈 조회 - 상속관계

부모 타입으로 조회하면, 자식 타입도 함께 조회된다.
따라서, 모든 자바 객체의 최고 부모인 Object 타입으로 조회하면 모든 스프링 빈을 조회하게 된다.

그렇기에 부모타입으로 조회할 시에 자식이 두개 이상이 있다면 오류가 발생하게 된다. 따라서, 이때는 빈 이름을 지정해야 하거나 특정 하위타입으로 조회하면된다. 하지만 특정 하위타입으로 조회한다는 것은 역할이 아닌 구현을 사용하는 것이므로 좋은 방법은 아니다.

## BeanFactory와 Application Context

BeanFactory

- 스프링 컨테이너의 최상위 인터페이스다.
- 스프링 빈을 관리하고 조회하는 역할을 담당한다.
- `getBean()` 을 제공한다.

ApplicationContext

- BeanFactory의 기능을 모두 상속받아 제공한다.
- 빈을 관리하고 검색하는 기능을 BeanFactory가 제공해준다. 하지만 애플리케이션을 개발할 때는 빈을 관리하고 조회하는 기능은 물론 수 많은 부가기능이 필요하다.
- ApplicationContext는 BeanFactory뿐만아니라 여러가지 부가기능을 가지고 있다.
- 메세시소스를 활용한 국제화 기능
- 환경변수 (로컬, 개발, 운영 등을 구분해서 처리)
- 애플리케이션 이벤트 (이벤트를 발행하고 구독하는 모델을 편리하게 지원)
- 편리한 리소스 조회 (파일, 클래스패스, 외부 등에서 리소스를 편리하게 조회)

BeanFactory를 직접 사용할 일은 거의 없고 부가기능이 포함된 ApplicationContext를 사용한다.
BeanFactory, ApplicationContext를 스프링 컨테이너라고 한다.

XML을 사용해서도 스프링 빈 등록이 가능하다. 하지만 자주 사용되는 방법은 아니다.

## 스프링 빈 설정 메타정보 - BeanDifinition

스프링이 이렇게 다양한 설정 형식을 지원할 수 있는 이유는 `BeanDefinition` 이라는 추상화가 있기 때문이다.
쉽게 설명하여 역할과 구현을 개념적으로 나눈 것이다. XML을 읽거나 자바 코드를 읽어 BeanDefinition을 만들게 되고 스프링 컨테이너는 BeanDefinition만을 의존하게 된다.

BeanDefinition을 집적 생성해서 스프링 컨테이너에도 등록할 수 있지만 실제로 사용될 일은 거의 없다.
BeanDefinition은 스프링이 다양한 형태의 설정 정보를 추상화하여 사용하는 것으로 이해하면 된다.
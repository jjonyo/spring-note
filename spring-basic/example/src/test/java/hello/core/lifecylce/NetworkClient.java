package hello.core.lifecylce;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NetworkClient {
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

  public void init() {
    connect();
    call("초기화 연결 메세지");
  }

  public void close() {
    disconnect();
  }
}

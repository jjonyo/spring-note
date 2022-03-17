package hello.core.domain.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Order {

  private Long memberId;
  private String itemName;
  private int price;
  private int discountPrice;

  public Order(Long memberId, String itemName, int price, int discountPrice) {
    this.memberId = memberId;
    this.itemName = itemName;
    this.price = price;
    this.discountPrice = discountPrice;
  }

  public int calculatePrice() {
    return price - discountPrice;
  }
}

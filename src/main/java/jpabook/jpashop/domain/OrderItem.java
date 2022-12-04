package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jpabook.jpashop.domain.item.Item;
import javax.persistence.*;

//jpa를 사용해서 테이블과 매핑할 클래스는 붙여줘야함
@Entity
@Table(name = "order_item")
@Getter @Setter
//생성자로 생성못하게
//protected OrderItem() {}랑 같음
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    //시퀀스값 쓰는, 기본키 할당
    @Id @GeneratedValue
    //order_item테이블의 id열과 매칭
    @Column(name = "order_item_id")
    private Long id;

    //주문과 상품 사이에 있어 다대댜
    //하나의 상품에 여러 주문상품
    //fetch = FetchType.LAZY는 order를 갖고올때 member를 같이 갖고옴, 같이 조회하겠다는 의미
    //@ManyToOne는fetch = FetchType.Eager이 기본값으로 되어있어 바꿔줘야함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //주문 상품

    //하나의 주문에 여러 주문상품
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; //주문

    private int orderPrice; //주문 가격

    private int count; //주문 수량

    //==생성 메서드==//
    //어떤 물품을 얼마에 몇개를 샀느지
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count);

        return orderItem;
    }

    //==비즈니스 로직==//
    /** 주문 취소 */
    public void cancel() {
        //취소했으니 재고 늘려주기, Item엔티티의 필드 즉 컬럼을 변경하니 업데이트 쿼리 날라감
        getItem().addStock(count);
    }

    //==조회 로직==//
    /** 주문상품 전체 가격 조회 */
    public int getTotalPrice() {
        //주문가격과 수량을 곱해서 반환
        return getOrderPrice() * getCount();
    }
}

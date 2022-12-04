package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//예제에서는 설명을 쉽게하기 위해 엔티티 클래스에 Getter, Setter를 모두 열고, 최대한 단순하게 설계
//실무에서는 가급적 Getter는 열어두고, Setter는 꼭 필요한 경우에만 사용하는 것을 추천
//참고: 이론적으로 Getter, Setter 모두 제공하지 않고, 꼭 필요한 별도의 메서드를 제공하는게 가장 이상적
//이다. 하지만 실무에서 엔티티의 데이터는 조회할 일이 너무 많으므로, Getter의 경우 모두 열어두는 것이
//편리하다. Getter는 아무리 호출해도 호출 하는 것 만으로 어떤 일이 발생하지는 않는다. 하지만 Setter는
//문제가 다르다. Setter를 호출하면 데이터가 변한다. Setter를 막 열어두면 가까운 미래에 엔티티에가 도대
//체 왜 변경되는지 추적하기 점점 힘들어진다. 그래서 엔티티를 변경할 때는 Setter 대신에 변경 지점이 명확
//하도록 변경을 위한 비즈니스 메서드를 별도로 제공

//jpa를 사용해서 테이블과 매핑할 클래스는 붙여줘야함
@Entity
//테이블 명시
@Table(name = "orders")
@Getter @Setter
//생성자로 생성못하게
//protected Order() {}랑 같음
//Order 객체를 생성할 때 다양한 방법으로 생성하게 되면 나중에 유지보수가 아려워짐
//JPA 는 스펙상 PROTECTED까지 생성자 생성할 수 있게 허용함
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    //order테이블의 id열과 매칭
    @Column(name = "order_id")
    private Long id;

    //order와 member는 다대일 관계
    //fetch = FetchType.LAZY는 order를 갖고올때 member를 같이 갖고옴, 같이 조회하겠다는 의미
    //@ManyToOne는fetch = FetchType.Eager이 기본값으로 되어있어 바꿔줘야함
    @ManyToOne(fetch = FetchType.LAZY)
    //외래키 이름 설정, 여기에 값 세팅하면 member_id왜래키값이 변경됨
    @JoinColumn(name = "member_id")
    private Member member; //주문 회원

    //OrderItem클래스의 order에 의해 매핑된
    //@OneToMany는 FetchType.LAZY가 기본값
    //cascade = CascadeType.ALL는 orderItems에 값을 넣어두고 order저장하면 같이 저장됨
    //order를 persist하면 OrderItem도 같이
    //원래는 OrderItem 하나씩 넣고 그 다음 Order 넣어야하는데, 엔티티당 각각 persist
    //Order orderItems 컬렉션에 OrderItem 다 넣고 Order persist 하면 같이 들어감
    //즉 Order 에 OrderItem 세팅해놓으면 Order 저장할 때 같이 저장되는
    //현재 속해있는 클래스인 엔티티가 저장될때 cascade 되어있는 것도 같이 저장된다
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    //주문과 배송은 1대1이기 때문에
    //fetch = FetchType.LAZY는 order를 갖고올때 member를 같이 갖고옴, 같이 조회하겠다는 의미
    //CascadeType.ALL order저장하면 delievery도 같이 저장 원래는 각각 저장
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //외래키 이름 설정, 여기에 값 세팅하면 delivery_id왜래키값이 변경됨
    //1대 1 관계에서는 접근을 많이 하는 곳에 외래키
    //주문을 가지고 배송을 찾으므로 대부분
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; //배송정보

    //주문시간
    //LocalDateTime쓰면 하이버네이트가 알아서 지원
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

//    Member member1 = new Member();
//    Order order = new Order();
//    member1.getOrders().add(order);
//    order.setMember(member);

    //위 내용과 같은, 원자적으로 묶는
    //양방향이면 서로 값 세팅, 한 코드로 해결
    //양방향 연관관계 다 걸리게
    //==연관관계 메서드==//

    public void setMember(Member member) {
        this.member = member;//파라미터로 들어온 회원 객체를 현재 Order클래스 회원 객체에 세팅
        member.getOrders().add(this);//파라미터로 들어온 회원 객체의 주문리스트에 현재 주문 객체 추가
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    //주문은 회원, 배송, 주문물품들 정보를 파라미터로
    //뭔가 생성하는 지점 바꾸려면 이 메서드만 바꾸면 됨
    public static Order createOrder(Member member, Delivery delivery,
                                    OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        //여러 개 받은 주문물품들 for each문으로 하나씩 Order객체에 추가
       for (OrderItem orderItem : orderItems) {

            order.addOrderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //==비즈니스 로직==//
    /** 주문 취소 */
    public void cancel() {
        //jpa는 entity안에 데이터만 바꿔주면
        //jpa가 바뀐 부분들을 감지해 db업데이트 쿼리가 날라감
        //밑에 바뀐 order와 orderitemd에 쿼리 날라감

        //배송이 되버리면 취소불가
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        //취소가 가능하면 상태를 취소로 바꾸기
        this.setStatus(OrderStatus.CANCEL);

        //한번 주문할때 고객이 상품 여러 개 주문할 수 있기 때문에 하나씩 다 취소
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /** 전체 주문 가격 조회 */
    public int getTotalPrice() {
        int totalPrice = 0;

        for (OrderItem orderItem : orderItems) {
            //각 물품의 가격을 더해
            totalPrice += orderItem.getTotalPrice();
        }

        return totalPrice;

//        return orderItems.stream()
//                .mapToInt(OrderItem::getTotalPrice)
//                .sum();
    }
}

package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 서비스는 단순하게 엔티티 조회하고 호출하고 연결해주는 정도의 역할
 * 복잡한 비즈니스 로직은 엔티티에 이렇게 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용 하는것을
 * 도메인 모델 패턴
 * JPA 쓰면 이렇게 하는 개발을 하게 됨, 뭐가 더 유지보수하기 좋은지 고민, 이게 꼭 정답은 아니다
 *
 * 반대로 엔티티에 비즈니스 로직 거의 없고 서비스 계층에서 대부분의 비즈니스 로직 처리하는 것을
 * 트랜잭션 스크립트 패넌
 *
 * @Service안에 @Component가 있어 @ComponentScan의 대상이 됨
 */
@Service
/**
 * jpa의 모든 변경이나 로직들은 가급적이면 트랜잭션 안에서 실행되야함
 * readOnly = true jpa가 조회하는데 성능을 최적화함
 * db한테 읽기 전용이라 하면 리소스 너무 쓰지 마라
 * public메서드에 공통적으로 적용하는
 */
@Transactional(readOnly = true)
//@AllArgsConstructor
//final이 있는 필드만 생성자 만들어주는
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    /** 주문 */
    //저장할 수 있게 readOnly = true 적용 안함
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회
        //id에 맞는 Member객체 Item객체 찾기, 주문하는 회원과 주문되는 상품이 뭔지
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성, 회원객체에서 주소 갖고와 세팅
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        //주문상품 생성, 이외에 다른 생성방법은 막아야함
        //단순하게 하기위해 주문상품 하나만 넘김
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성, 회원, 배송, 주문상품을 파라미터로
        Order order = Order.createOrder(member, delivery, orderItem);

        /**
         * 주문 저장, orderitem delievery 자동으로 퍼시스트
         * 다른 것이 참조할 수 없는 프라이빗인 경우에 쓰면 좋다
         * 다른데서도 참조하고 갖다 쓰면 cascade 막 쓰면 안됨
         * 잘못하면 order 지울 때 다 지워지는 경우 발생
         * 별도의 repository를 생성해서 퍼시스트를 개별적으로 하는게 낫다
         * 다른데는 참조하는게 없고 라이프사이클 자체를 퍼시스트 할때 같이 해야하는
         * 현재는 Order 만 Delivery OrderItem 사용하므로 퍼시스트 해야하는 라이프 사이클 완전히 똑같으므로
         */
        orderRepository.save(order);

        return order.getId();
    }

    /** 주문 취소 */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        //엔티티 안의 데이터만 바꿔주면 값이 변경된 포인트를 감지해서
        //변경 내역들을 db에 업데이트 쿼리 날림
        order.cancel();
    }

    /** 주문 검색 */
     public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
     }

}
/**
 * 주문 서비스의 주문과 주문 취소 메서드를 보면 비즈니스 로직 대부분이 엔티티에 있다.
 * 서비스 계층 은 단순히 엔티티에 필요한 요청을 위임하는 역할을 한다.
 * 이처럼 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 것을 도메인 모델 패턴
 * jpa나 orm들을 쓰때 많이 사용
 * 반대로 엔티티에는 비즈니스 로직이 거의 없고 서비스 계층에서 대부분
 * 의 비즈니스 로직을 처리하는 것을 트랜잭션 스크립트 패턴, sql을 다룰때 많이 사용
 */

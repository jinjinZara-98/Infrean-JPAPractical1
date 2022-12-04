package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

//jpa를 사용해서 테이블과 매핑할 클래스는 붙여줘야함
@Entity
@Getter @Setter
public class Delivery {

    //시퀀스값 쓰는, 기본키 할당
    @Id @GeneratedValue
    //delivery테이블의 id열과 매칭
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    //내장 타입이기 때문에
    @Embedded
    private Address address;

    //ordinal이 기본 타입, 숫자로 들어감
    //때문에 String으로 써줘야함, Ready Camp 중간에 들어가도 순서에 밀리는게 없음
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //ENUM [READY(준비), COMP(배송)]
}

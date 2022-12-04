package jpabook.jpashop.domain;

import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

    private String memberName; //회원 이름
    private OrderStatus orderStatus;//주문 상태[ORDER, CANCEL]

    //Getter, Setter 어노테이션으로 두 필드의 게터 세터 자동으로 생성됨
}

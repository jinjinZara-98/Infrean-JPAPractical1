package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

//jpa내장타입이므로, 어딘가에 내장 될 수 있다
@Embeddable
// 값 타입은 변경 불가능하게 설계해야 한다.
// @Setter 를 제거하고, 생성자에서 값을 모두 초기화해서 변경 불가능한 클래스를 만들자
@Getter
@Setter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    //jpa는 생성을 할때 리플렉션이나 프록시를 써야하는데 기본 생성자가 필요함
    //public으로 하면 사람들이 많이 호출할 수 있기 때문에, protected가 더 안전
    //JPA가 이런 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수 있도록 지원해야 하기 때문
    protected Address() {

    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}

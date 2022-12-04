package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 양방향 연관관계, 주인을 정하다
 * 주문에서 회원을 바꿀 때, 주문에서 회원 객체를 통해 변경 또는 회원에서 주문 리스트를 통해 변경
 * 둘 다 바꿔버리면 jpa 는 뭘 보고 확인을 해야하는지 모른다
 * 양방향 참조임
 * 디비에서 외래키는 주문에서 회원 기본키밖에 없음
 * 회원 주문과의 관계를 바꾸고 싶으면 외래키의 값을 변경해야됨
 * jpa는 회원에도 주문 리스트 필드가 있고 주문에도 회원 객체 필드가 있으니 둘중에 두 값 모두 확인을 해서
 * 바꿔야 하나, 어디 값이 변경이 되었을 떄 변경을 해야하는지 모름
 * 외래키값을 업데이트 치는것을 JPA 에서는 둘 중에 하나만 선택하게 약속
 * 객체는 변경 포인트 2곳 디비는 한 곳
 * 둘 중에 하나를 주인이란 개념으로 잡느다
 *
 * 컬렉션은 필드에서 초기화 하자.
 * 컬렉션은 필드에서 바로 초기화 하는 것이 안전하다.
 * null 문제에서 안전하다.
 * 하이버네이트는 엔티티를 영속화 할 때, 컬랙션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경한다.
 * 만약 getOrders() 처럼 임의의 메서드에서 컬력션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문제가 발생할 수 있다.
 * 따라서 필드레벨에서 생성하는 것이 가장 안전하고, 코드도 간결하다.
 *
 * Member member = new Member();
 * System.out.println(member.getOrders().getClass());
 * em.persist(team);
 * System.out.println(member.getOrders().getClass());
 *
 * 출력 결과
 * class java.util.ArrayList
 * class org.hibernate.collection.internal.PersistentBag
 *
 * 하이버네이트가 변경된걸 추적해야 하기 때문에 영속화하면 바뀌어버림
 * 누군가 셋터로 컬렉션 바꾸면 하이버네이트가 원하는 매커니즘으로 돌아가지 않음
 * 컬렉션을 밖으로 꺼내 수정하면 안됨
 *
 * 하이버네이트 기존 구현: 엔티티의 필드명을 그대로 테이블의 컬럼명으로 사용
 * ( SpringPhysicalNamingStrategy )
 *
 * 스프링 부트 신규 설정 (엔티티(필드) 테이블(컬럼))
 * 1. 카멜 케이스 언더스코어(memberPoint member_point)
 * 2. .(점) _(언더스코어)
 * 3. 대문자 소문자
 *
 * 적용 2 단계
 * 1. 논리명 생성: 명시적으로 컬럼, 테이블명을 직접 적지 않으면 ImplicitNamingStrategy 사용
 * spring.jpa.hibernate.naming.implicit-strategy : 테이블이나, 컬럼명을 명시하지 않을 때 논리명
 * 적용,
 *
 * 2. 물리명 적용:
 * spring.jpa.hibernate.naming.physical-strategy : 모든 논리명에 적용됨, 실제 테이블에 적용
 * (username usernm 등으로 회사 룰로 바꿀 수 있음)
 * 스프링 부트 기본 설정
 * spring.jpa.hibernate.naming.implicit-strategy:
 * org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
 * spring.jpa.hibernate.naming.physical-strategy:
 * org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
 * * */
//jpa를 사용해서 테이블과 매핑할 클래스는 붙여줘야함
@Entity
//lombok으로 게터 세터
//예제에서는 설명을 쉽게하기 위해 엔티티 클래스에 Getter, Setter를 모두 열고, 최대한 단순하게 설계
//실무에서는 가급적 Getter는 열어두고, Setter는 꼭 필요한 경우에만 사용
@Getter @Setter
public class Member{

    //시퀀스값 쓰는, 기본키 할당
    @Id
    @GeneratedValue
    //member테이블의 id열과 매칭
    @Column(name = "member_id")
    private Long id;

    private String name;

    //내장 타입을 포함했다는, 내장 타입 쓸때는 @Embedded나 @Embeddable 둘 중 하나만 있으면 됨
    @Embedded
    private Address address;

    //하나의 회원이 여러 물품 지원연관관계 주인이 아니다
    //order테이블에 있는 member필드에 의해서 매핑된거다
    //mappedBy나는 매핑하는 애가 아니고 저거에 의해서 매핑된 거울일뿐이다
    //읽기전용이 됨, 뭔가 값을 넣는다고 저 외래키값이 변경되지 않음
    @OneToMany(mappedBy = "member")
    //컬렉션은 필드에서 바로 초기화 하는 것이 안전
    private List<Order> orders = new ArrayList<>();

    //    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(name = "Favorite_Cloth", joinColumns = @JoinColumn(name = "MEMBER_ID"))
//    @Column(name="Cloth_name")
//    private Set<String> favoriteCloths = new HashSet<>();
//
//    @ElementCollection
//    @CollectionTable(name = "Address", joinColumns = @JoinColumn(name = "MEMBER_ID"))
//    @Column(name="Cloth_name")
//    private List<Address> addressesHistory = new ArrayList<Address>();

//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name="city", column=@Column(name = "company_city")),
//            @AttributeOverride(name="street", column=@Column(name = "company_street")),
//            @AttributeOverride(name="zipcode", column=@Column(name = "company_zipcode"))
//    })
//    private Address companyAddress;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "MEMBER_ID")
//    private List<AddressEntity> addressHistory = new ArrayList<AddressEntity>();

}

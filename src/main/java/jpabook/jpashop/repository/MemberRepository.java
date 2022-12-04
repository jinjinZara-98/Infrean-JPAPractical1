package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

//@Repository안에 @Component가 있어 @ComponentScan의 대상이 됨
@Repository
@RequiredArgsConstructor
public class MemberRepository {

//    스프링이 EntityManager객체를 만들어 주입함, 생성자 없이 주입받는, 필드 주입
//    @PersistenceContext 같은 표준 어노테이션이 있어야 주입이 되는데
//    스프링부트는 @Autowired 가능함
//    그래서 @RequiredArgsConstructor 사용 가능한
//    private EntityManager em;

    //@RequiredArgsConstructor로 인해 생성자 자동으로 생성해주고 주입 받음, final붙어서
    //생성자 하나면 @Autowired 생략 가능
    final private EntityManager em;

//    EntityManagerFactory객체 주입
//    @PersistenceUnit
//    private EntityManagerFactory emf;

    //영속성 객체에 member를 넣음, 트랜잭션이 커밋 되는 시점에 insert가 날라감
    //키는 Member클래스에서 지정한 id값
    public void save(Member member) {
        //psersist한다고 iosert문이 나가진 않음
        //db트랜잭션이 커밋될때 플러쉬되면서 jpa영속성컨텍스트에 있는 member객체가 insert쿼리가 만들어지면서 나감
        em.persist(member);
    }

    //단건 조회
    //jpa가 제공하는 find메서드, 클래스타입과 id를 넘기면 Member객체 반환
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    //sql은 테이블을 대상 jsql은 객체를 대상으로, from Member m
    //jpql 쿼리, 반환타입
    //.getResultList()는 결과를 리스트로
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    //:name는 파라미터 name을 바인딩
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

    }

}


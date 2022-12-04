package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    //jpa쓰기 위해 매니저 생성
    private final EntityManager em;

    //저장
    public void save(Order order) {
        em.persist(order);
    }

    //id에 맞는 주문 찾기
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    //검색
    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL, order랑 member 자연조인함
        String jpql = "select o From Order o join o.member m";

        boolean isFirstCondition = true;

        //주문 상태 검색, 주문 상태를 선택했고
        if (orderSearch.getOrderStatus() != null) {
            //첫번째 조건이 참이면
            if (isFirstCondition) {
                //jpql에 조건문 시작 후 조건 false로
                jpql += " where";
                isFirstCondition = false;
                //첫번째 조건이 false면 다음 조건문 시작
            } else {
                jpql += " and";
            }
            //:status는 파라미터,
            jpql += " o.status = :status";
        }

        //회원 이름 검색, 회원 이름 갖고와 값이 있는지 확인
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건

        //주문상태를 갖고오는데 비어있지 않다면 파라미터에 값 세팅
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    //jpql을 자바코드로 작성할 수 있게 jpa에 제공하는 표준, 권장하는 방법은 아님
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인

        //동적 쿼리에 대한 컨디션조합을 깔끔하게 만들 수 있음
        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건

        return query.getResultList();

        String teamName = "팀A";
        String query = "SELECT m, t FROM Member m LFFT JOIN m.team t ON t.name = 'A'"
                + "WHERE t.name = : teamName";

        List<Member> members = em.createQuery(query, Member.class)
                .setParameter("teamName", teamName)
                .getResultList();

        

    }
}

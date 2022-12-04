package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.List;

//@Repository안에 @Component가 있어 @ComponentScan의 대상이 됨
@Repository
@RequiredArgsConstructor
public class ItemRepository {

    //@RequiredArgsConstructor로 인해 생성자 없이 주입 받음, final붙어서
    private final EntityManager em;

    //상품 저장
    public void save(Item item) {

        //들어온 item은 처음 저장하기 전에 id가 없어야함, 신규등록
        if (item.getId() == null) {
            em.persist(item);
        } else {
            //merge는 업데이트 비슷한, db에 등록됬거나 어디에 가져오거나
            //item은 영속성 컨텍스트로 안바뀌고 merge()의 반환값이 영속성 컨텍스트
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}

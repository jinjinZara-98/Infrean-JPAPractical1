package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

//@Service안에 @Component가 있어 @ComponentScan의 대상이 됨
@Service
@Transactional(readOnly = true)
//final이 있는 필드만 생성자 만들어주는
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    //저장할 수 있게 readOnly = true 적용 안함
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    //@Transactional은 트랜잭션을 커밋시키고 jpa는 flush()를 날림, 영속성 컨텍스트중 변경된 entity를 다 찾아줌
    //set 으로 바뀐걸 업데이트쿼리로 db에 날려줌
    //merge 랑 같은 의미, 똑같은 식별자(id)로 객체를 찾은 후 param으로 넘어온 값으로 다 바꿔치기함
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stackQuantity) {//itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
        Item findItem = itemRepository.findOne(itemId); //같은 엔티티를 조회한다.
        findItem.setPrice(price); //Book객체의 값들을 갖고와 데이터를 수정한다.
        findItem.setName(name);
        findItem.setStockQuantity(stackQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}

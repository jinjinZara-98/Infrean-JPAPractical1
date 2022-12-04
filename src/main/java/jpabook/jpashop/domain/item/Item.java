package jpabook.jpashop.domain.item;

import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import jpabook.jpashop.domain.Category;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//jpa를 사용해서 테이블과 매핑할 클래스는 붙여줘야함
@Entity
//상속관계 매핑, 상속관계 전략 지정, 부모테이블에서 지정
//SINGLE_TABLE 한 테이블에 다 박는
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//저장할때 구분할 수 있게
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
//추상클래스, 인터페이스처럼 상속하여 구현체를 만들기위해
public abstract class Item {

    //시퀀스값 쓰는, 기본키 할당
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    //공동속성들  이름, 가격, 재고수량
    private String name;
    private int price;
    private int stockQuantity;

    //카테고리도 리스트로 아이템을 가지고, 리스트도 카테고리를 아이템을 가짐
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();

    //==비즈니스 로직==//
    //핵심 비즈니스 로직을 엔티티에 직접 넣음
    //재고를 늘리고 줄이고
    //@Setter뺌, 변경해야할 일이 있으면 핵심 비지니스 메서드를 가지고 변경
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;

        //수량이 0보다 작으면
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
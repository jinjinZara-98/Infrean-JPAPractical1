package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import jpabook.jpashop.domain.item.Item;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//jpa를 사용해서 테이블과 매핑할 클래스는 붙여줘야함
//테이블 관계도 ERD 에서는 다대다 관계 못써서 Category_Item 테이블 만들어서 중간에 둠
@Entity
@Getter @Setter
public class Category {

    //시퀀스값 쓰는, 기본키 할당
    @Id @GeneratedValue
    //category테이블의 id열과 매칭
    @Column(name = "category_id")
    private Long id;

    private String name;

    //카테고리도 리스트로 아이템을 가지고, 리스트도 카테고리를 아이템을 가짐
    @ManyToMany
    //중간 테이블 매핑, 객체는 다대다 관계 가능, 관계형DB는 컬렉션 관계를 양쪽에서 가질 수 없음
    //일대다 다대일로 중간테이블로 풀어내는
    @JoinTable(name = "category_item",
            //중간테이블의 category_id열과 매핑
            joinColumns = @JoinColumn(name = "category_id"),
            //이 테이블에서 상품쪽으로 들어가는 item_id열과 매핑
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    //카테고리 구조는 트리처럼 뻗어나감, 위로도 볼 수 있어야하고
    //같은 엔티티에 대해서 셀프로 양방향 연관관계 걸음
    //부모
    //이게 컬럼으로 추가되는
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    //자식, OneToMany 에서 One 이 카테고리 테이블을 말하니
    //원래 리스트 제네릭에 Many쪽 엔티티 클래스가 들어가야 되는데
    //테이블 안에서 셀프조인하는거니 Category 를 넣는는    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
    
    //==연관관계 메서드==//
    public void addChildCategory(Category child) {
        //이 카테고리 클래스의 자식 리스트에 파라미터로 들어온거 추가
        this.child.add(child);
        child.setParent(this);
    }
}

package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

//jpa를 사용해서 테이블과 매핑할 클래스는 붙여줘야함
@Entity
//저장할때 구분할 수 있게
@DiscriminatorValue("A")
@Getter @Setter
public class Album extends Item {

    private String artist;
    private String etc;
}

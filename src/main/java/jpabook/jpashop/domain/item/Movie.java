package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

//jpa를 사용해서 테이블과 매핑할 클래스는 붙여줘야함
@Entity
//저장할때 구분할 수 있게
@DiscriminatorValue("M")
@PrimaryKeyJoinColumn(name="MOVIE_iD")
@Getter @Setter
public class Movie extends Item {

    private String director;
    private String actor;
}

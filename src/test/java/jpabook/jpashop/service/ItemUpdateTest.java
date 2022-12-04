package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

//@RunWith(SpringRunner.class)와 @SpringBootTest가 있어야
//스프링 인티그레이션 해서 스프링 올려 테스트 가능, 안그럼 @Autowired 다 실패함
//junit실행할때 스프링이랑 엮어서 실행할래
@RunWith(SpringRunner.class)
@SpringBootTest
//반복 가능한 테스트 지원, 각각의 테스트를 실행할 때마다 트랜잭션을 시작하고 테스트가 끝나면
//트랜잭션을 강제로 롤백 (이 어노테이션이 테스트 케이스에서 사용될 때만 롤백)
//롤백하면 스프링이 jpa입장에서는 쿼리를 날릴 필요가 없음
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        //TX
        book.setName("");

        //변경감지 == 더티체킹, jpa의 entity를 바꿀수 있음 원하는걸로 업데이트트
        //TX commit
   }
}

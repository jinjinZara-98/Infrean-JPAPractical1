package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//@RunWith(SpringRunner.class)와 @SpringBootTest가 있어야
//스프링 인티그레이션 해서 스프링 올려 테스트 가능, 안그럼 @Autowired 다 실패함
//junit 실행할때 스프링이랑 엮어서 실행한다는 의미
//JPA가 DB까지 직접 도는 것을 보여주기 위해 메모리 모드로 DB까지 엮어 테스트
@RunWith(SpringRunner.class)
//스프링부트 띄운 상태로 테스트할 때 사용, 이꺼 안쓰면 @Autowired 실패
@SpringBootTest
//반복 가능한 테스트 지원, 각각의 테스트를 실행할 때마다 트랜잭션을 시작하고 테스트가 끝나면
//트랜잭션을 강제로 롤백 (이 어노테이션이 테스트 케이스에서 사용될 때만 롤백)
//롤백하면 스프링이 jpa입장에서는 쿼리를 날릴 필요가 없음
//커밋을 하면 플러시하면서 영컨에 있는 개체가 db에 들어가면서 insert 문 나감
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;

    @Autowired MemberRepository memberRepository;

    //롤백이지만 db에 쿼리 날리는거 보고싶으면
    @Autowired EntityManager em;
    //em.flush()하면 Long saveId = memberService.join(member)쿼리가 db에 반영됨

    @Test
    //롤백안하고 커밋하고 싶으면, 콘솔에 insert문 나가는거 보이고 h2db에서 실행하면 결과 보임
    @Rollback(value = false)
    public void 회원가입() throws Exception {
        //Given
        Member member = new Member();
        member.setName("kim");

        //When
        Long saveId = memberService.join(member);

        //Then
        //flush()하면 db에 쿼리 나감, 이렇게 하면 테스트 성공하고 db에 쿼리가 적용되지만
        //여전히 롤백되서 영구 저장은 안됨
        //즉 플러시만 하면 db에 쿼리만 나가는
        //쿼리 나간거 롤백안시키고 영구저장하려면 @Rollback(value = false)
        em.flush();
        assertEquals(member, memberRepository.findOne(saveId));

        //jpa에서는 같은 트랜잭션 안에서 같은 Entity id값이 똑같으면 같은 영속성컨텍스트에서 똑같은 애가 관리됨
    }

    //try catch문 대신
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //Given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        //When
        //예외가 발생해야 한다. 똑같은 이름
        //MemberService클래스에서 예외가 터져 던지는데 try catch문이 없으니 여기까지옴
        memberService.join(member1);
        memberService.join(member2);
//        try {
//            memberService.join(member2);
//        } catch (IllegalStateException e) {
//            return;
//        }

        //Then
        //코드가 돌다 여기오면 안되는거, 여기 오면 잘못된거
        //AssertJ의 정적 메서드임
        fail("예외가 발생해야 한다.");
    }
}
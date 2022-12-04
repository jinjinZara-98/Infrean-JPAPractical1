package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

//@Service안에 @Component가 있어 @ComponentScan의 대상이 됨
@Service
//jpa의 모든 변경이나 로직들은 가급적이면 트랜잭션 안에서 실행되야함
//readOnly = true jpa가 조회하는데 성능을 최적화함
//db한테 읽기 전용이라 하면 리소스 너무 쓰지 마라, 더티 체킹도 안하고
//public메서드에 공통적으로 적용하는
@Transactional(readOnly = true)
//@AllArgsConstructor
//final이 있는 필드만 생성자 만들어주는
@RequiredArgsConstructor
public class MemberService {

    //스프링을 실행하면 어플리케이션 로딩 시점에 조립이 다 끝난다
    //생성자에서 안적어주면 에러 떠서 final넣는게 좋음
    private final MemberRepository memberRepository;

    //자동 의존 주입,
    //필드에 붙이면 주입이 까다롭고
    //세터 메서드에 붙이면 실제 어플리케이션이 동작을 잘하고 있어 바꿀 일이 없으면 세터 주입이 별로임
    //파라미터를 넣어줘야 하기 때문에 놓치지 않고 잘 챙길 수 있음
    //생성자가 하나면 @Autowired 생략가능
//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원가입
     */

    //변경, 읽기가 아닌 쓰게에는 readOnly = true 넣으면 안됨
    //readOnly = false가 기본값이고 따로 이렇게 설정한 이게 우선
    @Transactional
    public Long join(Member member) {
        //중복 회원 검증
        validateDuplicateMember(member);
        //중복되지 않으면 저장소에 저장
        memberRepository.save(member);

        return member.getId();
    }

    //중복 회원 검증 메서드

    //드문 사례지만 동시에 같은 member객체로 가입하면서 접근 할 수 있기 때문에 멀티쓰레드나 이런 상황 고려해
    //db에서 Member의 이름을 유니크 제약조건
    private void validateDuplicateMember(Member member) {
        //파라미터로 들어온 Member객체의 이름과 저장소에 같은 이름이 있는지 비교
        List<Member> findMembers =
                memberRepository.findByName(member.getName());
        //존재한다면
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
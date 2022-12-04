package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;
import java.util.List;

@Controller
//final이 있는 필드만 생성자 만들어주는
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    //회원가입 폼
    //Get방식으로 /members/new로 들어오면
    @GetMapping(value = "/members/new")
    public String createForm(Model model) {
        //컨트롤러에서 뷰로 넘어갈때 데이터를 실어 넘김, 빈 껍데기 객체 들고감
        model.addAttribute("memberForm", new MemberForm());

        return "members/createMemberForm";
    }

    //회원가입 폼에서 회원가입 버튼 눌렀을때
    //Post방식으로 /members/new로 들어오면
    @PostMapping(value = "/members/new")
    //@Valid는 MemberForm의 @NotEmpty를 validation할 수 있음, 회원이름 필수로
    //원래 form 객체에 오류 있으면 튕겨지는데 BindingResult 있으면 오류 담겨서 이 메서드 실행 가능
    //스프링과 통홥되어 있어서 스프링이 BindingResult를 화면까지 끌고가 어떤 에러가 있는지 화면에 뿌릴 수 있음

    //DTO 쓰는 이유, Member 엔티티에도 검증 로직을 구현할 수 있지만 엔티티에 구현하면
    //점점 코드가 지저분해지고 엔티티를 화면에 왔다갔다한느 폼 데이터로 사용하기 시작하면
    //안맞고 억지로 끼워맞춰야함. 화면에 딱 맞는 핏한 DTO를 만들어야
    //엔티티를 폼으로 쓰면 화면에 대한 기능이 점점 더 증가. 화면에 종속적
    //템플릿 엔진으로 랜더링할 때는 어차피 서버 내에서 원하는 데이터만 출력하기 때문에 괜찮음
    //API 는 무조건 DTO로
    public String create(@Valid MemberForm form, BindingResult result) {
        //@Valid한거에 BindingResult result 쓰면 객체 result에 에러가 담겨 밑 코드 실행

        if (result.hasErrors()) {
            //에러가 있으면 이 url로 끌고가줌
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member(); Member member2 = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        //저장되고 재로딩 되지않게 form에 보내버림, 다시 메인페이지로 감
        return "redirect:/";
    }

    //회원 목록 버튼 눌렀을때
    //추가
    @GetMapping(value = "/members")
    public String list(Model model) {

        //List<Member>처럼 Member Entity를 그대로 뿌리기보단 dto로 변환해 화면에 꼭 필요한 데이터만 가지고 출력
        //api를 만들땐 절대 Entity를 넘기면 안됨, Entity로직이 추가되면 api스펙이 변함
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberList";
    }

//    요구사항이 정말 단순할 때는 폼 객체( MemberForm ) 없이 엔티티( Member )를 직접 등록과 수정 화면
//    에서 사용해도 된다. 하지만 화면 요구사항이 복잡해지기 시작하면, 엔티티에 화면을 처리하기 위한 기능이
//    점점 증가한다. 결과적으로 엔티티는 점점 화면에 종속적으로 변하고, 이렇게 화면 기능 때문에 지저분해진
//    엔티티는 결국 유지보수하기 어려워진다. 실무에서 엔티티는 핵심 비즈니스 로직만 가지고 있고, 화면을 위한
//    로직은 없어야 한다. 화면이나 API에 맞는 폼 객체나 DTO를 사용하자.
//    그래서 화면이나 API 요구사항을 이것들로 처리하고, 엔티티는 최대한 순수하게 유지
}

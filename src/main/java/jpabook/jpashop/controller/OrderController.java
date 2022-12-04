package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    //상품 주문 폼
    @GetMapping(value = "/order")
    public String createForm(Model model) {

        //주문하는 회원가 주문할 상품을 select박스로 뿌려주고 거기서 고르는 것이므로
        //회원, 상품 리스트 다 갖고오는
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        //리스트 갖고와 담기
        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    //상품 주문 폼에서 상품 주문 버튼 눌렀을때
    @PostMapping(value = "/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId, @RequestParam("count") int count) {

        //@RequestParam의 이름은 html name과 일치하는 값을 갖고옴, 값들을 order에 넣고
        orderService.order(memberId, itemId, count);

        //주문하면 주문 리스트로 넘어가기 밑에 메서드 실행됨
        return "redirect:/orders";
    }

    //홈화면에서 주문 내역 버튼 누를때

    //검색버튼을 누르면 검색조건의 데이터가 바인딩된 상태로 이 메서드가 다시 호출됨
    //검색의 form양식이 post가 아님, action 생략하면 현재 그 페이지 url을 호출하므로
    @GetMapping(value = "/orders")

    //@ModelAttribute("orderSearch")에 이렇게 세팅하면 OrderSearch orderSearch이 medel객체에 자동으로 담김
    //처음에 주문리스트 페이지 들어가면 OrderSearch클래스 필드들이 비어있으니, 모든 주문을 보여주는?

    //담긴게 form submit하면 값이 들어옴, 뿌릴수도 있음, model.addAttribute("orderSearch", orderSearch);
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {

        //검색조건을 파라미터로 넣어 주문 리스트를 받음
        List<Order> orders = orderService.findOrders(orderSearch);

        //조건에 맞는 주문 상품들 갖고와 모델에 담음
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    //주문리스트에서 취소 버튼 누르면
    @PostMapping(value = "/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {

        //url에 있는 주문id를 Long orderId에 넣고 이걸 cancelOrder에 담아 취소시킴
        orderService.cancelOrder(orderId);

        //취소 상태를 담아 리다이렉트로 다시 주문리스트로 다시 가는
        return "redirect:/orders";
    }
}

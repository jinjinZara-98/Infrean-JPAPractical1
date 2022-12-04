package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
//final이 있는 필드만 생성자 만들어주는
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    //상품 등록 폼
    @GetMapping(value = "/items/new")
    public String createForm(Model model) {

        model.addAttribute("form", new BookForm());

        return "items/createItemForm";
    }

    //상품 등록 폼에서 상품등록 버튼 눌렀을때
    @PostMapping(value = "/items/new")
    public String create(BookForm form) {

        //Book객체 하나 생성해주고 사용자가 입력한 값이 들어있는 BookForm에서의 값을 꺼내 세팅ㄹ해줌
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);

        //저장된 책 목록으로 가기
        return "redirect:/items";
    }

    //상품목록 버튼 눌렀을 때
    /**
     * 상품 목록
     */
    @GetMapping(value = "/items")
    public String list(Model model) {

        //모든 상품목록을 갖고와 리스트 생성해 거기에 대입하여 모델에 담아 뷰에 보냄
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }

    //상품목록페이지중 상품하나를 골라 수정버튼을 눌렀을때
    /**
     * 상품 수정 폼
     */
    @GetMapping(value = "/items/{itemId}/edit")
    //@PathVariable("itemId")로 url에 있는 itemId값을 Long itemId에 매핑해 대입시켜줌
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {

        //url에 들어온 itemId값으로 상품 조회, 조회한 상품을의 값들을 상품 입력 폼 값에다 세팅
        Book item = (Book) itemService.findOne(itemId);
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        //삼품 수정 폼 페이지로 가면 상품수정폼에 값들이 들어가있어서 페이지로 들어갔을때 값들이 다 적혀있음
        //그래서 사용자가 원하는 값들 수정할 수 있음
        return "items/updateItemForm";
    }

    //상품 수정 폼에서 상품 수정 버튼 눌렀을때
    /**
     * 상품 수정
     */
    @PostMapping(value = "/items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {

        //똑같이 url에서 itemId갖고오고 상품수정폼에서 입력한 값들을 가져와 수정 진행
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }
}

package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//리턴값 논리경로
@Controller
public class HelloController {

    //url이 hello로 들어오면
    @GetMapping("hello")
    public String hello(Model model) {

        model.addAttribute("data", "hello!!");

        //hello.html로 보내기
        return "hello";
    }
}

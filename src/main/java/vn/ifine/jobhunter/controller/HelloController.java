package vn.ifine.jobhunter.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() {
        // if (true)
        // throw new IdInvalidException("check mate hoidanit");
        return "Hello World (Hỏi Dân IT & Eric ủa alo update)";
    }
}

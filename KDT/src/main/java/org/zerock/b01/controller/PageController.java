package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/layout")
public class PageController {

    @GetMapping("/layout")
    public void layout() { log.info ("layout page test..."); }

    @GetMapping("/mypage")
    public void myPage() {
        log.info("mypage page test...");
    }

    @GetMapping("/guide")
    public void guide() { log.info ("guide page test..."); }
}

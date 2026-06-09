package com.irallyin.server.web.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminSpaController {

    @GetMapping({"/", "/index.html", "/admin", "/admin/"})
    public String adminIndex() {
        return "forward:/admin/index.html";
    }
}

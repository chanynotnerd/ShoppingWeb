package com.example.shoppingweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {


    @GetMapping("/itemmanage")
    public String getItemManagePage() {
        return "/admin/itemmanage";
    }

    @GetMapping("/order")
    public String getOrderPage() {
        return "/admin/order";
    }

    @GetMapping("/usermanage")
    public String getUserPage() {
        return "/admin/usermanage";
    }

    @GetMapping("/payment")
    public String getPaymentPage() {
        return "/admin/payment";
    }

    @GetMapping({"", "/"})
    public String getAdminMain() {
        return "/admin/adminIndex";
    }
}

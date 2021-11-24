package com.shop.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping(value = "/error")
    public String error(HttpServletRequest request, Model model) {
        Object statusCodeObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(statusCodeObj != null) {
            int statusCode = Integer.valueOf(statusCodeObj.toString());

            if(statusCode == HttpStatus.UNAUTHORIZED.value()) {
                model.addAttribute("message", "로그인 후 이용해 주십시오.");
            }
        }

        return "error";
    }

}
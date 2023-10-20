package com.nantaaditya.dbmigration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SwaggerController {

  @GetMapping(value = "/swagger")
  public RedirectView redirectWithUsingRedirectView() {
    return new RedirectView("swagger-ui/index.html");
  }
}

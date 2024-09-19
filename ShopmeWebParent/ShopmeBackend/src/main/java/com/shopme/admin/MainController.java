package com.shopme.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping({ "", "/" })
	public String index(Model model) {
		String title = "Home - ShopmeAdmin";
		model.addAttribute("title", title);

		return "index";
	}

	@GetMapping("/login")
	public String login(Model model) {
		String title = "Login";
		model.addAttribute("title", title);

		return "login";
	}
}

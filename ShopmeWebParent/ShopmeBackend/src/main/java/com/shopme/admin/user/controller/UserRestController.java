package com.shopme.admin.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.user.service.UserService;

@RestController
public class UserRestController {

	@Autowired
	private UserService userService;

	@PostMapping("/users/check_email_uniqueness")
	public String isEmailUnique(@RequestParam("email") String email) {
		System.out.println("inside email uniqueness");
		boolean emailUnique = userService.isEmailUnique(email);
		return emailUnique ? "OK" : "Duplicate";
	}

	@PostMapping("/users/check_email_new")
	public String isEmailNew(@RequestParam("id") Integer id, @RequestParam("email") String email) {
		System.out.println("inside email new");
		boolean emailNew = userService.isEmailNew(id, email);
		return emailNew ? "OK" : "New";
	}
}

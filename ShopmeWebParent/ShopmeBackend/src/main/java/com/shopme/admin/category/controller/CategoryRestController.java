package com.shopme.admin.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.category.service.CategoryService;

@RestController
public class CategoryRestController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping("/categories/check_name")
	public String isNameUnique(@RequestParam("name") String name) {
		boolean result = categoryService.isNameUnique(name);
		return result ? "Ok" : "Duplicate";
	}

	@PostMapping("/categories/check_alias")
	public String isAliasUnique(@RequestParam("alias") String alias) {
		boolean result = categoryService.isAliasUnique(alias);
		return result ? "Ok" : "Duplicate";
	}

	@PostMapping("/categories/check_edit_name")
	public String isNameAssociatedWithId(@RequestParam("name") String name, @RequestParam("id") Integer id) {
		boolean result = categoryService.isNameAssociatedWithId(id, name);
		return result ? "Ok" : "New";
	}

	@PostMapping("/categories/check_edit_alias")
	public String isAliasAssociatedWithId(@RequestParam("alias") String alias, @RequestParam("id") Integer id) {
		boolean result = categoryService.isAliasAssociatedWithId(id, alias);
		return result ? "Ok" : "New";
	}
}

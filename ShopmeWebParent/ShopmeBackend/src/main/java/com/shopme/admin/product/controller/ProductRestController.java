package com.shopme.admin.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.product.service.ProductService;
import com.shopme.common.models.Category;

@RestController
public class ProductRestController {

	@Autowired
	private ProductService productService;

	@PostMapping("/products/brand/associated/categories")
	public List<Category> getAssociatedCategories(@RequestParam("brandId") Integer id) {
		List<Category> categories = productService.associatedCategories(id);
		return categories;
	}

	@PostMapping("/products/check_name_unique")
	public String isProductNameUnique(@RequestParam("name") String name) {
		boolean result = productService.checkProductNameIsUnique(name);
		return result ? "Ok" : "Duplicate";
	}

	@PostMapping("/products/check_alias_unique")
	public String isProductAliasUnique(@RequestParam("alias") String alias) {
		boolean result = productService.checkProductAliasIsUnique(alias);
		return result ? "Ok" : "Duplicate";
	}

	@PostMapping("/products/edit_name_unique")
	public String isProductNameAssociatedWithId(@RequestParam("name") String name, @RequestParam("id") Integer id) {
		boolean result = productService.checkProductNameAssociatedWithId(name, id);
		return result ? "Ok" : "New";
	}

	@PostMapping("/products/edit_alias_unique")
	public String isProductAliasAssociatedWithId(@RequestParam("alias") String alias, @RequestParam("id") Integer id) {
		boolean result = productService.checkProductAliasAssociatedWithId(alias, id);
		return result ? "Ok" : "New";
	}
}

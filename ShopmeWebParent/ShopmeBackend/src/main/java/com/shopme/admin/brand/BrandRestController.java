package com.shopme.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {

	@Autowired
	private BrandService brandService;

	@PostMapping("/brands/check_brand_name")
	public String isBrandNameUnique(@RequestParam("name") String name) {
		boolean result = brandService.isBrandNameUnique(name);
		return result ? "Ok" : "Duplicate";
	}

	@PostMapping("/brands/check_edit_brand_name")
	public String isBrandNameAssociatedWithId(@RequestParam("name") String name, @RequestParam("id") Integer id) {
		boolean result = brandService.isBrandNameAssociatedWithId(name, id);
		return result ? "Ok" : "New";
	}
}

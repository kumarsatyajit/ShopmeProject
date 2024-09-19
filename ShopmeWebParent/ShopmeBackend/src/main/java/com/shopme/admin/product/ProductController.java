package com.shopme.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.models.Brand;
import com.shopme.common.models.Product;
import com.shopme.common.payloads.ProductDto;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/products")
	public String productIndex(Model model) {
		String title = "Manage Products";
		List<Product> products = productService.listAllProducts();

		model.addAttribute("title", title);
		model.addAttribute("products", products);

		return "products/product_index";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		String title = "Manage Products";
		String pageTitle = "Add Product";
		String defaultImage = "/images/product-default-image.jpg";

		ProductDto productDto = new ProductDto();
		productDto.setId(1);
		List<Brand> brands = productService.getBrands();

		model.addAttribute("title", title);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("productDto", productDto);
		model.addAttribute("brands", brands);
		model.addAttribute("defaultImage", defaultImage);

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(@ModelAttribute ProductDto productDto) {
		System.out.println("Product Name: " + productDto.getName());
		System.out.println("Product Alias: " + productDto.getName().toLowerCase().replaceAll(" ", "-"));
		System.out.println("Brand Id: " + productDto.getBrandId());
		System.out.println("Category Id: " + productDto.getCategoryId());
		System.out.println("Enabled: " + productDto.isEnabled());
		System.out.println("InStock: " + productDto.isInStock());
		System.out.println("Cost: " + productDto.getCost());
		System.out.println("Price: " + productDto.getPrice());
		System.out.println("Discount: " + productDto.getDiscountPercent());
		System.out.println("Short Description: " + productDto.getShortDescription());
		System.out.println("Full Description: " + productDto.getFullDescription());

		return "redirect:/products";
	}

	@GetMapping("/products/update/enabled/status")
	public String updateProductEnabledStatus(@RequestParam("id") Integer id, @RequestParam("enabled") boolean enabled,
			RedirectAttributes redirectAttributes) {
		if (enabled) {
			String productName = productService.updateProductEnabledStatus(id, false);

			String message = String.format("Product: <b>%s</b> with ID: <b>%s</b> has been <b>disabled</b>",
					productName, id);
			redirectAttributes.addFlashAttribute("message", message);
		} else {
			String productName = productService.updateProductEnabledStatus(id, true);

			String message = String.format("Product: <b>%s</b> with ID: <b>%s</b> has been <b>enabled</b>", productName,
					id);
			redirectAttributes.addFlashAttribute("message", message);
		}

		return "redirect:/products";
	}

	@GetMapping("/products/delete")
	public String deleteProduct(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
		String productName = productService.deleteProduct(id);

		String message = String.format("Product: <b>%s</b> with ID: <b>%s</b> has been <b>deleted</b>", productName,
				id);
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/products";
	}
}

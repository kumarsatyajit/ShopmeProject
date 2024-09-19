package com.shopme.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.brand.repository.BrandRepository;
import com.shopme.common.models.Brand;
import com.shopme.common.models.Category;
import com.shopme.common.models.Product;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private BrandRepository brandRepo;

	public List<Product> listAllProducts() {
		List<Product> products = (List<Product>) productRepo.findAll();
		return products;
	}

	public List<Brand> getBrands() {
		List<Brand> brands = brandRepo.findAll();
		return brands;
	}

	public List<Category> associatedCategories(Integer id) {
		List<Category> categories = brandRepo.getAssociatedCategories(id);
		return categories;
	}

	public boolean checkProductNameIsUnique(String name) {
		Product product = productRepo.findByName(name);
		return product == null;
	}

	public boolean checkProductAliasIsUnique(String alias) {
		Product product = productRepo.findByAlias(alias);
		return product == null;
	}

	public boolean checkProductNameAssociatedWithId(String name, Integer id) {
		Product product = productRepo.findByName(name);
		return product.getId().equals(id);
	}

	public boolean checkProductAliasAssociatedWithId(String alias, Integer id) {
		Product product = productRepo.findByAlias(alias);
		return product.getId().equals(id);
	}

	@Transactional
	public String updateProductEnabledStatus(Integer id, boolean enabled) {
		productRepo.updateProductStatus(id, enabled);
		String productName = productRepo.getProductName(id);

		return productName;
	}

	public String deleteProduct(Integer id) {
		String productName = productRepo.getProductName(id);
		productRepo.deleteById(id);

		return productName;
	}

}

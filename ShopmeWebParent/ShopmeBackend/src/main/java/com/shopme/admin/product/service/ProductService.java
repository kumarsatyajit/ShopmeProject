package com.shopme.admin.product.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.brand.repository.BrandRepository;
import com.shopme.admin.product.repository.ProductRepository;
import com.shopme.common.models.Brand;
import com.shopme.common.models.Category;
import com.shopme.common.models.Product;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 5;

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private BrandRepository brandRepo;

	public List<Product> listAllProducts() {
		List<Product> products = (List<Product>) productRepo.findAll();
		return products;
	}

	public Page<Product> listByPage(int PageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(PageNum - 1, PRODUCT_PER_PAGE, sort);

		if (!keyword.isEmpty()) {
			Page<Product> page = productRepo.findAll(keyword, pageable);

			return page;
		}

		Page<Product> page = productRepo.findAll(pageable);

		return page;
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

	public Product deleteProduct(Integer id) {
		Product product = productRepo.findById(id).get();
		productRepo.delete(product);

		return product;
	}

	public Product getProduct(Integer id) {
		Product product = productRepo.findById(id).get();
		return product;
	}

	public Product saveProduct(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}

		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-").toLowerCase();
			product.setAlias(defaultAlias);
		} else {
			String defaultAlias = product.getAlias().replaceAll(" ", "-").toLowerCase();
			product.setAlias(defaultAlias);
		}

		product.setUpdatedTime(new Date());
		Product saveProduct = productRepo.save(product);

		return saveProduct;
	}
}

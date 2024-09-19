package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.product.repository.ProductRepository;
import com.shopme.common.models.Brand;
import com.shopme.common.models.Category;
import com.shopme.common.models.Product;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 38);
		Category category = entityManager.find(Category.class, 6);

		Product product = new Product();

		product.setName("Dell Inspiron 15");
		product.setAlias("dell-inspiron-15");
		product.setShortDescription("A good laptop from dell");
		product.setFullDescription("This is a very good laptop full description");

		product.setBrand(brand);
		product.setCategory(category);

		product.setPrice(456);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());

		Product saveProduct = productRepo.save(product);

		assertThat(saveProduct.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllProducts() {
		Iterable<Product> products = productRepo.findAll();
		products.forEach(product -> System.out.println(product));
	}

	@Test
	public void testGetProduct() {
		Product product = productRepo.findById(1).get();
		System.out.println(product);
	}
	
	@Test
	public void testDeleteProduct() {
		productRepo.deleteById(2);
	}
	
	@Test
	public void testSaveProductImages() {
		Brand brand = entityManager.find(Brand.class, 38);
		Category category = entityManager.find(Category.class, 6);

		Product product = new Product();

		product.setName("Dell Inspiron 15");
		product.setAlias("dell-inspiron-15");
		product.setShortDescription("A good laptop from dell");
		product.setFullDescription("This is a very good laptop full description");

		product.setBrand(brand);
		product.setCategory(category);

		product.setPrice(456);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());

		Product saveProduct = productRepo.save(product);

		assertThat(saveProduct.getId()).isGreaterThan(0);
	}
}

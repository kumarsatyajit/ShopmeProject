package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.brand.repository.BrandRepository;
import com.shopme.admin.category.repository.CategoryRepository;
import com.shopme.common.models.Brand;
import com.shopme.common.models.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {

	@Autowired
	private BrandRepository brandRepo;

	@Autowired
	private CategoryRepository categoryRepo;

	@Test
	public void testCreateBrand() {
		String name = "Samsung";
		String logo = "brand-logo.png";

		Brand brand = new Brand();

		brand.setName(name);
		brand.setLogo(logo);

		Category category1 = categoryRepo.findByName("Memory");
		Category category2 = categoryRepo.findByName("Internal Hard Drives");

		brand.addCategory(category1, category2);

		Brand savedBrand = brandRepo.save(brand);

		assertThat(savedBrand.getId()).isGreaterThan(0);
		assertThat(savedBrand.getName()).isEqualTo(name);
		assertThat(savedBrand.getCategories()).contains(category1);
		assertThat(savedBrand.getCategories()).contains(category2);
	}

	@Test
	public void testPrintAllBrand() {
		Iterable<Brand> brands = brandRepo.findAll();
		brands.forEach(brand -> System.out.println(brand));
	}

	@Test
	public void testGetBrandById() {
		Integer id = 1;
		Brand brand = brandRepo.findById(id).get();

		assertThat(brand.getId()).isEqualTo(id);

	}

	@Test
	public void testUpdateBrand() {
		String name = "Samsung";
		String updateName = "Samsung Electronics";

		Brand brand = brandRepo.findByName(name);
		brand.setName(updateName);

		Brand savedBrand = brandRepo.save(brand);

		assertThat(savedBrand.getName()).isEqualTo(updateName);
	}

	@Test
	public void testDeleteBrand() {
		String name = "Apple";

		Brand brand = brandRepo.findByName(name);
		brandRepo.delete(brand);

		assertThat(brandRepo.findByName(name)).isEqualTo(null);
	}

	@Test
	public void testBrandPagination() {
		int pageNum = 0;
		int pageSize = 10;

		Sort sort = Sort.by("name").ascending();

		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		Page<Brand> page = brandRepo.findAll(pageable);
		List<Brand> brands = page.getContent();

		for (Brand brand : brands) {
			System.out.println(brand);
		}
	}

	@Test
	public void testGetAllBrands() {
		List<Brand> brands = brandRepo.findAll();
		brands.forEach(brand -> System.out.println(brand));
	}

	@Test
	public void testGetAssociatedCategories() {
		Integer brandId = 1;

		List<Category> categories = brandRepo.getAssociatedCategories(brandId);
		categories.forEach(category -> System.out.println(category));
	}

}

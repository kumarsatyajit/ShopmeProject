package com.shopme.admin.user;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.category.repository.CategoryRepository;
import com.shopme.common.models.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepo;

	@Test
	public void testPrintRootCategory() {
		List<Category> rootCategories = categoryRepo.getRootCategories();
		for (Category category : rootCategories)
			System.out.println(category);
	}

	@Test
	public void testGetCategoryByName() {
		String name = "Electronicss";
		Category category = categoryRepo.findByName(name);
		System.out.println(category);
	}

	@Test
	public void testGetCategoryByAlias() {
		String alias = "camerass";
		Category category = categoryRepo.findByAlias(alias);
		System.out.println(category);
	}

	@Test
	public void testListRootCategoriesForDocument() {
		Sort sort = Sort.by("name").ascending();
		List<Category> rootCategories = categoryRepo.listRootCategoriesForDocument(sort);

		for (Category category : rootCategories) {
			System.out.println(category);
		}
	}
}

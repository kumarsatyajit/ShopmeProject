package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.models.Category;

public interface CategoryRepository
		extends CrudRepository<Category, Integer>, PagingAndSortingRepository<Category, Integer> {

	@Query("SELECT c FROM Category c WHERE c.parent IS NULL")
	public List<Category> getRootCategories();

	Category findByName(String name);

	Category findByAlias(String alias);

	@Modifying
	@Query("UPDATE Category c SET c.enabled= ?2 WHERE c.id= ?1")
	public void updateCategoryStatus(Integer id, boolean enabled);

	@Query("SELECT c FROM Category c WHERE c.parent IS NULL")
	public List<Category> listRootCategoriesForDocument(Sort sort);
	
	@Query("SELECT c FROM Category c WHERE c.parent IS NULL")
	public List<Category> listRootCategories();

}

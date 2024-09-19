package com.shopme.admin.category.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public List<Category> listRootCategories(Sort sort);

	@Query("SELECT c FROM Category c WHERE c.parent IS NULL")
	public Page<Category> listRootCategories(Pageable pageable);

	@Query("SELECT c FROM Category c LEFT JOIN c.parent p WHERE " + "c.name LIKE %?1% OR " + "c.alias LIKE %?1% OR "
			+ "p.name LIKE %?1%")
	public Page<Category> findAll(String keyword, Pageable pageable);

}

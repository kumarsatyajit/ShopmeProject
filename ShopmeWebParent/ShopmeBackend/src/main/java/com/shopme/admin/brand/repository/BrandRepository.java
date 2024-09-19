package com.shopme.admin.brand.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.shopme.common.models.Brand;
import com.shopme.common.models.Category;

@Repository
public interface BrandRepository extends CrudRepository<Brand, Integer>, PagingAndSortingRepository<Brand, Integer> {

	Brand findByName(String name);

	@Query("SELECT b FROM Brand b JOIN b.categories c WHERE " + "CAST(b.id AS string) LIKE %?1% OR "
			+ "b.name LIKE %?1% OR " + "c.name LIKE %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);

	@Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
	public @NonNull List<Brand> findAll();

	@Query("SELECT NEW com.shopme.common.models.Category(c.id, c.name) " + "FROM Brand b JOIN b.categories c " + "WHERE b.id = :id")
	public @NonNull List<Category> getAssociatedCategories(@Param("id") Integer id);
}

package com.shopme.admin.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.models.Brand;

@Repository
public interface BrandRepository extends CrudRepository<Brand, Integer>, PagingAndSortingRepository<Brand, Integer> {

	Brand findByName(String name);

	@Query("SELECT b FROM Brand b JOIN b.categories c WHERE " + "CAST(b.id AS string) LIKE %?1% OR "
			+ "b.name LIKE %?1% OR " + "c.name LIKE %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);
}

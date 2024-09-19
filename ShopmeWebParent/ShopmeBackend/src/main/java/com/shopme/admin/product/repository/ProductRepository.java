package com.shopme.admin.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.models.Product;

@Repository
public interface ProductRepository
		extends CrudRepository<Product, Integer>, PagingAndSortingRepository<Product, Integer> {

	Product findByName(String name);

	Product findByAlias(String alias);

	@Modifying
	@Query("UPDATE Product p SET p.enabled= ?2 WHERE p.id= ?1")
	public void updateProductStatus(Integer id, boolean enabled);

	@Query("SELECT p.name FROM Product p WHERE p.id= ?1")
	public String getProductName(Integer id);

	@Query("SELECT p FROM Product p " + "JOIN p.brand b " + "JOIN p.category c "
			+ "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(p.alias) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(p.fullDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	Page<Product> findAll(@Param("keyword") String keyword, Pageable pageable);

}

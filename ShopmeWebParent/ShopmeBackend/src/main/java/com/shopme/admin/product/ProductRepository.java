package com.shopme.admin.product;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.models.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

	Product findByName(String name);

	Product findByAlias(String alias);

	@Modifying
	@Query("UPDATE Product p SET p.enabled= ?2 WHERE p.id= ?1")
	public void updateProductStatus(Integer id, boolean enabled);

	@Query("SELECT p.name FROM Product p WHERE p.id= ?1")
	public String getProductName(Integer id);
}

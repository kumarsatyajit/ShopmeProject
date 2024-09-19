package com.shopme.admin.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {

	@Query("SELECT u FROM User u WHERE u.email= :email")
	public User getUserByEmail(@Param("email") String email);

	@Query("UPDATE User u SET u.enabled= :enabled WHERE u.id= :id")
	@Modifying
	public void updateUserStatus(@Param("enabled") boolean enabled, @Param("id") Integer id);

	@Query("SELECT u FROM User u JOIN u.roles r WHERE" + " u.firstName LIKE %?1% OR" + " u.lastName LIKE %?1% OR"
			+ " u.email LIKE %?1% OR" + " r.name LIKE %?1% OR" + " CAST(u.id AS string) LIKE %?1% OR"
			+ " CONCAT(u.firstName,' ',u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
}

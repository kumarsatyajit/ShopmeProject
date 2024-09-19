package com.shopme.admin.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.models.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

}

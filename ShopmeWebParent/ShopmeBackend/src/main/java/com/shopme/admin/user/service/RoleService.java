package com.shopme.admin.user.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.user.repository.RoleRepository;
import com.shopme.common.models.Role;

@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepo;

	public List<Role> listAllRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	public Set<Role> insertSelectedRoles(Set<Integer> roleIds) {
		Set<Role> roles = listAllRoles().stream().filter(role -> roleIds.contains(role.getId()))
				.collect(Collectors.toSet());
		return roles;
	}
}

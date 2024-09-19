package com.shopme.admin.user.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.user.repository.UserRepository;
import com.shopme.common.models.Role;
import com.shopme.common.models.User;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	public static final int USER_PER_PAGE = 5;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public Page<User> listByPage(Integer pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);

		if (!keyword.isEmpty())
			return userRepo.findAll(keyword, pageable);

		return userRepo.findAll(pageable);
	}

	public List<User> listAllUsers() {
		return (List<User>) userRepo.findAll();
	}

	public boolean isEmailUnique(String email) {
		User user = userRepo.getUserByEmail(email);
		return user == null;
	}

	public boolean isEmailNew(Integer id, String email) {
		User user = userRepo.getUserByEmail(email);
		return user.getId() == id;
	}

	public String insertPassword(String password) {
		String encodePassword = passwordEncoder.encode(password);
		return encodePassword;
	}

	public User saveUser(User user) {
		User saveUser = userRepo.save(user);
		return saveUser;
	}

	public User getUser(Integer userId) {
		User user = userRepo.findById(userId).get();
		return user;
	}

	public Set<Integer> getUserRoleIds(Set<Role> roles) {
		Set<Integer> roleIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
		return roleIds;
	}

	public List<User> getUsers() {
		return (List<User>) userRepo.findAll();
	}

	public User deleteUser(Integer id) {
		User user = userRepo.findById(id).get();
		userRepo.delete(user);

		return user;
	}

	@Transactional
	public void updateUserStatus(boolean enabled, Integer id) {
		userRepo.updateUserStatus(enabled, id);
	}

	public User getUserByEmail(String email) {
		User user = userRepo.getUserByEmail(email);
		return user;
	}
}

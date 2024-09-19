package com.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shopme.admin.user.repository.UserRepository;
import com.shopme.common.models.User;

public class ShopmeUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.getUserByEmail(username);
		if (user != null) {
			ShopmeUserDetails shopmeUserDetails = new ShopmeUserDetails(user);
			return shopmeUserDetails;
		}

		throw new UsernameNotFoundException(String.format("Could not user with email: %s", username));
	}

}

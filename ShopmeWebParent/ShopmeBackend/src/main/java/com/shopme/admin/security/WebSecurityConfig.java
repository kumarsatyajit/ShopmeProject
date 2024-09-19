package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}

	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());

		return authenticationProvider;
	}

	@Bean
	SecurityFilterChain configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/users/**").hasAuthority("Admin")
				.requestMatchers("/categories/**").hasAnyAuthority("Admin", "Editor").requestMatchers("/brands/**")
				.hasAnyAuthority("Admin", "Editor").requestMatchers("/products/**")
				.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper").anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/login").usernameParameter("email").permitAll())
				.logout(logout -> logout.permitAll())
				.rememberMe(rem -> rem.key("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789!@#$%^&*()_")
						.tokenValiditySeconds(7 * 24 * 60 * 60));

		return http.build();
	}

	@Bean
	WebSecurityCustomizer configureWebSecurity() throws Exception {
		return (web) -> web.ignoring().requestMatchers("/webjars/**", "/css/**", "/js/**", "/images/**",
				"/templates/**");
	}
}

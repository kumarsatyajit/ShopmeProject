package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.service.UserService;
import com.shopme.admin.utility.FileUploadUtility;
import com.shopme.common.models.Role;
import com.shopme.common.models.User;
import com.shopme.common.payloads.AccountDetailsDto;

@Controller
public class AccountController {

	@Autowired
	private UserService userService;

	@GetMapping("/account")
	public String accountDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser, Model model) {
		String email = loggedUser.getUsername();
		User user = userService.getUserByEmail(email);

		String title = "User Account Details";
		String defaultImage = user.getUserImagePath();

		AccountDetailsDto accountDto = new AccountDetailsDto();
		accountDto.setId(user.getId());
		accountDto.setFirstName(user.getFirstName());
		accountDto.setLastName(user.getLastName());
		accountDto.setEmail(user.getEmail());
		accountDto.setAssignedRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

		model.addAttribute("accountDto", accountDto);
		model.addAttribute("title", title);
		model.addAttribute("defaultImage", defaultImage);

		return "users/account_details";
	}

	@PostMapping("/account/save")
	public String saveAccountDetails(@ModelAttribute AccountDetailsDto accountDto,
			@RequestParam("image") MultipartFile multipartFile, RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {

		String userEmail = accountDto.getEmail();
		User user = userService.getUserByEmail(userEmail);
		user.setFirstName(accountDto.getFirstName());
		user.setLastName(accountDto.getLastName());
		if(!accountDto.getPassword().isEmpty())
			user.setPassword(userService.insertPassword(accountDto.getPassword()));
		if (!multipartFile.isEmpty()) {
			@SuppressWarnings("null")
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setImage(fileName);

			User savedUser = userService.saveUser(user);

			String uploadDir = "users-photos/" + savedUser.getId();
			FileUploadUtility.cleanDir(uploadDir);
			FileUploadUtility.saveFile(uploadDir, fileName, multipartFile);

			loggedUser.setFirstName(savedUser.getFirstName());
			loggedUser.setLastName(savedUser.getLastName());

			String message = String.format("Your account details have been updated.");
			redirectAttributes.addFlashAttribute("message", message);

			return "redirect:/account";
		}

		User savedUser = userService.saveUser(user);

		loggedUser.setFirstName(savedUser.getFirstName());
		loggedUser.setLastName(savedUser.getLastName());

		String message = "Your account details have been updated";
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/account";
	}
}

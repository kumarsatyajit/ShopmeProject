package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.DocumentException;
import com.shopme.admin.user.exporter.UserCsvExporter;
import com.shopme.admin.user.exporter.UserExcelExporter;
import com.shopme.admin.user.exporter.UserPdfExporter;
import com.shopme.admin.user.service.RoleService;
import com.shopme.admin.user.service.UserService;
import com.shopme.admin.utility.FileUploadUtility;
import com.shopme.common.models.Role;
import com.shopme.common.models.User;
import com.shopme.common.payloads.UserDto;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@GetMapping("/users")
	public String firstPage(Model model) {
		Integer pageNum = 1;
		String sortField = "firstName";
		String sortDir = "asc";
		String keyword = "";

		return listByPage(pageNum, sortField, sortDir, keyword, model);
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") Integer pageNum,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir,
			@RequestParam("keyword") String keyword, Model model) {

		String title = "Manage Users - ShopmeAdmin";
		Page<User> page = userService.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> users = page.getContent();

		String reverseDir = sortDir.equals("asc") ? "desc" : "asc";
		long start = (pageNum - 1) * UserService.USER_PER_PAGE + 1;
		long end = start + UserService.USER_PER_PAGE - 1;
		long totalUsers = page.getTotalElements();

		model.addAttribute("title", title);
		model.addAttribute("baseUrl", "/users/page/");
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("users", users);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("result", "users");
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("size", users.size());
		model.addAttribute("lastPage", page.getTotalPages());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseDir", reverseDir);
		model.addAttribute("keyword", keyword);

		return "users/user_index";
	}

	@GetMapping("/users/new")
	public String addUser(Model model) {
		String title = "Manage Users - New User";
		String pageTitle = "New User";
		String defaultImage = "/images/user-default-img.webp";
		List<Role> allRoles = roleService.listAllRoles();
		UserDto userDto = new UserDto();

		model.addAttribute("title", title);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("userDto", userDto);
		model.addAttribute("allRoles", allRoles);
		model.addAttribute("defaultImage", defaultImage);

		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(@ModelAttribute UserDto userDto, @RequestParam("image") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {
		if (userDto.getId() != null) {
			System.out.println("Inside edit target block");
			User user = userService.getUser(userDto.getId());
			user.setFirstName(userDto.getFirstName());
			user.setLastName(userDto.getLastName());
			user.setEmail(userDto.getEmail());
			if (!userDto.getPassword().isEmpty())
				user.setPassword(userService.insertPassword(userDto.getPassword()));
			user.setEnabled(userDto.isEnabled());
			user.setRoles(roleService.insertSelectedRoles(userDto.getRoleIds()));
			if (!multipartFile.isEmpty()) {
				@SuppressWarnings("null")
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				user.setImage(fileName);
				User updateUser = userService.saveUser(user);
				String uploadDir = "users-photos/" + updateUser.getId();
				FileUploadUtility.cleanDir(uploadDir);
				FileUploadUtility.saveFile(uploadDir, fileName, multipartFile);

				String message = String.format(
						"User: <strong>%s</strong> with ID: <strong>%s</strong> edited successfully.",
						updateUser.getFirstName() + " " + updateUser.getLastName(), updateUser.getId());
				redirectAttributes.addFlashAttribute("message", message);

				return "redirect:/users";
			}
			User saveUser = userService.saveUser(user);
			String message = String.format(
					"User: <strong>%s</strong> with ID: <strong>%s</strong> edited successfully.",
					saveUser.getFirstName() + " " + saveUser.getLastName(), saveUser.getId());
			redirectAttributes.addFlashAttribute("message", message);

			return "redirect:/users";

		} else {
			User user = new User();
			user.setFirstName(userDto.getFirstName());
			user.setLastName(userDto.getLastName());
			user.setEmail(userDto.getEmail());
			user.setPassword(userService.insertPassword(userDto.getPassword()));
			user.setEnabled(userDto.isEnabled());
			user.setRoles(roleService.insertSelectedRoles(userDto.getRoleIds()));
			if (!multipartFile.isEmpty()) {
				@SuppressWarnings("null")
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				user.setImage(fileName);
				User saveUser = userService.saveUser(user);
				String uploadDir = "users-photos/" + saveUser.getId();
				FileUploadUtility.saveFile(uploadDir, fileName, multipartFile);

				String message = String.format(
						"User: <strong>%s</strong> with ID: <strong>%s</strong> created successfully.",
						saveUser.getFirstName() + " " + saveUser.getLastName(), saveUser.getId());
				redirectAttributes.addFlashAttribute("message", message);

				return "redirect:/users";

			}

			User saveUser = userService.saveUser(user);
			String message = String.format(
					"User: <strong>%s</strong> with ID: <strong>%s</strong> created successfully.",
					saveUser.getFirstName() + " " + saveUser.getLastName(), saveUser.getId());
			redirectAttributes.addFlashAttribute("message", message);

			return "redirect:/users";
		}
	}

	@GetMapping("/users/edit")
	public String editUser(@RequestParam("id") Integer userId, Model model) {
		User user = userService.getUser(userId);

		String title = "Manage Users - Edit User";
		String pageTitle = "Edit User";
		List<Role> allRoles = roleService.listAllRoles();
		String defaultImage = user.getUserImagePath();

		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());
		userDto.setEmail(user.getEmail());
		userDto.setEnabled(user.isEnabled());
		userDto.setRoleIds(userService.getUserRoleIds(user.getRoles()));

		model.addAttribute("title", title);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("allRoles", allRoles);
		model.addAttribute("defaultImage", defaultImage);
		model.addAttribute("userDto", userDto);

		return "users/user_form";
	}

	@GetMapping("/users/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		UserCsvExporter exporter = new UserCsvExporter();
		List<User> users = userService.getUsers();
		exporter.export(users, response);
	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		UserExcelExporter exporter = new UserExcelExporter();
		List<User> users = userService.getUsers();
		exporter.export(users, response);
	}

	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws DocumentException, IOException {
		UserPdfExporter exporter = new UserPdfExporter();
		List<User> users = userService.getUsers();
		exporter.export(users, response);
	}

	@GetMapping("/users/delete")
	public String deleteUser(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) throws IOException {
		User user = userService.deleteUser(id);
		String dirName = "users-photos/" + user.getId();
		FileUploadUtility.removeDir(dirName);
		String message = String.format("User: <strong>%s</strong> with ID: <strong>%s</strong> deleted successfully.",
				user.getFirstName() + ' ' + user.getLastName(), user.getId());

		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/users";
	}

	@GetMapping("/users/update/status")
	public String updateUserStatus(@RequestParam("enabled") boolean enabled, @RequestParam("id") Integer id,
			RedirectAttributes redirectAttributes) {
		String message;
		if (enabled) {
			userService.updateUserStatus(false, id);
			message = String.format("User with ID: <strong>%s</strong> has been <strong>disable</strong>", id);
			redirectAttributes.addFlashAttribute("message", message);

			return "redirect:/users";
		} else {
			userService.updateUserStatus(true, id);
			message = String.format("User with ID: <strong>%s</strong> has been <strong>enable</strong>", id);
			redirectAttributes.addFlashAttribute("message", message);

			return "redirect:/users";
		}
	}
}

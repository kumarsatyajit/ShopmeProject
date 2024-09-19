package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.DocumentException;
import com.shopme.admin.utility.FileUploadUtility;
import com.shopme.common.models.Category;
import com.shopme.common.payloads.CategoryDto;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/categories")
	public String listAllCategories(Model model) {
		List<Category> categories = categoryService.listAllCategories();

		String title = "Manage Categories";

		model.addAttribute("title", title);
		model.addAttribute("categories", categories);

		return "categories/category_index";
	}

	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		String title = "Manage Categories - New Category";
		String pageTitle = "New Category";
		String defaultImage = "/images/category-default_image.png";

		CategoryDto categoryDto = new CategoryDto();
		List<String> names = categoryService.listCategoryNames();

		model.addAttribute("title", title);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("defaultImage", defaultImage);
		model.addAttribute("categoryDto", categoryDto);
		model.addAttribute("names", names);

		return "categories/category_form";
	}

	@PostMapping("/categories/save")
	public String saveCategory(@ModelAttribute CategoryDto categoryDto,
			@RequestParam("image") MultipartFile multipartFile, RedirectAttributes redirectAttributes)
			throws IOException {
		if (categoryDto.getId() != null) {
			Category category = categoryService.getCategoryById(categoryDto.getId());

			category.setName(categoryDto.getName());
			category.setAlias(categoryDto.getAlias());
			category.setEnabled(categoryDto.isEnabled());
			category.setParent(categoryService.getParentCategory(categoryDto.getParent()));
			if (!multipartFile.isEmpty()) {
				@SuppressWarnings("null")
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				category.setImage(fileName);

				Category saveCategory = categoryService.saveCategory(category);

				String uploadDir = "categories-photos/" + saveCategory.getId();
				FileUploadUtility.cleanDir(uploadDir);
				FileUploadUtility.saveFile(uploadDir, fileName, multipartFile);

				String message = String.format(
						"Category <strong>%s</strong> with ID: <strong>%s</strong> edited successfully",
						saveCategory.getName(), saveCategory.getId());

				redirectAttributes.addFlashAttribute("message", message);
			}

			Category saveCategory = categoryService.saveCategory(category);
			String message = String.format(
					"Category <strong>%s</strong> with ID: <strong>%s</strong> edited successfully",
					saveCategory.getName(), saveCategory.getId());

			redirectAttributes.addFlashAttribute("message", message);

			return "redirect:/categories";
		} else {
			Category category = new Category();

			category.setName(categoryDto.getName());
			category.setAlias(categoryDto.getAlias());
			category.setEnabled(categoryDto.isEnabled());
			if (categoryDto.getParent() != null)
				category.setParent(categoryService.getParentCategory(categoryDto.getParent()));
			@SuppressWarnings("null")
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);

			Category saveCategory = categoryService.saveCategory(category);

			String uploadDir = "categories-photos/" + saveCategory.getId();
			FileUploadUtility.saveFile(uploadDir, fileName, multipartFile);

			String message = String.format(
					"Category <strong>%s</strong> with ID: <strong>%s</strong> created successfully",
					saveCategory.getName(), saveCategory.getId());

			redirectAttributes.addFlashAttribute("message", message);

			return "redirect:/categories";
		}

	}

	@GetMapping("/categories/edit")
	public String editCategory(Model model, @RequestParam("id") Integer id) {
		Category category = categoryService.getCategoryById(id);

		String title = "Manage Categories - Edit Category";
		String pageTitle = "Edit Category";
		String defaultImage = category.getCategoryImage();
		List<String> names = categoryService.listCategoryNames();

		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId(category.getId());
		categoryDto.setName(category.getName());
		categoryDto.setAlias(category.getAlias());
		categoryDto.setEnabled(category.isEnabled());
		categoryDto.setParent(category.getParentName());

		model.addAttribute("title", title);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("defaultImage", defaultImage);
		model.addAttribute("names", names);
		model.addAttribute("categoryDto", categoryDto);

		return "categories/category_form";
	}

	@GetMapping("/categories/update/status")
	public String updateCategoryStatus(@RequestParam("id") Integer id, @RequestParam("enabled") boolean enabled,
			RedirectAttributes redirectAttributes) {
		if (enabled) {
			categoryService.updateCategoryStatus(id, false);
			String message = String.format("Category with ID: <b>%s</b> has been <b>disable</b>", id);
			redirectAttributes.addFlashAttribute("message", message);
		} else {
			categoryService.updateCategoryStatus(id, true);
			String message = String.format("Category with ID: <b>%s</b> has been <b>enabled</b>", id);
			redirectAttributes.addFlashAttribute("message", message);
		}

		return "redirect:/categories";
	}

	@GetMapping("/categories/delete")
	public String deleteCategory(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes)
			throws IOException {
		Category category = categoryService.deleteCategory(id);

		String dirName = "/categories-photos/" + category.getId();
		FileUploadUtility.removeDir(dirName);

		String message = String.format("Category <b>%s</b> with ID: <b>%s</b> deleted successfully", category.getName(),
				category.getId());
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/categories";
	}

	@GetMapping("/categories/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		List<Category> categories = categoryService.listAllCategoriesForDocument();
		exporter.export(categories, response);
	}

	@GetMapping("/categories/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		CategoryExcelExporter exporter = new CategoryExcelExporter();
		List<Category> categories = categoryService.listAllCategoriesForDocument();
		exporter.export(categories, response);
	}

	@GetMapping("/categories/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws DocumentException, IOException {
		CategoryPdfExporter exporter = new CategoryPdfExporter();
		List<Category> categories = categoryService.listAllCategoriesForDocument();
		exporter.export(categories, response);
	}

}

package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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

import com.shopme.admin.category.service.CategoryService;
import com.shopme.admin.utility.FileUploadUtility;
import com.shopme.common.models.Brand;
import com.shopme.common.payloads.BrandDto;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class BrandController {

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/brands")
	public String firstPage(Model model) {
		Integer pageNum = 1;
		String sortField = "name";
		String sortDir = "asc";
		String keyword = "";

		return listByPage(pageNum, sortField, sortDir, keyword, model);
	}

	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") Integer pageNum,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir,
			@RequestParam("keyword") String keyword, Model model) {

		String title = "Manage Users - ShopmeAdmin";
		Page<Brand> page = brandService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Brand> brands = page.getContent();

		String reverseDir = sortDir.equals("asc") ? "desc" : "asc";
		long start = (pageNum - 1) * BrandService.BRAND_PER_PAGE + 1;
		long end = start + BrandService.BRAND_PER_PAGE - 1;
		long totalBrands = page.getTotalElements();

		model.addAttribute("title", title);
		model.addAttribute("baseUrl", "/brands/page/");
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("brands", brands);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("result", "brands");
		model.addAttribute("totalBrands", totalBrands);
		model.addAttribute("size", brands.size());
		model.addAttribute("lastPage", page.getTotalPages());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseDir", reverseDir);
		model.addAttribute("keyword", keyword);

		return "brands/brand_index";
	}

	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		String title = "Manage Brands - New Brand";
		String pageTitle = "New Brand";
		String defaultImage = "/images/brand-default-logo.webp";
		List<String> categoryNames = categoryService.listCategoryNames();
		BrandDto brandDto = new BrandDto();

		model.addAttribute("title", title);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("defaultImage", defaultImage);
		model.addAttribute("categoryNames", categoryNames);
		model.addAttribute("brandDto", brandDto);

		return "brands/brand_form";
	}

	@PostMapping("/brands/save")
	public String saveBrand(@ModelAttribute BrandDto brandDto, @RequestParam("image") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {

		if (brandDto.getId() != null) {
			Brand brand = brandService.getBrandById(brandDto.getId());
			brand.setName(brandDto.getName());
			brand.setCategories(categoryService.getSelectedCategories(brandDto.getCategories()));

			if (!multipartFile.isEmpty()) {
				@SuppressWarnings("null")
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				brand.setLogo(fileName);

				Brand saveBrand = brandService.saveBrand(brand);
				String uploadDir = "brand-logos/" + saveBrand.getId();

				FileUploadUtility.cleanDir(uploadDir);
				FileUploadUtility.saveFile(uploadDir, fileName, multipartFile);

				String message = String.format("Brand: <b>%s</b> with ID: <b>%s</b> successfully edited.",
						saveBrand.getName(), saveBrand.getId());

				redirectAttributes.addFlashAttribute("message", message);

				return "redirect:/brands";

			}
			Brand saveBrand = brandService.saveBrand(brand);
			String message = String.format("Brand: <b>%s</b> with ID: <b>%s</b> successfully edited.",
					saveBrand.getName(), saveBrand.getId());

			redirectAttributes.addFlashAttribute("message", message);

			return "redirect:/brands";
		} else {
			Brand brand = new Brand();
			brand.setName(brandDto.getName());
			brand.setCategories(categoryService.getSelectedCategories(brandDto.getCategories()));

			@SuppressWarnings("null")
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);

			Brand saveBrand = brandService.saveBrand(brand);
			String uploadDir = "brand-logos/" + saveBrand.getId();

			FileUploadUtility.saveFile(uploadDir, fileName, multipartFile);
			String message = String.format("Brand: <b>%s</b> with ID: <b>%s</b> successfully created.",
					saveBrand.getName(), saveBrand.getId());

			redirectAttributes.addFlashAttribute("message", message);

			return "redirect:/brands";
		}
	}

	@GetMapping("/brands/edit")
	public String editBrand(@RequestParam("id") Integer id, Model model) {
		Brand brand = brandService.getBrandById(id);

		String title = "Manage Brands - Edit Brand";
		String pageTitle = "Edit Brand";
		List<String> categoryNames = categoryService.listCategoryNames();
		String defaultImage = brand.getBrandlogo();

		BrandDto brandDto = new BrandDto();
		brandDto.setId(brand.getId());
		brandDto.setName(brand.getName());
		Set<String> selectedCategories = categoryService.getCategoryNames(brand.getCategories());

		model.addAttribute("title", title);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("categoryNames", categoryNames);
		model.addAttribute("defaultImage", defaultImage);
		model.addAttribute("brandDto", brandDto);
		model.addAttribute("selectedCategories", selectedCategories);

		return "brands/brand_form";
	}

	@GetMapping("/brands/delete")
	public String deleteBrand(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes)
			throws IOException {
		Brand brand = brandService.deleteBrand(id);

		String dirName = "brand-logos/" + brand.getId();
		FileUploadUtility.removeDir(dirName);

		String message = String.format("Brand <b>%s</b> with ID: <b>%s</b> deleted successfully.", brand.getName(),
				brand.getId());
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/brands";
	}

	@GetMapping("/brands/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		BrandCsvExporter exporter = new BrandCsvExporter();
		List<Brand> brands = brandService.listAllBrands();
		exporter.export(brands, response);
	}

	@GetMapping("/brands/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		BrandExcelExporter exporter = new BrandExcelExporter();
		List<Brand> brands = brandService.listAllBrands();
		exporter.export(brands, response);
	}

}

package com.shopme.admin.product.controller;

import java.io.IOException;
import java.util.ArrayList;
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

import com.shopme.admin.brand.service.BrandService;
import com.shopme.admin.category.service.CategoryService;
import com.shopme.admin.product.exporter.ProductCsvExporter;
import com.shopme.admin.product.exporter.ProductExcelExporter;
import com.shopme.admin.product.service.ProductService;
import com.shopme.admin.utility.FileUploadUtility;
import com.shopme.common.models.Brand;
import com.shopme.common.models.Product;
import com.shopme.common.models.ProductDetail;
import com.shopme.common.models.ProductImage;
import com.shopme.common.payloads.ProductDto;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/products")
	public String firstPage(Model model) {
		Integer pageNum = 1;
		String sortField = "name";
		String sortDir = "asc";
		String keyword = "";

		return listByPage(pageNum, sortField, sortDir, keyword, model);
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") Integer pageNum,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir,
			@RequestParam("keyword") String keyword, Model model) {

		String title = "Manage Products - ShopmeAdmin";
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Product> products = page.getContent();

		String reverseDir = sortDir.equals("asc") ? "desc" : "asc";
		long start = (pageNum - 1) * ProductService.PRODUCT_PER_PAGE + 1;
		long end = start + ProductService.PRODUCT_PER_PAGE - 1;
		long totalProducts = page.getTotalElements();

		model.addAttribute("title", title);
		model.addAttribute("baseUrl", "/products/page/");
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("products", products);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("result", "products");
		model.addAttribute("totalEntities", totalProducts);
		model.addAttribute("size", products.size());
		model.addAttribute("lastPage", page.getTotalPages());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseDir", reverseDir);
		model.addAttribute("keyword", keyword);

		return "products/product_index";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		String title = "Manage Products";
		String pageTitle = "Add Product";
		String defaultImage = "/images/product-default-image.jpg";

		ProductDto productDto = new ProductDto();
		List<Brand> brands = productService.getBrands();

		model.addAttribute("title", title);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("productDto", productDto);
		model.addAttribute("brands", brands);
		model.addAttribute("defaultImage", defaultImage);
		model.addAttribute("currentExtraImageCount", 1);

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(@ModelAttribute ProductDto productDto,
			@RequestParam(name = "image") MultipartFile mainImage,
			@RequestParam(name = "images", required = false) MultipartFile[] extraImages,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIds", required = false) String[] extraImageIds,
			@RequestParam(name = "imageNames", required = false) String[] extraImageNames,
			@RequestParam(name = "detailsIds", required = false) String[] detailsIds,
			@RequestParam(name = "detailsNames", required = false) String[] detailsNames,
			@RequestParam(name = "detailsValues", required = false) String[] detailsValues,
			RedirectAttributes redirectAttributes) throws IOException {
		if (productDto.getId() != null) {
			Product product = productService.getProduct(productDto.getId());
			setProductField(productDto, product);
			setProductMainImage(mainImage, product);
			setProductExtraImages(extraImageIds, extraImageNames, extraImages, product);
			setProductDetails(detailsIds, detailsNames, detailsValues, detailNames, detailValues, product);
			Product saveProduct = productService.saveProduct(product);
			updateUploadImages(extraImageIds, extraImageNames, mainImage, extraImages, saveProduct);

			String message = String.format("Product: <b>%s</b> with ID: <b>%s</b> edited successfully",
					saveProduct.getName(), saveProduct.getId());

			redirectAttributes.addFlashAttribute("message", message);
		} else {
			Product product = new Product();
			setProductField(productDto, product);
			setProductMainImage(mainImage, product);
			setProductExtraImages(extraImages, product);
			setProductDetails(detailNames, detailValues, product);
			Product saveProduct = productService.saveProduct(product);
			saveUploadImages(mainImage, extraImages, saveProduct);

			String message = String.format("Product: <b>%s</b> with ID: <b>%s</b> created successfully",
					saveProduct.getName(), saveProduct.getId());

			redirectAttributes.addFlashAttribute("message", message);
		}
		return "redirect:/products";
	}

	private void setProductDetails(String[] detailsIds, String[] detailsNames, String[] detailsValues,
			String[] detailNames, String[] detailValues, Product product) {

		if (detailsIds == null || detailsNames == null || detailsValues == null) {
			product.clearProductDetails();
			setProductDetails(detailNames, detailValues, product);
			return;
		}

		ArrayList<ProductDetail> unchangedProductDetails = new ArrayList<ProductDetail>();

		for (int index = 0; index < detailsIds.length; index++) {
			Integer id = Integer.parseInt(detailsIds[index]);
			String name = detailsNames[index];
			String value = detailsValues[index];

			ProductDetail productDetail = new ProductDetail(id, name, value, product);
			unchangedProductDetails.add(productDetail);
		}

		product.getDetails().retainAll(unchangedProductDetails);

		for (int index = 0; index < detailNames.length; index++) {
			String name = detailNames[index];
			String value = detailValues[index];

			if (!product.containsProductDetail(name, value))
				product.addProductDetails(name, value);
		}
	}

	private void setProductExtraImages(String[] extraImageIds, String[] extraImageNames, MultipartFile[] extraImages,
			Product product) throws IOException {

		if (extraImageIds == null || extraImageNames == null) {
			product.clearExtraImages();
			FileUploadUtility.cleanDir("product-images/" + product.getId() + "/extras");
			setProductExtraImages(extraImages, product);
			return;
		}

		ArrayList<ProductImage> unchangedImages = new ArrayList<>();

		for (int index = 0; index < extraImageIds.length; index++) {
			Integer id = Integer.parseInt(extraImageIds[index]);
			String name = extraImageNames[index];

			ProductImage productImage = new ProductImage(id, name, product);
			unchangedImages.add(productImage);
		}

		ArrayList<ProductImage> changedImages = new ArrayList<>(product.getExtraImages());
		changedImages.removeAll(unchangedImages);
		product.getExtraImages().removeAll(changedImages);

		changedImages.forEach(productImage -> {
			String directory = "product-images/" + product.getId() + "/extras/";
			String fileName = productImage.getName();
			try {
				FileUploadUtility.deleteFile(directory, fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		for (MultipartFile extraImage : extraImages) {
			if (!extraImage.isEmpty()) { // Ensure non-empty file
				String fileName = StringUtils.cleanPath(extraImage.getOriginalFilename());
				if (!product.containsImageName(fileName)) {
					product.addExtraImage(fileName);
				}
			}
		}
	}

	private void updateUploadImages(String[] extraImageIds, String[] extraImageNames, MultipartFile mainImage,
			MultipartFile[] extraImages, Product product) throws IOException {

		if (!mainImage.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
			String uploadDir = "product-images/" + product.getId();
			FileUploadUtility.cleanDir(uploadDir);
			FileUploadUtility.saveFile(uploadDir, fileName, mainImage);
		}

		if (extraImageIds == null || extraImageNames == null) {
			saveUploadImages(mainImage, extraImages, product);

			return;
		}

		if (extraImages == null)
			return;

		for (int index = 0; index < extraImages.length; index++) {
			MultipartFile extraImage = extraImages[index];
			if (!extraImage.isEmpty()) {
				String fileName = StringUtils.cleanPath(extraImage.getOriginalFilename());
				String uploadDir = "product-images/" + product.getId() + "/extras";
				FileUploadUtility.saveFile(uploadDir, fileName, extraImage);
			}
		}
	}

	private void saveUploadImages(MultipartFile mainImage, MultipartFile[] extraImages, Product product)
			throws IOException {
		if (!mainImage.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
			String uploadDir = "product-images/" + product.getId();
			FileUploadUtility.saveFile(uploadDir, fileName, mainImage);
		}

		if (extraImages == null)
			return;

		for (int index = 0; index < extraImages.length; index++) {
			MultipartFile extraImage = extraImages[index];
			if (!extraImage.isEmpty()) {
				String fileName = StringUtils.cleanPath(extraImage.getOriginalFilename());
				String uploadDir = "product-images/" + product.getId() + "/extras";
				FileUploadUtility.saveFile(uploadDir, fileName, extraImage);
			}
		}
	}

	private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {

		if (detailNames == null || detailValues == null)
			return;

		for (int index = 0; index < detailNames.length; index++) {
			String name = detailNames[index];
			String value = detailValues[index];

			if (!name.isEmpty() && !value.isEmpty())
				product.addProductDetails(name, value);
		}
	}

	private void setProductExtraImages(MultipartFile[] extraImages, Product product) {
		if (extraImages == null)
			return;

		for (int index = 0; index < extraImages.length; index++) {
			MultipartFile extraImage = extraImages[index];

			if (!extraImage.isEmpty()) {
				String fileName = StringUtils.cleanPath(extraImage.getOriginalFilename());

				if (!product.containsImageName(fileName)) {
					product.addExtraImage(fileName);
				}
			}
		}
	}

	private void setProductMainImage(MultipartFile mainImage, Product product) {
		if (!mainImage.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}

	private void setProductField(ProductDto productDto, Product product) {
		product.setName(productDto.getName());
		product.setAlias(productDto.getAlias());
		product.setShortDescription(productDto.getShortDescription());
		product.setFullDescription(productDto.getFullDescription());
		product.setBrand(brandService.getBrandById(productDto.getBrandId()));
		product.setCategory(categoryService.getCategoryById(productDto.getCategoryId()));
		product.setEnabled(productDto.isEnabled());
		product.setInStock(productDto.isInStock());
		product.setCost(productDto.getCost());
		product.setPrice(productDto.getPrice());
		product.setDiscountPercent(productDto.getDiscountPercent());
		product.setLength(productDto.getLength());
		product.setWidth(productDto.getWidth());
		product.setHeight(productDto.getHeight());
		product.setWeight(productDto.getWeight());
	}

	@GetMapping("/products/edit")
	public String editProduct(@RequestParam("id") Integer id, Model model) {
		String title = "Manage Products";
		String pageTitle = String.format("Edit Product(ID: %s)", id);
		List<Brand> brands = productService.getBrands();
		String defaultImage = "/images/product-default-image.jpg";

		Product product = productService.getProduct(id);
		ProductDto productDto = new ProductDto();
		productDto.setId(product.getId());
		productDto.setName(product.getName());
		productDto.setAlias(product.getAlias());
		productDto.setBrandId(product.getBrand().getId());
		productDto.setCategoryId(product.getCategory().getId());

		productDto.setEnabled(product.isEnabled());
		productDto.setInStock(product.isInStock());
		productDto.setCost(product.getCost());
		productDto.setPrice(product.getPrice());
		productDto.setDiscountPercent(product.getDiscountPercent());

		productDto.setShortDescription(product.getShortDescription());
		productDto.setFullDescription(product.getFullDescription());

		productDto.setLength(product.getLength());
		productDto.setWidth(product.getWidth());
		productDto.setHeight(product.getHeight());
		productDto.setWeight(product.getWeight());
		productDto.setMainImage(product.getProductMainImage());

		int currentExtraImageCount = product.getExtraImages().size();

		model.addAttribute("title", title);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("brands", brands);
		model.addAttribute("productDto", productDto);
		model.addAttribute("currentExtraImageCount", currentExtraImageCount);
		model.addAttribute("productDetails", product.getDetails());
		model.addAttribute("extraImages", product.getExtraImages());
		model.addAttribute("totalExtraImages", (Integer) product.getExtraImages().size());
		model.addAttribute("defaultImage", defaultImage);

		return "products/product_form";
	}

	@GetMapping("/products/update/enabled/status")
	public String updateProductEnabledStatus(@RequestParam("id") Integer id, @RequestParam("enabled") boolean enabled,
			RedirectAttributes redirectAttributes) {
		if (enabled) {
			String productName = productService.updateProductEnabledStatus(id, false);

			String message = String.format("Product: <b>%s</b> with ID: <b>%s</b> has been <b>disabled</b>",
					productName, id);
			redirectAttributes.addFlashAttribute("message", message);
		} else {
			String productName = productService.updateProductEnabledStatus(id, true);

			String message = String.format("Product: <b>%s</b> with ID: <b>%s</b> has been <b>enabled</b>", productName,
					id);
			redirectAttributes.addFlashAttribute("message", message);
		}

		return "redirect:/products";
	}

	@GetMapping("/products/delete")
	public String deleteProduct(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes)
			throws IOException {
		Product deleteProduct = productService.deleteProduct(id);
		String productName = deleteProduct.getName();

		String dirName = "product-images/" + deleteProduct.getId();
		FileUploadUtility.removeDir(dirName);

		String message = String.format("Product: <b>%s</b> with ID: <b>%s</b> has been <b>deleted</b>", productName,
				id);
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/products";
	}

	@GetMapping("/products/details")
	public String showProductDetails(@RequestParam("id") Integer id, Model model) {
		Product product = productService.getProduct(id);
		model.addAttribute("product", product);

		return "products/product_detail";
	}

	@GetMapping("/products/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		ProductCsvExporter exporter = new ProductCsvExporter();
		List<Product> products = productService.listAllProducts();
		exporter.export(products, response);
	}
	
	@GetMapping("/products/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		ProductExcelExporter exporter = new ProductExcelExporter();
		List<Product> products = productService.listAllProducts();
		exporter.export(products, response);
	}
}

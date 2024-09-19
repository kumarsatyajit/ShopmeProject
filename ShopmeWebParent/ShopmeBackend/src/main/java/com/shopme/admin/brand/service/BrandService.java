package com.shopme.admin.brand.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.brand.repository.BrandRepository;
import com.shopme.common.models.Brand;

@Service
public class BrandService {

	public static final int BRAND_PER_PAGE = 10;

	@Autowired
	private BrandRepository brandRepo;

	public List<Brand> listAllBrands() {
		List<Brand> brands = (List<Brand>) brandRepo.findAll();
		return brands;
	}

	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, BRAND_PER_PAGE, sort);

		if (!keyword.isEmpty()) {
			Page<Brand> page = brandRepo.findAll(keyword, pageable);

			return page;
		}

		Page<Brand> page = brandRepo.findAll(pageable);

		return page;
	}

	public boolean isBrandNameUnique(String name) {
		Brand brand = brandRepo.findByName(name);
		return brand == null;
	}

	public boolean isBrandNameAssociatedWithId(String name, Integer id) {
		Brand brand = brandRepo.findByName(name);
		return brand.getId().equals(id);
	}

	public Brand saveBrand(Brand brand) {
		Brand save = brandRepo.save(brand);
		return save;
	}

	public Brand getBrandById(Integer id) {
		Brand brand = brandRepo.findById(id).get();
		return brand;
	}

	public Brand deleteBrand(Integer id) {
		Brand brand = brandRepo.findById(id).get();
		brandRepo.delete(brand);

		return brand;
	}
}

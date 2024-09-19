package com.shopme.admin.category.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.category.repository.CategoryRepository;
import com.shopme.admin.utility.CategoryPageInfo;
import com.shopme.common.models.Category;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	public static final int ROOT_CATEGORY_PER_PAGE = 4;

	@Autowired
	private CategoryRepository categoryRepo;

	public List<Category> listByPage(CategoryPageInfo categoryPageInfo, int pageNum, String sortDir, String sortField,
			String keyword) {
		Sort sort = Sort.by(sortField);

		if (sortDir.equals("asc"))
			sort = sort.ascending();
		else if (sortDir.equals("desc"))
			sort = sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORY_PER_PAGE, sort);

		if (!keyword.isEmpty()) {
			Page<Category> page = categoryRepo.findAll(keyword, pageable);
			categoryPageInfo.setPage(page);

			return page.getContent();
		}

		Page<Category> page = categoryRepo.listRootCategories(pageable);
		List<Category> rootCategories = page.getContent();
		List<Category> categories = new ArrayList<Category>();

		categoryPageInfo.setPage(page);

		for (Category rootCategory : rootCategories) {
			addChild(categories, rootCategory, "", sortDir);
		}

		return categories;
	}

	private void addChild(List<Category> categories, Category category, String prefix, String sortDir) {
		Category copyCategory = Category.copyAll(category, prefix);
		categories.add(copyCategory);

		SortedSet<Category> children = sortSubCategories(category.getChildren(), sortDir);
		for (Category child : children) {
			addChild(categories, child, prefix + "--", sortDir);
		}
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		Comparator<Category> comparator = (c1, c2) -> sortDir.equals("asc") ? c1.getName().compareTo(c2.getName())
				: c2.getName().compareTo(c1.getName());

		SortedSet<Category> sortedSet = new TreeSet<Category>(comparator);
		sortedSet.addAll(children);

		return sortedSet;
	}

	// ------------------------------------------

	public List<String> listCategoryNames() {
		List<Category> rootCategories = categoryRepo.listRootCategories(Sort.by("name").ascending());
		List<String> categories = new ArrayList<String>();

		for (Category rootCategory : rootCategories) {
			addChildNames(categories, rootCategory, "");
		}

		return categories;
	}

	private void addChildNames(List<String> categories, Category category, String prefix) {
		categories.add(prefix + category.getName());

		Set<Category> children = sortedChildren(category.getChildren());
		for (Category child : children) {
			addChildNames(categories, child, prefix + "--");
		}
	}

	private SortedSet<Category> sortedChildren(Set<Category> children) {
		SortedSet<Category> sortedSet = new TreeSet<Category>(new Comparator<Category>() {
			@Override
			public int compare(Category c1, Category c2) {
				return c1.getName().compareTo(c2.getName());
			}
		});
		sortedSet.addAll(children);

		return sortedSet;
	}

	public boolean isNameUnique(String name) {
		Category category = categoryRepo.findByName(name);
		return category == null;
	}

	public boolean isAliasUnique(String alias) {
		Category category = categoryRepo.findByAlias(alias);
		return category == null;
	}

	public boolean isNameAssociatedWithId(Integer id, String name) {
		Category category = categoryRepo.findByName(name);
		return category.getId().equals(id);
	}

	public boolean isAliasAssociatedWithId(Integer id, String alias) {
		Category category = categoryRepo.findByAlias(alias);
		return category.getId().equals(id);
	}

	public Category saveCategory(Category category) {
		Category save = categoryRepo.save(category);
		return save;
	}

	public Category getParentCategory(String name) {
		name = name.replaceAll("--", "");
		Category category = categoryRepo.findByName(name);
		return category;
	}

	public Category getCategoryById(Integer id) {
		Category category = categoryRepo.findById(id).get();
		return category;
	}

	@Transactional
	public void updateCategoryStatus(Integer id, boolean enabled) {
		categoryRepo.updateCategoryStatus(id, enabled);
	}

	public Category deleteCategory(Integer id) {
		Category category = categoryRepo.findById(id).get();
		categoryRepo.delete(category);

		return category;
	}

	public List<Category> listAllCategoriesForDocument() {
		List<Category> rootCategories = categoryRepo.listRootCategoriesForDocument(Sort.by("name").ascending());
		List<Category> categories = new ArrayList<Category>();

		for (Category rootCategory : rootCategories) {
			addChild(categories, rootCategory);
		}

		return categories;
	}

	private void addChild(List<Category> categories, Category category) {
		categories.add(category);

		SortedSet<Category> children = sortedChildren(category.getChildren());
		for (Category child : children) {
			addChild(categories, child);
		}
	}

	public Set<Category> getSelectedCategories(Set<String> names) {
		Set<Category> selectedCategories = new HashSet<Category>();

		names.forEach(name -> {
			String categoryName = name.replaceAll("--", "");
			Category category = categoryRepo.findByName(categoryName);
			selectedCategories.add(category);
		});

		return selectedCategories;
	}

	public Set<String> getCategoryNames(Set<Category> categories) {
		Set<String> categoryNames = categories.stream().map(Category::getName).collect(Collectors.toSet());
		return categoryNames;
	}
}

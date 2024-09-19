package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.models.Category;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepo;

	public List<Category> listAllCategories() {
		List<Category> rootCategories = categoryRepo.listRootCategories();
		List<Category> categories = new ArrayList<Category>();

		for (Category rootCategory : rootCategories) {
			addChild(categories, rootCategory, "");
		}

		return categories;
	}

	private void addChild(List<Category> categories, Category category, String prefix) {
		Category copyCategory = Category.copyAll(category, prefix);
		categories.add(copyCategory);

		SortedSet<Category> children = sortedChildren(category.getChildren());
		for (Category child : children) {
			addChild(categories, child, prefix + "--");
		}
	}

	public List<String> listCategoryNames() {
		List<Category> rootCategories = categoryRepo.getRootCategories();
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

}

package com.shopme.admin.utility;

import org.springframework.data.domain.Page;

import com.shopme.common.models.Category;

public class CategoryPageInfo {
	private Page<Category> page;

	public Page<Category> getPage() {
		return page;
	}

	public void setPage(Page<Category> page) {
		this.page = page;
	}
}

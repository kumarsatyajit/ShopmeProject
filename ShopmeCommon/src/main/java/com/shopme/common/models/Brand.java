package com.shopme.common.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "brands")
public class Brand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 45, nullable = false, unique = true)
	private String name;

	@Column(length = 128, nullable = false)
	private String logo;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "brands_categories", joinColumns = { @JoinColumn(name = "brand_id") }, inverseJoinColumns = {
			@JoinColumn(name = "category_id") })
	private Set<Category> categories = new HashSet<Category>();

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Brand other = (Brand) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public void addCategory(Category... categories) {
		List<Category> categoryList = Arrays.asList(categories);
		this.categories.addAll(categoryList);
	}

	public String getBrandlogo() {
		return "/brand-logos/" + this.id + "/" + this.logo;
	}

	@Transient
	public String getCategoryNames() {
		String categories = this.categories.stream().map(Category::getName).collect(Collectors.joining(","));
		return categories;
	}

	public Brand(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
}

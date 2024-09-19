package com.shopme.common.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 128, nullable = false, unique = true)
	private String name;

	@Column(length = 64, nullable = false, unique = true)
	private String alias;

	@Column(length = 128, nullable = false)
	private String image;

	private boolean enabled;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	@JsonManagedReference
	private Category parent;

	@OneToMany(mappedBy = "parent", cascade = { CascadeType.PERSIST })
	@JsonBackReference
	private Set<Category> children = new HashSet<Category>();

	public Category(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", alias=" + alias + ", image=" + image + ", enabled="
				+ enabled + "]";
	}

	@Transient
	public String getParentName() {
		return parent != null ? parent.getName() : "No Parent";
	}

	@Transient
	public String getCategoryImage() {
		return "/categories-images/" + this.id + "/" + this.image;
	}

	public static Category copyAll(Category category, String prefix) {
		Category copyCategory = new Category();

		copyCategory.setId(category.getId());
		copyCategory.setName(prefix + category.getName());
		copyCategory.setAlias(category.getAlias());
		copyCategory.setEnabled(category.isEnabled());
		copyCategory.setParent(category.getParent());
		copyCategory.setImage(category.getImage());
		copyCategory.setHasChildren(category.getChildren().size() > 0);

		return copyCategory;
	}

	@Transient
	private boolean hasChildren;

}

package com.shopme.common.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 256, nullable = false, unique = true)
	private String name;

	@Column(length = 256, nullable = false, unique = true)
	private String alias;

	@Column(name = "short_description", length = 1000, nullable = false)
	private String shortDescription;

	@Column(name = "full_description", length = 4096, nullable = false)
	private String fullDescription;

	@Column(name = "created_time", nullable = false)
	private Date createdTime;

	@Column(name = "updated_time", nullable = false)
	private Date updatedTime;

	private boolean enabled;

	@Column(name = "in_stock")
	private boolean inStock;

	private float cost;
	private float price;

	@Column(name = "discount_percent")
	private float discountPercent;

	private float length;
	private float width;
	private float height;
	private float weight;

	@Column(name = "main_image", nullable = false)
	private String mainImage;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference
	private List<ProductImage> extraImages = new ArrayList<ProductImage>();

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference
	private List<ProductDetail> details = new ArrayList<ProductDetail>();

	public void addExtraImage(String image) {
		this.extraImages.add(new ProductImage(image, this));
	}

	public void addProductDetails(String name, String value) {
		this.details.add(new ProductDetail(name, value, this));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Transient
	public String getProductMainImage() {
		if (this.id == null || this.mainImage == null || this.mainImage.isEmpty())
			return "/images/product-default-image.jpg";
		return "/product-images/" + this.id + "/" + this.mainImage;
	}

	public boolean containsImageName(String fileName) {
		Iterator<ProductImage> iterator = extraImages.iterator();

		while (iterator.hasNext()) {
			ProductImage productImage = iterator.next();
			if (productImage.getName().equals(fileName))
				return true;
		}

		return false;
	}

	public boolean containsProductDetail(String name, String value) {
		Iterator<ProductDetail> iterator = details.iterator();

		while (iterator.hasNext()) {
			ProductDetail productDetail = iterator.next();
			if (productDetail.getName().equals(name) && productDetail.getValue().equals(value))
				return true;
		}

		return false;
	}

	public void clearExtraImages() {
		this.extraImages.clear();
	}

	public void clearProductDetails() {
		this.details.clear(); // Set a new empty list
	}

	@Transient
	public String getExtraImageUrls() {
		String urls = this.extraImages.stream().map(ProductImage::getImage).collect(Collectors.joining(","));
		return urls;
	}

	@Transient
	public String getBrandName() {
		return this.brand.getName();
	}

	@Transient
	public String getCategoryName() {
		return this.category.getName();
	}

}

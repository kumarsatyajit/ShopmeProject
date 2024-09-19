package com.shopme.common.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_details")
public class ProductDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(nullable = false, length = 255)
	private String value;

	@ManyToOne
	@JoinColumn(name = "product_id")
	@JsonManagedReference
	private Product product;

	public ProductDetail(String name, String value, Product product) {
		this.name = name;
		this.value = value;
		this.product = product;
	}

	@Override
	public String toString() {
		return "ProductDetail [id=" + id + ", name=" + name + ", value=" + value + "]";
	}
}

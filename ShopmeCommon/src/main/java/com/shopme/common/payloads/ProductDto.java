package com.shopme.common.payloads;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

	private Integer id;
	private String name;
	private String alias;
	private String shortDescription;
	private String fullDescription;
	private Date createdTime;
	private Date updatedTime;
	private boolean enabled;
	private boolean inStock;
	private float cost;
	private float price;
	private float discountPercent;
	private float length;
	private float width;
	private float height;
	private float weight;

	private Integer categoryId;
	private Integer brandId;
	
	private String mainImage;
}

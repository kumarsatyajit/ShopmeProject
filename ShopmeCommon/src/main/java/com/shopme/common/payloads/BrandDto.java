package com.shopme.common.payloads;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandDto {

	private Integer id;
	private String name;
	private Set<String> categories; 
}

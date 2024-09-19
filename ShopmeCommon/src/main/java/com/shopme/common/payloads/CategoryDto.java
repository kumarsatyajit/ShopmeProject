package com.shopme.common.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

	private Integer id;
	private String name;
	private String alias;
	private boolean enabled;
	private String parent;
}

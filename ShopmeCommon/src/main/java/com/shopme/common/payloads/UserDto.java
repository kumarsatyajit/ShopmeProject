package com.shopme.common.payloads;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	private Integer id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private boolean enabled;
	private Set<Integer> roleIds = new HashSet<Integer>();
}

package com.shopme.common.payloads;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsDto {

	private Integer id;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String confirmPassword;
	private List<String> assignedRoles;
}

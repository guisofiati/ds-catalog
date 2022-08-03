package com.github.guisofiati.dscatalog.dto;

import com.github.guisofiati.dscatalog.services.validation.UserInsertValid;

@UserInsertValid // verifica se o email inserido ja existe no banco
public class UserInsertDTO extends UserDTO{
	
	private static final long serialVersionUID = 1L;
	
	private String password;

	public UserInsertDTO() {
		super();
	}

	public UserInsertDTO(Long id, String firstName, String lastName, String email, String password) {
		super(id, firstName, lastName, email);
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

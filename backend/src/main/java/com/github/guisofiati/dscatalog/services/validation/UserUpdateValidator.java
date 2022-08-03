package com.github.guisofiati.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.github.guisofiati.dscatalog.dto.UserUpdateDTO;
import com.github.guisofiati.dscatalog.entities.User;
import com.github.guisofiati.dscatalog.repositories.UserRepository;
import com.github.guisofiati.dscatalog.resources.exceptions.FieldMessage;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private HttpServletRequest req; // pegar o id que veio na req, para poder atualizar o user
	
	@Override
	public void initialize(UserUpdateValid ann) {
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();

		@SuppressWarnings("unchecked")
		var uriVars = (Map<String, String>) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE); //pega os atributos da url da request
		long userIdReq = Long.parseLong(uriVars.get("id"));
		
		User user = repository.findByEmail(dto.getEmail());
		if (user != null && user.getId() != userIdReq) { // se o id do user n for o mesmo do que esta querendo ser atualizado, entao ja existe
			list.add(new FieldMessage("email", "Email ja existe"));
		}
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}

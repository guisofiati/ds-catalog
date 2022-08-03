package com.github.guisofiati.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.guisofiati.dscatalog.dto.UserInsertDTO;
import com.github.guisofiati.dscatalog.entities.User;
import com.github.guisofiati.dscatalog.repositories.UserRepository;
import com.github.guisofiati.dscatalog.resources.exceptions.FieldMessage;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {
	
	@Autowired
	private UserRepository repository;
	
	@Override
	public void initialize(UserInsertValid ann) {
	}

	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista
		// verificar se o email ja existe no banco
		User user = repository.findByEmail(dto.getEmail());
		if (user != null) { // se voltar diferente de vazio, é porque ja existe
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

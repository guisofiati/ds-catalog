package com.github.guisofiati.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.guisofiati.dscatalog.dto.CategoryDTO;
import com.github.guisofiati.dscatalog.entities.Category;
import com.github.guisofiati.dscatalog.repositories.CategoryRepository;
import com.github.guisofiati.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true) // transacao async, ou faz tudo ou n faz nada
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
		return list
				.stream()
				.map(category -> new CategoryDTO(category))
				.collect(Collectors.toList());
		 		 
//		 List<CategoryDTO> listDto = new ArrayList<>();
//		 for (Category cat : list) {
//			 listDto.add(new CategoryDTO(cat));
//		 }
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id); //metodo get do optional obtem oque esta nele
		Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity id " + id + " not found.")); 
		return new CategoryDTO(entity);
	}
}

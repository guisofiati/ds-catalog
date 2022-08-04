package com.github.guisofiati.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.guisofiati.dscatalog.dto.CategoryDTO;
import com.github.guisofiati.dscatalog.entities.Category;
import com.github.guisofiati.dscatalog.repositories.CategoryRepository;
import com.github.guisofiati.dscatalog.services.exceptions.DatabaseException;
import com.github.guisofiati.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true) // transacao async, ou faz tudo ou n faz nada
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		Page<Category> list = repository.findAll(pageable);
		return list.map(category -> new CategoryDTO(category));
		 		 
//		 List<CategoryDTO> listDto = new ArrayList<>();
//		 for (Category cat : list) {
//			 listDto.add(new CategoryDTO(cat));
//		 }
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id); //metodo get do optional obtem oque esta nele
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity id " + id + " not found.")); 
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		//transformar dto em uma entidade para salvar no banco
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		// se usar o find by id do repository, vai tocar o banco 2 vezes
		// buscando o id e depois salvando a categoria atualizada.
		// Category entity = repository.findById(id)
		// quando precisar atualizar, usar o getReference, pois ele vai primeiro
		// instanciar o obj provisorio com o id e so quando mandar salvar
		// que ele vai acessar o banco
		try {
			Category entity = repository.getOne(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id " + id + " not found.");
		}
	}
	
	// nao usa o transaction pois precisamos capturar uma exceçao do banco
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) { // id que nao existe
			throw new ResourceNotFoundException("Id " + id + " not found to delete.");
		}
		catch (DataIntegrityViolationException e) { // deletar uma categoria com produtos associadas a ela. problema de integriade referencial
			throw new DatabaseException("Integrity violation");
		}
	}
}

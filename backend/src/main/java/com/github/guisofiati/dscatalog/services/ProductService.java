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
import com.github.guisofiati.dscatalog.dto.ProductDTO;
import com.github.guisofiati.dscatalog.entities.Category;
import com.github.guisofiati.dscatalog.entities.Product;
import com.github.guisofiati.dscatalog.repositories.CategoryRepository;
import com.github.guisofiati.dscatalog.repositories.ProductRepository;
import com.github.guisofiati.dscatalog.services.exceptions.DatabaseException;
import com.github.guisofiati.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true) // transacao async, ou faz tudo ou n faz nada
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		Page<Product> list = repository.findAll(pageable);
		return list.map(product -> new ProductDTO(product)); // usando construtor de 1 arg. sem as categorias
		 		 
//		 List<ProductDTO> listDto = new ArrayList<>();
//		 for (Product cat : list) {
//			 listDto.add(new ProductDTO(cat));
//		 }
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id); //metodo get do optional obtem oque esta nele
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity id " + id + " not found.")); 
		return new ProductDTO(entity, entity.getCategories()); // vai vir as categorias penduradas no obj json
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		//transformar dto em uma entidade para salvar no banco
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}
	
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		// se usar o find by id do repository, vai tocar o banco 2 vezes
		// buscando o id e depois salvando a categoria atualizada.
		// Product entity = repository.findById(id)
		// quando precisar atualizar, usar o getReference, pois ele vai primeiro
		// instanciar o obj provisorio com o id e so quando mandar salvar
		// que ele vai acessar o banco
		try {
			Product entity = repository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
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
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
		entity.setMoment(dto.getMoment());
		
		entity.getCategories().clear();
		
		for (CategoryDTO catDto : dto.getCategories()) {
			Category cat = categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(cat);
		}
	}
}

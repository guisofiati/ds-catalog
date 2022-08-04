package com.github.guisofiati.dscatalog.services;

import static org.mockito.Mockito.never;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
// import org.mockito.Mockito.doNothing -> Import estatico
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.guisofiati.dscatalog.dto.ProductDTO;
import com.github.guisofiati.dscatalog.entities.Category;
import com.github.guisofiati.dscatalog.entities.Product;
import com.github.guisofiati.dscatalog.factory.Factory;
import com.github.guisofiati.dscatalog.repositories.CategoryRepository;
import com.github.guisofiati.dscatalog.repositories.ProductRepository;
import com.github.guisofiati.dscatalog.services.exceptions.DatabaseException;
import com.github.guisofiati.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page; // usar esse tipo para os testes 
	private Product product;
	private ProductDTO dto;
	private Category category;
	
	
	@BeforeEach()
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 3L;
		dependentId = 2L;
		product = Factory.createProduct();
		dto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(product));
		category = Factory.createCategory();
		
		// simular comportamento do repositorio
		// quando o id existir
		Mockito.doNothing().when(repository).deleteById(existingId);
		// quando o id nao existir
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		// deletar um id que tem associacao com outro, deixando o obj sem referencia
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
	
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product)); // traz o obj
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty()); // traz vazio

		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		// verifica se alguma chamada foi feita
		// Mockito.times = vezes que foi chamado
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository).deleteById(dependentId);
	}
	
	@Test
	public void findAllShouldReturnDataPaged() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository).findAll(pageable);
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() {
		
		dto = service.findById(existingId);
		
		Assertions.assertNotNull(dto);
		
		Mockito.verify(repository).findById(existingId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			dto = service.findById(nonExistingId);
			
			Assertions.assertNull(dto);
		});
		
		Mockito.verify(repository).findById(nonExistingId);
	}
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() {
		
		service.update(existingId, dto);
		
		Assertions.assertNotNull(dto);
		
		Mockito.verify(repository).getOne(existingId);
		Mockito.verify(repository, Mockito.times(1)).save(product);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, dto);
		});
		
		Mockito.verify(repository).getOne(nonExistingId);
		Mockito.verify(repository, never()).save(product); // nunca foi chamado
	}
}

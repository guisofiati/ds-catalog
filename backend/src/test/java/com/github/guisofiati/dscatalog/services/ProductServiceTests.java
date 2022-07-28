package com.github.guisofiati.dscatalog.services;

import java.util.List;
import java.util.Optional;

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
import com.github.guisofiati.dscatalog.entities.Product;
import com.github.guisofiati.dscatalog.repositories.ProductRepository;
import com.github.guisofiati.dscatalog.services.exceptions.DatabaseException;
import com.github.guisofiati.dscatalog.services.exceptions.ResourceNotFoundException;
import com.github.guisofiati.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page; // usar esse tipo para os testes 
	Product product;
	
	@BeforeEach()
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 3L;
		dependentId = 2L;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		
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
}

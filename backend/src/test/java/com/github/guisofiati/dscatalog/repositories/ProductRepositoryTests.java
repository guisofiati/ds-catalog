package com.github.guisofiati.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.github.guisofiati.dscatalog.entities.Product;
import com.github.guisofiati.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	// 3 operaçoes no repository: find by id, delete, save
	
	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	private Optional<Product> result;
	
	@BeforeEach // antes de cada teste
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 26L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
				
		repository.deleteById(existingId); // faz rollback no banco depois
		
		result = repository.findById(existingId); 
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(product.getId(), countTotalProducts + 1);
	}
	
	@Test
	public void findByIdShouldReturnOptionalNotEmptyWhenIdExists() {
		
		result = repository.findById(existingId);
		
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnOptionalEmptyWhenIdDoesNotExists() {
		
		result = repository.findById(nonExistingId);
		
		Assertions.assertTrue(result.isEmpty());
	}
}

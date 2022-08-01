package com.github.guisofiati.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.guisofiati.dscatalog.repositories.ProductRepository;
import com.github.guisofiati.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
public class ProductServiceIntegrationTests {
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	// dados reais da instancia do banco
	private long existingId;
	private long nonExistingId;
	private int countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25;
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		service.delete(existingId);
		
		Assertions.assertEquals(countTotalProducts - 1, repository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
}

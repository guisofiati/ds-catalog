package com.github.guisofiati.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.github.guisofiati.dscatalog.dto.ProductDTO;
import com.github.guisofiati.dscatalog.factory.Factory;
import com.github.guisofiati.dscatalog.services.ProductService;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private MockMvc mockMvc;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO)); // permite instanciar pagina
		
		Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() throws Exception {
		//mockMvc.perform(get("/products")).andExpect(status().isOk());
		
		ResultActions result = mockMvc.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		Pageable pageable = PageRequest.of(0, 20);
		Mockito.verify(service).findAllPaged(pageable); // ver se foi chamado
	}
}

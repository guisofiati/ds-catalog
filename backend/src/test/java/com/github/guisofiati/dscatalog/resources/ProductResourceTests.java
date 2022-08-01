package com.github.guisofiati.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.guisofiati.dscatalog.dto.ProductDTO;
import com.github.guisofiati.dscatalog.factory.Factory;
import com.github.guisofiati.dscatalog.services.ProductService;
import com.github.guisofiati.dscatalog.services.exceptions.DatabaseException;
import com.github.guisofiati.dscatalog.services.exceptions.ResourceNotFoundException;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private long existingId;
	private long nonExistingId;
	private long dependentId;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		existingId = 2L;
		dependentId = 3L;
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO)); // permite instanciar pagina
		
		Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(service.findById(existingId)).thenReturn(productDTO);
		Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		Mockito.when(service.update(eq(existingId), any())).thenReturn(productDTO);
		Mockito.when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(productDTO);

		Mockito.doNothing().when(service).delete(existingId);
		Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);
		
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
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists()); // '$' acessar o json
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgUrl").exists());
		result.andExpect(jsonPath("$.moment").exists());
		result.andExpect(jsonPath("$.categories").exists());
	}
	
	@Test 
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId));
	
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductWhenIdExists() throws Exception {
		
		// convertando objeto java para string 
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
		.content(jsonBody)
		.contentType(MediaType.APPLICATION_JSON) // tipo de dados da req
		.accept(MediaType.APPLICATION_JSON)); // tipo de dados da res
	
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists()); 
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgUrl").exists());
		result.andExpect(jsonPath("$.moment").exists());
		result.andExpect(jsonPath("$.categories").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
		.content(jsonBody)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)); 
	
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void insertShouldReturnProduct() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists()); 
		result.andExpect(jsonPath("$.name").exists());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId));
	
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId));
	
		result.andExpect(status().isNotFound());
	}
}

package com.github.guisofiati.dscatalog.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.guisofiati.dscatalog.dto.CategoryDTO;
import com.github.guisofiati.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories") // rota rest 
public class CategoryResource {
	
	@Autowired
	private CategoryService service;
	
	@GetMapping
	public ResponseEntity<List<CategoryDTO>> findAll() {
		List<CategoryDTO> listDto = service.findAll();
		return ResponseEntity.ok().body(listDto); // returna list no corpo da res
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
		CategoryDTO catDto = service.findById(id);
		return ResponseEntity.ok().body(catDto);
	}
}

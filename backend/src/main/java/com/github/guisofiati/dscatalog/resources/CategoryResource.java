package com.github.guisofiati.dscatalog.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.guisofiati.dscatalog.entities.Category;
import com.github.guisofiati.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories") // rota rest 
public class CategoryResource {
	
	@Autowired
	private CategoryService service;
	
	@GetMapping
	public ResponseEntity<List<Category>> findAll() {
		List<Category> list = service.findAll();
		return ResponseEntity.ok().body(list); // returna list no corpo da res
	}
}

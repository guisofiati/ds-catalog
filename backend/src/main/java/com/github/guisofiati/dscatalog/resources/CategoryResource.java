package com.github.guisofiati.dscatalog.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.guisofiati.dscatalog.entities.Category;

@RestController
@RequestMapping(value = "/categories") // rota rest 
public class CategoryResource {
	
	@GetMapping
	public ResponseEntity<List<Category>> findAll() {
		List<Category> list = new ArrayList<>();
		list.add(new Category(1L, "Books"));
		list.add(new Category(2L, "Electronics"));
		list.add(new Category(3L, "Food"));
		return ResponseEntity.ok().body(list); // returna list no corpo da res
	}
}

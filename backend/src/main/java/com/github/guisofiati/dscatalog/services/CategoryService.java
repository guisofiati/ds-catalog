package com.github.guisofiati.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.guisofiati.dscatalog.entities.Category;
import com.github.guisofiati.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true) // transacao async, ou faz tudo ou n faz nada
	public List<Category> findAll() {
		return repository.findAll();
	}
}

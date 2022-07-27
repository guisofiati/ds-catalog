package com.github.guisofiati.dscatalog.tests;

import java.time.Instant;

import com.github.guisofiati.dscatalog.dto.ProductDTO;
import com.github.guisofiati.dscatalog.entities.Category;
import com.github.guisofiati.dscatalog.entities.Product;

public class Factory {
	
	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Ultimate Phone", 800.0, "https://img.com/img.png", Instant.parse("2022-03-12T03:00:00Z"));
		product.getCategories().add(new Category(2L, "Electronics"));
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
}

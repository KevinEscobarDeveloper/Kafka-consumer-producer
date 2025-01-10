package com.kevin.product_service;

import com.kevin.product_service.models.Product;
import com.kevin.product_service.repository.ProductRepository;
import com.kevin.product_service.service.Impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RMapCacheReactive;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProductServiceApplicationTests {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private RMapCacheReactive<String, Product> productCache;

	private ProductServiceImpl productService;

	private final long cacheTtl = 10;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		productService = new ProductServiceImpl(productRepository, productCache, cacheTtl);
		Mockito.lenient().when(productCache.get(anyString())).thenReturn(Mono.empty());
		Mockito.lenient().when(productRepository.findById(anyString())).thenReturn(Mono.empty());
	}

	@Test
	void allProductsInCacheCase() {
		List<String> productIds = Arrays.asList("1", "2");
		Product product1 = new Product("1", "test", 1.0);
		Product product2 = new Product("2", "test 2", 1.0);

		when(productCache.get("1")).thenReturn(Mono.just(product1));
		when(productCache.get("2")).thenReturn(Mono.just(product2));

		Mono<List<Product>> result = productService.findByIds(productIds);

		StepVerifier.create(result)
				.expectNextMatches(products ->
						products.size() == 2 &&
								products.contains(product1) &&
								products.contains(product2)
				)
				.verifyComplete();


		verify(productCache, times(1)).get("1");
		verify(productCache, times(1)).get("2");
	}

	@Test
	void someProductsInCacheCase() {
		// Mock data
		List<String> productIds = Arrays.asList("1", "2");
		Product product1 = new Product("1", "test", 1.0);
		Product product2 = new Product("2", "test 2", 1.0);

		when(productCache.get("1")).thenReturn(Mono.just(product1));
		when(productCache.get("2")).thenReturn(Mono.empty());
		when(productRepository.findById("2")).thenReturn(Mono.just(product2));
		when(productCache.put("2", product2, cacheTtl, TimeUnit.MINUTES)).thenReturn(Mono.just(product2));


		Mono<List<Product>> result = productService.findByIds(productIds);
		StepVerifier.create(result)
				.expectNextMatches(products ->
						products.size() == 2 &&
								products.contains(product1) &&
								products.contains(product2)
				)
				.verifyComplete();

		verify(productCache, times(1)).get("1");
		verify(productCache, times(1)).get("2");
		verify(productRepository, times(1)).findById("2");
		verify(productCache, times(1)).put("2", product2, cacheTtl, TimeUnit.MINUTES);

	}

	@Test
	void saveProductCase() {
		Product product = new Product("1", "test 1", 1.5);
		when(productRepository.save(product)).thenReturn(Mono.just(product));
		when(productCache.fastPut("1", product)).thenReturn(Mono.just(true));

		Mono<Product> result = productService.saveProduct(product);

		StepVerifier.create(result)
				.expectNext(product)
				.verifyComplete();

		verify(productRepository, times(1)).save(product);
		verify(productCache, times(1)).fastPut("1", product);
	}
}

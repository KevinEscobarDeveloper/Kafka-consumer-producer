package com.kevin.product_service.controller;

import com.kevin.product_service.models.Product;
import com.kevin.product_service.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
@Tag(name = "Product Service", description = "Microservicio que valida productos por IDs")
public class ProductController {
    private final IProductService iProductService;

    @Operation(
            summary = "Recuperar libros basado en sus ids",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos encontrados por IDs")
            }
    )
    @GetMapping("list")
    public Mono<List<Product>> findProductsByIds(@RequestParam List<String>  ids) {
        return iProductService.findByIds(ids);
    }
}

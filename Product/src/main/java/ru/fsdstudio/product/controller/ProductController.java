package ru.fsdstudio.product.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import ru.fsdstudio.product.dto.BookDto;
import ru.fsdstudio.product.dto.ProductDto;
import ru.fsdstudio.product.service.ProductService;

@RestController
@RequestMapping("/rest/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public Page<ProductDto> getList(
            @Schema(description = "Pageable", defaultValue = "page=0&size=10&sort=id,asc")
            @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return productService.getList(pageable);
    }
    
    @GetMapping("/books")
    public Page<BookDto> getBookList(
            @Schema(description = "Pageable", defaultValue = "page=0&size=10&sort=id,asc")
            @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return productService.getBookList(pageable);
    }
    
    @GetMapping("/books/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return productService.getBookById(id);
    }
    
    @GetMapping("/{id}")
    public ProductDto getOne(@PathVariable Long id) {
        return productService.getOne(id);
    }
    
    @PostMapping
    public ProductDto create(@RequestBody ProductDto dto) {
        return productService.create(dto);
    }
    
    @PostMapping("/books")
    public BookDto createBook(@RequestBody BookDto bookDto) {
        return productService.createBook(bookDto);
    }
    
    @PatchMapping("/{id}")
    public ProductDto patch(
            @PathVariable Long id,
            @RequestBody
            @Schema(description = "Patch request", example =
                    "{\"name\": \"New name\", " +
                            "\"description\": \"New description\", " +
                            "\"price\": \"New price\", " +
                            "\"quantity\": \"New quantity\"}")
            JsonNode patchNode) throws IOException {
        return productService.patch(id, patchNode);
    }
    
    @PatchMapping("/books/{id}")
    public BookDto patchBook(
            @PathVariable Long id,
            @RequestBody
            @Schema(description = "Patch request", example =
                    "{\"name\": \"New name\", " +
                            "\"description\": \"New description\", " +
                            "\"author\": \"New author\", " +
                            "\"price\": \"New price\", " +
                            "\"quantity\": \"New quantity\"}")
            JsonNode patchNode) throws IOException {
        return productService.patchBook(id, patchNode);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}

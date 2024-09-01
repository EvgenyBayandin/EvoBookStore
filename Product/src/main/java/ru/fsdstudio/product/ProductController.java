package ru.fsdstudio.product;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public Page<ProductDto> getList(Pageable pageable) {
        return productService.getList(pageable);
    }
    
    @GetMapping("/{id}")
    public ProductDto getOne(@PathVariable Long id) {
        return productService.getOne(id);
    }
    
    @GetMapping("/by-ids")
    public List<ProductDto> getMany(@RequestParam List<Long> ids) {
        return productService.getMany(ids);
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
    public ProductDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return productService.patch(id, patchNode);
    }
    
    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return productService.patchMany(ids, patchNode);
    }
    
    @DeleteMapping("/{id}")
    public ProductDto delete(@PathVariable Long id) {
        return productService.delete(id);
    }
    
    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        productService.deleteMany(ids);
    }
}

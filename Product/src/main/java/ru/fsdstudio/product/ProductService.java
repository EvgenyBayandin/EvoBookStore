package ru.fsdstudio.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class ProductService {
    
    private final ProductMapper productMapper;
    
    private final BookMapper bookMapper;
    
    private final ProductRepository productRepository;
    
    private final BookRepository bookRepository;
    
    private final StockRepository stockRepository;
    
    private final ObjectMapper objectMapper;
    
    public Page<ProductDto> getList(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toDto);
    }
    
    public ProductDto getOne(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        return productMapper.toDto(productOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }
    
    public List<ProductDto> getMany(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        return products.stream().map(productMapper::toDto).toList();
    }
    
    public ProductDto create(ProductDto dto) {
        Product product = productMapper.toEntity(dto);
        Product resultProduct = productRepository.save(product);
        return productMapper.toDto(resultProduct);
    }
    
    public BookDto createBook(BookDto bookDto) {
        Book book = new Book();
        book.setName(bookDto.getName());
        book.setDescription(bookDto.getDescription());
        book.setAuthor(bookDto.getAuthor());
        
        Stock stock = new Stock();
        stock.setPrice(bookDto.getPrice());
        stock.setQuantity(bookDto.getQuantity());
        stock.setProduct(book);
        
        bookRepository.save(book);
        stockRepository.save(stock);
        
        BookDto response = bookMapper.toDto(book);
        response.setPrice(stock.getPrice());
        response.setQuantity(stock.getQuantity());
        
        return response;
    }
    
    public ProductDto patch(Long id, JsonNode patchNode) throws IOException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
        
        ProductDto productDto = productMapper.toDto(product);
        objectMapper.readerForUpdating(productDto).readValue(patchNode, JsonNode.class);
        productMapper.updateWithNull(productDto, product);
        
        Product resultProduct = productRepository.save(product);
        return productMapper.toDto(resultProduct);
    }
    
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Product> products = productRepository.findAllById(ids);
        
        for (Product product : products) {
            ProductDto productDto = productMapper.toDto(product);
            objectMapper.readerForUpdating(productDto).readValue(patchNode, JsonNode.class);
            productMapper.updateWithNull(productDto, product);
        }
        
        List<Product> resultProducts = productRepository.saveAll(products);
        return resultProducts.stream().map(Product::getId).toList();
    }
    
    public ProductDto delete(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            productRepository.delete(product);
        }
        return productMapper.toDto(product);
    }
    
    public void deleteMany(List<Long> ids) {
        productRepository.deleteAllById(ids);
    }
}

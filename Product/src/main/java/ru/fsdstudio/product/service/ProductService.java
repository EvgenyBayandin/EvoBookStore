package ru.fsdstudio.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.fsdstudio.product.dto.BookDto;
import ru.fsdstudio.product.dto.ProductDto;
import ru.fsdstudio.product.entity.Book;
import ru.fsdstudio.product.entity.Product;
import ru.fsdstudio.product.entity.Stock;
import ru.fsdstudio.product.mapper.BookMapper;
import ru.fsdstudio.product.mapper.ProductMapper;
import ru.fsdstudio.product.repo.BookRepository;
import ru.fsdstudio.product.repo.ProductRepository;
import ru.fsdstudio.product.repo.StockRepository;

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
        return products.map(product -> {
            Stock stock = getStock(product.getId());
            return stock != null
                    ? productMapper.toDto(product, stock.getPrice(), stock.getQuantity())
                    : productMapper.toDto(product, BigDecimal.ZERO, 0L);
        });
    }
    
    public ProductDto getOne(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        Product product = productOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id `%s` not found".formatted(id)));
        
        Stock stock = getStock(id);
        
        return stock != null
                ? productMapper.toDto(product, stock.getPrice(), stock.getQuantity())
                : productMapper.toDto(product, BigDecimal.ZERO, 0L);
    }
    
    public List<ProductDto> getMany(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        return products.stream()
                .map(product -> {
                    Stock stock = getStock(product.getId());
                    return stock != null
                            ? productMapper.toDto(product, stock.getPrice(), stock.getQuantity())
                            : productMapper.toDto(product, BigDecimal.ZERO, 0L);
                })
                .toList();
    }
    
    public ProductDto create(ProductDto dto) {
        Product product = productMapper.toEntity(dto);
        
        Stock stock = new Stock();
        stock.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        stock.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 0L);
        stock.setProduct(product);
        product.setStock(stock);
        
        Product resultProduct = productRepository.saveAndFlush(product);
        
        return productMapper.toDto(resultProduct, stock.getPrice(), stock.getQuantity());
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id `%s` not found".formatted(id)));
        
        Stock stock = getStockOrDefault(product.getId());
        
        ProductDto productDto = productMapper.toDto(product, stock.getPrice(), stock.getQuantity());
        objectMapper.readerForUpdating(productDto).readValue(patchNode, JsonNode.class);
        productMapper.updateWithNull(productDto, product);
        
        Product resultProduct = productRepository.save(product);
        return productMapper.toDto(resultProduct, stock.getPrice(), stock.getQuantity());
    }
    
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Product> products = productRepository.findAllById(ids);
        
        for (Product product : products) {
            Stock stock = getStockOrDefault(product.getId());
            
            ProductDto productDto = productMapper.toDto(product, stock.getPrice(), stock.getQuantity());
            objectMapper.readerForUpdating(productDto).readValue(patchNode, JsonNode.class);
            productMapper.updateWithNull(productDto, product);
        }
        
        List<Product> resultProducts = productRepository.saveAll(products);
        return resultProducts.stream()
                .map(product -> {
                    Stock stock = getStockOrDefault(product.getId());
                    
                    return productMapper.toDto(product, stock.getPrice(), stock.getQuantity());
                })
                .map(ProductDto::getId)
                .toList();
    }
    
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id `%s` not found".formatted(id)));
            productRepository.delete(product);
    }
    
    public void deleteMany(List<Long> ids) {
        productRepository.deleteAllById(ids);
    }
    
    public Stock getStock(Long productId) {
        return stockRepository.findByProductId(productId);
    }
    
    public Stock getStockOrDefault(Long productId) {
        Stock stock = getStock(productId);
        return stock != null ? stock : new Stock();
    }
}

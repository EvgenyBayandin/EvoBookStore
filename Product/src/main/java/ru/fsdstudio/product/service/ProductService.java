package ru.fsdstudio.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    
    public Page<BookDto> getBookList(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);
        
        return bookPage.map(book -> {
            BookDto bookDto = bookMapper.toDto(book);
            Stock stock = getStockOrDefault(book.getId());
            bookDto.setPrice(stock.getPrice());
            bookDto.setQuantity(stock.getQuantity());
            
            return bookDto;
        });
    }
    
    public BookDto getBookById(Long productId) {
        Optional<Book> bookOptional = bookRepository.findById(productId);
        Book book = bookOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with productId `%s` not found".formatted(productId)));
        
        Stock stock = getStockOrDefault(book.getId());
        
        BookDto result = bookMapper.toDto(book);
        result.setPrice(stock.getPrice());
        result.setQuantity(stock.getQuantity());
        
        return result;
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
    
    @Transactional
    public ProductDto patch(Long id, JsonNode patchNode) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id `%s` not found".formatted(id)));
        
        Stock stock = getStockOrDefault(product.getId());
        
        ProductDto productDto = productMapper.toDto(product, stock.getPrice(), stock.getQuantity());
        
        ProductDto updatedProductDto = objectMapper.readerForUpdating(productDto).readValue(patchNode);
        
        productMapper.updateWithNull(updatedProductDto, product);
        
        stock.setPrice(updatedProductDto.getPrice());
        stock.setQuantity(updatedProductDto.getQuantity());
        
        product.setStock(stock);
        Product savedProduct = productRepository.save(product);
        Stock savedStock = stockRepository.save(stock);
        
        ProductDto resultDto = productMapper.toDto(savedProduct, savedStock.getPrice(), savedStock.getQuantity());
        
        return resultDto;
    }
    
    @Transactional
    public BookDto patchBook(Long id, JsonNode patchNode) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id `%s` not found".formatted(id)));
        
        if (!(product instanceof Book)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not a book");
        }
        
        Book book = (Book) product;
        Stock stock = getStockOrDefault(product.getId());
        
        BookDto bookDto = bookMapper.toDto(book);
        bookDto.setPrice(stock.getPrice());
        bookDto.setQuantity(stock.getQuantity());
        
        BookDto updatedBookDto = objectMapper.readerForUpdating(bookDto).readValue(patchNode);
        
        bookMapper.partialUpdate(updatedBookDto, book);
        stock.setPrice(updatedBookDto.getPrice());
        stock.setQuantity(updatedBookDto.getQuantity());
        
        book.setStock(stock);
        Book savedBook = productRepository.save(book);
        Stock savedStock = stockRepository.save(stock);
        
        BookDto resultDto = bookMapper.toDto(savedBook);
        resultDto.setPrice(savedStock.getPrice());
        resultDto.setQuantity(savedStock.getQuantity());
        
        return resultDto;
    }
    
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id `%s` not found".formatted(id)));
            productRepository.delete(product);
    }
    
    public Stock getStock(Long productId) {
        return stockRepository.findByProductId(productId);
    }
    
    public Stock getStockOrDefault(Long productId) {
        Stock stock = getStock(productId);
        return stock != null ? stock : new Stock();
    }
}

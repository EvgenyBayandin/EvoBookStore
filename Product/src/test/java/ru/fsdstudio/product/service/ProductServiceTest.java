package ru.fsdstudio.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private StockRepository stockRepository;
    
    @Mock
    private ProductMapper productMapper;
    
    @InjectMocks
    private ProductService productService;
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private BookMapper bookMapper;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @Test
    public void testGetList_WithProducts() {
        // Arrange
        List<Product> products = List.of(new Product(), new Product());
        Page<Product> page = new PageImpl<>(products);
        when(productRepository.findAll(Pageable.unpaged())).thenReturn(page);
        
        // Act
        Page<ProductDto> result = productService.getList(Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }
    
    @Test
    public void testGetList_WithoutProducts() {
        // Arrange
        when(productRepository.findAll(Pageable.unpaged())).thenReturn(Page.empty());
        
        // Act
        Page<ProductDto> result = productService.getList(Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }
    
    @Test
    public void testGetList_WithStock() {
        // Arrange
        Product product = new Product();
        Stock stock = new Stock();
        stock.setPrice(BigDecimal.valueOf(10.0));
        stock.setQuantity(10L);
        product.setStock(stock);
        when(productRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(product)));
        when(productMapper.toDto(any(Product.class), any(BigDecimal.class), anyLong())).thenReturn(new ProductDto());
        
        // Act
        Page<ProductDto> result = productService.getList(Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
    
    @Test
    public void testGetList_WithoutStock() {
        // Arrange
        Product product = new Product();
        when(productRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(product)));
        when(productMapper.toDto(product, BigDecimal.ZERO, 0L)).thenReturn(new ProductDto());
        
        // Act
        Page<ProductDto> result = productService.getList(Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
    
    @Test
    public void testGetOne_WithExistingProduct() {
        // Arrange
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        Stock stock = new Stock();
        stock.setPrice(BigDecimal.valueOf(10.0));
        stock.setQuantity(10L);
        stock.setProduct(product);
        product.setStock(stock);
        
        product.setName("name");
        product.setDescription("description");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        ProductDto expected = new ProductDto(product.getId(), product.getName(), product.getDescription());
        when(productMapper.toDto(product, BigDecimal.ZERO, 0L)).thenReturn(expected);
        
        // Act
        ProductDto result = productService.getOne(id);
        
        // Assert
        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getDescription(), result.getDescription());
    }
    
    @Test
    public void testGetOne_WithoutProduct() {
        // Arrange
        Long id = 1L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> productService.getOne(id));
    }
    
    @Test
    public void testGetOne_WithoutStock() {
        // Arrange
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        Stock stock = new Stock();
        stock.setPrice(BigDecimal.valueOf(10.0));
        stock.setQuantity(0L);
        stock.setProduct(product);
        product.setStock(stock);
        
        product.setName("name");
        product.setDescription("description");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        ProductDto expected = new ProductDto(product.getId(), product.getName(), product.getDescription());
        when(productMapper.toDto(any(Product.class), any(BigDecimal.class), anyLong())).thenReturn(expected);
        
        // Act
        ProductDto result = productService.getOne(id);
        
        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }
    
    @Test
    public void testGetMany_WithExistingProducts() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAllById(ids)).thenReturn(products);
        when(productMapper.toDto(any(Product.class), any(BigDecimal.class), anyLong())).thenReturn(new ProductDto());

        // Act
        List<ProductDto> result = productService.getMany(ids);

        // Assert
        assertEquals(2, result.size());
    }
    
    @Test
    public void testCreateProductWithValidData() {
        // Arrange
        ProductDto dto = new ProductDto(null, "Product name", "Product description", BigDecimal.valueOf(10.0), 5L);
        
        Product product = new Product();
        product.setName("Product name");
        product.setDescription("Product description");
        
        Stock stock = new Stock();
        stock.setPrice(BigDecimal.valueOf(10.0));
        stock.setQuantity(5L);
        stock.setProduct(product);
        product.setStock(stock);
        
        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Product name");
        savedProduct.setDescription("Product description");
        savedProduct.setStock(stock);
        
        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.saveAndFlush(product)).thenReturn(savedProduct);
        when(productMapper.toDto(eq(savedProduct), any(BigDecimal.class), any(Long.class)))
                .thenReturn(new ProductDto(1L, "Product name", "Product description", BigDecimal.valueOf(10.0), 5L));
        
        // Act
        ProductDto resultDto = productService.create(dto);
        
        // Assert
        assertNotNull(resultDto);
        assertEquals(1L, resultDto.getId());
        assertEquals("Product name", resultDto.getName());
        assertEquals("Product description", resultDto.getDescription());
        assertEquals(BigDecimal.valueOf(10.0), resultDto.getPrice());
        assertEquals(5L, resultDto.getQuantity());
        
        verify(productRepository).saveAndFlush(product);
        verify(productMapper).toDto(eq(savedProduct), eq(BigDecimal.valueOf(10.0)), eq(5L));
    }
    
    @Test
    public void testCreateProductWithInvalidData() {
        // Arrange
        ProductDto dto = new ProductDto(null, "", "");
        
        // Act and Assert
        assertThrows(Exception.class, () -> productService.create(dto));
    }
    
    @Test
    void testCreateBook() {
        // Arrange
        BookDto inputDto = BookDto.builder()
                .name("Test Book")
                .description("Test Description")
                .author("Test Author")
                .price(BigDecimal.valueOf(19.99))
                .quantity(100L)
                .build();
        
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setName("Test Book");
        savedBook.setDescription("Test Description");
        savedBook.setAuthor("Test Author");
        
        Stock savedStock = new Stock();
        savedStock.setId(1L);
        savedStock.setPrice(BigDecimal.valueOf(19.99));
        savedStock.setQuantity(100L);
        savedStock.setProduct(savedBook);
        
        savedBook.setStock(savedStock);
        
        BookDto outputDto = BookDto.builder()
                .id(1L)
                .name("Test Book")
                .description("Test Description")
                .author("Test Author")
                .price(BigDecimal.valueOf(19.99))
                .quantity(100L)
                .build();
        
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        when(stockRepository.save(any(Stock.class))).thenReturn(savedStock);
        when(bookMapper.toDto(any(Book.class))).thenReturn(outputDto);
        
        // Act
        BookDto result = productService.createBook(inputDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals("Test Author", result.getAuthor());
        assertEquals(BigDecimal.valueOf(19.99), result.getPrice());
        assertEquals(100L, result.getQuantity());
        
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(stockRepository, times(1)).save(any(Stock.class));
        verify(bookMapper, times(1)).toDto(any(Book.class));
    }
    
    @Test
    void testPatch() throws IOException {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        Stock stock = new Stock();
        stock.setPrice(BigDecimal.TEN);
        stock.setQuantity(5L);
        
        ProductDto productDto = new ProductDto();
        JsonNode patchNode = mock(JsonNode.class);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productService.getStockOrDefault(productId)).thenReturn(stock);
        when(productMapper.toDto(product, stock.getPrice(), stock.getQuantity())).thenReturn(productDto);
        
        ObjectReader mockReader = mock(ObjectReader.class);
        when(objectMapper.readerForUpdating(any())).thenReturn(mockReader);
        when(mockReader.readValue(any(JsonNode.class), eq(JsonNode.class))).thenReturn(patchNode);
        
        when(productRepository.save(product)).thenReturn(product);
        
        // Act
        ProductDto result = productService.patch(productId, patchNode);
        
        // Assert
        assertNotNull(result);
        verify(productRepository).findById(productId);
        verify(productMapper).updateWithNull(eq(productDto), eq(product));
        verify(productRepository).save(product);
    }
    
    @Test
    void testPatchMany() throws IOException {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        Stock stock1 = new Stock();
        stock1.setPrice(BigDecimal.ONE);
        stock1.setQuantity(1L);
        Stock stock2 = new Stock();
        stock2.setPrice(BigDecimal.ONE);
        stock2.setQuantity(1L);
        List<Product> products = Arrays.asList(new Product(stock1), new Product(stock2));
        products.get(0).setId(1L);
        products.get(1).setId(2L);
        Stock stock = new Stock();
        stock.setPrice(BigDecimal.TEN);
        stock.setQuantity(5L);
        
        ProductDto productDto1 = new ProductDto();
        productDto1.setId(1L);
        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2L);
        JsonNode patchNode = mock(JsonNode.class);
        
        when(productRepository.findAllById(ids)).thenReturn(products);
        when(productService.getStockOrDefault(anyLong())).thenReturn(stock);
        when(productMapper.toDto(any(Product.class), any(BigDecimal.class), any(Long.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            if (product.getId().equals(1L)) {
                return productDto1;
            } else {
                return productDto2;
            }
        });
        
        ObjectReader mockReader = mock(ObjectReader.class);
        when(objectMapper.readerForUpdating(any())).thenReturn(mockReader);
        when(mockReader.readValue(any(JsonNode.class), eq(JsonNode.class))).thenReturn(patchNode);
        
        when(productRepository.saveAll(products)).thenReturn(products);
        
        // Act
        List<Long> result = productService.patchMany(ids, patchNode);
        
        // Assert
        assertNotNull(result);
        assertEquals(ids, result);
        verify(productRepository).findAllById(ids);
        verify(productRepository).saveAll(products);
        verify(productMapper, times(4)).toDto(any(Product.class), any(BigDecimal.class), any(Long.class));
    }
    
    @Test
    void testDelete() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        // Act
        productService.delete(productId);
        
        // Assert
        verify(productRepository).findById(productId);
        verify(productRepository).delete(product);
    }
    
    @Test
    void testDeleteNotFound() {
        // Arrange
        Long productId = 1L;
        
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> productService.delete(productId));
        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any());
    }
}

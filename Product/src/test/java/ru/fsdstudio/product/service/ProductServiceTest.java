package ru.fsdstudio.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.math.BigDecimal;
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
        product.setName("Old Name");
        product.setDescription("Old Description");
        
        Stock stock = new Stock();
        stock.setPrice(BigDecimal.TEN);
        stock.setQuantity(5L);
        product.setStock(stock);
        
        ProductDto inputProductDto = new ProductDto();
        inputProductDto.setName("New Name");
        inputProductDto.setDescription("New Description");
        inputProductDto.setPrice(BigDecimal.valueOf(15));
        inputProductDto.setQuantity(10L);
        
        JsonNode patchNode = mock(JsonNode.class);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productService.getStockOrDefault(productId)).thenReturn(stock);
        
        ObjectReader mockReader = mock(ObjectReader.class);
        when(objectMapper.readerForUpdating(any())).thenReturn(mockReader);
        when(mockReader.readValue(any(JsonNode.class))).thenReturn(inputProductDto);
        
        // Настройка мока для updateWithNull
        doAnswer(invocation -> {
            ProductDto dto = invocation.getArgument(0);
            Product productToUpdate = invocation.getArgument(1);
            productToUpdate.setName(dto.getName());
            productToUpdate.setDescription(dto.getDescription());
            productToUpdate.getStock().setPrice(dto.getPrice());
            productToUpdate.getStock().setQuantity(dto.getQuantity());
            return null;
        }).when(productMapper).updateWithNull(any(ProductDto.class), any(Product.class));
        
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        when(productMapper.toDto(any(Product.class), any(BigDecimal.class), any(Long.class)))
                .thenAnswer(invocation -> {
                    Product updatedProduct = invocation.getArgument(0);
                    BigDecimal price = invocation.getArgument(1);
                    Long quantity = invocation.getArgument(2);
                    ProductDto resultDto = new ProductDto();
                    resultDto.setId(updatedProduct.getId());
                    resultDto.setName(updatedProduct.getName());
                    resultDto.setDescription(updatedProduct.getDescription());
                    resultDto.setPrice(price);
                    resultDto.setQuantity(quantity);
                    return resultDto;
                });
        
        // Act
        ProductDto result = productService.patch(productId, patchNode);
        
        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(BigDecimal.valueOf(15), result.getPrice());
        assertEquals(10L, result.getQuantity());
        
        verify(productRepository).findById(productId);
        verify(productMapper).updateWithNull(eq(inputProductDto), eq(product));
        verify(productRepository).save(product);
        verify(stockRepository).save(stock);
    }
    
    @Test
    void testPatchBook() throws IOException {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setName("Old Name");
        book.setDescription("Old Description");
        book.setAuthor("Old Author");
        
        Stock stock = new Stock();
        stock.setPrice(BigDecimal.TEN);
        stock.setQuantity(5L);
        book.setStock(stock);
        
        BookDto inputBookDto = BookDto.builder()
                .name("New Name")
                .description("New Description")
                .author("New Author")
                .price(BigDecimal.valueOf(15))
                .quantity(10L)
                .build();
        
        JsonNode patchNode = mock(JsonNode.class);

        when(productRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(productService.getStockOrDefault(bookId)).thenReturn(stock);
        
        ObjectReader mockReader = mock(ObjectReader.class);
        when(objectMapper.readerForUpdating(any(BookDto.class))).thenReturn(mockReader);
        when(mockReader.readValue(any(JsonNode.class))).thenAnswer(invocation -> {
            BookDto updatedDto = BookDto.builder()
                    .name("New Name")
                    .description("New Description")
                    .author("New Author")
                    .price(BigDecimal.valueOf(15))
                    .quantity(10L)
                    .build();
            return updatedDto;
        });
        
        // Имитируем обновление книги и стока
        doAnswer(invocation -> {
            BookDto dto = invocation.getArgument(0);
            Book bookToUpdate = invocation.getArgument(1);
            bookToUpdate.setName(dto.getName());
            bookToUpdate.setDescription(dto.getDescription());
            bookToUpdate.setAuthor(dto.getAuthor());
            bookToUpdate.getStock().setPrice(dto.getPrice());
            bookToUpdate.getStock().setQuantity(dto.getQuantity());
            return bookToUpdate;
        }).when(bookMapper).partialUpdate(any(BookDto.class), any(Book.class));
        
        when(productRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Настраиваем возврат обновленного DTO
        when(bookMapper.toDto(any(Book.class))).thenAnswer(invocation -> {
            Book updatedBook = invocation.getArgument(0);
            return BookDto.builder()
                    .id(updatedBook.getId())
                    .name(updatedBook.getName())
                    .description(updatedBook.getDescription())
                    .author(updatedBook.getAuthor())
                    .price(updatedBook.getStock().getPrice())
                    .quantity(updatedBook.getStock().getQuantity())
                    .build();
        });
        
        // Act
        BookDto result = productService.patchBook(bookId, patchNode);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals("New Author", result.getAuthor());
        assertEquals(BigDecimal.valueOf(15), result.getPrice());
        assertEquals(10L, result.getQuantity());
        
        verify(productRepository).findById(bookId);
        verify(bookMapper).partialUpdate(any(BookDto.class), any(Book.class));
        verify(productRepository).save(any(Book.class));
        verify(stockRepository).save(any(Stock.class));
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
    
    @Test
    public void testGetBookList_EmptyPage() {
        // Arrange
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        
        // Act
        Page<BookDto> result = productService.getBookList(Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testGetBookList_WithBooks() {
        // Arrange
        Book book1 = new Book();
        book1.setId(1L);
        book1.setName("Book 1");
        book1.setAuthor("Author 1");
        
        Book book2 = new Book();
        book2.setId(2L);
        book2.setName("Book 2");
        book2.setAuthor("Author 2");
        
        List<Book> books = List.of(book1, book2);
        
        Page<Book> bookPage = new PageImpl<>(books);
        
        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setName("Book 1");
        bookDto1.setAuthor("Author 1");
        
        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setName("Book 2");
        bookDto2.setAuthor("Author 2");
        
        Stock stock1 = new Stock();
        stock1.setPrice(BigDecimal.TEN);
        stock1.setQuantity(10L);
        
        Stock stock2 = new Stock();
        stock2.setPrice(BigDecimal.valueOf(20.0));
        stock2.setQuantity(20L);
        
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);
        when(stockRepository.findByProductId(1L)).thenReturn(stock1);
        when(stockRepository.findByProductId(2L)).thenReturn(stock2);
        
        // Act
        Page<BookDto> result = productService.getBookList(Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
        
        BookDto resultBookDto1 = result.getContent().get(0);
        assertEquals(1L, resultBookDto1.getId());
        assertEquals("Book 1", resultBookDto1.getName());
        assertEquals(BigDecimal.TEN, resultBookDto1.getPrice());
        assertEquals(10L, resultBookDto1.getQuantity());
        
        BookDto resultBookDto2 = result.getContent().get(1);
        assertEquals(2L, resultBookDto2.getId());
        assertEquals("Book 2", resultBookDto2.getName());
        assertEquals(BigDecimal.valueOf(20.0), resultBookDto2.getPrice());
        assertEquals(20L, resultBookDto2.getQuantity());
        
        verify(bookRepository).findAll(any(Pageable.class));
        verify(bookMapper, times(2)).toDto(any(Book.class));
        verify(stockRepository, times(2)).findByProductId(anyLong());
    }
    
    @Test
    public void testGetBookList_WithSomeBooksWithoutStock() {
        // Arrange
        Book book1 = new Book();
        book1.setId(1L);
        book1.setName("Book 1");
        
        Book book2 = new Book();
        book2.setId(2L);
        book2.setName("Book 2");
        
        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setName("Book 1");
        
        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setName("Book 2");
        
        Stock stock1 = new Stock();
        stock1.setPrice(BigDecimal.valueOf(10.0));
        stock1.setQuantity(10L);
        
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(book1, book2)));
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);
        when(stockRepository.findByProductId(1L)).thenReturn(stock1);
        when(stockRepository.findByProductId(2L)).thenReturn(null);
        
        // Act
        Page<BookDto> result = productService.getBookList(Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
        
        BookDto resultBookDto1 = result.getContent().get(0);
        assertEquals(1L, resultBookDto1.getId());
        assertEquals("Book 1", resultBookDto1.getName());
        assertEquals(BigDecimal.valueOf(10.0), resultBookDto1.getPrice());
        assertEquals(10L, resultBookDto1.getQuantity());
        
        BookDto resultBookDto2 = result.getContent().get(1);
        assertEquals(2L, resultBookDto2.getId());
        assertEquals("Book 2", resultBookDto2.getName());
        assertEquals(BigDecimal.ZERO, resultBookDto2.getPrice());
        assertEquals(0L, resultBookDto2.getQuantity());
        
        verify(bookRepository).findAll(any(Pageable.class));
        verify(bookMapper, times(2)).toDto(any(Book.class));
        verify(stockRepository, times(2)).findByProductId(anyLong());
    }
    
    @Test
    void testGetBookById() {
        // Arrange
        Long productId = 1L;
        Book book = new Book();
        book.setId(productId);
        book.setName("Test Book");
        book.setAuthor("Test Author");
        
        BookDto bookDto = new BookDto();
        bookDto.setId(productId);
        bookDto.setName("Test Book");
        bookDto.setAuthor("Test Author");
        
        Stock stock = new Stock();
        stock.setPrice(BigDecimal.valueOf(15.99));
        stock.setQuantity(10L);
        
        when(bookRepository.findById(productId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(stockRepository.findByProductId(productId)).thenReturn(stock);
        
        // Act
        BookDto result = productService.getBookById(productId);
        
        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Book", result.getName());
        assertEquals("Test Author", result.getAuthor());
        assertEquals(BigDecimal.valueOf(15.99), result.getPrice());
        assertEquals(10L, result.getQuantity());
        
        verify(bookRepository).findById(productId);
        verify(bookMapper).toDto(book);
        verify(stockRepository).findByProductId(productId);
    }
    
    @Test
    void testGetBookByIdNotFound() {
        // Arrange
        Long productId = 1L;
        when(bookRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> productService.getBookById(productId));
        verify(bookRepository).findById(productId);
    }
}

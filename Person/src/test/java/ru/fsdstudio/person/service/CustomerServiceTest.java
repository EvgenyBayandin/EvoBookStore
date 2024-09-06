package ru.fsdstudio.person.service;

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
import ru.fsdstudio.person.config.PasswordEncoderConfig;
import ru.fsdstudio.person.dto.CustomerRequestDtoV1;
import ru.fsdstudio.person.dto.CustomerResponceDtoV1;
import ru.fsdstudio.person.entity.Customer;
import ru.fsdstudio.person.mapper.CustomerMapper;
import ru.fsdstudio.person.repo.CustomerRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private CustomerMapper customerMapper;
    
    @InjectMocks
    private CustomerService customerService;
    
    @Mock
    private PasswordEncoderConfig passwordEncoder;
    
    @Test
    public void testGetList_EmptyRepository() {
        // Arrange
        when(customerRepository.findAll(Pageable.unpaged())).thenReturn(Page.empty());
        
        // Act
        Page<CustomerResponceDtoV1> result = customerService.getList(Pageable.unpaged());
        
        // Assert
        assertEquals(0, result.getTotalElements());
    }
    
    @Test
    public void testGetList_RepositoryWithCustomers() {
        // Arrange
        List<Customer> customers = List.of(new Customer(), new Customer());
        Page<Customer> page = new PageImpl<>(customers);
        when(customerRepository.findAll(Pageable.unpaged())).thenReturn(page);
        
        // Act
        Page<CustomerResponceDtoV1> result = customerService.getList(Pageable.unpaged());
        
        // Assert
        assertEquals(2, result.getTotalElements());
    }
    
    @Test
    public void testGetList_Pageable() {
        // Arrange
        List<Customer> customers = List.of(new Customer(), new Customer());
        Page<Customer> page = new PageImpl<>(customers, Pageable.ofSize(10), 20);
        when(customerRepository.findAll(Pageable.ofSize(10))).thenReturn(page);
        
        // Act
        Page<CustomerResponceDtoV1> result = customerService.getList(Pageable.ofSize(10));
        
        // Assert
        assertEquals(20, result.getTotalElements());
    }
    
    @Test
    public void testGetOne_CustomerFound() {
        // Arrange
        Long id = 1L;
        Customer customer = new Customer();
        customer.setId(id);
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        CustomerResponceDtoV1 expectedDto = new CustomerResponceDtoV1(1L, "John", "John", "Doe", "john.doe@mail.ru", "john", "ROLE_USER");
        
        when(customerMapper.toDto1(customer)).thenReturn(expectedDto);
        
        // Act
        CustomerResponceDtoV1 actualDto = customerService.getOne(id);
        
        // Assert
        assertEquals(expectedDto, actualDto);
    }
    
    @Test
    public void testGetOne_CustomerNotFound() {
        // Arrange
        Long id = 1L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> customerService.getOne(id));
    }
    
    @Test
    public void testCreateCustomerWithValidData() {
        // Arrange
        CustomerRequestDtoV1 dto = new CustomerRequestDtoV1(1L,"John", "Doe", "Doe", "john.doe@example.com", "John", "password", "ROLE_USER");
        Customer customer = new Customer();
        when(customerMapper.toEntity(dto)).thenReturn(customer);
        when(passwordEncoder.encode(customer.getPassword())).thenReturn("encodedPassword");
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDto1(customer)).thenReturn(new CustomerResponceDtoV1(1L, "John", "Doe", "Doe", "john.doe@example.com", "encodedPassword", "ROLE_USER"));
        
        // Act
        CustomerResponceDtoV1 result = customerService.create(dto);
        
        // Assert
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
    }
    
    @Test
    public void testCreateCustomerWithEmptyPassword() {
        // Arrange
        CustomerRequestDtoV1 dto = new CustomerRequestDtoV1(1L,"John", "Doe", "Doe", "john.doe@example.com", "John", "", "ROLE_USER");
        
        // Act and Assert
        assertThrows(NullPointerException.class, () -> customerService.create(dto));
    }
    
    
    @Test
    public void testCreateCustomerWithExistingEmail() {
        // Arrange
        CustomerRequestDtoV1 dto = new CustomerRequestDtoV1(1L,"John", "Doe", "Doe", "", "John", "password", "ROLE_USER");
        Customer customer = new Customer();
        customer.setPassword("password");
        when(customerMapper.toEntity(dto)).thenReturn(customer);
        when(passwordEncoder.encode(customer.getPassword())).thenReturn("encodedPassword");
        when(customerRepository.save(customer)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Email already exists"));
        
        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> customerService.create(dto));
    }
    
    @Test
    public void testPatch_CustomerNotFound() {
        // Arrange
        Long id = 1L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> customerService.patch(id, null));
    }
    
    @Test
    public void testDeleteExistingCustomer() {
        // Arrange
        Long id = 1L;
        Customer customer = new Customer();
        customer.setId(id);
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        
        // Act
        CustomerResponceDtoV1 result = customerService.delete(id);
        
        // Assert
        assertEquals(customerMapper.toDto1(customer), result);
    }
    
    @Test
    public void testDeleteNonExistingCustomer() {
        // Arrange
        Long id = 1L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act
        CustomerResponceDtoV1 result = customerService.delete(id);
        
        // Assert
        assertNull(result);
    }
}
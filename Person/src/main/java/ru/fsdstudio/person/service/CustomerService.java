package ru.fsdstudio.person.service;

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
import ru.fsdstudio.person.config.PasswordEncoderConfig;
import ru.fsdstudio.person.dto.CustomerRequestDtoV1;
import ru.fsdstudio.person.dto.CustomerResponceDtoV1;
import ru.fsdstudio.person.entity.Customer;
import ru.fsdstudio.person.mapper.CustomerMapper;
import ru.fsdstudio.person.repo.CustomerRepository;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final PasswordEncoderConfig passwordEncoder;
    
    private final CustomerMapper customerMapper;
    
    private final CustomerRepository customerRepository;
    
    private final ObjectMapper objectMapper;
    
    public Page<CustomerResponceDtoV1> getList(Pageable pageable) {
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customers.map(customerMapper::toDto1);
    }
    
    public CustomerResponceDtoV1 getOne(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        return customerMapper.toDto1(customerOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }
    
    public List<CustomerResponceDtoV1> getMany(List<Long> ids) {
        List<Customer> customers = customerRepository.findAllById(ids);
        return customers.stream().map(customerMapper::toDto1).toList();
    }
    
    public CustomerResponceDtoV1 create(CustomerRequestDtoV1 dto) {
        Customer customer = customerMapper.toEntity(dto);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRole(ru.fsdstudio.person.entity.Role.USER);
        Customer resultCustomer = customerRepository.save(customer);
        return customerMapper.toDto1(resultCustomer);
    }
    
    public CustomerResponceDtoV1 patch(Long id, JsonNode patchNode) throws IOException {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
        
        CustomerRequestDtoV1 customerRequestDtoV1 = customerMapper.toDto(customer);
        objectMapper.readerForUpdating(customerRequestDtoV1).readValue(patchNode);
        customerMapper.updateWithNull(customerRequestDtoV1, customer);
        
        Customer resultCustomer = customerRepository.save(customer);
        return customerMapper.toDto1(resultCustomer);
    }
    
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Customer> customers = customerRepository.findAllById(ids);
        
        for (Customer customer : customers) {
            CustomerRequestDtoV1 customerRequestDtoV1 = customerMapper.toDto(customer);
            objectMapper.readerForUpdating(customerRequestDtoV1).readValue(patchNode);
            customerMapper.updateWithNull(customerRequestDtoV1, customer);
        }
        
        List<Customer> resultCustomers = customerRepository.saveAll(customers);
        return resultCustomers.stream().map(Customer::getId).toList();
    }
    
    public CustomerResponceDtoV1 delete(Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null) {
            customerRepository.delete(customer);
        }
        return customerMapper.toDto1(customer);
    }
    
    public void deleteMany(List<Long> ids) {
        customerRepository.deleteAllById(ids);
    }
}

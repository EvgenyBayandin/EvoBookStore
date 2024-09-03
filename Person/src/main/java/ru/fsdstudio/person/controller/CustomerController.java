package ru.fsdstudio.person.controller;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.fsdstudio.person.dto.CustomerRequestDtoV1;
import ru.fsdstudio.person.dto.CustomerResponceDtoV1;
import ru.fsdstudio.person.service.CustomerService;

@RestController
@RequestMapping("/rest/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerServiceImpl;
    
    @GetMapping
    public Page<CustomerResponceDtoV1> getList(Pageable pageable) {
        return customerServiceImpl.getList(pageable);
    }
    
    @GetMapping("/{id}")
    public CustomerResponceDtoV1 getOne(@PathVariable("id") Long id) {
        return customerServiceImpl.getOne(id);
    }
    
    @GetMapping("/by-ids")
    public List<CustomerResponceDtoV1> getMany(@RequestParam List<Long> ids) {
        return customerServiceImpl.getMany(ids);
    }
    
    @PostMapping
    public CustomerResponceDtoV1 create(@RequestBody CustomerRequestDtoV1 dto) {
        return customerServiceImpl.create(dto);
    }
    
    @PatchMapping("/{id}")
    public CustomerResponceDtoV1 patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return customerServiceImpl.patch(id, patchNode);
    }
    
    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return customerServiceImpl.patchMany(ids, patchNode);
    }
    
    @DeleteMapping("/{id}")
    public CustomerResponceDtoV1 delete(@PathVariable Long id) {
        return customerServiceImpl.delete(id);
    }
    
    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        customerServiceImpl.deleteMany(ids);
    }
}

package ru.fsdstudio.person;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/rest/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerServiceImpl customerServiceImpl;
    
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

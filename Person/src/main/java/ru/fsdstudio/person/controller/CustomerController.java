package ru.fsdstudio.person.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import ru.fsdstudio.person.dto.CustomerRequestDtoV1;
import ru.fsdstudio.person.dto.CustomerResponceDtoV1;
import ru.fsdstudio.person.service.CustomerService;

@RestController
@RequestMapping("/rest/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    public Page<CustomerResponceDtoV1> getList(
            @Schema(description = "Pageable", defaultValue = "page=0&size=10&sort=id,asc")
            @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return customerService.getList(pageable);
    }
    
    @GetMapping("/{id}")
    public CustomerResponceDtoV1 getOne(@PathVariable("id") Long id) {
        return customerService.getOne(id);
    }
    
    @PostMapping
    public CustomerResponceDtoV1 create(@RequestBody CustomerRequestDtoV1 dto) {
        return customerService.create(dto);
    }
    
    @PatchMapping("/{id}")
    public CustomerResponceDtoV1 patch(
            @PathVariable Long id,
            @RequestBody
            @Schema(description = "Patch request", example =
                    "{\"firstName\": \"New firstName\", " +
                            "\"lastName\": \"New lastName\", " +
                            "\"patronymic\": \"New patronymic or empty string\", " +
                            "\"email\": \"new@example.com\", " +
                            "\"username\": \"newUsername\"}")
            JsonNode patchNode
    ) throws IOException {
        return customerService.patch(id, patchNode);
    }
    
    @DeleteMapping("/{id}")
    public CustomerResponceDtoV1 delete(@PathVariable Long id) {
        return customerService.delete(id);
    }
}

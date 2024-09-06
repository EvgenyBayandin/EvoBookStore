package ru.fsdstudio.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import ru.fsdstudio.order.dto.OrderDto;
import ru.fsdstudio.order.service.OrderService;

@RestController
@RequestMapping("/rest/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public Page<OrderDto> getList(
            @Schema(description = "Pageable", defaultValue = "page=0&size=10&sort=id,asc")
            @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return orderService.getList(pageable);
    }
    
    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        return orderService.getOne(id);
    }
    
    @GetMapping("/user-orders")
    public Page<OrderDto> getUserOrders(
            @Schema(description = "Pageable", defaultValue = "page=0&size=10&sort=id,asc")
            @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable, @RequestParam Long userId) {
        return orderService.getUserOrders(pageable, userId);
    }
    
    @PostMapping
    public OrderDto create(@RequestBody OrderDto dto) {
        return orderService.create(dto);
    }
    
    @PatchMapping("/{id}")
    public OrderDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return orderService.patch(id, patchNode);
    }
    
    @DeleteMapping("/{id}")
    public OrderDto delete(@PathVariable Long id) {
        return orderService.delete(id);
    }
}

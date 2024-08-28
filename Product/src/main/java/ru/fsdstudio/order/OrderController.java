package ru.fsdstudio.order;

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
@RequestMapping("/rest/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public Page<OrderDto> getList(Pageable pageable) {
        return orderService.getList(pageable);
    }
    
    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        return orderService.getOne(id);
    }
    
    @GetMapping("/by-ids")
    public List<OrderDto> getMany(@RequestParam List<Long> ids) {
        return orderService.getMany(ids);
    }
    
    @GetMapping("/user-orders")
    public Page<OrderDto> getUserOrders(Pageable pageable, @RequestParam Long userId) {
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
    
    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return orderService.patchMany(ids, patchNode);
    }
    
    @DeleteMapping("/{id}")
    public OrderDto delete(@PathVariable Long id) {
        return orderService.delete(id);
    }
    
    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        orderService.deleteMany(ids);
    }
}

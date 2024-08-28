package ru.fsdstudio.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class OrderService {
    
    private final OrderMapper orderMapper;
    
    private final OrderRepository orderRepository;
    
    private final ObjectMapper objectMapper;
    
    public Page<OrderDto> getList(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::toDto);
    }
    
    public OrderDto getOne(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        return orderMapper.toDto(orderOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }
    
    public List<OrderDto> getMany(List<Long> ids) {
        List<Order> orders = orderRepository.findAllById(ids);
        return orders.stream().map(orderMapper::toDto).toList();
    }
    
    public Page<OrderDto> getUserOrders(Pageable pageable, Long userId) {
        Page<Order> orders = orderRepository.findByCustomerId(pageable, userId);
        return orders.map(order -> {
            OrderDto orderDto = orderMapper.toDto(order);
            orderDto.getOrderItems().addAll(order.getOrderItems().stream()
                    .map(orderItem -> {
                        OrderDto.OrderItemsDto orderItemsDto = new OrderDto.OrderItemsDto();
                        orderItemsDto.setId(orderItem.getId());
                        orderItemsDto.setProductId(orderItem.getProduct().getId());
                        orderItemsDto.setProductName(orderItem.getProduct().getName());
                        orderItemsDto.setProductDescription(orderItem.getProduct().getDescription());
                        orderItemsDto.setQuantity(orderItem.getQuantity());
                        orderItemsDto.setAmount(orderItem.getAmount());
                        return orderItemsDto;
                    }).collect(Collectors.toList()));
            return orderDto;
        });
    }
    
    public OrderDto create(OrderDto dto) {
        Order order = orderMapper.toEntity(dto);
        Order resultOrder = orderRepository.save(order);
        return orderMapper.toDto(resultOrder);
    }
    
    public OrderDto patch(Long id, JsonNode patchNode) throws IOException {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
        
        OrderDto orderDto = orderMapper.toDto(order);
        objectMapper.readerForUpdating(orderDto).readValue(patchNode, JsonNode.class);
        orderMapper.updateWithNull(orderDto, order);
        
        Order resultOrder = orderRepository.save(order);
        return orderMapper.toDto(resultOrder);
    }
    
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Order> orders = orderRepository.findAllById(ids);
        
        for (Order order : orders) {
            OrderDto orderDto = orderMapper.toDto(order);
            objectMapper.readerForUpdating(orderDto).readValue(patchNode, JsonNode.class);
            orderMapper.updateWithNull(orderDto, order);
        }
        
        List<Order> resultOrders = orderRepository.saveAll(orders);
        return resultOrders.stream().map(Order::getId).toList();
    }
    
    public OrderDto delete(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            orderRepository.delete(order);
        }
        return orderMapper.toDto(order);
    }
    
    public void deleteMany(List<Long> ids) {
        orderRepository.deleteAllById(ids);
    }
}

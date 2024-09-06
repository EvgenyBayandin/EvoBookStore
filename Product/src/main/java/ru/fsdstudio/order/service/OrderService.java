package ru.fsdstudio.order.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.fsdstudio.order.dto.OrderDto;
import ru.fsdstudio.order.dto.OrderItemDto;
import ru.fsdstudio.order.entity.Order;
import ru.fsdstudio.order.entity.OrderItem;
import ru.fsdstudio.order.entity.Status;
import ru.fsdstudio.order.mapper.OrderMapper;
import ru.fsdstudio.order.repo.OrderItemRepository;
import ru.fsdstudio.order.repo.OrderRepository;
import ru.fsdstudio.product.entity.Product;
import ru.fsdstudio.product.entity.Stock;
import ru.fsdstudio.product.repo.ProductRepository;
import ru.fsdstudio.product.repo.StockRepository;
import ru.fsdstudio.product.service.PersonService;

@RequiredArgsConstructor
@Service
public class OrderService {
    
    private final OrderMapper orderMapper;
    
    private final OrderRepository orderRepository;
    
    private final StockRepository stockRepository;
    
    private final OrderItemRepository orderItemRepository;
    
    private final ObjectMapper objectMapper;
    
    private final PersonService personService;
    
    private final ProductRepository productRepository;
    
    public Page<OrderDto> getList(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::toOrderDto);
    }
    
    public OrderDto getOne(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        return orderOptional.map(this::toOrderDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id `%s` not found".formatted(id)));
    }
    
    public Page<OrderDto> getUserOrders(Pageable pageable, Long userId) {
        Page<Order> orders = orderRepository.findByCustomerId(userId, pageable);
        return orders.map(this::toOrderDto);
    }
    
    public OrderDto create(OrderDto dto) {
        // Проверяем существование пользователя
        if (!personService.existsById(dto.getCustomerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with id `%s` not found".formatted(dto.getCustomerId()));
        }
        
        // Проверяем существование товара
        for (OrderItemDto orderItem : dto.getOrderItems()) {
            if (!productRepository.existsById(orderItem.getProductId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id `%s` not found".formatted(orderItem.getProductId()));
            }
        }
        
        // Проверяем количество товара на складе для каждого продукта
        for (OrderItemDto orderItem : dto.getOrderItems()) {
            if (!hasEnoughStock(orderItem.getProductId(), orderItem.getQuantity())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock quantity for product with name %s".formatted(orderItem.getProductName()));
            }
        }
        
        // Создаем запись в базе данных только если количество товара достаточное
        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setStatus(Status.CREATED);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());
        order = orderRepository.save(order);
        
        // Создаем запись в таблице order_items для каждого продукта в списке
        for (OrderItemDto orderItemDto : dto.getOrderItems()) {
            Product product = productRepository.findById(orderItemDto.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + orderItemDto.getProductId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemDto.getQuantity());
            orderItem.setAmount(stockRepository.findByProductId(product.getId()).getPrice().multiply(BigDecimal.valueOf(orderItemDto.getQuantity())));
            orderItemRepository.save(orderItem);
            order.getOrderItems().add(orderItem);
            
            // Уменьшаем количество товара в складе
            Stock stock = stockRepository.findByProductId(product.getId());
            stock.setQuantity(Math.max(0, stock.getQuantity() - orderItemDto.getQuantity()));
            stockRepository.save(stock);
        }
        
        // Рассчитываем totalAmount после того, как все элементы будут добавлены в заказ
        BigDecimal totalAmount = order.getOrderItems().stream().map(OrderItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        order = orderRepository.saveAndFlush(order);
        
        return toOrderDto(order);
    }
    
    private boolean hasEnoughStock(Long productId, Long quantity) {
        // Проверяем количество товара на складе
        Stock stock = stockRepository.findByProductId(productId);
        return stock != null && stock.getQuantity() > 0 && stock.getQuantity() >= quantity;
    }
    
    public OrderDto patch(Long id, JsonNode patchNode) throws IOException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id `%s` not found".formatted(id)));
        
        OrderDto orderDto = orderMapper.toDto(order);
        objectMapper.readerForUpdating(orderDto).readValue(patchNode, JsonNode.class);
        orderMapper.updateWithNull(orderDto, order);
        
        order.setUpdatedAt(Instant.now());
        
        Order resultOrder = orderRepository.save(order);
        return orderMapper.toDto(resultOrder);
    }
    
    public OrderDto delete(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            orderRepository.delete(order);
        }
        return orderMapper.toDto(order);
    }
    
    private OrderDto toOrderDto(Order order) {
        List<OrderItem> itemsInOrder = orderItemRepository.getItemsInOrder(order.getId());
        List<OrderItemDto> products = itemsInOrder.stream()
                .map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productDescription(item.getProduct().getDescription())
                        .quantity(item.getQuantity())
                        .amount(item.getAmount())
                        .build())
                .collect(Collectors.toList());
        Object[] totalQuantityAndAmount = orderItemRepository.getTotalQuantityAndPrice(order.getId());
        OrderDto orderDto = orderMapper.toDto(order);
        orderDto.setTotalQuantityAndAmount(totalQuantityAndAmount);
        orderDto.setOrderItems(products);
        return orderDto;
    }
}

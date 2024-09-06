package ru.fsdstudio.order.service;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import ru.fsdstudio.order.dto.OrderDto;
import ru.fsdstudio.order.entity.Order;
import ru.fsdstudio.order.mapper.OrderMapper;
import ru.fsdstudio.order.repo.OrderItemRepository;
import ru.fsdstudio.order.repo.OrderRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderItemRepository orderItemRepository;
    
    @Mock
    private OrderMapper orderMapper;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    public void testGetList_NoOrders() {
        // Arrange
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        
        // Act
        Page<OrderDto> result = orderService.getList(PageRequest.of(0, 10));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        
        verify(orderRepository).findAll(any(Pageable.class));
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(orderMapper);
    }
    
    @Test
    public void testGetList_WithOrders() {
        // Arrange
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        List<Order> orders = List.of(order1, order2);
        
        OrderDto orderDto1 = new OrderDto();
        OrderDto orderDto2 = new OrderDto();
        
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(orders));
        when(orderItemRepository.getItemsInOrder(anyLong())).thenReturn(Collections.emptyList());
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto1).thenReturn(orderDto2);
        
        // Act
        Page<OrderDto> result = orderService.getList(PageRequest.of(0, 10));
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        
        verify(orderRepository).findAll(any(Pageable.class));
        verify(orderItemRepository, times(2)).getItemsInOrder(anyLong());
        verify(orderMapper, times(2)).toDto(any(Order.class));
    }
    
    @Test
    public void testGetList_WithPageable() {
        // Arrange
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        List<Order> orders = List.of(order1, order2);
        
        OrderDto orderDto1 = new OrderDto();
        OrderDto orderDto2 = new OrderDto();
        
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(orders));
        when(orderItemRepository.getItemsInOrder(anyLong())).thenReturn(Collections.emptyList());
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto1).thenReturn(orderDto2);
        
        // Act
        Page<OrderDto> result = orderService.getList(PageRequest.of(1, 5));
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        
        verify(orderRepository).findAll(any(Pageable.class));
        verify(orderItemRepository, times(2)).getItemsInOrder(anyLong());
        verify(orderMapper, times(2)).toDto(any(Order.class));
    }
}
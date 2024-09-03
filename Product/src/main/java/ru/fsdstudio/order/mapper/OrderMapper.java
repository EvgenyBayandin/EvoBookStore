package ru.fsdstudio.order.mapper;

import org.mapstruct.*;
import ru.fsdstudio.order.dto.OrderDto;
import ru.fsdstudio.order.entity.Order;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    Order toEntity(OrderDto orderDto);
    
    @AfterMapping
    default void linkOrderItems(@MappingTarget Order order) {
        order.getOrderItems().forEach(orderItem -> orderItem.setOrder(order));
    }
    
    OrderDto toDto(Order order);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order partialUpdate(OrderDto orderDto, @MappingTarget Order order);
    
    Order updateWithNull(OrderDto orderDto, @MappingTarget Order order);
}
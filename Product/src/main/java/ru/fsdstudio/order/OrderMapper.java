package ru.fsdstudio.order;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

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
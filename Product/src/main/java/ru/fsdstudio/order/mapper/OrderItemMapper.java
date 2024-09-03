package ru.fsdstudio.order.mapper;

import org.mapstruct.*;
import ru.fsdstudio.order.dto.OrderItemDto;
import ru.fsdstudio.order.entity.OrderItem;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemMapper {
    @Mapping(source = "productDescription", target = "product.description")
    @Mapping(source = "productName", target = "product.name")
    @Mapping(source = "productId", target = "product.id")
    OrderItem toEntity(OrderItemDto orderItemDto);
    
    @InheritInverseConfiguration(name = "toEntity")
    OrderItemDto toDto(OrderItem orderItem);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "toEntity")
    OrderItem partialUpdate(OrderItemDto orderItemDto, @MappingTarget OrderItem orderItem);
}
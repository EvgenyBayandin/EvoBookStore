package ru.fsdstudio.product.mapper;

import java.math.BigDecimal;
import org.mapstruct.*;
import ru.fsdstudio.product.dto.ProductDto;
import ru.fsdstudio.product.entity.Product;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    Product toEntity(ProductDto productDto);
    
    ProductDto toDto(Product product, BigDecimal price, Long quantity);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product partialUpdate(ProductDto productDto, @MappingTarget Product product);
    
    Product updateWithNull(ProductDto productDto, @MappingTarget Product product);
}
package ru.fsdstudio.product;

@org.mapstruct.Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    Product toEntity(ru.fsdstudio.product.ProductDto productDto);
    
    ru.fsdstudio.product.ProductDto toDto(Product product);
    
    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    ru.fsdstudio.product.Product partialUpdate(ru.fsdstudio.product.ProductDto productDto, @org.mapstruct.MappingTarget ru.fsdstudio.product.Product product);
    
    Product updateWithNull(ru.fsdstudio.product.ProductDto productDto, @org.mapstruct.MappingTarget Product product);
}
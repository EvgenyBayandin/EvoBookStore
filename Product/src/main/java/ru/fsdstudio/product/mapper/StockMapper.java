package ru.fsdstudio.product.mapper;

import org.mapstruct.*;
import ru.fsdstudio.product.dto.StockDto;
import ru.fsdstudio.product.entity.Stock;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StockMapper {
    Stock toEntity(StockDto stockDto);
    
    StockDto toDto(Stock stock);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Stock partialUpdate(StockDto stockDto, @MappingTarget Stock stock);
}
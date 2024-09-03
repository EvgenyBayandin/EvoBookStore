package ru.fsdstudio.person.mapper;

import org.mapstruct.*;
import ru.fsdstudio.person.dto.CustomerRequestDtoV1;
import ru.fsdstudio.person.dto.CustomerResponceDtoV1;
import ru.fsdstudio.person.entity.Customer;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {
    Customer toEntity(CustomerRequestDtoV1 customerRequestDtoV1);
    
    CustomerRequestDtoV1 toDto(Customer customer);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer partialUpdate(CustomerRequestDtoV1 customerRequestDtoV1, @MappingTarget Customer customer);
    
    Customer updateWithNull(CustomerRequestDtoV1 customerRequestDtoV1, @MappingTarget Customer customer);
    
    Customer toEntity1(CustomerResponceDtoV1 customerResponceDtoV1);
    
    CustomerResponceDtoV1 toDto1(Customer customer);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer partialUpdate1(CustomerResponceDtoV1 customerResponceDtoV1, @MappingTarget Customer customer);
}
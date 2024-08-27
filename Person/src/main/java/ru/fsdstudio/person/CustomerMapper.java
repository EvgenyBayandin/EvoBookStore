package ru.fsdstudio.person;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

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
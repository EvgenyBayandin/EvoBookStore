package ru.fsdstudio.product.mapper;

import org.mapstruct.*;
import ru.fsdstudio.product.dto.BookDto;
import ru.fsdstudio.product.entity.Book;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {
    Book toEntity(BookDto bookDto);
   
    BookDto toDto(Book book);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Book partialUpdate(BookDto bookDto, @MappingTarget Book book);
}
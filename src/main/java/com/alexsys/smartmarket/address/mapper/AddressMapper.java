package com.alexsys.smartmarket.address.mapper;

import org.mapstruct.*;

import com.alexsys.smartmarket.address.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    // update existing entity from DTO (only non-null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Address target, Address source);
}

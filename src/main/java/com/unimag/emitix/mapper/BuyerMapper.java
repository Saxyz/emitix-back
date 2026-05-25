package com.unimag.emitix.mapper;

import com.unimag.emitix.dto.BuyerResponse;
import com.unimag.emitix.entity.Buyer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuyerMapper {

    BuyerResponse toResponse(Buyer buyer);
}

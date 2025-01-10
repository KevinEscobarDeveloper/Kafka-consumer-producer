package com.kevin.client_service.mappers;

import com.kevin.client_service.dto.ClientDto;
import com.kevin.client_service.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    ClientDto toDto(Client client);
}

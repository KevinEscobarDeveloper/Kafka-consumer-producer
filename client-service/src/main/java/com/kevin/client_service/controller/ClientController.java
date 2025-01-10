package com.kevin.client_service.controller;


import com.kevin.client_service.dto.ClientDto;
import com.kevin.client_service.service.IClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/clients")
@Tag(name = "Product Service", description = "Microservicio recupera clientes activos")
public class ClientController {
    private final IClientService iClientService;

    @Operation(
            summary = "Recuperar clientes activos basado en su id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
            }
    )
    @GetMapping("/{clientId}")
    public Mono<ClientDto> getClientById(@PathVariable String clientId) {
        return iClientService.getClientById(clientId);
    }
}

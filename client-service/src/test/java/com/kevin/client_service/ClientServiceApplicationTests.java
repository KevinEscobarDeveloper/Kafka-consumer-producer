package com.kevin.client_service;

import com.kevin.client_service.dto.ClientDto;
import com.kevin.client_service.exception.CustomException;
import com.kevin.client_service.mappers.ClientMapper;
import com.kevin.client_service.models.Client;
import com.kevin.client_service.repository.ClientRepository;
import com.kevin.client_service.service.Impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class ClientServiceApplicationTests {

	@Mock
	private ClientRepository clientRepository;

	private ClienteServiceImpl clienteService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		clienteService = new ClienteServiceImpl(clientRepository);
	}

	@Test
	void clientActiveAndFoundCase() {
		String clientId = "123";
		Client client = new Client(clientId, "Kevin", "Mexico", true, Arrays.asList("order1", "order2"));
		ClientDto clientDto = ClientMapper.INSTANCE.toDto(client);

		when(clientRepository.findById(clientId)).thenReturn(Mono.just(client));
		when(clientRepository.findByClientIdAndIsActive(clientId, true)).thenReturn(Mono.just(client));

		Mono<ClientDto> result = clienteService.getClientById(clientId);

		StepVerifier.create(result)
				.expectNext(clientDto)
				.verifyComplete();

		verify(clientRepository, times(1)).findById(clientId);
		verify(clientRepository, times(1)).findByClientIdAndIsActive(clientId, true);
	}

	@Test
	void clientNotFoundCase() {
		String clientId = "123";

		when(clientRepository.findById(clientId)).thenReturn(Mono.empty());

		Mono<ClientDto> result = clienteService.getClientById(clientId);

		StepVerifier.create(result)
				.expectErrorMatches(throwable ->
						throwable instanceof CustomException &&
								throwable.getMessage().equals("Cliente no encontrado") &&
								((CustomException) throwable).getHttpCode() == 404
				)
				.verify();

		verify(clientRepository, times(1)).findById(clientId);
		verify(clientRepository, never()).findByClientIdAndIsActive(anyString(), anyBoolean());
	}

	@Test
	void clientInactiveCase() {
		String clientId = "123";
		Client client = new Client(clientId, "Kevin", "Mexico", false, Arrays.asList("order1", "order2"));

		when(clientRepository.findById(clientId)).thenReturn(Mono.just(client));
		when(clientRepository.findByClientIdAndIsActive(clientId, true)).thenReturn(Mono.empty());

		Mono<ClientDto> result = clienteService.getClientById(clientId);

		StepVerifier.create(result)
				.expectErrorMatches(throwable ->
						throwable instanceof CustomException &&
								throwable.getMessage().equals("Cliente inactivo") &&
								((CustomException) throwable).getHttpCode() == 403
				)
				.verify();

		verify(clientRepository, times(1)).findById(clientId);
		verify(clientRepository, times(1)).findByClientIdAndIsActive(clientId, true);
	}

}

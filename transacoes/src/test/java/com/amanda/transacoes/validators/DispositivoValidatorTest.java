package com.amanda.transacoes.validators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.DispositivoDto;
import com.amanda.transacoes.repositories.DispositivoRepository;
import com.amanda.transacoes.services.ClienteService;

@ExtendWith(MockitoExtension.class)
class DispositivoValidatorTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private DispositivoRepository dispositivoRepository;

    @InjectMocks
    private DispositivoValidator dispositivoValidator;

    private DispositivoDto dispositivoDto;
    private UUID clienteId;
    private UUID dispositivoId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        dispositivoId = UUID.randomUUID();
        dispositivoDto = new DispositivoDto(clienteId, "Dispositivo Teste");
    }

    @Test
    void validateCreate_DescricaoNula_DeveLancarExcecao() {
        dispositivoDto.setDescricao(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoValidator.validateCreate(dispositivoDto);
        });
        
        assertEquals("400 BAD_REQUEST \"A descrição não deve ser nula.\"", exception.getMessage());
    }

    @Test
    void validateCreate_DescricaoVazia_DeveLancarExcecao() {
        dispositivoDto.setDescricao("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoValidator.validateCreate(dispositivoDto);
        });

        assertEquals("400 BAD_REQUEST \"A descrição não deve ser vazia.\"", exception.getMessage());
    }

    @Test
    void validateCreate_ClienteIdNulo_DeveLancarExcecao() {
        dispositivoDto.setClienteId(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoValidator.validateCreate(dispositivoDto);
        });

        assertEquals("400 BAD_REQUEST \"O clienteID não deve ser nulo.\"", exception.getMessage());
    }

    @Test
    void validateCreate_ClienteNaoEncontrado_DeveLancarExcecao() {
        when(clienteService.existsById(clienteId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoValidator.validateCreate(dispositivoDto);
        });

        assertEquals("404 NOT_FOUND \"Cliente não encontrado.\"", exception.getMessage());
    }

    @Test
    void validateCreate_DadosValidos_DevePassar() {
        when(clienteService.existsById(clienteId)).thenReturn(true);

        assertDoesNotThrow(() -> dispositivoValidator.validateCreate(dispositivoDto));
    }

    @Test
    void validateUpdate_ClienteExiste_DevePassar() {
        when(clienteService.existsById(clienteId)).thenReturn(true);

        assertDoesNotThrow(() -> dispositivoValidator.validateUpdate(dispositivoDto));
    }

    @Test
    void validateUpdate_ClienteNaoEncontrado_DeveLancarExcecao() {
        when(clienteService.existsById(clienteId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoValidator.validateUpdate(dispositivoDto);
        });

        assertEquals("404 NOT_FOUND \"Cliente não encontrado.\"", exception.getMessage());
    }

    @Test
    void validateUpdate_ClienteIdNulo_NaoDeveValidarExistencia() {
        dispositivoDto.setClienteId(null);

        assertDoesNotThrow(() -> dispositivoValidator.validateUpdate(dispositivoDto));

        verify(clienteService, never()).existsById(any());
    }

    @Test
    void validateDelete_DispositivoNaoEncontrado_DeveLancarExcecao() {
        when(dispositivoRepository.existsById(dispositivoId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoValidator.validateDelete(dispositivoId);
        });

        assertEquals("404 NOT_FOUND \"Dispositivo não encontrado.\"", exception.getMessage());
    }

    @Test
    void validateDelete_DispositivoEncontrado_DevePassar() {
        when(dispositivoRepository.existsById(dispositivoId)).thenReturn(true);

        assertDoesNotThrow(() -> dispositivoValidator.validateDelete(dispositivoId));
    }
}

package com.amanda.transacoes.validators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.ClienteDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.repositories.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteValidatorTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteValidator clienteValidator;

    private ClienteDto clienteDto;
    private ClienteModel cliente;
    private UUID clienteId;

    @BeforeEach
    void setup() {
        clienteId = UUID.randomUUID();
        clienteDto = new ClienteDto("Amanda Souza", "591.460.470-26");
        cliente = new ClienteModel("Amanda Souza", "591.460.470-26", "159001", true, 1000.0);
        cliente.setId(clienteId);
    }

    @Test
    void validateCreate_DadosValidos_DevePassar() {
        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);

        assertDoesNotThrow(() -> clienteValidator.validateCreate(clienteDto));
    }

    @Test
    void validateCreate_CpfJaCadastrado_DeveLancarExcecao() {
        when(clienteRepository.existsByCpf(anyString())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteValidator.validateCreate(clienteDto);
        });

        assertEquals("400 BAD_REQUEST \"O CPF já está cadastrado no sistema.\"", exception.getMessage());
    }

    @Test
    void validateCreate_NomeVazio_DeveLancarExcecao() {
        clienteDto.setNome("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteValidator.validateCreate(clienteDto);
        });

        assertEquals("400 BAD_REQUEST \"O nome não deve ser vazio ou nulo.\"", exception.getMessage());
    }

    @Test
    void validateUpdate_CpfValido_DevePassar() {
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        
        assertDoesNotThrow(() -> clienteValidator.validateUpdate(clienteDto, clienteId));
    }
       
    @Test
    void validateUpdate_CpfVazio_DeveFazerNada() {
        clienteDto.setCpf(null);

        assertDoesNotThrow(() -> clienteValidator.validateUpdate(clienteDto, clienteId));

        verify(clienteRepository, never()).findByCpf(anyString());
    }

    @Test
    void validateUpdate_CpfJaCadastradoParaOutroCliente_DeveLancarExcecao() {
        clienteDto.setCpf("162.971.380-52");
        UUID outroClienteId = UUID.randomUUID(); 
        ClienteModel outroCliente = new ClienteModel("Maria Souza", "162.971.380-52", "159456", true, 800.0);
        outroCliente.setId(outroClienteId);

        when(clienteRepository.findByCpf(clienteDto.getCpf())).thenReturn(Optional.of(outroCliente));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteValidator.validateUpdate(clienteDto, clienteId);
        });

        assertEquals("400 BAD_REQUEST \"O CPF já está cadastrado no sistema.\"", exception.getMessage());
    }

    @Test
    void validateUpdate_CpfJaCadastradoParaOMesmoCliente_DevePassar() {

        when(clienteRepository.findByCpf(clienteDto.getCpf())).thenReturn(Optional.of(cliente));

        assertEquals("591.460.470-26", clienteDto.getCpf());
        assertDoesNotThrow(() -> clienteValidator.validateUpdate(clienteDto, clienteId));
    }

    @Test
    void validateDebitar_SaldoSuficiente_DevePassar() {
        assertDoesNotThrow(() -> clienteValidator.validateDebitar(cliente, 100, 10));
    }

    @Test
    void validateDebitar_SaldoInsuficiente_DeveLancarExcecao() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteValidator.validateDebitar(cliente, 60000, 10);
        });
        assertEquals("400 BAD_REQUEST \"Saldo insuficiente.\"", exception.getMessage());
    }

    @Test
    void validateDelete_SaldoZero_DevePassar() {
        cliente.setSaldo(0);

        assertDoesNotThrow(() -> clienteValidator.validateDelete(cliente));
    }

    @Test
    void validateDelete_SaldoPositivo_DeveLancarExcecao() {
        cliente.setSaldo(50);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteValidator.validateDelete(cliente);
        });

        assertEquals("400 BAD_REQUEST \"O cliente não pode ser deletado porque ainda possui saldo em conta.\"", exception.getMessage());
    }
    
    @Test
    void isCpfValido_FormatoInvalido_DeveLancarExcecao() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteValidator.isCpfValido("123123");
        });

        assertEquals("400 BAD_REQUEST \"Formato de CPF invalido, o CPF deve ter 11 numeros.\"", exception.getMessage());
    }
    
    @Test
    void isCpfValido_TodosDigitosIguais_DeveLancarExcecao() {
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteValidator.isCpfValido("111.111.111.11");
        });

        assertEquals("400 BAD_REQUEST \"CPF invalido, todos os digitos sao iguais.\"", exception.getMessage());
    } 

    @Test
    void isCpfValido_DigitosVerificadoresInvalidos_DeveLancarExcecao() {
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteValidator.isCpfValido("162971380-50");
        });

        assertEquals("400 BAD_REQUEST \"CPF inválido, dígitos verificadores não conferem.\"", exception.getMessage());
    } 
}

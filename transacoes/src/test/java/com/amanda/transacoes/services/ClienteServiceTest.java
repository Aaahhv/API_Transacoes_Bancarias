package com.amanda.transacoes.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.ClienteDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.utils.CpfUtil;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private DispositivoService dispositivoService;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteModel cliente;
    private ClienteDto clienteDto;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        clienteDto = new ClienteDto("Amanda Souza", "591.46047-026");
        cliente = new ClienteModel("Amanda Souza", "591.460.470-26", "159001", true, 1000.0);
        cliente.setId(clienteId);
    }

    @Test
    void create_DadosValidos() {
        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.create(clienteDto);

        assertNotNull(resultado);
        assertEquals("Amanda Souza", resultado.getNome());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void create_ExcecaoCpfJaExiste() {
        when(clienteRepository.existsByCpf(anyString())).thenReturn(true);
    
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.create(clienteDto)
        );
    
        assertEquals("400 BAD_REQUEST \"O CPF já está cadastrado no sistema.\"", exception.getMessage());
    }
    
    @Test
    void create_ExcecaoNomeVazio() {
        ClienteDto clienteDtoInvalido = new ClienteDto("", "59146047026");
    
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.create(clienteDtoInvalido)
        );
    
        assertEquals("400 BAD_REQUEST \"O nome não deve ser vazio ou nulo.\"", exception.getMessage());
    }

    @Test
    void gerarNumConta_NumeroDeContaDisponivel() {
        when(clienteRepository.existsByNumConta(anyString())).thenReturn(false); 

        String numConta = clienteService.gerarNumConta();

        assertNotNull(numConta);
        assertTrue(numConta.startsWith("159"));
        assertEquals(6, numConta.length()); 
    }

    @Test
    void gerarNumConta_ExcecaoTodosNumerosEmUso() {
        when(clienteRepository.existsByNumConta(anyString())).thenReturn(true); 

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.gerarNumConta()
        );

        assertEquals("500 INTERNAL_SERVER_ERROR \"Não foi possível gerar um novo número de conta.\"", exception.getMessage());
    }

 
    @Test
    void update_ClienteValido() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.update(clienteDto, clienteId);

        assertEquals(clienteDto.getNome(), resultado.getNome());
        assertEquals(CpfUtil.formatsCpf(clienteDto.getCpf()), resultado.getCpf());
        verify(clienteRepository).save(any(ClienteModel.class));
    }
 

    @Test
    void update_ExcecaoClienteNaoEncontrado() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.update(clienteDto, clienteId)
        );

        assertEquals("404 NOT_FOUND \"Cliente não encontrado.\"", exception.getMessage());
    }

    @Test
    void update_ExcecaoCpfJaExiste() {
        ClienteModel outroCliente = new ClienteModel("Outro Cliente", "123.456.789-01", "159002", true, 500.0);
        outroCliente.setId(UUID.randomUUID());

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByCpf(anyString())).thenReturn(true);
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.of(outroCliente));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.update(clienteDto, clienteId)
        );

        assertEquals("400 BAD_REQUEST \"O CPF já está cadastrado no sistema.\"", exception.getMessage());
    }

    @Test
    void update_NomeNulo() {
        ClienteDto clienteDtoInvalido = new ClienteDto(null, "726.559.780-05");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.update(clienteDtoInvalido, clienteId);

        assertEquals("Amanda Souza", resultado.getNome());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void update_CpfNulo() {
        ClienteDto clienteDtoInvalido = new ClienteDto("Novo Nome", null);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.update(clienteDtoInvalido, clienteId);

        assertEquals("591.460.470-26", resultado.getCpf());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void update_CpfJaExisteMasEhDoMesmoCliente() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByCpf(cliente.getCpf())).thenReturn(true);
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.of(cliente)); // Retorna o mesmo cliente
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);
    
        ClienteModel resultado = clienteService.update(clienteDto, clienteId);
    
        assertEquals(clienteDto.getCpf(), resultado.getCpf());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void getAll_ListaDeClientes() {
        List<ClienteModel> listaClientes = Arrays.asList(cliente);
        when(clienteRepository.findAll()).thenReturn(listaClientes);

        List<ClienteModel> resultado = clienteService.getAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void getById_ClienteExiste() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        Optional<ClienteModel> resultado = clienteService.getById(clienteId);

        assertTrue(resultado.isPresent());
        assertEquals(clienteId, resultado.get().getId());

    }

    @Test
    void getById_ClienteNaoExiste() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());
    
        Optional<ClienteModel> resultado = clienteService.getById(clienteId);
    
        assertFalse(resultado.isPresent());
    }

    @Test
    void getByCpf_CpfExistente() {
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.of(cliente));

        Optional<ClienteModel> resultado = clienteService.getByCpf(cliente.getCpf());

        assertTrue(resultado.isPresent());
        assertEquals(cliente.getCpf(), resultado.get().getCpf());
    }

    @Test
    void getByCpf_CpfNaoExiste() {
        when(clienteRepository.findByCpf("000.000.000-00")).thenReturn(Optional.empty());

        Optional<ClienteModel> resultado = clienteService.getByCpf("000.000.000-00");

        assertFalse(resultado.isPresent());
    }

    @Test
    void existsById_ClienteExiste() {
        when(clienteRepository.existsById(clienteId)).thenReturn(true);

        boolean resultado = clienteService.existsById(clienteId);

        assertTrue(resultado);
        verify(clienteRepository).existsById(clienteId);
    }

    @Test
    void existsById_ClienteNaoExiste() {
        when(clienteRepository.existsById(clienteId)).thenReturn(false);

        boolean resultado = clienteService.existsById(clienteId);

        assertFalse(resultado);
        verify(clienteRepository).existsById(clienteId);
    }

    
    @Test
    void deleteById_ClienteExiste() {
        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(clienteId);

        assertDoesNotThrow(() -> clienteService.deleteById(clienteId));
        verify(clienteRepository).deleteById(clienteId);
    }

    @Test
    void deleteById_DExcecaoClienteNaoExiste() {
        when(clienteRepository.existsById(clienteId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.deleteById(clienteId)
        );

        assertEquals("404 NOT_FOUND \"Cliente não encontrado.\"", exception.getMessage());
    }

    @Test
    void ativar_AtivarClienteExiste() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.ativar(clienteId, false);

        assertFalse(resultado.getAtivo());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void ativar_DesativarClienteExiste() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.ativar(clienteId, false);

        assertFalse(resultado.getAtivo());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void ativar_ExcecaoClienteNaoEncontrado() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.ativar(clienteId, true)
        );

        assertEquals("404 NOT_FOUND \"Cliente não encontrado.\"", exception.getMessage());
    }

    @Test
    void isCpfValido_CpfValido() {
        assertDoesNotThrow(() -> clienteService.isCpfValido("52998224725")); // CPF real válido
    }

    @Test
    void isCpfValido_FormatoInvalido() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.isCpfValido("123A567890") // Contém letra e tem menos de 11 números
        );

        assertEquals("400 BAD_REQUEST \"Formato de CPF invalido, o CPF deve ter 11 numeros.\"", exception.getMessage());
    }

    @Test
    void isCpfValido_ExcecaoTodosDigitosIguais() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.isCpfValido("11111111111") // Todos os dígitos são iguais
        );

        assertEquals("400 BAD_REQUEST \"CPF invalido, todos os digitos sao iguais.\"", exception.getMessage());
    }

    @Test
    void isCpfValido_ExcecaoDigitosVerificadoresInvalidos() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.isCpfValido("52998224720") // CPF inválido com erro nos dígitos verificadores
        );

        assertEquals("400 BAD_REQUEST \"CPF inválido, dígitos verificadores não conferem.\"", exception.getMessage());
    }

    @Test
    void debitar_DebitarSaldoValido() {
        when(clienteRepository.findByNumConta(cliente.getNumConta())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.debitar(cliente.getNumConta(), 100, 10);

        assertEquals(890.0, resultado.getSaldo());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void debitar_ExcecaoSaldoInsuficiente() {
        when(clienteRepository.findByNumConta(cliente.getNumConta())).thenReturn(Optional.of(cliente));

        Exception exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.debitar(cliente.getNumConta(), 2000, 10));

        assertEquals("400 BAD_REQUEST \"Saldo insuficiente.\"", exception.getMessage());
    }

    @Test
    void debitar_ExcecaoContaNaoEncontrada() {
        when(clienteRepository.findByNumConta("159001")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.debitar("159001", 100.0, 10.0)
        );

        assertEquals("404 NOT_FOUND \"Número de conta 159001 não encontrado.\"", exception.getMessage());
    }
    

    @Test
    void creditar_ContaValida() {
        when(clienteRepository.findByNumConta(cliente.getNumConta())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.creditar(cliente.getNumConta(), 200, 10);

        assertEquals(1190.0, resultado.getSaldo());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void creditar_ExcecaoContaNaoEncontrada() {
        when(clienteRepository.findByNumConta("159001")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.creditar("159001", 100.0, 10.0)
        );

        assertEquals("404 NOT_FOUND \"Número de conta 159001 não encontrado.\"", exception.getMessage());
    }


    @Test
    void isClienteAtivo_DeveRetornarTrueQuandoClienteEstaAtivo() {
        cliente.setAtivo(true);
        when(clienteRepository.findByNumConta(cliente.getNumConta())).thenReturn(Optional.of(cliente));

        boolean resultado = clienteService.isClienteAtivo(cliente.getNumConta());

        assertTrue(resultado);
    }

    @Test
    void isClienteAtivo_ClienteInativo() {
        cliente.setAtivo(false);
        when(clienteRepository.findByNumConta(cliente.getNumConta())).thenReturn(Optional.of(cliente));

        boolean resultado = clienteService.isClienteAtivo(cliente.getNumConta());

        assertFalse(resultado);
    }

    @Test
    void isClienteAtivo_ExcecaoQNumContaNaoEncontrado() {
        when(clienteRepository.findByNumConta("999999")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.isClienteAtivo("999999")
        );

        assertEquals("404 NOT_FOUND \"Número de conta 999999 não encontrado\"", exception.getMessage());
    }

}
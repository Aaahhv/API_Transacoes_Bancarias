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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.ClienteDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.utils.CpfUtil;
import com.amanda.transacoes.validators.ClienteValidator;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private DispositivoService dispositivoService;

    @Mock
    private TransacaoService transacaoService;

    @Mock
    private ClienteValidator clienteValidator;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteModel cliente;
    private ClienteModel clienteZeroSaldo;
    private ClienteDto clienteDto;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        clienteDto = new ClienteDto("Amanda Souza", "591.46047-026");
        cliente = new ClienteModel("Amanda Souza", "591.460.470-26", "159001", true, 1000.0);
        clienteZeroSaldo = new ClienteModel("Amanda Souza", "596.764.010-05", "159002", true, 0.0);
        cliente.setId(clienteId);
    }

    @Test
    void create_DadosValidos_DeveSalvarCliente() {
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.create(clienteDto);

        assertNotNull(resultado);
        assertEquals("Amanda Souza", resultado.getNome());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void gerarNumConta_Sucesso_DeveGerarNumeroUnico() {
        String numConta = clienteService.gerarNumConta();
        assertNotNull(numConta);
        assertTrue(numConta.startsWith("159"));
        assertEquals(6, numConta.length()); 
    }

    @Test
    void gerarNumConta_TodasTentativasFalham_DeveLancarExcecao() {
        when(clienteRepository.existsByNumConta(anyString())).thenReturn(true); 

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteService.gerarNumConta();
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Não foi possível gerar um novo número de conta.", exception.getReason());
    }

    @Test
    void getAll_ListaDeClientes_DeveRetornarListaDeClientes() {
        List<ClienteModel> listaClientes = Arrays.asList(cliente);
        when(clienteRepository.findAll()).thenReturn(listaClientes);

        List<ClienteModel> resultado = clienteService.getAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void getById_ClienteIdExiste_DeveRetornarCliente() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        Optional<ClienteModel> foundCliente = clienteService.getById(clienteId);

        assertTrue(foundCliente.isPresent());
        assertEquals(cliente.getId(), foundCliente.get().getId());
    }

    @Test
    void getById_ClienteIdNaoExiste_DeveRetornarEmpty() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        Optional<ClienteModel> foundCliente = clienteService.getById(clienteId);

        assertFalse(foundCliente.isPresent());
    }

    @Test
    void getByCpf_CpfExistente_DeveRetornarCliente() {
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.of(cliente));

        Optional<ClienteModel> resultado = clienteService.getByCpf(cliente.getCpf());

        assertTrue(resultado.isPresent());
        assertEquals(cliente.getCpf(), resultado.get().getCpf());
    }

    @Test
    void existsById_ClienteExiste_DeveRetornarTrue() {
        when(clienteRepository.existsById(clienteId)).thenReturn(true);

        boolean resultado = clienteService.existsById(clienteId);

        assertTrue(resultado);
        verify(clienteRepository).existsById(clienteId);
    }

    @Test
    void existsById_ClienteNaoExiste_DeveRetornarFalse() {
        when(clienteRepository.existsById(clienteId)).thenReturn(false);

        boolean resultado = clienteService.existsById(clienteId);

        assertFalse(resultado);
        verify(clienteRepository).existsById(clienteId);
    }

    @Test
    void existsByNumConta_ClienteExiste_DeveRetornarTrue() {
        when(clienteRepository.existsByNumConta("159001")).thenReturn(true);

        boolean resultado = clienteService.existsByNumConta("159001");

        assertTrue(resultado);
        verify(clienteRepository).existsByNumConta("159001");
    }
/* 
    @Test
    void existsByNumConta_ClienteNaoExiste() {
        when(clienteRepository.existsByNumConta("159001")).thenReturn(false);

        boolean resultado = clienteService.existsByNumConta("159001");

        assertFalse(resultado);
        verify(clienteRepository).existsByNumConta("159001");
    }*/
 
    @Test
    void update_ClienteValido_DeveAtualizarCliente() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.update(clienteDto, clienteId);

        assertEquals(clienteDto.getNome(), resultado.getNome());
        assertEquals(CpfUtil.formatsCpf(clienteDto.getCpf()), resultado.getCpf());
        verify(clienteRepository).save(any(ClienteModel.class));
    }
 

    @Test
    void update_ClienteIdNaoExiste_DeveLancarExcecao() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.update(clienteDto, clienteId)
        );

        assertEquals("404 NOT_FOUND \"Cliente não encontrado.\"", exception.getMessage());
    }

    @Test
    void update_NomeNulo_DeveAtualizarApenasOCpf() {
        ClienteDto clienteDtoInvalido = new ClienteDto(null, "726.559.780-05");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.update(clienteDtoInvalido, clienteId);

        assertEquals("Amanda Souza", resultado.getNome());
        assertEquals("726.559.780-05", resultado.getCpf());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void update_CpfNulo_DeveAtualizarApenasONome() {
        ClienteDto clienteDtoInvalido = new ClienteDto("Novo Nome", null);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.update(clienteDtoInvalido, clienteId);

        assertEquals("Novo Nome", resultado.getNome());
        assertEquals("591.460.470-26", resultado.getCpf());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void deleteById_ClienteIdValido_DeveDeletarCliente() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteZeroSaldo));
        doNothing().when(dispositivoService).deleteByClienteId(clienteId);
        doNothing().when(transacaoService).deleteByClienteId(clienteZeroSaldo);
        doNothing().when(clienteRepository).deleteById(clienteId);


        assertDoesNotThrow(() -> clienteService.deleteById(clienteId));
        verify(clienteRepository).deleteById(clienteId);
    }

    @Test
    void deleteById_ClienteIdNaoExiste_DeveLancarExcecao() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.deleteById(clienteId)
        );

        assertEquals("404 NOT_FOUND \"Cliente não encontrado.\"", exception.getMessage());
    }

    @Test
    void ativar_ClienteExiste_DeveFazerAtivacaoCorreta() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.ativar(clienteId, false);

        assertFalse(resultado.getAtivo());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void ativar_DesativarClienteExiste_DeveDesativarCorretamente() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.ativar(clienteId, false);

        assertFalse(resultado.getAtivo());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void ativar_ClienteNaoExiste_DeveLancarExcecao() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.ativar(clienteId, true)
        );

        assertEquals("404 NOT_FOUND \"Cliente não encontrado.\"", exception.getMessage());
    }

    @Test
    void debitar_DebitarSaldoValido_DeveDebitarCorretamente() {
        when(clienteRepository.findByNumConta(cliente.getNumConta())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.debitar(cliente.getNumConta(), 100, 10);

        assertEquals(890.0, resultado.getSaldo());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void debitar_NumContaInexistente_ExcecaoContaNaoEncontrada() {
        when(clienteRepository.findByNumConta("159001")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.debitar("159001", 100.0, 10.0)
        );

        assertEquals("404 NOT_FOUND \"Número de conta 159001 não encontrado.\"", exception.getMessage());
    }
    

    @Test
    void creditar_ContaValida_DeveCreditarCorretamente() {
        when(clienteRepository.findByNumConta(cliente.getNumConta())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel resultado = clienteService.creditar(cliente.getNumConta(), 200, 10);

        assertEquals(1190.0, resultado.getSaldo());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    @Test
    void creditar_ContaInexistente_ExcecaoContaNaoEncontrada() {
        when(clienteRepository.findByNumConta("159001")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.creditar("159001", 100.0, 10.0)
        );

        assertEquals("404 NOT_FOUND \"Número de conta 159001 não encontrado.\"", exception.getMessage());
    }

    @Test
    void isClienteAtivo_ClienteAtivo_DeveRetornarTrue() {
        when(clienteRepository.findByNumConta(cliente.getNumConta())).thenReturn(Optional.of(cliente));

        boolean ativo = clienteService.isClienteAtivo(cliente.getNumConta());

        assertTrue(ativo);
    }

    @Test
    void isClienteAtivo_ClienteInativo_DeveLancarExcessao() {
        cliente.setAtivo(false);
        cliente.setNumConta("159001");
        when(clienteRepository.findByNumConta(cliente.getNumConta())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.isClienteAtivo(cliente.getNumConta())
        );

        assertEquals("404 NOT_FOUND \"Número de conta 159001 não encontrado.\"", exception.getMessage());
    }

    @Test
    void ativar_ClienteExistente_DeveAtivar() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);

        ClienteModel result = clienteService.ativar(clienteId, true);

        assertTrue(result.getAtivo());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void ativar_ClienteExistente_DeveLancarExcessao() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteService.ativar(clienteId, true);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

   
/* 
    @Test
    void update_CpfJaExiste_DeveLancarExcecao() {
        ClienteModel outroCliente = new ClienteModel("Outro Cliente", "123.456.789-01", "159002", true, 500.0);
        outroCliente.setId(UUID.randomUUID());

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.of(outroCliente));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            clienteService.update(clienteDto, clienteId)
        );

        assertEquals("400 BAD_REQUEST \"O CPF já está cadastrado no sistema.\"", exception.getMessage());
    }*/
  /*
    @Test
    void update_CpfJaExisteMasEhDoMesmoCliente() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.of(cliente)); // Retorna o mesmo cliente
        when(clienteRepository.save(any(ClienteModel.class))).thenReturn(cliente);
    
        ClienteModel resultado = clienteService.update(clienteDto, clienteId);
    
        assertEquals(clienteDto.getCpf(), resultado.getCpf());
        verify(clienteRepository).save(any(ClienteModel.class));
    }

    
        */
}
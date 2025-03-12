package com.amanda.transacoes.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.TransacaoRepository;
import com.amanda.transacoes.validators.TransacaoValidator;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;
    
    @Mock
    private ClienteService clienteService;
    
    @Mock
    private OperacaoService operacaoService;

    @Mock
    private DispositivoService dispositivoService;

    @Mock
    private TransacaoValidator transacaoValidator;

    @InjectMocks
    private TransacaoService transacaoService;

    private TransacaoDto transacaoDtoCredito;

    private TransacaoDto transacaoDtoDebito;

    private TransacaoModel transacaoModelCredito;
    
    private TransacaoModel transacaoModelCredito2;

    private TransacaoModel transacaoModelDebito;

    private TransacaoModel transacaoModelDebito2;

    private ClienteModel clienteModel;

    private UUID clienteId;

    @BeforeEach
    void setUp() {
        transacaoDtoCredito = new TransacaoDto("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        
        transacaoModelCredito = new TransacaoModel("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        transacaoModelCredito.setId(UUID.randomUUID());
        transacaoModelCredito2 = new TransacaoModel("159001", "XXXXXX", 1500.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        transacaoModelCredito2.setId(UUID.randomUUID());

        transacaoDtoDebito = new TransacaoDto("159002", "159001", 100.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        
        transacaoModelDebito = new TransacaoModel("159002", "159001", 100.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        transacaoModelDebito.setId(UUID.randomUUID());
        transacaoModelDebito2 = new TransacaoModel("XXXXXX", "159001", 100.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        transacaoModelDebito2.setId(UUID.randomUUID());
        
        clienteId = UUID.randomUUID();
        clienteModel = new ClienteModel("Cliente Teste", "330.510.330-22", "159001", true, 1000.0);
        clienteModel.setId(clienteId);
    }

   @Test
    void create_TransacaoCredito_DeveCriarComSucesso() {
        when(transacaoRepository.save(any())).thenReturn(transacaoModelCredito);

        TransacaoModel result = transacaoService.create(transacaoDtoCredito);

        assertNotNull(result);
        assertEquals(transacaoDtoCredito.getCcOrigem(), result.getCcOrigem());
        assertEquals(transacaoDtoCredito.getTipoOperacao(), result.getTipoOperacao());
        assertEquals(transacaoDtoCredito.getOperacao(), result.getOperacao());
    }

    @Test
     void create_TransacaoDebito_DeveCriarComSucesso() {
         when(transacaoRepository.save(any())).thenReturn(transacaoModelDebito);
 
         TransacaoModel result = transacaoService.create(transacaoDtoDebito);
 
         assertNotNull(result);
         assertEquals(transacaoDtoDebito.getCcOrigem(), result.getCcOrigem());
         assertEquals(transacaoDtoDebito.getTipoOperacao(), result.getTipoOperacao());
         assertEquals(transacaoDtoDebito.getOperacao(), result.getOperacao());
     }

    @Test
    void getAll_Valido_DeveRetornaListaDeTransacoes() {
        TransacaoModel transacao1 = new TransacaoModel("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        TransacaoModel transacao2 = new TransacaoModel("159003", "159004", 200.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("c3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        List<TransacaoModel> transacoes = Arrays.asList(transacao1, transacao2);

        when(transacaoRepository.findAll()).thenReturn(transacoes);

        List<TransacaoModel> resultado = transacaoService.getAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("159001", resultado.get(0).getCcOrigem());
        assertEquals("159003", resultado.get(1).getCcOrigem());
        verify(transacaoRepository, times(1)).findAll();
    }
 
    @Test
    void getById_TransacaoExiste_DeveRetornarTransacao() {
        UUID id = UUID.randomUUID();
        when(transacaoRepository.findById(id)).thenReturn(Optional.of(transacaoModelCredito));

        Optional<TransacaoModel> resultado = transacaoService.getById(id);

        assertTrue(resultado.isPresent());
        assertEquals("159001", resultado.get().getCcOrigem());
    }
 
    @Test
    void getByClienteId_ClienteComTransacoes_DeveRetornarTransacoes() {
        when(clienteService.getById(clienteId)).thenReturn(Optional.of(clienteModel));
        when(transacaoRepository.findByCcOrigem(clienteModel.getNumConta())).thenReturn(Arrays.asList(transacaoModelCredito,transacaoModelCredito2));
        when(transacaoRepository.findByCcDestino(clienteModel.getNumConta())).thenReturn(Arrays.asList(transacaoModelDebito));

        List<TransacaoModel> result = transacaoService.getByClienteId(clienteId);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(transacaoModelCredito));
        assertTrue(result.contains(transacaoModelCredito2));
        assertTrue(result.contains(transacaoModelDebito));
    }

    @Test
    void getByClienteId_ClienteSemTransacoes_DeveRetornarListaVazia() {
        when(clienteService.getById(clienteId)).thenReturn(Optional.of(clienteModel));
        when(transacaoRepository.findByCcOrigem(clienteModel.getNumConta())).thenReturn(Collections.emptyList());
        when(transacaoRepository.findByCcDestino(clienteModel.getNumConta())).thenReturn(Collections.emptyList());

        List<TransacaoModel> result = transacaoService.getByClienteId(clienteId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
 
    @Test
    void getByClienteId_ClienteNaoExiste_DeveLancarExcecao() {
        when(clienteService.getById(clienteId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            transacaoService.getByClienteId(clienteId)
        );

        assertEquals("404 NOT_FOUND \"Cliente não encontrado.\"", exception.getMessage());
    }

    @Test
    void deleteByClienteId_ClienteTemTransacoesDestino_DeveDeletarSeContaOrigemNaoExiste() {
        when(transacaoRepository.findByCcDestino(clienteModel.getNumConta())).thenReturn(Arrays.asList(transacaoModelDebito, transacaoModelDebito2));
        when(clienteService.existsByNumConta(transacaoModelDebito.getCcOrigem())).thenReturn(true);
        when(clienteService.existsByNumConta(transacaoModelDebito2.getCcOrigem())).thenReturn(false);

        when(transacaoRepository.findByCcOrigem(clienteModel.getNumConta())).thenReturn(Arrays.asList(transacaoModelCredito, transacaoModelCredito2));
        when(clienteService.existsByNumConta(transacaoModelCredito.getCcDestino())).thenReturn(true);
        when(clienteService.existsByNumConta(transacaoModelCredito2.getCcDestino())).thenReturn(false);
        
        transacaoService.deleteByClienteId(clienteModel);

        verify(transacaoRepository, never()).deleteById(transacaoModelDebito.getId());
        verify(transacaoRepository).deleteById(transacaoModelDebito2.getId()); 

        verify(transacaoRepository, never()).deleteById(transacaoModelCredito.getId());
        verify(transacaoRepository).deleteById(transacaoModelCredito2.getId()); 
    } 

    @Test
    void deleteById_DeleteValido_NaoDeveFazerNada() {
        UUID id = UUID.randomUUID();
        doNothing().when(transacaoRepository).deleteById(id);

        assertDoesNotThrow(() -> transacaoService.deleteById(id));
    }
}

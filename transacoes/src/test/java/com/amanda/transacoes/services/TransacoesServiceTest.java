package com.amanda.transacoes.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.TransacaoRepository;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;
    
    @Mock
    private ClienteService clienteService;
    
    @Mock
    private OperacaoService operacaoService;

    @InjectMocks
    private TransacaoService transacaoService;

    private TransacaoDto transacaoDto;

    private TransacaoDto transacaoDtoDebito;

    private TransacaoModel transacaoModel;

    @BeforeEach
    void setUp() {
        transacaoDto = new TransacaoDto("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        transacaoDtoDebito = new TransacaoDto("159001", "159002", 100.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        transacaoModel = new TransacaoModel("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
    }

    @Test
    void getAll_DeveRetornarListaDeTransacoes() {
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
    void getById_TransacaoExiste() {
        UUID id = UUID.randomUUID();
        when(transacaoRepository.findById(id)).thenReturn(Optional.of(transacaoModel));

        Optional<TransacaoModel> resultado = transacaoService.getById(id);

        assertTrue(resultado.isPresent());
        assertEquals("159001", resultado.get().getCcOrigem());
    }


    @Test
    void deleteById_ExcessaoTransacaoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(transacaoRepository.existsById(id)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.deleteById(id));
            
        assertEquals("404 NOT_FOUND \"Transação não encontrada.\"", exception.getMessage());
    }

    @Test
    void deleteById_DeleteValido() {
        UUID id = UUID.randomUUID();
        when(transacaoRepository.existsById(id)).thenReturn(true);
        doNothing().when(transacaoRepository).deleteById(id);
        assertDoesNotThrow(() -> transacaoService.deleteById(id));
    }
}

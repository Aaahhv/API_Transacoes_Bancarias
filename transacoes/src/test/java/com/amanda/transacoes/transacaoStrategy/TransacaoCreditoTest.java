package com.amanda.transacoes.transacaoStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.TransacaoRepository;
import com.amanda.transacoes.services.ClienteService;
import com.amanda.transacoes.services.DispositivoService;
import com.amanda.transacoes.services.OperacaoService;
import com.amanda.transacoes.validators.TransacaoValidator;

@ExtendWith(MockitoExtension.class)
public class TransacaoCreditoTest {
    
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
    private TransacaoCredito transacaoCredito;

    private TransacaoDto transacaoDtoCredito;

    @BeforeEach
    void setUp() {
        transacaoDtoCredito = new TransacaoDto("159002", "159001", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.TED, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
    }

    @Test
    void createTransacao_TransacaoSaqueValida_DeveCriarComSucesso() {
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
    
        TransacaoModel result = transacaoCredito.createTransacao(transacaoDtoCredito);

        assertEquals(transacaoDtoCredito.getCcOrigem(),result.getCcOrigem());
        verify(clienteService, never()).debitar(any(), anyDouble(), anyDouble());
        verify(clienteService).creditar(transacaoDtoCredito.getCcOrigem(), transacaoDtoCredito.getValor(),operacaoService.getTaxaOperacao(transacaoDtoCredito.getTipoOperacao()));
    }

    @Test
    void createTransacao_TransacaoTedValidaOrigemEstrangeira_DeveCriarComSucesso() {
        transacaoDtoCredito.setCcOrigem("123");
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.TED);

        TransacaoModel result = transacaoCredito.createTransacao(transacaoDtoCredito);

        assertEquals(transacaoDtoCredito.getCcDestino(),result.getCcDestino());
        verify(clienteService, times(1)).debitar(anyString(), anyDouble(), anyDouble());
        verify(clienteService).debitar(transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getValor(),operacaoService.getTaxaOperacao(transacaoDtoCredito.getTipoOperacao()));
        verify(clienteService, never()).creditar(any(), anyDouble(), anyDouble());
    }

    @Test
    void createTransacao_TransacaoTedValidaDestinoEstrangeira_DeveCriarComSucesso() {
        transacaoDtoCredito.setCcDestino("123");
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.TED);

        TransacaoModel result = transacaoCredito.createTransacao(transacaoDtoCredito);

        assertEquals(transacaoDtoCredito.getCcDestino(),result.getCcDestino());
        verify(clienteService, times(1)).creditar(anyString(), anyDouble(), anyDouble());
        verify(clienteService).creditar(transacaoDtoCredito.getCcOrigem(), transacaoDtoCredito.getValor(), 0);
        verify(clienteService, never()).debitar(any(), anyDouble(), anyDouble());
    }
}

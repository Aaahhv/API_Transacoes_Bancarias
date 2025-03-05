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
        transacaoDto = new TransacaoDto("1590001", "1590002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX);
        transacaoDtoDebito = new TransacaoDto("1590001", "1590002", 100.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED);
        transacaoModel = new TransacaoModel("1590001", "1590002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX);
    }

    @Test
    void create_TransacaoCreditoValida() {
        TransacaoModel transacaoModel = new TransacaoModel("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX);
        when(transacaoRepository.save(any(TransacaoModel.class))).thenReturn(transacaoModel);
        when(operacaoService.isOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(transacaoDto.getTipoOperacao(), transacaoDto.getValor())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(),any())).thenReturn(true);
        when(clienteService.isClienteAtivo(transacaoDto.getCcOrigem())).thenReturn(true);
        when(clienteService.isClienteAtivo(transacaoDto.getCcDestino())).thenReturn(true);

        TransacaoModel resultado = transacaoService.create(transacaoDto);
        assertNotNull(resultado);

        assertEquals("159001", resultado.getCcOrigem());
        verify(transacaoRepository, times(1)).save(any(TransacaoModel.class));
    }

    @Test
    void create_ExcessaoValorMenorQueZero() {
        transacaoDto.setValor(0);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.create(transacaoDto));
        assertEquals("400 BAD_REQUEST \"O valor da transação deve ser maior que zero.\"", exception.getMessage());
    }

    @Test
    void create_ExcessaoOperacaoInativa() {
        when(operacaoService.isOperacaoAtiva(any())).thenReturn(false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.create(transacaoDto));
        assertEquals("400 BAD_REQUEST \"Operação inativa.\"", exception.getMessage());
    }

    @Test
    void create_ExcessaoValorExcedeLimite() {
        when(operacaoService.isOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(any(), anyDouble())).thenReturn(false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.create(transacaoDto));
        assertEquals("400 BAD_REQUEST \"Valor da transação excede o limite de 0.0 da operacao PIX.\"", exception.getMessage());
    }

    @Test
    void create_ExcessaoHorarioInvalido() {
        when(operacaoService.isOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(false);
        when(operacaoService.isLimiteValorValido(transacaoDto.getTipoOperacao(), transacaoDto.getValor())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.create(transacaoDto));
        assertEquals("400 BAD_REQUEST \"Horario inválido para a operação\"", exception.getMessage());
    }

    @Test
    void create_ExcessaoNenhumaContaNaoPertenceInstituicao() {
        when(operacaoService.isOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(any(), anyDouble())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(true);
        transacaoDto.setCcOrigem("123456");
        transacaoDto.setCcDestino("654321");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.create(transacaoDto));
        assertEquals("400 BAD_REQUEST \"Nenhuma conta nessa transação pertence a nossa instituição.\"", exception.getMessage());
    }

    @Test
    void create_ExcessaoContaOrigemVazia() {
        when(operacaoService.isOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(transacaoDtoDebito.getTipoOperacao(), transacaoDtoDebito.getValor())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(),any())).thenReturn(true);
        transacaoDtoDebito.setCcOrigem("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.create(transacaoDtoDebito));
        assertEquals("400 BAD_REQUEST \"A conta de origem não deve ser vazia.\"", exception.getMessage());
    }

    
    @Test
    void create_ExcessaoContaDestinoVazia() {
        when(operacaoService.isOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(transacaoDtoDebito.getTipoOperacao(), transacaoDtoDebito.getValor())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(),any())).thenReturn(true);
        transacaoDtoDebito.setCcDestino("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.create(transacaoDtoDebito));
        assertEquals("400 BAD_REQUEST \"A conta de destino não deve ser vazia.\"", exception.getMessage());
    }

/*         
    @Test
    void create_ExcessaoContaOrigemIgualDestino() {
        when(operacaoService.isOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(transacaoDtoDebito.getTipoOperacao(), transacaoDtoDebito.getValor())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(),any())).thenReturn(true);
        transacaoDtoDebito.setCcDestino("159001");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.create(transacaoDtoDebito));
        assertEquals("Nao é possivel enviar TED para a mesma conta.\"", exception.getMessage());
    } */

    @Test
    void create_ExcessaoContaOrigemInativa() {
        when(operacaoService.isOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(transacaoDto.getTipoOperacao(), transacaoDto.getValor())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(),any())).thenReturn(true);
        when(clienteService.isClienteAtivo(anyString())).thenReturn(false);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.create(transacaoDto));
        assertEquals("400 BAD_REQUEST \"Conta de ORIGEM inativa.\"", exception.getMessage());
    }

    @Test
    void operacaoCredito_Valido() {
        when(transacaoRepository.save(any())).thenReturn(transacaoModel);
        when(clienteService.isClienteAtivo(transacaoDto.getCcOrigem())).thenReturn(true);
        when(clienteService.isClienteAtivo(transacaoDto.getCcDestino())).thenReturn(true);
        TransacaoModel result = transacaoService.operacaoCredito(transacaoDto);
        assertNotNull(result);
    }

    
    @Test
    void operacaoCredito_ThrowsException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Número de conta 1590001 não encontrado."))
                .when(clienteService).creditar(any(), anyDouble(), anyDouble());

        when(clienteService.isClienteAtivo(transacaoDto.getCcOrigem())).thenReturn(true);
        //when(clienteService.isClienteAtivo(transacaoDto.getCcDestino())).thenReturn(true);
        
        
        //transacaoDto.setCcOrigem("111111");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            transacaoService.operacaoCredito(transacaoDto));
        
        assertEquals("404 NOT_FOUND \"Número de conta 1590001 não encontrado.\"", exception.getMessage());
    }
    

    @Test
    void operacaoDebito_Valido() {
        when(clienteService.isClienteAtivo(transacaoDto.getCcOrigem())).thenReturn(true);
        when(clienteService.isClienteAtivo(transacaoDto.getCcDestino())).thenReturn(true);
        when(transacaoRepository.save(any())).thenReturn(transacaoModel);
        TransacaoModel result = transacaoService.operacaoDebito(transacaoDto);
        assertNotNull(result);
    }

    @Test
    void operacaoCreditoDeposito_Valido() {
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(operacaoService.getTaxaOperacao(any())).thenReturn(0.0);
        when(transacaoRepository.save(any())).thenReturn(transacaoModel);
        TransacaoModel result = transacaoService.operacaoCreditoDeposito(transacaoDto);
        assertNotNull(result);
    }

    @Test
    void operacaoCreditoDeposito_ExcessaoContaOrigemInativa() {
        transacaoDto.setOperacao(OperacaoEnum.CREDITO);
        transacaoDto.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        when(clienteService.isClienteAtivo(any())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.operacaoCreditoDeposito(transacaoDto));
        
        assertEquals("400 BAD_REQUEST \"Conta de origem inativa.\"", exception.getMessage());
    }

    @Test
    void operacaoCreditoDeposito_ValidoComTaxa() {
        transacaoDto.setOperacao(OperacaoEnum.CREDITO);
        transacaoDto.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(operacaoService.getTaxaOperacao(any())).thenReturn(5.0);
        TransacaoModel transacaoModel = new TransacaoModel("159001", "", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.DEPOSITO);
        when(transacaoRepository.save(any())).thenReturn(transacaoModel);
        
        TransacaoModel result = transacaoService.operacaoCreditoDeposito(transacaoDto);
        
        assertNotNull(result);
        verify(clienteService, times(1)).creditar(transacaoDto.getCcOrigem(), transacaoDto.getValor(), 5.0);
    }

    @Test
    void operacaoDebitoSaque_Valido() {
        transacaoDto.setOperacao(OperacaoEnum.DEBITO);
        transacaoDto.setTipoOperacao(TipoOperacaoEnum.SAQUE);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(operacaoService.getTaxaOperacao(any())).thenReturn(0.0);
        when(transacaoRepository.save(any())).thenReturn(transacaoModel);
        TransacaoModel result = transacaoService.operacaoDebitoSaque(transacaoDto);
        assertNotNull(result);
    }

    @Test
    void operacaoDebitoSaque_ValidoComTaxa() {
        transacaoDto.setOperacao(OperacaoEnum.DEBITO);
        transacaoDto.setTipoOperacao(TipoOperacaoEnum.SAQUE);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(operacaoService.getTaxaOperacao(any())).thenReturn(5.0);
        TransacaoModel transacaoModel = new TransacaoModel("159001", "", 100.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.SAQUE);
        when(transacaoRepository.save(any())).thenReturn(transacaoModel);
        
        TransacaoModel result = transacaoService.operacaoDebitoSaque(transacaoDto);
        
        assertNotNull(result);
        verify(clienteService, times(1)).debitar(transacaoDto.getCcOrigem(), transacaoDto.getValor(), 5.0);
    }

    @Test
    void operacaoDebitoSaque_ExcessaoContaOrigemInativa() {
        transacaoDto.setOperacao(OperacaoEnum.DEBITO);
        transacaoDto.setTipoOperacao(TipoOperacaoEnum.SAQUE);
        when(clienteService.isClienteAtivo(any())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            transacaoService.operacaoDebitoSaque(transacaoDto));
        
        assertEquals("400 BAD_REQUEST \"Conta de origem inativa.\"", exception.getMessage());
    }





//====================================================
    @Test
    void getAll_DeveRetornarListaDeTransacoes() {
        TransacaoModel transacao1 = new TransacaoModel("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX);
        TransacaoModel transacao2 = new TransacaoModel("159003", "159004", 200.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED);
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
        TransacaoModel transacaoModel = new TransacaoModel("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX);
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

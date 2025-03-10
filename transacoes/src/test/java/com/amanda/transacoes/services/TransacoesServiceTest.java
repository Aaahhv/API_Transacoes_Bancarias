package com.amanda.transacoes.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
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

    @Mock
    private DispositivoService dispositivoService;

    @InjectMocks
    private TransacaoService transacaoService;

    private TransacaoDto transacaoDtoCredito;

    private TransacaoDto transacaoDtoDebito;

    private TransacaoModel transacaoModelCredito;



    @BeforeEach
    void setUp() {
        transacaoDtoCredito = new TransacaoDto("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        transacaoDtoDebito = new TransacaoDto("159001", "159002", 100.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        transacaoModelCredito = new TransacaoModel("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
          }
    

    @Test
    void create_TransacaoCreditoValida() {
        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(any(), anyDouble())).thenReturn(true);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(dispositivoService.isDispositivoAtivo(any())).thenReturn(true);
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); //faz com que, quando o método save() do transacaoRepository for chamado, ele retorne exatamente o mesmo objeto que foi passado como argumento.

        TransacaoModel transacao = transacaoService.create(transacaoDtoCredito);

        assertNotNull(transacao);
        assertEquals(OperacaoEnum.CREDITO, transacao.getOperacao());
        verify(transacaoRepository, times(1)).save(any());
    }
 
    @Test
    void create_TransacaoCreditoValida_ccOrigem111111() {
        transacaoDtoCredito.setOperacao(OperacaoEnum.CREDITO);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.TED);
        transacaoDtoCredito.setCcOrigem("111111");
        transacaoDtoCredito.setCcDestino("159002");

        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(any(), anyDouble())).thenReturn(true);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(dispositivoService.isDispositivoAtivo(any())).thenReturn(true);
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoModel transacao = transacaoService.create(transacaoDtoCredito);

        assertNotNull(transacao);
        assertEquals(OperacaoEnum.CREDITO, transacao.getOperacao());
        verify(transacaoRepository, times(1)).save(any());
    }

    @Test
    void create_TransacaoCreditoValida_ccDestino111111() {
        transacaoDtoCredito.setOperacao(OperacaoEnum.CREDITO);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.TED);
        transacaoDtoCredito.setCcOrigem("159001");
        transacaoDtoCredito.setCcDestino("111111");

        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(any(), anyDouble())).thenReturn(true);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(dispositivoService.isDispositivoAtivo(any())).thenReturn(true);
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoModel transacao = transacaoService.create(transacaoDtoCredito);

        assertNotNull(transacao);
        assertEquals(OperacaoEnum.CREDITO, transacao.getOperacao());
        verify(transacaoRepository, times(1)).save(any());
    }

    @Test
    void create_TransacaoDebitoValida_ccOrigem111111() {
        transacaoDtoDebito.setOperacao(OperacaoEnum.DEBITO);
        transacaoDtoDebito.setTipoOperacao(TipoOperacaoEnum.TED);
        transacaoDtoDebito.setCcOrigem("111111");
        transacaoDtoDebito.setCcDestino("159002");

        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(any(), anyDouble())).thenReturn(true);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(dispositivoService.isDispositivoAtivo(any())).thenReturn(true);
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoModel transacao = transacaoService.create(transacaoDtoDebito);

        assertNotNull(transacao);
        assertEquals(OperacaoEnum.DEBITO, transacao.getOperacao());
        verify(transacaoRepository, times(1)).save(any());
    }

    @Test
    void create_TransacaoDebitoValida_ccDestino111111() {
        transacaoDtoDebito.setOperacao(OperacaoEnum.DEBITO);
        transacaoDtoDebito.setTipoOperacao(TipoOperacaoEnum.TED);
        transacaoDtoDebito.setCcOrigem("159001");
        transacaoDtoDebito.setCcDestino("111111");

        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(any(), anyDouble())).thenReturn(true);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(dispositivoService.isDispositivoAtivo(any())).thenReturn(true);
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoModel transacao = transacaoService.create(transacaoDtoDebito);

        assertNotNull(transacao);
        assertEquals(OperacaoEnum.DEBITO, transacao.getOperacao());
        verify(transacaoRepository, times(1)).save(any());
    }

    @Test
    void create_TransacaoDebitoSaqueValida() {
        transacaoDtoDebito.setOperacao(OperacaoEnum.DEBITO);
        transacaoDtoDebito.setTipoOperacao(TipoOperacaoEnum.SAQUE);
        transacaoDtoDebito.setCcDestino("");

        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(any(), anyDouble())).thenReturn(true);
        when(operacaoService.getTaxaOperacao(any())).thenReturn(5.0);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(dispositivoService.isDispositivoAtivo(any())).thenReturn(true);
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoModel transacao = transacaoService.create(transacaoDtoDebito);

        assertNotNull(transacao);
        assertEquals(OperacaoEnum.DEBITO, transacao.getOperacao());
        verify(transacaoRepository, times(1)).save(any());
    }

    @Test
    void create_TransacaoCreditoDepositoValida() {
        transacaoDtoCredito.setOperacao(OperacaoEnum.CREDITO);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        transacaoDtoCredito.setCcDestino("");

        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(true);
        when(operacaoService.isLimiteValorValido(any(), anyDouble())).thenReturn(true);
        when(operacaoService.getTaxaOperacao(any())).thenReturn(5.0);
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        when(dispositivoService.isDispositivoAtivo(any())).thenReturn(true);
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoModel transacao = transacaoService.create(transacaoDtoCredito);

        assertNotNull(transacao);
        assertEquals(OperacaoEnum.CREDITO, transacao.getOperacao());
        verify(transacaoRepository, times(1)).save(any());
    }
/* 
    @Test
    void validarHorario_ExcessaoHorarioInvalido() {
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarHorario(transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Horario inválido para a operação\"", exception.getMessage());
    }

    @Test
    void validarValor_ExcessaoValorNegativo() {
        transacaoDtoCredito.setValor(-50.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarValor(transacaoDtoCredito.getTipoOperacao(), transacaoDtoCredito.getValor()));
        assertEquals("400 BAD_REQUEST \"O valor da transação deve ser maior que zero.\"", exception.getMessage());
    }

    @Test
    void validarValor_ExcessaoValorExcedeLimiteDeValor() {
        transacaoDtoCredito.setValor(50.0);
        when(operacaoService.isLimiteValorValido(transacaoDtoCredito.getTipoOperacao(), transacaoDtoCredito.getValor())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarValor(transacaoDtoCredito.getTipoOperacao(), transacaoDtoCredito.getValor()));
        assertEquals("400 BAD_REQUEST \"O valor da transação excede o limite do tipo de operação.\"", exception.getMessage());
    }

    @Test
    void validarOperacao_ExcessaoOperacaoInativa() {
        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(false);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarOperacao(transacaoDtoDebito.getOperacao(),  transacaoDtoDebito.getTipoOperacao()));
        assertEquals("403 FORBIDDEN \"Tipo de operação inativo.\"", exception.getMessage());
    }

    @Test
    void validarOperacao_ExcessaoCreditoNaoPodeSerSaque() {
        transacaoDtoCredito.setOperacao(OperacaoEnum.CREDITO);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.SAQUE);
        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(true);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarOperacao(transacaoDtoCredito.getOperacao(),  transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Operacao de CREDITO não pode ser do tipo SAQUE.\"", exception.getMessage());
    }

    @Test
    void validarOperacao_ExcessaoDebitoNaoPodeSerDeposito() {
        transacaoDtoDebito.setOperacao(OperacaoEnum.DEBITO);
        transacaoDtoDebito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(true);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarOperacao(transacaoDtoDebito.getOperacao(),  transacaoDtoDebito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Operacao de DEBITO não pode ser do tipo DEPOSITO.\"", exception.getMessage());
    }

    @Test
    void validarContas_DepositoSaque_ExcessaoContaOrigemNaoPertenceBanco() {
        transacaoDtoCredito.setCcOrigem("123456");
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarContas(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"A conta não pertence ao banco\"", exception.getMessage());
    }

    @Test
    void validarContas_DepositoSaque_ExcessaoContaOrigemInativa() {
        when(clienteService.isClienteAtivo(any())).thenReturn(false);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarContas(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("403 FORBIDDEN \"Conta de origem inativa.\"", exception.getMessage());
    }

    @Test
    void validarContas_DepositoSaque_ExcessaoContaDestinoNaoVazia() {
        when(clienteService.isClienteAtivo(any())).thenReturn(true);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        transacaoDtoCredito.setCcDestino("1590003");
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarContas(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"No tipo de operacao DEPOSITO a conta de destino deve ser vazia.\"", exception.getMessage());
    }

    @Test
    void validarContas_ExcessaoContaOrigemVazia() {
        transacaoDtoCredito.setCcOrigem("");
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.PIX);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarContas(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"A conta de origem não deve ser vazia.\"", exception.getMessage());
    }

    @Test
    void validarContas_ExcessaoContaDestinoVaziaParaOutrosTipos() {
        transacaoDtoCredito.setCcDestino("");
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.PIX);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarContas(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"A conta de destino não deve ser vazia.\"", exception.getMessage());
    }

    @Test
    void validarContas_ExcessaoNenhumaContaPertenceBanco() {
        transacaoDtoDebito.setCcOrigem("123456");
        transacaoDtoDebito.setCcDestino("654321");
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarContas(transacaoDtoDebito.getCcOrigem(),transacaoDtoDebito.getCcDestino(), transacaoDtoDebito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Nenhuma conta nessa transação pertence a nossa instituição.\"", exception.getMessage());
    }

    @Test
    void validarContas_ExcessaoContaOrigemInativa() {
        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcOrigem())).thenReturn(false);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarContas(transacaoDtoDebito.getCcOrigem(),transacaoDtoDebito.getCcDestino(), transacaoDtoDebito.getTipoOperacao()));
        assertEquals("403 FORBIDDEN \"Conta de origem inativa.\"", exception.getMessage());
    }

    @Test
    void validarContas_ExcessaoContaDestinoInativa() {
        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcOrigem())).thenReturn(true);
        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcDestino())).thenReturn(false);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarContas(transacaoDtoDebito.getCcOrigem(),transacaoDtoDebito.getCcDestino(), transacaoDtoDebito.getTipoOperacao()));
        assertEquals("403 FORBIDDEN \"Conta de destino inativa.\"", exception.getMessage());
    }

    @Test
    void validarContas_ExcessaoMesmaContaOrigemEDestino() {
        transacaoDtoDebito.setTipoOperacao(TipoOperacaoEnum.PIX);
        transacaoDtoDebito.setCcOrigem("1590001");
        transacaoDtoDebito.setCcDestino("1590001");

        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcOrigem())).thenReturn(true);
        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcDestino())).thenReturn(true);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarContas(transacaoDtoDebito.getCcOrigem(),transacaoDtoDebito.getCcDestino(), transacaoDtoDebito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Nao é possivel enviar PIX para a mesma conta.\"", exception.getMessage());
    }

    @Test
    void validarDispositivo_ExcessaoDispositivoInativo() {
        UUID dispositivoId = UUID.randomUUID();
        when(dispositivoService.isDispositivoAtivo(dispositivoId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoService.validarDispositivo(dispositivoId));
        assertEquals("403 FORBIDDEN \"Dispositivo inativo\"", exception.getMessage());
    }
*/
    @Test
    void getAll_RetornaListaDeTransacoes() {
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
        when(transacaoRepository.findById(id)).thenReturn(Optional.of(transacaoModelCredito));

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

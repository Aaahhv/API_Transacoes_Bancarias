package com.amanda.transacoes.validators;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.models.DispositivoModel;
import com.amanda.transacoes.repositories.TransacaoRepository;
import com.amanda.transacoes.services.ClienteService;
import com.amanda.transacoes.services.DispositivoService;
import com.amanda.transacoes.services.OperacaoService;

@ExtendWith(MockitoExtension.class)
public class TransacaoValidatorTest {
    @Mock
    private TransacaoRepository transacaoRepository;
    
    @Mock
    private ClienteService clienteService;
    
    @Mock
    private OperacaoService operacaoService;

    @Mock
    private DispositivoService dispositivoService;

    @InjectMocks
    private TransacaoValidator transacaoValidator;

    private TransacaoDto transacaoDtoCredito;

    private TransacaoDto transacaoDtoDebito;

    private DispositivoModel dispositivoModel;
    
    private ClienteModel clienteModel;

    private UUID clienteId;
    private UUID dispositivoId;

    @BeforeEach
    void setUp() {
        transacaoDtoCredito = new TransacaoDto("159001", "159002", 100.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
        transacaoDtoDebito = new TransacaoDto("159001", "159002", 100.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4"));
    
        clienteId = UUID.fromString("34fbe2a0-9815-4c35-a2ba-0c1fcccf66b2");
        dispositivoId = UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4");
        dispositivoModel = new DispositivoModel("Dispositivo Teste", true, clienteId); 
        dispositivoModel.setId(dispositivoId);
        clienteModel = new ClienteModel("Amanda Souza", "591.460.470-26", "159001", true, 1000.0);   
        clienteModel.setId(clienteId);
    
    }

    @Test
    void validateCreate_TransacaoValida_DeveCriarTransacao() {
        when(operacaoService.isTipoDeOperacaoAtiva(TipoOperacaoEnum.PIX)).thenReturn(true);
        when(clienteService.isClienteAtivo("159001")).thenReturn(true);
        when(clienteService.isClienteAtivo("159002")).thenReturn(true);

        when(dispositivoService.getById(dispositivoModel.getId())).thenReturn(Optional.of(dispositivoModel));
        when(clienteService.getById(clienteModel.getId())).thenReturn(Optional.of(clienteModel));
        when(dispositivoService.isDispositivoAtivo(dispositivoModel.getId())).thenReturn(true);

        when(operacaoService.isLimiteValorValido(TipoOperacaoEnum.PIX, 100.0)).thenReturn(true);
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(true);
    
        assertDoesNotThrow(() -> transacaoValidator.validateCreate(transacaoDtoCredito));
    }
        
    @Test
    void validarHorario_ExcessaoHorarioInvalido() {
        when(operacaoService.isHorarioValido(any(), any())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarHorario(transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Horario inválido para a operação\"", exception.getMessage());
    }

    @Test
    void validarValor_ExcessaoValorNegativo() {
        transacaoDtoCredito.setValor(-50.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarValor(transacaoDtoCredito.getTipoOperacao(), transacaoDtoCredito.getValor()));
        assertEquals("400 BAD_REQUEST \"O valor da transação deve ser maior que zero.\"", exception.getMessage());
    }

    @Test
    void validarValor_ExcessaoValorExcedeLimiteDeValor() {
        transacaoDtoCredito.setValor(50.0);
        when(operacaoService.isLimiteValorValido(transacaoDtoCredito.getTipoOperacao(), transacaoDtoCredito.getValor())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarValor(transacaoDtoCredito.getTipoOperacao(), transacaoDtoCredito.getValor()));
        assertEquals("400 BAD_REQUEST \"O valor da transação excede o limite do tipo de operação.\"", exception.getMessage());
    }

    @Test
    void validarOperacao_OperacaoDebitoDeposito_DeveFazerNada() {
        transacaoDtoDebito.setOperacao(OperacaoEnum.DEBITO);
        transacaoDtoDebito.setTipoOperacao(TipoOperacaoEnum.SAQUE);
        when(operacaoService.isTipoDeOperacaoAtiva(TipoOperacaoEnum.SAQUE)).thenReturn(true);

        assertDoesNotThrow(() -> transacaoValidator.validarOperacao(transacaoDtoDebito.getOperacao(),  transacaoDtoDebito.getTipoOperacao()));
    }

    @Test
    void validarOperacao_ExcessaoOperacaoInativa() {
        when(operacaoService.isTipoDeOperacaoAtiva(any())).thenReturn(false);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarOperacao(transacaoDtoDebito.getOperacao(),  transacaoDtoDebito.getTipoOperacao()));
        assertEquals("403 FORBIDDEN \"Tipo de operação inativo.\"", exception.getMessage());
    }

    @Test
    void validarOperacao_ExcessaoCreditoNaoPodeSerSaque() {
        transacaoDtoCredito.setOperacao(OperacaoEnum.CREDITO);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.SAQUE);
        when(operacaoService.isTipoDeOperacaoAtiva(TipoOperacaoEnum.SAQUE)).thenReturn(true);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarOperacao(transacaoDtoCredito.getOperacao(),  transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Operacao de CREDITO não pode ser do tipo SAQUE.\"", exception.getMessage());
    }


    @Test
    void validarOperacao_ExcessaoDebitoNaoPodeSerDeposito() {
        transacaoDtoDebito.setOperacao(OperacaoEnum.DEBITO);
        transacaoDtoDebito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        when(operacaoService.isTipoDeOperacaoAtiva(TipoOperacaoEnum.DEPOSITO)).thenReturn(true);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarOperacao(transacaoDtoDebito.getOperacao(),  transacaoDtoDebito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Operacao de DEBITO não pode ser do tipo DEPOSITO.\"", exception.getMessage());
    }

    @Test
    void validarContas_InformacoesdeContaValida_DeveValidarConta() {
        transacaoDtoCredito.setCcOrigem("159000");
        transacaoDtoCredito.setCcDestino("");
        when(clienteService.isClienteAtivo("159000")).thenReturn(true);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.SAQUE);
        
        assertDoesNotThrow(() -> transacaoValidator.validarContas(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
    }

    @Test
    void validarContaDepositoSaque_OrigemNaoPertenceAoBanco_DeveLancarExcessao() {
        transacaoDtoCredito.setCcOrigem("123456");
        transacaoDtoCredito.setCcDestino("");
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarContas(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"A conta não pertence ao banco\"", exception.getMessage());
    }

    @Test
    void validarContaDepositoSaque_ContaDeOrigemInativa_DeveLancarExcessao() {
        when(clienteService.isClienteAtivo(transacaoDtoCredito.getCcOrigem())).thenReturn(false);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarContas(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("403 FORBIDDEN \"Conta de origem inativa.\"", exception.getMessage());
    }
 
    @Test
    void validarContaDepositoSaque_ContaDestinoNaoVaziaNoDepositoSaque_DeveLancarExcessao() {
        when(clienteService.isClienteAtivo(transacaoDtoCredito.getCcOrigem())).thenReturn(true);
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);
        transacaoDtoCredito.setCcDestino("1590003");
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarContas(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"No tipo de operacao DEPOSITO a conta de destino deve ser vazia.\"", exception.getMessage());
    }

    @Test
    void validarContaPixTedDoc_ContaOrigemVazia_DeveLancarExcessao() {
        transacaoDtoCredito.setCcOrigem("");
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.PIX);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarContaPixTedDoc(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"A conta de origem não deve ser vazia.\"", exception.getMessage());
    }

    @Test
    void validarContaPixTedDoc_ContaDestinoVazia_DeveLancarExcessao() {
        transacaoDtoCredito.setCcDestino("");
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.PIX);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarContaPixTedDoc(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"A conta de destino não deve ser vazia.\"", exception.getMessage());
    }
    

    @Test
    void validarContaPixTedDoc_ContaDeDestinoNaoPertenceAoBanco_DevePassar() {
        transacaoDtoCredito.setCcOrigem("1590001");  
        transacaoDtoCredito.setCcDestino("1234567"); 

        when(clienteService.isClienteAtivo(transacaoDtoCredito.getCcOrigem())).thenReturn(true);

        assertDoesNotThrow(() -> transacaoValidator.validarContaPixTedDoc(transacaoDtoCredito.getCcOrigem(), transacaoDtoCredito.getCcDestino(), transacaoDtoCredito.getTipoOperacao()
        ));
    }

    @Test
    void validarContaPixTedDoc_ContaDeOrigemNaoPertenceAoBanco_DevePassar() {
        transacaoDtoCredito.setCcOrigem("123456");  
        transacaoDtoCredito.setCcDestino("1590001"); 

        when(clienteService.isClienteAtivo(transacaoDtoCredito.getCcDestino())).thenReturn(true);

        assertDoesNotThrow(() -> transacaoValidator.validarContaPixTedDoc(transacaoDtoCredito.getCcOrigem(),transacaoDtoCredito.getCcDestino(),transacaoDtoCredito.getTipoOperacao()
        ));
    }

    @Test
    void validarContaPixTedDoc_NenhumaContaPertenceAoBanco_DeveLancarExcessao() {
        transacaoDtoDebito.setCcOrigem("123456");
        transacaoDtoDebito.setCcDestino("654321");
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarContaPixTedDoc(transacaoDtoDebito.getCcOrigem(),transacaoDtoDebito.getCcDestino(), transacaoDtoDebito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Nenhuma conta nessa transação pertence a nossa instituição.\"", exception.getMessage());
    }

    @Test
    void validarContaPixTedDoc_ContaDeOrigemInativa_DeveLancarExcessao() {
        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcOrigem())).thenReturn(false);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarContaPixTedDoc(transacaoDtoDebito.getCcOrigem(),transacaoDtoDebito.getCcDestino(), transacaoDtoDebito.getTipoOperacao()));
        assertEquals("403 FORBIDDEN \"Conta de origem inativa.\"", exception.getMessage());
    }

    @Test
    void validarContaPixTedDoc_ContaDestinoInativa_DeveLancarExcessao() {
        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcOrigem())).thenReturn(true);
        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcDestino())).thenReturn(false);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarContaPixTedDoc(transacaoDtoDebito.getCcOrigem(),transacaoDtoDebito.getCcDestino(), transacaoDtoDebito.getTipoOperacao()));
        assertEquals("403 FORBIDDEN \"Conta de destino inativa.\"", exception.getMessage());
    }

    @Test
    void validarContaPixTedDoc_MesmaContaDeOrigemEDestino_DeveLancarExcessao() {
        transacaoDtoDebito.setTipoOperacao(TipoOperacaoEnum.PIX);
        transacaoDtoDebito.setCcOrigem("1590001");
        transacaoDtoDebito.setCcDestino("1590001");

        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcOrigem())).thenReturn(true);
        when(clienteService.isClienteAtivo(transacaoDtoDebito.getCcDestino())).thenReturn(true);
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarContaPixTedDoc(transacaoDtoDebito.getCcOrigem(),transacaoDtoDebito.getCcDestino(), transacaoDtoDebito.getTipoOperacao()));
        assertEquals("400 BAD_REQUEST \"Nao é possivel enviar PIX para a mesma conta.\"", exception.getMessage());
    }
/* 
    @Test
    void validarDispositivo_OperacaoSaqueDispositivoValido_DevePassar() {
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.SAQUE);

        when(dispositivoService.isDispositivoAtivo(dispositivoId)).thenReturn(true);

        assertDoesNotThrow(() -> transacaoValidator.validarDispositivo(transacaoDtoCredito));
    }

    @Test
    void validarDispositivo_OperacaoDepositoDispositivoInvalido_DeveLancarExcessao() {
        transacaoDtoCredito.setTipoOperacao(TipoOperacaoEnum.DEPOSITO);

        when(dispositivoService.isDispositivoAtivo(dispositivoId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validarDispositivo(transacaoDtoCredito));

        assertEquals("403 FORBIDDEN \"Caixa eletrônico inativo\"", exception.getMessage());
    }
*/
    @Test
    void validateDeleteById_IdInvalido_DeveLancarExcessao() {
        UUID transacaoId = UUID.randomUUID();
        when(transacaoRepository.existsById(transacaoId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transacaoValidator.validateDeleteById(transacaoId));
        assertEquals("404 NOT_FOUND \"Transação não encontrada.\"", exception.getMessage());
    }

    @Test
    void validateDeleteById_IdValido_NaoDeveFazerNada() {
        UUID transacaoId = UUID.randomUUID();
        when(transacaoRepository.existsById(transacaoId)).thenReturn(true);

        assertDoesNotThrow(() -> transacaoValidator.validateDeleteById(transacaoId));
    }
}

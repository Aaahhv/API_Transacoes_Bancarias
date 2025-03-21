package com.amanda.transacoes.services.relatorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amanda.transacoes.dtos.PeriodoDataDto;
import com.amanda.transacoes.dtos.relatorios.ClienteETiposOperacaoDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.repositories.TransacaoRepository;

@ExtendWith(MockitoExtension.class)
public class InformacoesDeClientesServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;
    
    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private InformacoesDeClienteService informacoesDeClienteService;

    private List<TransacaoModel> transacoes;

    @BeforeEach
    void setUp() {
        transacoes = new ArrayList<>();
        transacoes.add(new TransacaoModel("159001", "159002", 10.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159001", "159003", 20.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.TED, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159004", "", 40.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.DEPOSITO, SituacaoOperacaoEnum.CANCELADO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159002", "", 80.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.SAQUE, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159002", "159001", 160, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED, SituacaoOperacaoEnum.PENDENTE, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("XXXXXX", "159001", 320, OperacaoEnum.DEBITO, TipoOperacaoEnum.DOC, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159004", null, 6400.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.DEPOSITO, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.get(0).setDataTransacao(LocalDateTime.of(2024, 3, 15, 12, 30));
        transacoes.get(1).setDataTransacao(LocalDateTime.of(2024, 4, 15, 12, 30));
        transacoes.get(2).setDataTransacao(LocalDateTime.of(2024, 5, 15, 12, 30));
        transacoes.get(3).setDataTransacao(LocalDateTime.of(2024, 6, 15, 12, 30));
        transacoes.get(4).setDataTransacao(LocalDateTime.of(2024, 7, 15, 12, 30));
        transacoes.get(5).setDataTransacao(LocalDateTime.of(2024, 8, 15, 12, 30));
        transacoes.get(6).setDataTransacao(LocalDateTime.of(2024, 9, 15, 12, 30));

        List<ClienteModel> clientes = new ArrayList<>();
        
        ClienteModel cliente1 = new ClienteModel("Amanda Souza", "591.460.470-26", "159001", true, 1000.0);
        ClienteModel cliente2 = new ClienteModel("Yuri", "619.279.430-86", "159002", true, 1000.0);
        ClienteModel cliente3 = new ClienteModel("Gabriel Ferreira", "970.399.040-12", "159003", true, 1000.0);
        ClienteModel cliente4 = new ClienteModel("Caneca Azul", "695.390.250-79", "159004", true, 1000.0);
        
        clientes.add(cliente1);
        clientes.add(cliente2);
        clientes.add(cliente3);
        clientes.add(cliente4);

        lenient().when(transacaoRepository.findAll()).thenReturn(transacoes);
        lenient().when(clienteRepository.findAll()).thenReturn(clientes);
        lenient().when(clienteRepository.existsByNumConta("159001")).thenReturn(true);
        lenient().when(clienteRepository.existsByNumConta("159002")).thenReturn(true);
        lenient().when(clienteRepository.existsByNumConta("159003")).thenReturn(true);
        lenient().when(clienteRepository.existsByNumConta("159004")).thenReturn(true);
        lenient().when(clienteRepository.existsByNumConta("XXXXXX")).thenReturn(false);
        lenient().when(clienteRepository.findByNumConta("159001")).thenReturn(Optional.of(cliente1));
        lenient().when(clienteRepository.findByNumConta("159002")).thenReturn(Optional.of(cliente2));
        lenient().when(clienteRepository.findByNumConta("159003")).thenReturn(Optional.of(cliente3));
        lenient().when(clienteRepository.findByNumConta("159004")).thenReturn(Optional.of(cliente4));
    }

    
    @Test
    void getSaldoBanco_DeveRetornarSomaDosSaldosDosClientes() {
        Map<String, Double> saldo = informacoesDeClienteService.getSaldoBanco();

        assertEquals(4000.0, saldo.get("Saldo do banco"));
    }

    @Test
    void getQuantidadeClientesAtivos_DeveRetornarQuantidadeCorreta() {
        Map<String, Integer> quantidade = informacoesDeClienteService.getQuantidadeClientesAtivos();

        assertEquals(4, quantidade.get("Quantidade de clientes ativos"));
    }

    @Test
    void getQuantidadeDeTipoOperacaoPorCliente_DeveRetornarQuantidadePorCliente() {
        List<ClienteETiposOperacaoDto> relatorioDto = informacoesDeClienteService.getQuantidadeDeTipoOperacaoPorCliente();

        for(ClienteETiposOperacaoDto clienteDto : relatorioDto){
            if(clienteDto.getCliente().getNumConta().equals("159001")){
                
            assertEquals(2, clienteDto.getTipoOperacao().get(TipoOperacaoEnum.TED));
            assertEquals(1, clienteDto.getTipoOperacao().get(TipoOperacaoEnum.PIX));
            assertEquals(1, clienteDto.getTipoOperacao().get(TipoOperacaoEnum.DOC));
            }
        }
        assertEquals(4, relatorioDto.size());
    }

    @Test
    void getExtratoClienteComFiltro_DeveRetornarTransacoesFiltradas() {
        Map<String, List<TransacaoModel>> resultado = informacoesDeClienteService.getExtratoClienteComFiltro("159001", OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX);

        assertFalse(resultado.get("159001").isEmpty());
        assertEquals(1, resultado.get("159001").size());
    }

    /* 
    @Test
    void getClienteCincoMilPorMes_DeveRetornarClientesComTransacoesAcimaDe5000() {
        Map<YearMonth, List<ClienteEValorDto>> resultado = informacoesDeClienteService.getClientesCincoMilNoMes();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size()); 
    }*/

    @Test
    void getExtratoClientePorDia_DeveRetornarExtratoOrdenado() {
        PeriodoDataDto periodo = new PeriodoDataDto(LocalDateTime.of(2024, 4, 15, 12, 30), LocalDateTime.of(2024, 8, 15, 12, 30));

        Map<String, Map<LocalDate, List<TransacaoModel>>> resultado = informacoesDeClienteService.getExtratoClientePorDiaDurantePeriodo("159001", periodo);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.containsKey("159001"));
    }
/* 
    @Test
    void getExtratoPorConta_DeveRetornarTransacoesDaConta() {
        List<TransacaoModel> extrato = informacoesDeClienteService.getExtratoPorConta("159001");

        assertFalse(extrato.isEmpty());
    }*/

}

package com.amanda.transacoes.services.relatorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.amanda.transacoes.dtos.relatorios.RelatorioOperacionalDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.services.TransacaoService;

@ExtendWith(MockitoExtension.class)
public class InformacoesOperacionaisServiceTest {

    @Mock
    private TransacaoService transacaoService;

    @InjectMocks
    private InformacoesOperacionaisService informacoesOperacionaisService;

    private YearMonth mes;

    @BeforeEach
    void setUp() {
        List<TransacaoModel> transacoes = new ArrayList<>();
        transacoes.add(new TransacaoModel("159001", "159002", 5, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159001", "159002", 10.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.PIX, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159001", "159003", 20.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.TED, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159004", "", 40.0, OperacaoEnum.CREDITO, TipoOperacaoEnum.DEPOSITO, SituacaoOperacaoEnum.CANCELADO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159002", "", 80.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.SAQUE, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159002", "159001", 160, OperacaoEnum.DEBITO, TipoOperacaoEnum.TED, SituacaoOperacaoEnum.PENDENTE, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("XXXXXX", "159001", 320, OperacaoEnum.DEBITO, TipoOperacaoEnum.DOC, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159008", "", 640.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.DEPOSITO, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
        transacoes.add(new TransacaoModel("159004", "", 1280.0, OperacaoEnum.DEBITO, TipoOperacaoEnum.DEPOSITO, SituacaoOperacaoEnum.CONCLUIDO, UUID.fromString("b3f8c1e6-5e3a-4a0b-9b34-3f2c1b37a9e4")));
    
        mes = YearMonth.of(2024, 12);
        when(transacaoService.findByYearMonthBetween(mes)).thenReturn(transacoes.subList(1, 9));
        
    }
 
    @Test
    @DisplayName("Nesse teste, o relatorio deve conter apenas operacoes marcadas com SituacaoOperacaoEnum.CONCLUIDO")
    void testGetOperacoes_TransacoesExistem_DeveRetornarRelatorioCorreto() {
        RelatorioOperacionalDto relatorio = informacoesOperacionaisService.getOperacoes(mes);

        assertEquals(2, relatorio.getRelatorioOperacionalDto().get("operacao").get("CREDITO").getQuantidade());
        assertEquals(30.0, relatorio.getRelatorioOperacionalDto().get("operacao").get("CREDITO").getValor());

        assertEquals(4, relatorio.getRelatorioOperacionalDto().get("operacao").get("DEBITO").getQuantidade());
        assertEquals(2320.0, relatorio.getRelatorioOperacionalDto().get("operacao").get("DEBITO").getValor());
    }

    @Test
    @DisplayName("Nesse teste, o relatorio deve conter apenas operacoes marcadas com SituacaoOperacaoEnum.CONCLUIDO")
    void testGetTipoOperacoes_TransacoesExistem_DeveRetornarRelatorioCorreto() {
        RelatorioOperacionalDto relatorio = informacoesOperacionaisService.getTipoOperacoes(mes);

        assertEquals(1, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("SAQUE").getQuantidade());
        assertEquals(80.0, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("SAQUE").getValor());

        assertEquals(1, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("PIX").getQuantidade());
        assertEquals(10.0, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("PIX").getValor());

        assertEquals(2, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("DEPOSITO").getQuantidade());
        assertEquals(1920.0, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("DEPOSITO").getValor());

        assertEquals(1, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("TED").getQuantidade());
        assertEquals(20.0, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("TED").getValor());

        assertEquals(1, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("DOC").getQuantidade());
        assertEquals(320.0, relatorio.getRelatorioOperacionalDto().get("tipo_operacao").get("DOC").getValor());
    }

    @Test
    void testGetSituacaoOperacao_TransacoesExistem_DeveRetornarRelatorioCorreto() {
        RelatorioOperacionalDto relatorio = informacoesOperacionaisService.getSituacaoOperacao(mes);

        assertEquals(6, relatorio.getRelatorioOperacionalDto().get("situacao_operacao").get("CONCLUIDO").getQuantidade());
        assertEquals(2350.0, relatorio.getRelatorioOperacionalDto().get("situacao_operacao").get("CONCLUIDO").getValor());

        assertEquals(1, relatorio.getRelatorioOperacionalDto().get("situacao_operacao").get("CANCELADO").getQuantidade());
        assertEquals(40.0, relatorio.getRelatorioOperacionalDto().get("situacao_operacao").get("CANCELADO").getValor());

        assertEquals(1, relatorio.getRelatorioOperacionalDto().get("situacao_operacao").get("PENDENTE").getQuantidade());
        assertEquals(160.0, relatorio.getRelatorioOperacionalDto().get("situacao_operacao").get("PENDENTE").getValor());
    }



}

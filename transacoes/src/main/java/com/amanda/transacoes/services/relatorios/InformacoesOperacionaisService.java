package com.amanda.transacoes.services.relatorios;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amanda.transacoes.dtos.relatorios.RelatorioOperacionalDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.services.TransacaoService;

@Service
public class InformacoesOperacionaisService {
    @Autowired
    private TransacaoService transacaoService;

    public RelatorioOperacionalDto getOperacoes(YearMonth mes) {
        List<TransacaoModel> transacoes = transacaoService.findByYearMonthBetween(mes);
    
        Map<OperacaoEnum, RelatorioOperacionalDto.MovimentacaoDto> movimentacoes = transacoes.stream()
            .filter(transacao -> transacao.getSituacao().equals(SituacaoOperacaoEnum.CONCLUIDO))
            .collect(Collectors.groupingBy(
                TransacaoModel::getOperacao,
                Collectors.collectingAndThen(
                    Collectors.summarizingDouble(TransacaoModel::getValor),
                    stats -> new RelatorioOperacionalDto.MovimentacaoDto((int) stats.getCount(), stats.getSum())
                )
            ));
    
        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto("operacao");
        movimentacoes.forEach((tipo, movimentacao) -> relatorio.adicionarMovimentacao(tipo.name(), movimentacao));
    
        return relatorio;
    }

    public RelatorioOperacionalDto getTipoOperacoes(YearMonth mes) {
        List<TransacaoModel> transacoes = transacaoService.findByYearMonthBetween(mes);

        Map<TipoOperacaoEnum, RelatorioOperacionalDto.MovimentacaoDto> movimentacoes = transacoes.stream()
            .filter(transacao -> transacao.getSituacao().equals(SituacaoOperacaoEnum.CONCLUIDO))
            .collect(Collectors.groupingBy(
                TransacaoModel::getTipoOperacao,
                Collectors.collectingAndThen(
                    Collectors.summarizingDouble(TransacaoModel::getValor),
                    stats -> new RelatorioOperacionalDto.MovimentacaoDto((int) stats.getCount(), stats.getSum())
                )
            ));

        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto("tipo_operacao");
        movimentacoes.forEach((tipo, movimentacao) -> relatorio.adicionarMovimentacao(tipo.name(), movimentacao));

        return relatorio;
    }

    public RelatorioOperacionalDto getSituacaoOperacao(YearMonth mes) {
        List<TransacaoModel> transacoes = transacaoService.findByYearMonthBetween(mes);

        Map<SituacaoOperacaoEnum, RelatorioOperacionalDto.MovimentacaoDto> movimentacoes = transacoes.stream()
            .collect(Collectors.groupingBy(
                TransacaoModel::getSituacao,
                Collectors.collectingAndThen(
                    Collectors.summarizingDouble(TransacaoModel::getValor),
                    stats -> new RelatorioOperacionalDto.MovimentacaoDto((int) stats.getCount(), stats.getSum())
                )
            ));

        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto("situacao_operacao");
        movimentacoes.forEach((tipo, movimentacao) -> relatorio.adicionarMovimentacao(tipo.name(), movimentacao));

        return relatorio;
    }
}

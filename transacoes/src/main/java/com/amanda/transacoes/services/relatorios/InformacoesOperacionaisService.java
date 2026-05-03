package com.amanda.transacoes.services.relatorios;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amanda.transacoes.dtos.relatorios.RelatorioOperacionalDto;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.services.TransacaoService;

@Service
public class InformacoesOperacionaisService {
    @Autowired
    private TransacaoService transacaoService;

    public RelatorioOperacionalDto getOperacoes(YearMonth mes) {   
        return getRelatorio(mes, transacao -> transacao.getSituacao().equals(SituacaoOperacaoEnum.CONCLUIDO), TransacaoModel::getOperacao, "operacao");
    }

    public RelatorioOperacionalDto getTipoOperacoes(YearMonth mes) {
        return getRelatorio(mes, transacao -> transacao.getSituacao().equals(SituacaoOperacaoEnum.CONCLUIDO), TransacaoModel::getTipoOperacao, "tipo_operacao");
    }

    public RelatorioOperacionalDto getSituacaoOperacao(YearMonth mes) {
        return getRelatorio(mes, transacao -> true, TransacaoModel::getSituacao, "situacao_operacao");
    }

    public <T> RelatorioOperacionalDto getRelatorio(YearMonth mes, Predicate<TransacaoModel> filtro, Function<TransacaoModel, T> agrupador, String nomeRelatorio) {
        List<TransacaoModel> transacoes = transacaoService.findByYearMonthBetween(mes);

        Map<T, RelatorioOperacionalDto.MovimentacaoDto> movimentacoes = transacoes.stream()
            .filter(filtro)
            .collect(Collectors.groupingBy(
                agrupador,
                Collectors.collectingAndThen(
                    Collectors.summarizingDouble(TransacaoModel::getValor),
                    stats -> new RelatorioOperacionalDto.MovimentacaoDto((int) stats.getCount(), stats.getSum())
                )
            ));

        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto(nomeRelatorio);
        movimentacoes.forEach((tipo, movimentacao) -> relatorio.adicionarMovimentacao(tipo.toString(), movimentacao));

        return relatorio;
    }
}

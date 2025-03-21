package com.amanda.transacoes.services.relatorios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amanda.transacoes.dtos.relatorios.RelatorioOperacionalDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.TransacaoRepository;

@Service
public class InformacoesOperacionaisService {
    
    @Autowired
    private TransacaoRepository transacaoRepository;

    public RelatorioOperacionalDto getOperacoes() {
        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        Map<OperacaoEnum, RelatorioOperacionalDto.MovimentacaoDto> movimentacoes = new HashMap<>();

        transacoes.stream()
            .filter(transacao -> transacao.getSituacao().equals(SituacaoOperacaoEnum.CONCLUIDO))
            .forEach(transacao -> 
                movimentacoes.merge(
                    transacao.getOperacao(),                                                                            
                    new RelatorioOperacionalDto.MovimentacaoDto(1, transacao.getValor()),                         
                    (movimentacaoExistente, novaMovimentacao) -> new RelatorioOperacionalDto.MovimentacaoDto(               
                        movimentacaoExistente.getQuantidade() + 1, 
                        movimentacaoExistente.getValor() + novaMovimentacao.getValor()
                    )
                )
            );

        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto("operacao");

        movimentacoes.forEach((tipo, movimentacao) -> relatorio.adicionarMovimentacao(tipo.name(), movimentacao));

        return relatorio;
    }

    public RelatorioOperacionalDto getTipoOperacoes() {
        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        Map<TipoOperacaoEnum, RelatorioOperacionalDto.MovimentacaoDto> movimentacoes = new HashMap<>();

        transacoes.stream()
            .filter(transacao -> transacao.getSituacao().equals(SituacaoOperacaoEnum.CONCLUIDO))
            .forEach(transacao -> 
                movimentacoes.merge(
                    transacao.getTipoOperacao(),                                                                            //key
                    new RelatorioOperacionalDto.MovimentacaoDto(1, transacao.getValor()),                        //se a key nao existe, adiciona esse value
                    (movimentacaoExistente, novaMovimentacao) -> new RelatorioOperacionalDto.MovimentacaoDto(               //se a key existe, aplica essa funcao
                        movimentacaoExistente.getQuantidade() + 1, 
                        movimentacaoExistente.getValor() + novaMovimentacao.getValor()
                    )
                )
            );

        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto("tipo_operacao");

        movimentacoes.forEach((tipo, movimentacao) -> relatorio.adicionarMovimentacao(tipo.name(), movimentacao));

        return relatorio;
    }

    public RelatorioOperacionalDto getSituacaoOperacao() {

        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        Map<SituacaoOperacaoEnum, RelatorioOperacionalDto.MovimentacaoDto> movimentacoes = new HashMap<>();

        transacoes.stream()
            .forEach(transacao -> 
                movimentacoes.merge(
                    transacao.getSituacao(),                                                                         
                    new RelatorioOperacionalDto.MovimentacaoDto(1, transacao.getValor()),                     
                    (movimentacaoExistente, novaMovimentacao) -> new RelatorioOperacionalDto.MovimentacaoDto(              
                        movimentacaoExistente.getQuantidade() + 1, 
                        movimentacaoExistente.getValor() + novaMovimentacao.getValor()
                    )
                )
            );

        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto("situacao_operacao");

        movimentacoes.forEach((tipo, movimentacao) -> relatorio.adicionarMovimentacao(tipo.name(), movimentacao));

        return relatorio;
    }
}

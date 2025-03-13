package com.amanda.transacoes.services.relatorios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amanda.transacoes.dtos.relatorios.MovimentacaoDto;
import com.amanda.transacoes.dtos.relatorios.RelatorioOperacionalDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.TransacaoRepository;

@Service
public class RelatorioOperacionalService {
    
    @Autowired
    private TransacaoRepository transacaoRepository;

    public RelatorioOperacionalDto getRelatorioOperacoes() {
        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        int quantidadeCredito = 0;
        double valorCredito = 0;
        int quantidadeDebito = 0;
        double valorDebito= 0;

        for (TransacaoModel transacao : transacoes) {
            if(transacao.getSituacao().equals(SituacaoOperacaoEnum.CONCLUIDO)){
                if(transacao.getOperacao().equals(OperacaoEnum.CREDITO)){
                    quantidadeCredito += 1;
                    valorCredito += transacao.getValor();
                }
                if(transacao.getOperacao().equals(OperacaoEnum.DEBITO)){
                    quantidadeDebito += 1;
                    valorDebito += transacao.getValor();
                }
            }
        }

        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto("operacao");
        relatorio.adicionarOperacao("CREDITO", new MovimentacaoDto(quantidadeCredito, valorCredito));
        relatorio.adicionarOperacao("DEBITO", new MovimentacaoDto(quantidadeDebito, valorDebito));

        return relatorio;
    }

    public RelatorioOperacionalDto getRelatorioTipoOperacoes() {
        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        int quantidadeDeposito = 0;
        double valorDeposito = 0;
        int quantidadeSaque = 0;
        double valorSaque= 0;
        int quantidadePix = 0;
        double valorPix= 0;
        int quantidadeTed = 0;
        double valorTed= 0;
        int quantidadeDoc = 0;
        double valorDoc= 0;

        for (TransacaoModel transacao : transacoes) {
            if(transacao.getSituacao().equals(SituacaoOperacaoEnum.CONCLUIDO)){
                if(transacao.getTipoOperacao().equals(TipoOperacaoEnum.DEPOSITO)){
                    quantidadeDeposito += 1;
                    valorDeposito += transacao.getValor();
                }
                if(transacao.getTipoOperacao().equals(TipoOperacaoEnum.SAQUE)){
                    quantidadeSaque += 1;
                    valorSaque += transacao.getValor();
                }
                if(transacao.getTipoOperacao().equals(TipoOperacaoEnum.PIX)){
                    quantidadePix += 1;
                    valorPix += transacao.getValor();
                }
                if(transacao.getTipoOperacao().equals(TipoOperacaoEnum.TED)){
                    quantidadeTed += 1;
                    valorTed += transacao.getValor();
                }
                if(transacao.getTipoOperacao().equals(TipoOperacaoEnum.DOC)){
                    quantidadeDoc += 1;
                    valorDoc += transacao.getValor();
                }
            }
        }

        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto("tipo_operacao");

        relatorio.adicionarOperacao("DEPOSITO", new MovimentacaoDto(quantidadeDeposito, valorDeposito));
        relatorio.adicionarOperacao("SAQUE", new MovimentacaoDto(quantidadeSaque, valorSaque));
        relatorio.adicionarOperacao("PIX", new MovimentacaoDto(quantidadePix, valorPix));
        relatorio.adicionarOperacao("TED", new MovimentacaoDto(quantidadeTed, valorTed));
        relatorio.adicionarOperacao("DOC", new MovimentacaoDto(quantidadeDoc, valorDoc));

        return relatorio;
    }

    public RelatorioOperacionalDto getRelatorioSituacaoOperacao() {
        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        int quantidadePendente = 0;
        double valorPendente = 0;
        int quantidadeConcluido = 0;
        double valorConcluido = 0;
        int quantidadeCancelado = 0;
        double valorCancelado = 0;


        for (TransacaoModel transacao : transacoes) {
                if(transacao.getSituacao().equals(SituacaoOperacaoEnum.PENDENTE)){
                    quantidadePendente += 1;
                    valorPendente += transacao.getValor();
                }
                if(transacao.getSituacao().equals(SituacaoOperacaoEnum.CONCLUIDO)){
                    quantidadeConcluido += 1;
                    valorConcluido += transacao.getValor();
                }
                if(transacao.getSituacao().equals(SituacaoOperacaoEnum.CANCELADO)){
                    quantidadeCancelado += 1;
                    valorCancelado += transacao.getValor();
                }
        }

        RelatorioOperacionalDto relatorio = new RelatorioOperacionalDto("situacao_operacao");
        relatorio.adicionarOperacao("PENDENTE", new MovimentacaoDto(quantidadePendente, valorPendente));
        relatorio.adicionarOperacao("CONCLUIDO", new MovimentacaoDto(quantidadeConcluido, valorConcluido));
        relatorio.adicionarOperacao("CANCELADO", new MovimentacaoDto(quantidadeCancelado, valorCancelado));

        return relatorio;
    }
}

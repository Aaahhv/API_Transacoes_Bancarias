package com.amanda.transacoes.utils;

import java.time.LocalDateTime;
import java.util.UUID;

import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;

public class TransacaoModelUtils {

    private TransacaoModelUtils() {}

    public static TransacaoModel createTransacao(String ccOrigem, String ccDestino, double valor,  OperacaoEnum operacao, 
                    TipoOperacaoEnum tipoOperacao, SituacaoOperacaoEnum situacao, UUID dispositivoId, LocalDateTime dataTransacao) {

        TransacaoModel transacao = new TransacaoModel(ccOrigem, ccDestino, valor, operacao, tipoOperacao, situacao, dispositivoId);
        transacao.setDataTransacao(dataTransacao);
        return transacao;
    }

    public static TransacaoModel createTransacao(UUID transacaoId, String ccOrigem, String ccDestino, double valor,  OperacaoEnum operacao, 
    TipoOperacaoEnum tipoOperacao, SituacaoOperacaoEnum situacao, UUID dispositivoId) {

        TransacaoModel transacao = new TransacaoModel(ccOrigem, ccDestino, valor, operacao, tipoOperacao, situacao, dispositivoId);
        transacao.setId(transacaoId);
        return transacao;
    }
}

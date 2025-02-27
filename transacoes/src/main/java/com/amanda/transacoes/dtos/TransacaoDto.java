package com.amanda.transacoes.dtos;

import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;

public class TransacaoDto {

    private String ccOrigem;
    private String ccDestino;   
    private double valor;
    private OperacaoEnum operacao;
    private TipoOperacaoEnum tipoTransacao;
    
    public TransacaoDto() {
    }

    public TransacaoDto(String ccOrigem, String ccDestino, double valor, OperacaoEnum operacao, TipoOperacaoEnum tipoTransacao) {
        this.ccOrigem = ccOrigem;
        this.ccDestino = ccDestino;
        this.valor = valor;
        this.operacao = operacao;
        this.tipoTransacao = tipoTransacao;
    }

    public String getCcOrigem() {
        return ccOrigem;
    }

    public void setCcOrigem(String ccOrigem) {
        this.ccOrigem = ccOrigem;
    }

    public String getCcDestino() {
        return ccDestino;
    }

    public void setCcDestino(String ccDestino) {
        this.ccDestino = ccDestino;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public OperacaoEnum getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoEnum operacao) {
        this.operacao = operacao;
    }

    public TipoOperacaoEnum getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoOperacaoEnum tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }
}

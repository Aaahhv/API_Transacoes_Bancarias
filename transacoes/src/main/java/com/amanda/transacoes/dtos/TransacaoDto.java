package com.amanda.transacoes.dtos;

import java.util.UUID;

import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;

public class TransacaoDto {

    private String ccOrigem;
    private String ccDestino;   
    private double valor;
    private OperacaoEnum operacao;
    private TipoOperacaoEnum tipoOperacao;
    private UUID dispositivoId;
    
    public TransacaoDto() {
    }

    public TransacaoDto(String ccOrigem, String ccDestino, double valor, OperacaoEnum operacao, TipoOperacaoEnum tipoOperacao, UUID dispositivoId) {
        this.ccOrigem = ccOrigem;
        this.ccDestino = ccDestino;
        this.valor = valor;
        this.operacao = operacao;
        this.tipoOperacao = tipoOperacao;
        this.dispositivoId = dispositivoId;
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

    public TipoOperacaoEnum getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoEnum tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public UUID getDispositivoId(){
        return dispositivoId;
    }

    public void setDispositivoId(UUID dispositivoId){
        this.dispositivoId = dispositivoId;
    }

}

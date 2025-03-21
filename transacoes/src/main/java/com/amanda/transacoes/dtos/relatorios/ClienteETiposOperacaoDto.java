package com.amanda.transacoes.dtos.relatorios;

import java.util.EnumMap;
import java.util.Map;

import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.ClienteModel;

public class ClienteETiposOperacaoDto {

    private ClienteModel cliente;
    private Map<TipoOperacaoEnum, Integer> tipoOperacao;

    public ClienteETiposOperacaoDto(ClienteModel cliente){
        this.cliente = cliente;
        this.tipoOperacao = new EnumMap<>(TipoOperacaoEnum.class);
    }

    public ClienteModel getCliente() {
        return cliente;
    }

    public void setCliente(ClienteModel cliente) {
        this.cliente = cliente;
    }

    public Map<TipoOperacaoEnum, Integer> getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(Map<TipoOperacaoEnum, Integer> tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public void incrementarTipoOperacao(TipoOperacaoEnum tipo) {
        tipoOperacao.put(tipo, tipoOperacao.getOrDefault(tipo, 0) + 1);
    }
}

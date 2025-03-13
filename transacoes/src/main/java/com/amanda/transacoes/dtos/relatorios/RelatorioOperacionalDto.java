package com.amanda.transacoes.dtos.relatorios;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.HashMap;
import java.util.Map;

public class RelatorioOperacionalDto {
    
    private String nomeWrapper; 
    private Map<String, MovimentacaoDto> movimentacoes; 

    public RelatorioOperacionalDto(String nomeWrapper) {
        this.nomeWrapper = nomeWrapper;
        this.movimentacoes = new HashMap<>();
    }

    public void adicionarOperacao(String nome, MovimentacaoDto movimentacao) {
        movimentacoes.put(nome, movimentacao);
    }

    @JsonAnyGetter
    public Map<String, Map<String, MovimentacaoDto>> getRelatorioOperacionalDto() {
        Map<String, Map<String, MovimentacaoDto>> wrapper = new HashMap<>();
        wrapper.put(nomeWrapper, movimentacoes); 
        return wrapper;
    }
}
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

    public void adicionarMovimentacao(String nome, MovimentacaoDto movimentacao) {
        movimentacoes.put(nome, movimentacao);
    }

    @JsonAnyGetter
    public Map<String, Map<String, MovimentacaoDto>> getRelatorioOperacionalDto() {
        Map<String, Map<String, MovimentacaoDto>> wrapper = new HashMap<>();
        wrapper.put(nomeWrapper, movimentacoes); 
        return wrapper;
    }

    public static class MovimentacaoDto {

        private int quantidade;
        private double valor;
    
        public MovimentacaoDto(int quantidade, double valor) {
            this.quantidade = quantidade;
            this.valor = valor;
        }
    
        public int getQuantidade() {
            return quantidade;
        }
    
        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
    
        public double getValor() {
            return valor;
        }
    
        public void setValor(double valor) {
            this.valor = valor;
        }
    }
}
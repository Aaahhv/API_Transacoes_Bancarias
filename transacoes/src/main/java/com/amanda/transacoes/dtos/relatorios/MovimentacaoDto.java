package com.amanda.transacoes.dtos.relatorios;

public class MovimentacaoDto {

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
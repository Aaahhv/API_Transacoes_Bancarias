package com.amanda.transacoes.dtos.relatorios;

import com.amanda.transacoes.models.ClienteModel;

public class ClienteEValorDto {
    
    private ClienteModel cliente;
    private double valor;

    public ClienteEValorDto(ClienteModel cliente, double valor){
        this.cliente = cliente;
        this.valor = valor;
    }

    public ClienteModel getCliente() {
        return cliente;
    }

    public void setCliente(ClienteModel cliente) {
        this.cliente = cliente;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}

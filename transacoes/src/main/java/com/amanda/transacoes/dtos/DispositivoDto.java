package com.amanda.transacoes.dtos;

import java.util.UUID;

public class DispositivoDto {
    
    private UUID clienteId;
    private String descricao;

    public DispositivoDto() {
    }

    public DispositivoDto(UUID clienteId, String descricao) {
        this.clienteId = clienteId;
        this.descricao = descricao;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

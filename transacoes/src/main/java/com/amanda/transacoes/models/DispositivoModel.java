package com.amanda.transacoes.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; 

@Entity
@Table(name = "Dispositivos")
public class DispositivoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID clienteId;

    @CreationTimestamp
    private LocalDateTime dataInclusao;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private boolean ativo;

    public DispositivoModel() {
    }

    public DispositivoModel(String descricao, boolean ativo, UUID clienteId) {
        this.descricao = descricao;
        this.ativo = ativo;
        this.clienteId = clienteId;
    }

    public UUID getId() { 
        return id; 
    }

    public void setId(UUID id) { 
        this.id = id; 
    }

    public LocalDateTime getdataInclusao() { 
        return dataInclusao; 
    }

    public void setdataInclusao(LocalDateTime dataInclusao) { 
        this.dataInclusao = dataInclusao; 
    }

    public boolean getAtivo() { 
        return ativo; 
    }

    public void setAtivo(boolean ativo) { 
        this.ativo = ativo; 
    }

    public String getDescricao() { 
        return descricao; 
    }

    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }

    public UUID getClienteId() {
        return clienteId; 
    }
    public void setClienteId(UUID clienteId) { 
        this.clienteId = clienteId; 
    }

}

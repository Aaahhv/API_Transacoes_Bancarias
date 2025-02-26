package com.amanda.transacoes.models;

import java.time.LocalDateTime;
import java.util.UUID;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table; 

@Entity
@Table(name = "Dispositivos")

public class DispositivoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime data_inclusao;

    private String descricao;

    @Column(nullable = false)
    private boolean ativo;

    @ManyToOne
    @JoinColumn(name = "clienteId", nullable = false)
    private ClienteModel cliente;

    public UUID getId() { 
        return id; 
    }

    public void setId(UUID id) { 
        this.id = id; 
    }

    public LocalDateTime getData_inclusao() { 
        return data_inclusao; 
    }

    public void setData_inclusao(LocalDateTime data_inclusao) { 
        this.data_inclusao = data_inclusao; 
    }

    public boolean getAtivo() { 
        return ativo; 
    }

    public void setAtivo(boolean ativo) { 
        this.ativo = ativo; 
    }

    public String geDescricao() { 
        return descricao; 
    }

    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }

    public ClienteModel getCliente() {
        return cliente; 
    }
    public void setCliente(ClienteModel cliente) { 
        this.cliente = cliente; 
    }

}

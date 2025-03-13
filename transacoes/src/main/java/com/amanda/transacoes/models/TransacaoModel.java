package com.amanda.transacoes.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; 
import jakarta.persistence.Table; 


@Entity
@Table(name = "Transacoes")

public class TransacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String ccOrigem;

    private String ccDestino;

    @CreationTimestamp
    private LocalDateTime dataTransacao;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private OperacaoEnum operacao;

    @Column(nullable = false)
    private TipoOperacaoEnum tipoOperacao;

    @Column(nullable = false)
    private SituacaoOperacaoEnum situacao;

    private UUID dispositivoId;

    public TransacaoModel() {
    }

    public TransacaoModel(String ccOrigem, String ccDestino, double valor, OperacaoEnum operacao, TipoOperacaoEnum tipoOperacao, SituacaoOperacaoEnum situacao, UUID dispositivoId) {
        this.ccOrigem = ccOrigem;
        this.ccDestino = ccDestino;
        this.valor = valor;
        this.operacao = operacao;
        this.tipoOperacao = tipoOperacao;
        //this.situacao = SituacaoOperacaoEnum.PENDENTE;
        this.situacao = situacao;
        this.dispositivoId = dispositivoId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
    
    public LocalDateTime getdataTransacao() {
        return dataTransacao;
    }

    public void setdataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
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

    public SituacaoOperacaoEnum getSituacao() {
        return situacao;
    }   

    public void setSituacao(SituacaoOperacaoEnum situacao) {
        this.situacao = situacao;
    }

    public UUID getDispositivoId() {
        return dispositivoId;
    }

    public void setDispositivoId(UUID dispositivoId) {
        this.dispositivoId = dispositivoId;
    }
}

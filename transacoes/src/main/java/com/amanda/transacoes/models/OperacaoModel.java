package com.amanda.transacoes.models;

import java.time.LocalTime;
import java.util.UUID;

import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; 

@Entity
@Table(name = "Operacao")
public class OperacaoModel {
    

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Column(unique = true, nullable = false)
    private TipoOperacaoEnum tipo; 

    private double taxa;

    @Column(nullable = false)
    private boolean ativo;

    private double limiteValor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime horarioInicio;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime horarioFim;


    public OperacaoModel() {
    }

    public OperacaoModel(TipoOperacaoEnum tipo, double taxa, boolean ativo, double limiteValor, LocalTime horarioInicio, LocalTime horarioFim) {
        this.tipo = tipo;
        this.ativo = ativo;
        this.taxa = taxa;
        this.limiteValor = limiteValor;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;

    }

    public OperacaoModel(TipoOperacaoEnum tipo, boolean ativo) {
        this.tipo = tipo;
        this.ativo = ativo;  
    }

    
    public OperacaoModel(TipoOperacaoEnum tipo, double taxa, boolean ativo, double limiteValor) {
        this.tipo = tipo;
        this.taxa = taxa;
        this.ativo = ativo; 
        this.limiteValor = limiteValor; 
    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TipoOperacaoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoOperacaoEnum tipo) {
        this.tipo = tipo;
    }

    public double getTaxa() {
        return taxa;
    }

    public void setTaxa(double taxa) {
        this.taxa = taxa;
    }

    public boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public double getLimiteValor() {
        return limiteValor;
    }

    public void setLimiteValor(double limiteValor) {
        this.limiteValor = limiteValor;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

}

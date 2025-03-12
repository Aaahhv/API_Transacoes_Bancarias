package com.amanda.transacoes.dtos;

import com.amanda.transacoes.enums.TipoOperacaoEnum;
public class OperacaoDto {
    
    private TipoOperacaoEnum tipo;
    private double taxa;
    private boolean ativo;
    private double limiteValor;
    private HorarioDto horario;

    public OperacaoDto() {
    }

    public OperacaoDto(TipoOperacaoEnum tipo, double taxa, boolean ativo, double  limiteValor, HorarioDto horario) {
        this.tipo = tipo;
        this.taxa = taxa;
        this.ativo = ativo;
        this.limiteValor = limiteValor;
        this.horario = horario;
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

    public double  getLimiteValor() {
        return limiteValor;
    }

    public void setLimiteValor(double  limite) {
        this.limiteValor = limite;
    }

    public HorarioDto getHorario() {
        return horario;
    }

    public void setHorario(HorarioDto horario) {
        this.horario = horario;
    }

}

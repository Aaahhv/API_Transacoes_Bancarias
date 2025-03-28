package com.amanda.transacoes.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PeriodoDataDto {

    //AQUI ESTA COM PROBLEMA PORQUE (NO FORMULARIO) A DATA ESTÁ VINDO COMO:  2025-03-14T19:29:16.514Z E ESSE Z NAO DEVE APARECER

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(type = "string", format = "date-time", example = "2025-03-14T08:00:00")
    private LocalDateTime dataInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(type = "string", format = "date-time", example = "2025-03-14T22:00:00")
    private LocalDateTime dataFim;

    public PeriodoDataDto() {
    }

    public PeriodoDataDto(LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }
}
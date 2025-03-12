package com.amanda.transacoes.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class HorarioDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", format = "time", example = "00:00:00")
    private LocalTime horaInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", format = "time", example = "22:00:00")
    private LocalTime horaFim;

    public HorarioDto() {
    }

    public HorarioDto(LocalTime horaInicio, LocalTime horaFim) {
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
}
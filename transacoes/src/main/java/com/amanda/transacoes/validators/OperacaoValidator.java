package com.amanda.transacoes.validators;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.OperacaoDto;

@Component
public class OperacaoValidator {

    public void validateUpdate(OperacaoDto operacaoDto) {

        if(operacaoDto.getHorario().getHoraInicio().isAfter(operacaoDto.getHorario().getHoraFim())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horário de início não pode ser maior que horário de fim.");
        }

        if(operacaoDto.getTaxa() < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A taxa não pode ser negativa.");
        }
    }
}

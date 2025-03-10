package com.amanda.transacoes.validators;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

import com.amanda.transacoes.dtos.OperacaoDto;
import com.amanda.transacoes.dtos.HorarioDto;

@ExtendWith(MockitoExtension.class)
class OperacaoValidatorTest {

    @InjectMocks
    private OperacaoValidator operacaoValidator;

    private OperacaoDto operacaoDto;
    private HorarioDto horarioDto;

    @BeforeEach
    void setUp() {
        horarioDto = new HorarioDto(LocalTime.of(9, 0), LocalTime.of(18, 0)); 
        operacaoDto = new OperacaoDto();
        operacaoDto.setHorario(horarioDto);
        operacaoDto.setTaxa(10.0);
    }

    @Test
    void validateUpdate_DadosValidos_DevePassar() {
        assertDoesNotThrow(() -> operacaoValidator.validateUpdate(operacaoDto));
    }

    @Test
    void validateUpdate_HorarioInicioMaiorQueFim_DeveLancarExcecao() {
        operacaoDto.getHorario().setHoraInicio(LocalTime.of(19, 0)); 
        operacaoDto.getHorario().setHoraFim(LocalTime.of(18, 0)); 

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            operacaoValidator.validateUpdate(operacaoDto);
        });

        assertEquals("400 BAD_REQUEST \"Horário de início não pode ser maior que horário de fim.\"", exception.getMessage());
    }

    @Test
    void validateUpdate_TaxaNegativa_DeveLancarExcecao() {
        operacaoDto.setTaxa(-5.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            operacaoValidator.validateUpdate(operacaoDto);
        });

        assertEquals("400 BAD_REQUEST \"A taxa não pode ser negativa.\"", exception.getMessage());
    }
}

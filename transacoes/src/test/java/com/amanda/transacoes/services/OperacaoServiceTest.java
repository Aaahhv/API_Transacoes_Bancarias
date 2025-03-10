package com.amanda.transacoes.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;


import com.amanda.transacoes.dtos.HorarioDto;
import com.amanda.transacoes.dtos.OperacaoDto;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.OperacaoModel;
import com.amanda.transacoes.repositories.OperacaoRepository;
import com.amanda.transacoes.validators.OperacaoValidator;

@ExtendWith(MockitoExtension.class)
class OperacaoServiceTest {

    @Mock
    private OperacaoRepository operacaoRepository;

    @Mock
    private OperacaoValidator operacaoValidator;

    @InjectMocks
    private OperacaoService operacaoService;

    private OperacaoModel operacao;

    private OperacaoDto operacaoDto;
    private OperacaoModel operacaoModel;

    @BeforeEach
    void setUp() {
        operacaoDto = new OperacaoDto(TipoOperacaoEnum.SAQUE, 15.0, true, 1000.0, new HorarioDto(LocalTime.of(9, 0), LocalTime.of(18, 0)));
        operacaoModel = new OperacaoModel(TipoOperacaoEnum.SAQUE, 10.0, false, Double.POSITIVE_INFINITY,LocalTime.of(8, 0), LocalTime.of(17, 0));
        operacao = new OperacaoModel(TipoOperacaoEnum.SAQUE, 5.0, true, 1000.0, LocalTime.of(8, 0), LocalTime.of(18, 0));
    }

    @Test
    void getAll() {
        when(operacaoRepository.findAll()).thenReturn(Arrays.asList(operacaoModel));

        List<OperacaoModel> result = operacaoService.getAll();

        assertEquals(1, result.size());
        assertEquals(operacaoModel, result.get(0));
    }


    @Test
    void update_OperacaoNaoEncontrada_DeveLancarExcecao() {
        when(operacaoRepository.findByTipo(operacaoDto.getTipo())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            operacaoService.update(operacaoDto);
        });

        
        assertEquals("404 NOT_FOUND \"Operação não encontrada.\"", exception.getMessage());

        verify(operacaoRepository, never()).save(any());
    }
 
    void update_OperacaoValida_DeveAtualizarComSucesso() {
        when(operacaoRepository.findByTipo(operacaoDto.getTipo())).thenReturn(Optional.of(operacaoModel));
        when(operacaoRepository.save(any(OperacaoModel.class))).thenReturn(operacaoModel);

        OperacaoModel atualizada = operacaoService.update(operacaoDto);

        assertNotNull(atualizada);
        assertEquals(operacaoDto.getTaxa(), atualizada.getTaxa());
        assertEquals(operacaoDto.getAtivo(), atualizada.getAtivo());
        assertEquals(operacaoDto.getHorario().getHoraInicio(), atualizada.getHorarioInicio());
        assertEquals(operacaoDto.getHorario().getHoraFim(), atualizada.getHorarioInicio());

        verify(operacaoValidator).validateUpdate(operacaoDto);
        verify(operacaoRepository).save(operacaoModel);
    }

    @Test
    void update_LimiteMaiorQueZero_DeveAtualizarLimite() {
        operacaoDto.setLimiteValor(500.0);
        when(operacaoRepository.findByTipo(operacaoDto.getTipo())).thenReturn(Optional.of(operacaoModel));
        when(operacaoRepository.save(any(OperacaoModel.class))).thenReturn(operacaoModel);

        OperacaoModel atualizada = operacaoService.update(operacaoDto);

        assertEquals(500.0, atualizada.getLimiteValor());
    }

    @Test
    void update_LimiteMenorOuIgualZero_DeveSerInfinito() {
        operacaoDto.setLimiteValor(0);
        when(operacaoRepository.findByTipo(operacaoDto.getTipo())).thenReturn(Optional.of(operacaoModel));
        when(operacaoRepository.save(any(OperacaoModel.class))).thenReturn(operacaoModel);

        OperacaoModel atualizada = operacaoService.update(operacaoDto);

        assertEquals(Double.POSITIVE_INFINITY, atualizada.getLimiteValor());
    }
   
    @Test
    void getByTipo() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(Optional.of(operacao));
        OperacaoModel result = operacaoService.getByTipo(TipoOperacaoEnum.SAQUE);
        assertEquals(operacao, result);
    }

    @Test
    void getByTipo_TipoNaoExiste_DeveLancarExcessao() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
        operacaoService.getByTipo(TipoOperacaoEnum.SAQUE);
        });

        assertEquals("404 NOT_FOUND \"Operação não encontrada.\"", exception.getMessage());
    }

    @Test
    void getTaxaOperacao() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(Optional.of(operacao));
        double taxa = operacaoService.getTaxaOperacao(TipoOperacaoEnum.SAQUE);
        assertEquals(5.0, taxa);
    }

    @Test
    void getLimiteValor() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(Optional.of(operacao));
        double limite = operacaoService.getLimiteValor(TipoOperacaoEnum.SAQUE);
        assertEquals(1000, limite);
    }

    @Test
    void isOperacaoAtiva() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(Optional.of(operacao));
        assertTrue(operacaoService.isTipoDeOperacaoAtiva(TipoOperacaoEnum.SAQUE));
    }

    @Test
    void isLimiteValorValido() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(Optional.of(operacao));
        assertTrue(operacaoService.isLimiteValorValido(TipoOperacaoEnum.SAQUE, 500.0));
        assertFalse(operacaoService.isLimiteValorValido(TipoOperacaoEnum.SAQUE, 2000.0));
    }

    @Test
    void isHorarioValido() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(Optional.of(operacao));
        assertTrue(operacaoService.isHorarioValido(TipoOperacaoEnum.SAQUE, LocalTime.of(9, 0)));
        assertFalse(operacaoService.isHorarioValido(TipoOperacaoEnum.SAQUE, LocalTime.of(7, 0)));
        assertFalse(operacaoService.isHorarioValido(TipoOperacaoEnum.SAQUE, LocalTime.of(19, 0)));
    }
}
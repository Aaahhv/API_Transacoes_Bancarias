package com.amanda.transacoes.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

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

@ExtendWith(MockitoExtension.class)
class OperacaoServiceTest {

    @Mock
    private OperacaoRepository operacaoRepository;

    @InjectMocks
    private OperacaoService operacaoService;

    private OperacaoModel operacao;

    @BeforeEach
    void setUp() {
        operacao = new OperacaoModel(TipoOperacaoEnum.SAQUE, 5.0, true, 1000.0, LocalTime.of(8, 0), LocalTime.of(18, 0));
    }

    @Test
    void testGetAll() {
        when(operacaoRepository.findAll()).thenReturn(Arrays.asList(operacao));

        List<OperacaoModel> result = operacaoService.getAll();

        assertEquals(1, result.size());
        assertEquals(operacao, result.get(0));
    }

    @Test
    void testUpdate_Valido() {
        OperacaoDto dto = new OperacaoDto(TipoOperacaoEnum.SAQUE, 10.0, false, 500.0, new HorarioDto(LocalTime.of(9, 0), LocalTime.of(17, 0)));
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);
        when(operacaoRepository.save(any())).thenReturn(operacao);

        OperacaoModel updated = operacaoService.update(dto);

        assertEquals(10.0, updated.getTaxa());
        assertFalse(updated.getAtivo());
        assertEquals(500.0, updated.getLimiteValor());
        assertEquals(LocalTime.of(9, 0), updated.getHorarioInicio());
        assertEquals(LocalTime.of(17, 0), updated.getHorarioFim());
    }

    @Test
    void testUpdate_ValidoLimiteInfinito() {
        OperacaoDto dto = new OperacaoDto(TipoOperacaoEnum.SAQUE, 10.0, false, -500.0, new HorarioDto(LocalTime.of(9, 0), LocalTime.of(17, 0)));
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);
        when(operacaoRepository.save(any())).thenReturn(operacao);

        OperacaoModel updated = operacaoService.update(dto);

        assertEquals(10.0, updated.getTaxa());
        assertFalse(updated.getAtivo());
        assertEquals(Double.POSITIVE_INFINITY, updated.getLimiteValor());
        assertEquals(LocalTime.of(9, 0), updated.getHorarioInicio());
        assertEquals(LocalTime.of(17, 0), updated.getHorarioFim());
    }

    @Test
    void testUpdate_ThrowsException_OperacaoNaoEncontrada() {
        OperacaoDto dto = new OperacaoDto(TipoOperacaoEnum.SAQUE, 10.0, true, 500.0, new HorarioDto(LocalTime.of(9, 0), LocalTime.of(17, 0)));
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> operacaoService.update(dto));
    }

    @Test
    void testUpdate_ThrowsException_HorarioInvalido() {
        OperacaoDto dto = new OperacaoDto(TipoOperacaoEnum.SAQUE, 10.0, true, 500.0, new HorarioDto(LocalTime.of(18, 0), LocalTime.of(8, 0)));
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);

        assertThrows(ResponseStatusException.class, () -> operacaoService.update(dto));
    }

    @Test
    void testUpdate_ThrowsException_TaxaNegativa() {
        OperacaoDto dto = new OperacaoDto(TipoOperacaoEnum.SAQUE, -5.0, true, 500.0, new HorarioDto(LocalTime.of(9, 0), LocalTime.of(17, 0)));
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);

        assertThrows(ResponseStatusException.class, () -> operacaoService.update(dto));
    }

    @Test
    void testGetByTipo() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);
        OperacaoModel result = operacaoService.getByTipo(TipoOperacaoEnum.SAQUE);
        assertEquals(operacao, result);
    }

    @Test
    void testGetTaxaOperacao() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);
        double taxa = operacaoService.getTaxaOperacao(TipoOperacaoEnum.SAQUE);
        assertEquals(5.0, taxa);
    }

    @Test
    void testGetLimiteValor() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);
        double limite = operacaoService.getLimiteValor(TipoOperacaoEnum.SAQUE);
        assertEquals(1000, limite);
    }

    @Test
    void testIsOperacaoAtiva() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);
        assertTrue(operacaoService.isTipoDeOperacaoAtiva(TipoOperacaoEnum.SAQUE));
    }

    @Test
    void testIsLimiteValorValido() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);
        assertTrue(operacaoService.isLimiteValorValido(TipoOperacaoEnum.SAQUE, 500.0));
        assertFalse(operacaoService.isLimiteValorValido(TipoOperacaoEnum.SAQUE, 2000.0));
    }

    @Test
    void testIsHorarioValido() {
        when(operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE)).thenReturn(operacao);
        assertTrue(operacaoService.isHorarioValido(TipoOperacaoEnum.SAQUE, LocalTime.of(9, 0)));
        assertFalse(operacaoService.isHorarioValido(TipoOperacaoEnum.SAQUE, LocalTime.of(7, 0)));
        assertFalse(operacaoService.isHorarioValido(TipoOperacaoEnum.SAQUE, LocalTime.of(19, 0)));
    }
}
package com.amanda.transacoes.controllers;

import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.amanda.transacoes.dtos.DispositivoDto;
import com.amanda.transacoes.dtos.HorarioDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.OperacaoModel;
import com.amanda.transacoes.services.DispositivoService;
import com.amanda.transacoes.services.OperacaoService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping (value = "/operacao", produces = {"application/json"})
@Tag(name = "Rotas de operacao", description = "API de transações bancárias")
public class OperacaoController {
    
    
    @Autowired
    private OperacaoService operacaoService;

    @GetMapping("/")
    public ResponseEntity<List<OperacaoModel>> read() {
        List<OperacaoModel> operacoes = operacaoService.getAll();
        return ResponseEntity.ok(operacoes);
    }

    @PutMapping("/")
    public ResponseEntity<OperacaoModel> updateHoraInicioFim(TipoOperacaoEnum operacao, HorarioDto horas) {
        OperacaoModel operacaoAtualizada = operacaoService.updateHora(operacao, horas.getHoraInicio(), horas.getHoraFim());
        return new ResponseEntity<>(operacaoAtualizada, HttpStatus.OK);
    }

}

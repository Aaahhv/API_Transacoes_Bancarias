package com.amanda.transacoes.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.amanda.transacoes.dtos.OperacaoDto;
import com.amanda.transacoes.models.OperacaoModel;
import com.amanda.transacoes.services.OperacaoService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping (value = "/operacao", produces = {"application/json"})
@Tag(name = "Rotas de Operacao", description = "API de transações bancárias")
public class OperacaoController {
    
    
    @Autowired
    private OperacaoService operacaoService;

    @GetMapping("/")
    public ResponseEntity<List<OperacaoModel>> read() {
        List<OperacaoModel> operacoes = operacaoService.getAll();
        return ResponseEntity.ok(operacoes);
    }

    @PutMapping("/")
    public ResponseEntity<OperacaoModel> updateHoraInicioFim(@RequestBody OperacaoDto operacaoDto) {
        OperacaoModel operacaoAtualizada = operacaoService.update(operacaoDto);
        return new ResponseEntity<>(operacaoAtualizada, HttpStatus.OK);
    }

}

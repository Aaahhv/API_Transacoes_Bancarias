package com.amanda.transacoes.controllers;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.services.TransacaoService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping (value = "/transacao", produces = {"application/json"})
@Tag(name = "Rotas de Transacao", description = "API de transações bancárias")
public class TransacaoController {
       
    @Autowired
    private TransacaoService transacaoService;

    @PostMapping("/")
    public ResponseEntity<TransacaoModel> create(@RequestBody TransacaoDto transacaoDto) {
        TransacaoModel newTransacao = transacaoService.create(transacaoDto);
        return new ResponseEntity<>(newTransacao, HttpStatus.CREATED);
    }
    
    @GetMapping("/")
     public ResponseEntity<List<TransacaoModel>> read() {
        List<TransacaoModel> clientes = transacaoService.getAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/id")
    public ResponseEntity<TransacaoModel> readById(UUID id) {
        return ResponseEntity.of(transacaoService.getById(id));
    }

 
    @DeleteMapping("/")
    public ResponseEntity<Void> delete(UUID id) {
        transacaoService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}

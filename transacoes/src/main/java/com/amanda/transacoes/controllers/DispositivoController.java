package com.amanda.transacoes.controllers;
import com.amanda.transacoes.dtos.DispositivoDto;
import com.amanda.transacoes.models.DispositivoModel;
import com.amanda.transacoes.services.DispositivoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping (value = "/dispositivo", produces = {"application/json"})
@Tag(name = "Rotas de Dispositivo", description = "API de transações bancárias")
public class DispositivoController {
    
    @Autowired
    private DispositivoService dispositivoService;

    @PostMapping("/")
    public ResponseEntity<DispositivoModel> create(@RequestBody DispositivoDto dispositivoDto) {
       DispositivoModel newDispositivo = dispositivoService.create(dispositivoDto);
       return new ResponseEntity<>(newDispositivo, HttpStatus.CREATED);
    }

    @GetMapping("/")
     public ResponseEntity<List<DispositivoModel>> read() {
        List<DispositivoModel> dispositivos = dispositivoService.getAll();
        return ResponseEntity.ok(dispositivos);
    }

    
}

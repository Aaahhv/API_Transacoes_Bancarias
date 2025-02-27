package com.amanda.transacoes.controllers;
import com.amanda.transacoes.dtos.DispositivoDto;
import com.amanda.transacoes.models.DispositivoModel;
import com.amanda.transacoes.services.DispositivoService;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/id")
    public ResponseEntity<DispositivoModel> readById(UUID id) {
        return ResponseEntity.of(dispositivoService.getById(id));
    }

    @PutMapping("/")
    public ResponseEntity<DispositivoModel> update(@RequestBody DispositivoDto dispositivoDto, UUID id) {
        DispositivoModel dispositivo = dispositivoService.update(dispositivoDto, id);
        return new ResponseEntity<>(dispositivo, HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> delete(UUID id) {
        dispositivoService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/id/ativo")
    public ResponseEntity<DispositivoModel> ativo(UUID id, boolean ativo) {
        return ResponseEntity.ok(dispositivoService.ativar(id, ativo));
    }


    
}

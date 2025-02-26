package com.amanda.transacoes.controllers;

import com.amanda.transacoes.dtos.ClienteDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.services.ClienteService;
import com.amanda.transacoes.utils.CpfUtil;

import java.util.List;
import java.util.UUID;

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
@RequestMapping (value = "/cliente", produces = {"application/json"})
@Tag(name = "transacoes-bancarias", description = "API de transações bancárias")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;

    @PostMapping("/create")
    public ResponseEntity<ClienteModel> create(@RequestBody ClienteDto clienteDto) {
       ClienteModel newCliente = clienteService.create(clienteDto);
       return new ResponseEntity<>(newCliente, HttpStatus.CREATED);
    }

    
    @GetMapping("/read")
     public ResponseEntity<List<ClienteModel>> read() {
        List<ClienteModel> clientes = clienteService.getAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/id")
    public ResponseEntity<ClienteModel> get(UUID id) {
        return ResponseEntity.of(clienteService.getById(id));
        
        /*Optional<ClienteModel> cliente = clienteService.getById(id);
        if (cliente.isPresent()) {
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
   }

    //updateNome

    //updateCpf

    //delete

    //PATCH
    //inactivate



}

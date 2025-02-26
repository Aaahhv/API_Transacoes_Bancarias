package com.amanda.transacoes.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.DispositivoDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.models.DispositivoModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.repositories.DispositivoRepository;

@Service
public class DispositivoService {
    
    @Autowired
    private DispositivoRepository dispositivoRepository; 
    
    @Autowired
    private ClienteRepository clienteRepository;

    public DispositivoModel create(DispositivoDto dispositivoDto) {
        if(dispositivoDto.getDescricao().isEmpty() || dispositivoDto.getDescricao() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição não deve ser nula ou vazia");
        }
        
        if(dispositivoDto.getClienteId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O clienteID não deve ser nulo");
        }
        
        ClienteModel cliente = clienteRepository.findById(dispositivoDto.getClienteId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        
        DispositivoModel dispositivo = new DispositivoModel(dispositivoDto.getDescricao(), true, cliente);
        return dispositivoRepository.save(dispositivo);
       
    }

    
    public List<DispositivoModel> getAll() {
        return dispositivoRepository.findAll();
        }
        
    public Optional<DispositivoModel> getById(UUID id) {
        return dispositivoRepository.findById(id);
        }
   
        
/* 
    public DispositivoModel update(DispositivoDto clienteDto, UUID id) {
        
    }
            
public void deleteById(UUID id) {
    if (!dispositivoRepository.existsById(id)) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
    }
    dispositivoRepository.deleteById(id);
}

public Optional<DispositivoModel> ativar(UUID id, boolean ativo) {
    DispositivoModel cliente = dispositivoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    cliente.setAtivo(ativo);
    dispositivoRepository.save(cliente);
    
    return dispositivoRepository.findById(id);
    }
    */
}



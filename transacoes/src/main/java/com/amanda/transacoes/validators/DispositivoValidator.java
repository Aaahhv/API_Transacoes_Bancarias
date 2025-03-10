package com.amanda.transacoes.validators;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.DispositivoDto;
import com.amanda.transacoes.repositories.DispositivoRepository;
import com.amanda.transacoes.services.ClienteService;

@Component
public class DispositivoValidator {

    private final DispositivoRepository dispositivoRepository;
    private final ClienteService clienteService;

    public DispositivoValidator(DispositivoRepository dispositivoRepository, ClienteService clienteService) {
        this.dispositivoRepository = dispositivoRepository;
        this.clienteService = clienteService;
    }

    public void validateCreate(DispositivoDto dispositivoDto) {
        if(dispositivoDto.getDescricao() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição não deve ser nula.");
        }

        if(dispositivoDto.getDescricao().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição não deve ser vazia.");
        }
         
        if(dispositivoDto.getClienteId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O clienteID não deve ser nulo.");
        }
        
        if(!clienteService.existsById(dispositivoDto.getClienteId())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado.");
        }
    }

    public void validateUpdate(DispositivoDto dispositivoDto) {

        if(dispositivoDto.getClienteId() != null){
            if(!clienteService.existsById(dispositivoDto.getClienteId())){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado.");
            }
        }
    }

    public void validateDelete(UUID id) {

        if (!dispositivoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado.");
        }
    }
}

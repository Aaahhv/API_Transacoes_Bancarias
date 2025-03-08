package com.amanda.transacoes.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.DispositivoDto;
import com.amanda.transacoes.models.DispositivoModel;
import com.amanda.transacoes.repositories.DispositivoRepository;
import com.amanda.transacoes.validators.DispositivoValidator;

import jakarta.transaction.Transactional;

@Service
public class DispositivoService {
    
    @Autowired
    private DispositivoRepository dispositivoRepository; 

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private DispositivoValidator dispositivoValidator;

    public DispositivoModel create(DispositivoDto dispositivoDto) {

        dispositivoValidator.validateCreate(dispositivoDto);
         
        DispositivoModel dispositivo = new DispositivoModel(dispositivoDto.getDescricao(), true, dispositivoDto.getClienteId());
        return dispositivoRepository.save(dispositivo);
    }

    
    public List<DispositivoModel> getAll() {
        return dispositivoRepository.findAll();
    }
        
    public Optional<DispositivoModel> getById(UUID id) {
        return dispositivoRepository.findById(id);
    }
   
        
    public DispositivoModel update(DispositivoDto dispositivoDto, UUID id) {
        
        DispositivoModel dispositivo = dispositivoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado."));
        
        if(dispositivoDto.getDescricao() != null){
            dispositivo.setDescricao(dispositivoDto.getDescricao());
        }

        if(dispositivoDto.getClienteId() != null){
            if(!clienteService.existsById(dispositivoDto.getClienteId())){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado.");
            }
            dispositivo.setClienteId(dispositivoDto.getClienteId());
        }
        
        dispositivoRepository.save(dispositivo);
        return dispositivo;
    }
    
    public void deleteById(UUID id) {

        if (!dispositivoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado.");
        }

        dispositivoRepository.deleteById(id);

    }

    //eu nao sei oque eh Transactional, eu nao sei pra que serve, eu nao sei de onde vem, eu nao sei que gosto tem, mas sem ele nao funciona. 
    @Transactional
    public void deleteByClienteId(UUID clienteId) {

        dispositivoRepository.deleteByClienteId(clienteId);

    }
        
    public DispositivoModel ativar(UUID id, boolean ativo) {

        DispositivoModel dispositivo = dispositivoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado."));
        dispositivo.setAtivo(ativo);
        
        return dispositivoRepository.save(dispositivo);
        }


    public boolean existsById(UUID id){
        return dispositivoRepository.existsById(id);
    }

    public boolean isDispositivoAtivo(UUID id) {
        DispositivoModel dispositivo = dispositivoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));
        
        return dispositivo.getAtivo();
    }
}



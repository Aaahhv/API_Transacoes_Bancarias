package com.amanda.transacoes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.amanda.transacoes.dtos.ClienteDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.utils.CpfUtil;


@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;    

    public ClienteModel create(ClienteDto clienteDto) {
        CpfUtil.isValidCpf(clienteDto.getCpf());
        
        if(clienteDto.getNome().isEmpty() || clienteDto.getNome() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome nao deve ser nulo ou vazio");
        }
        
        ///GERAR NUMERO DA CONTA
        ClienteModel cliente = new ClienteModel(clienteDto.getNome(), CpfUtil.formatsCpf(clienteDto.getCpf()), "1", true, 0);
        return clienteRepository.save(cliente);
    }


    public List<ClienteModel> getAll() {
        return clienteRepository.findAll();
    }

    public Optional<ClienteModel> getById(UUID id) {
        return clienteRepository.findById(id);
    }

    public void deleteById(UUID id) {
        clienteRepository.deleteById(id);
    }

}

package com.amanda.transacoes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.repositories.ClienteRepository;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository usuarioRepository;
    
    // Criar ou atualizar um usuario

    public ClienteModel create(ClienteModel usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<ClienteModel> getAll() {
        return usuarioRepository.findAll();
    }

    // Buscar usuario por ID
    public Optional<ClienteModel> getById(UUID id) {
        return usuarioRepository.findById(id);
    }

    // Deleta usuario por ID
    public void deletarPorId(UUID id) {
        usuarioRepository.deleteById(id);
    }

}

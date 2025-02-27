package com.amanda.transacoes.repositories;

import org.springframework.stereotype.Repository;

import com.amanda.transacoes.models.TransacaoModel;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoModel, UUID> {

    List<TransacaoModel> findAll();
    
    boolean existsById(UUID id);

    void deleteById (UUID id);
}

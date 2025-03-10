package com.amanda.transacoes.repositories;

import com.amanda.transacoes.models.DispositivoModel;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispositivoRepository extends JpaRepository<DispositivoModel, UUID> {
 
    List<DispositivoModel> findAll();
    
    boolean existsById(UUID id);

    void deleteById (UUID id);

    void deleteByClienteId(UUID id);

    List<DispositivoModel> findByClienteId(UUID clienteId);

}

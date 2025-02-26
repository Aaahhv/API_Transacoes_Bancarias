package com.amanda.transacoes.repositories;

import com.amanda.transacoes.models.DispositivoModel;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//@RepositoryRestResource(exported = false) // Desativa os endpoints para este repositório

@Repository
public interface DispositivoRepository extends JpaRepository<DispositivoModel, UUID> {
 
    List<DispositivoModel> findAll();
    
    boolean existsById(UUID id);

    void deleteById (UUID id);
}

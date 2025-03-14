package com.amanda.transacoes.repositories;

import com.amanda.transacoes.models.ClienteModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//@RepositoryRestResource(exported = false) // Desativa os endpoints para este repositório
@Repository
public interface ClienteRepository extends JpaRepository<ClienteModel, UUID> {
    List<ClienteModel> findAll();

    Optional<ClienteModel> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByNumConta(String cpf);

    Optional<ClienteModel> findByNumConta(String cpf);

    void deleteById (UUID id);
}

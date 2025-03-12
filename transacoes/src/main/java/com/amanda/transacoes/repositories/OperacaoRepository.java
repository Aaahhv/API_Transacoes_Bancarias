package com.amanda.transacoes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.OperacaoModel;

import java.util.Optional;
import java.util.UUID;

public interface OperacaoRepository extends JpaRepository<OperacaoModel, UUID> {
    Optional<OperacaoModel> findByTipo(TipoOperacaoEnum tipo);
}
package com.amanda.transacoes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.OperacaoModel;

import java.util.UUID;

public interface OperacaoRepository extends JpaRepository<OperacaoModel, UUID> {
    OperacaoModel findByTipo(TipoOperacaoEnum tipo);
}
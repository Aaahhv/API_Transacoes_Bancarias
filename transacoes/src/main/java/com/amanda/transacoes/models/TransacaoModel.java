package com.amanda.transacoes.models;

import java.util.UUID;

import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TransacaoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; 
import jakarta.persistence.Table; 


@Entity
@Table(name = "Transacoes")

public class TransacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String cc_origem;

    @Column(nullable = false)
    private String cc_destino;

    @Column(nullable = false)
    private String data;

    @Column(nullable = false)
    private boolean valor;

    @Column(nullable = false)
    private OperacaoEnum operacao;

    @Column(nullable = false)
    private TransacaoEnum tipo_transacao;

    @Column(nullable = false)
    private SituacaoOperacaoEnum situacao;

    @Column(nullable = false)
    private UUID dispositivo_id;


    
}

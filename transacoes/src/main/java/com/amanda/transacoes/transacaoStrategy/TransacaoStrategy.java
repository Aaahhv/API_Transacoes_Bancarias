package com.amanda.transacoes.transacaoStrategy;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.services.ClienteService;
import com.amanda.transacoes.services.OperacaoService;

public abstract class  TransacaoStrategy {

    protected final ClienteService clienteService;
    protected final OperacaoService operacaoService;

    public TransacaoStrategy(ClienteService clienteService, OperacaoService operacaoService) {
        this.clienteService = clienteService;
        this.operacaoService = operacaoService;
    }
    
    public abstract TransacaoModel createTransacao(TransacaoDto transa);

}

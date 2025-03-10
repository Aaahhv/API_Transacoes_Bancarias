package com.amanda.transacoes.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.TransacaoRepository;
import com.amanda.transacoes.transacaoStrategy.TransacaoCredito;
import com.amanda.transacoes.transacaoStrategy.TransacaoDebito;
import com.amanda.transacoes.transacaoStrategy.TransacaoStrategy;
import com.amanda.transacoes.validators.TransacaoValidator;

import jakarta.transaction.Transactional;

@Service
public class TransacaoService {   
    
    @Autowired
    private TransacaoRepository transacaoRepository;
    
    @Autowired 
    private ClienteService clienteService;
    
    @Autowired
    private OperacaoService operacaoService;

    @Autowired
    private TransacaoValidator transacaoValidator;

    public TransacaoModel create(TransacaoDto transacaoDto) {

        transacaoValidator.validateCreate(transacaoDto);

        TransacaoStrategy transacao;

        switch (transacaoDto.getOperacao()) {
            case OperacaoEnum.CREDITO:
                transacao = new TransacaoCredito(clienteService, operacaoService);
                break;
            case OperacaoEnum.DEBITO:
                transacao = new TransacaoDebito(clienteService, operacaoService);
                break;
            default:
                throw new IllegalArgumentException("Operação inválida: " + transacaoDto.getOperacao());
}
        TransacaoModel transacaoNova = transacao.createTransacao(transacaoDto);
        return transacaoRepository.save(transacaoNova);
    }


    public List<TransacaoModel> getAll() {
        return transacaoRepository.findAll();
    }

    public Optional<TransacaoModel> getById(UUID id) {
        return transacaoRepository.findById(id);
    }
    
    public List<TransacaoModel> getByClienteId(UUID id) {

        ClienteModel cliente = clienteService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrada."));
        
        List<TransacaoModel> transacoesDestino = transacaoRepository.findByCcDestino(cliente.getNumConta());

        List<TransacaoModel> transacoesOrigem = transacaoRepository.findByCcOrigem(cliente.getNumConta());

        List<TransacaoModel> transacoes = new ArrayList<>(transacoesDestino);

        transacoes.addAll(transacoesOrigem);

        return transacoes;
    }

    @Transactional
    public void deleteByClienteId(ClienteModel cliente) {

        List<TransacaoModel> transacoesDestino = transacaoRepository.findByCcDestino(cliente.getNumConta());

        for (TransacaoModel transacao : transacoesDestino) {

            transacao.setCcDestino("XXXXXX");

            if(!clienteService.existsByNumConta(transacao.getCcOrigem()) ){
                deleteById(transacao.getId());
            }
        }

        List<TransacaoModel> transacoesOrigem = transacaoRepository.findByCcOrigem(cliente.getNumConta());

        for (TransacaoModel transacao : transacoesOrigem) {

            transacao.setCcOrigem("XXXXXX");

            if(!clienteService.existsByNumConta(transacao.getCcDestino()) ){
                deleteById(transacao.getId());
            }
        }
    } 

    public void deleteById(UUID id) {
    
        transacaoValidator.ValidateDeleteById(id);
    
        transacaoRepository.deleteById(id);
    }
}

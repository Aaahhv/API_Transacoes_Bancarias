package com.amanda.transacoes.services;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.OperacaoModel;
import com.amanda.transacoes.repositories.OperacaoRepository;

import java.time.LocalTime;
import java.util.List;

@Service
public class OperacaoService {
    

    @Autowired
    private OperacaoRepository operacaoRepository;


    @PostConstruct
    public void inicializarConfiguracoes() {
        //DEPOSITO
        if (operacaoRepository.findByTipo(TipoOperacaoEnum.DEPOSITO) == null) {
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.DEPOSITO, true);
            operacaoRepository.save(novaConfig);
        }
 
        //SAQUE  
        if (operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE) == null) {
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.SAQUE, true);
            operacaoRepository.save(novaConfig);
        }

        //PIX
        if (operacaoRepository.findByTipo(TipoOperacaoEnum.PIX) == null) {
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.PIX, true);
            operacaoRepository.save(novaConfig);
        }

        //TED
        if (operacaoRepository.findByTipo(TipoOperacaoEnum.TED) == null ) {
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.TED, 20, true, 60000, LocalTime.of(6, 30), LocalTime.of(17, 0) ); // Ativado por padrão
            operacaoRepository.save(novaConfig);
        }

        //DOC
        if (operacaoRepository.findByTipo(TipoOperacaoEnum.DOC)== null) {
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.DOC, 10, true, 4999); 
            operacaoRepository.save(novaConfig);
        }
    }

    public List<OperacaoModel> getAll(){
        return operacaoRepository.findAll();
    }

    public OperacaoModel updateHora(TipoOperacaoEnum tipo, LocalTime horaInicio, LocalTime horaFim){

        if (horaInicio != null && horaFim != null) {
            OperacaoModel operacao = operacaoRepository.findByTipo(tipo);

            operacao.setHorarioInicio(horaInicio);
            operacao.setHorarioFim(horaFim);

            return operacaoRepository.save(operacao);
        }
        throw new IllegalArgumentException("NAO SEI O QUE HOUVE EM UPDATEHORA DE OPERACAOSERVICE");
    }

    public OperacaoModel getByTipo(TipoOperacaoEnum tipo){
        return operacaoRepository.findByTipo(tipo);
    }


}


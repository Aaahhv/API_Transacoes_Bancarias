package com.amanda.transacoes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.OperacaoDto;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.OperacaoModel;
import com.amanda.transacoes.repositories.OperacaoRepository;
import com.amanda.transacoes.validators.OperacaoValidator;

import java.time.LocalTime;
import java.util.List;

@Service
public class OperacaoService {
    
    @Autowired
    private OperacaoRepository operacaoRepository;

    @Autowired
    private OperacaoValidator operacaoValidator;

    @PostConstruct
    public void inicializarConfiguracoes() {

        List<OperacaoModel> operacoesPadrao = List.of(
            new OperacaoModel(TipoOperacaoEnum.DEPOSITO, 0, true, Double.POSITIVE_INFINITY, LocalTime.of(0, 0), LocalTime.of(23, 59)),
            new OperacaoModel(TipoOperacaoEnum.SAQUE, 0, true, Double.POSITIVE_INFINITY, LocalTime.of(0, 0), LocalTime.of(23, 59)),
            new OperacaoModel(TipoOperacaoEnum.PIX, 0, true, Double.POSITIVE_INFINITY, LocalTime.of(0, 0), LocalTime.of(23, 59)),
            new OperacaoModel(TipoOperacaoEnum.TED, 20, true, 60000.0, LocalTime.of(6, 30), LocalTime.of(17, 0)),
            new OperacaoModel(TipoOperacaoEnum.DOC, 10, true, 4999.0, LocalTime.of(0, 0), LocalTime.of(23, 59))
        );
    
        for (OperacaoModel operacao : operacoesPadrao) {
            if (operacaoRepository.findByTipo(operacao.getTipo()).isEmpty()) {
                operacaoRepository.save(operacao);
            }
        }
    }

    public List<OperacaoModel> getAll(){
        return operacaoRepository.findAll();
    }

    public OperacaoModel update(OperacaoDto operacaoDto){
        OperacaoModel operacaoModel = operacaoRepository.findByTipo(operacaoDto.getTipo()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operação não encontrada."));
        
        operacaoValidator.validateUpdate(operacaoDto);

        operacaoModel.setTaxa(operacaoDto.getTaxa());
        operacaoModel.setAtivo(operacaoDto.getAtivo());

        operacaoModel.setLimiteValor(Double.POSITIVE_INFINITY);
        if(operacaoDto.getLimiteValor() > 0 ){
            operacaoModel.setLimiteValor(operacaoDto.getLimiteValor());
        }
    
        operacaoModel.setHorarioInicio(operacaoDto.getHorario().getHoraInicio());
        operacaoModel.setHorarioFim(operacaoDto.getHorario().getHoraFim());

        return operacaoRepository.save(operacaoModel);
    }

    public OperacaoModel getByTipo(TipoOperacaoEnum tipo){
        return operacaoRepository.findByTipo(tipo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operação não encontrada."));
    }

    public double getLimiteValor(TipoOperacaoEnum tipo){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operação não encontrada."));
        return operacao.getLimiteValor();
    }
    
    public double getTaxaOperacao(TipoOperacaoEnum tipo){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operação não encontrada."));
        return operacao.getTaxa();
    }

    public boolean isTipoDeOperacaoAtiva(TipoOperacaoEnum tipo){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operação não encontrada."));
        return operacao.getAtivo();
    }
    
    public boolean isLimiteValorValido(TipoOperacaoEnum tipo, double valor){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operação não encontrada."));
        return valor <= operacao.getLimiteValor();
    }

    public boolean isHorarioValido(TipoOperacaoEnum tipo, LocalTime horario){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operação não encontrada."));
        return horario.isAfter(operacao.getHorarioInicio()) && horario.isBefore(operacao.getHorarioFim());
    }


}


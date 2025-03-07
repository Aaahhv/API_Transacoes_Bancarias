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
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.DEPOSITO, 0, true, Double.POSITIVE_INFINITY, LocalTime.of(0, 0), LocalTime.of(23, 59) );
            operacaoRepository.save(novaConfig);
        }
 
        //SAQUE  
        if (operacaoRepository.findByTipo(TipoOperacaoEnum.SAQUE) == null) {
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.SAQUE, 0, true, Double.POSITIVE_INFINITY, LocalTime.of(0, 0), LocalTime.of(23, 59) );
            operacaoRepository.save(novaConfig);
        }

        //PIX
        if (operacaoRepository.findByTipo(TipoOperacaoEnum.PIX) == null) {
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.PIX, 0, true, Double.POSITIVE_INFINITY, LocalTime.of(0, 0), LocalTime.of(23, 59) );
            operacaoRepository.save(novaConfig);
        }

        //TED
        if (operacaoRepository.findByTipo(TipoOperacaoEnum.TED) == null ) {
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.TED, 20, true, 60000.0, LocalTime.of(6, 30), LocalTime.of(17, 0) ); // Ativado por padrão
            operacaoRepository.save(novaConfig);
        }

        //DOC
        if (operacaoRepository.findByTipo(TipoOperacaoEnum.DOC)== null) {
            OperacaoModel novaConfig = new OperacaoModel(TipoOperacaoEnum.DOC, 10, true, 4999.0, LocalTime.of(0, 0), LocalTime.of(23, 59) );
            operacaoRepository.save(novaConfig);
        }
    }

    public List<OperacaoModel> getAll(){
        return operacaoRepository.findAll();
    }

    public OperacaoModel update(OperacaoDto operacaoDto){
        OperacaoModel operacaoModel = operacaoRepository.findByTipo(operacaoDto.getTipo());

        if(operacaoModel == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operação não encontrada.");
        }

        if(operacaoDto.getHorario().getHoraInicio().isAfter(operacaoDto.getHorario().getHoraFim())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horário de início não pode ser maior que horário de fim.");
        }

        if(operacaoDto.getTaxa() < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A taxa não pode ser negativa.");
        }
        
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
        return operacaoRepository.findByTipo(tipo);
    }

    public double getLimiteValor(TipoOperacaoEnum tipo){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo);
        return operacao.getLimiteValor();
    }
    
    public double getTaxaOperacao(TipoOperacaoEnum tipo){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo);
        return operacao.getTaxa();
    }

    public boolean isTipoDeOperacaoAtiva(TipoOperacaoEnum tipo){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo);
        return operacao.getAtivo();
    }
    
    public boolean isLimiteValorValido(TipoOperacaoEnum tipo, double valor){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo);
        return valor <= operacao.getLimiteValor();
    }

    public boolean isHorarioValido(TipoOperacaoEnum tipo, LocalTime horario){
        OperacaoModel operacao = operacaoRepository.findByTipo(tipo);
        return horario.isAfter(operacao.getHorarioInicio()) && horario.isBefore(operacao.getHorarioFim());
    }


}


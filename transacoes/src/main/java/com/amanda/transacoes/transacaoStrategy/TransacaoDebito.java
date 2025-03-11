package com.amanda.transacoes.transacaoStrategy;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.services.ClienteService;
import com.amanda.transacoes.services.OperacaoService;

public class TransacaoDebito extends  TransacaoStrategy {
    
    public TransacaoDebito(ClienteService clienteService, OperacaoService operacaoService) {
        super(clienteService, operacaoService);
    }

    public TransacaoModel createTransacao(TransacaoDto transacaoDto){

        if(transacaoDto.getTipoOperacao() == TipoOperacaoEnum.SAQUE){
            return createTransacaoSaque(transacaoDto);
        }
        return createTransacaoPixTedDoc(transacaoDto);
    }

    public TransacaoModel createTransacaoSaque(TransacaoDto transacaoDto){
    
        clienteService.debitar(transacaoDto.getCcOrigem(), transacaoDto.getValor(),operacaoService.getTaxaOperacao(transacaoDto.getTipoOperacao()));

        return new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao(), SituacaoOperacaoEnum.CONCLUIDO, transacaoDto.getDispositivoId()); 
    }

    public TransacaoModel createTransacaoPixTedDoc(TransacaoDto transacaoDto){
    
        if(transacaoDto.getCcOrigem().startsWith("159")){
            clienteService.debitar(transacaoDto.getCcOrigem(), transacaoDto.getValor(), operacaoService.getTaxaOperacao(transacaoDto.getTipoOperacao()));
        }
        
        if(transacaoDto.getCcDestino().startsWith("159")){
            clienteService.creditar(transacaoDto.getCcDestino(), transacaoDto.getValor(), 0);
        }

        return new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao(), SituacaoOperacaoEnum.CONCLUIDO, transacaoDto.getDispositivoId()); 
    }
}

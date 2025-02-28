package com.amanda.transacoes.services;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.TransacaoRepository;

@Service
public class TransacaoService {   
    
    @Autowired 
    private ClienteService clienteService;
    
    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private OperacaoService operacaoService;

    public TransacaoModel create(TransacaoDto transacaoDto) {
        

        //TAXA
        //CLIENTE NAO SER 159


        if(transacaoDto.getValor() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor da transacao deve ser maior que zero");}

        if(!operacaoService.isOperacaoAtiva(transacaoDto.getTipoOperacao())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao inativa");}

        if(!operacaoService.isLimiteValorValido(transacaoDto.getTipoOperacao(),transacaoDto.getValor())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor da transacao excede o limite da operacao");}

        if(!operacaoService.isHorarioValido(transacaoDto.getTipoOperacao(), LocalTime.now())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horario invalido para a operacao");}

        if(!transacaoDto.getCcDestino().startsWith("159") && !transacaoDto.getCcOrigem().startsWith("159")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhuma conta nessa transacao pertence a nossa instituicao");}

        
        if(transacaoDto.getTipoOperacao() == TipoOperacaoEnum.DEPOSITO){
            return operacaoCreditoDeposito(transacaoDto);
        }
        if(transacaoDto.getTipoOperacao() == TipoOperacaoEnum.SAQUE){
            return operacaoDebitoSaque(transacaoDto);
        }

        if(transacaoDto.getCcDestino().startsWith("159") == transacaoDto.getCcOrigem().startsWith("159")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao é possivel enviar " + transacaoDto.getTipoOperacao() + " para a mesma conta");}

        if(!clienteService.isClienteAtivo(transacaoDto.getCcOrigem())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de origem inativa");
        }
        if(!clienteService.isClienteAtivo(transacaoDto.getCcDestino())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de destino inativa");
        }
    
        if(transacaoDto.getOperacao() == OperacaoEnum.CREDITO){
            return operacaoCredito(transacaoDto);
        }
        if(transacaoDto.getOperacao() == OperacaoEnum.DEBITO){
            return operacaoDebito(transacaoDto);
        }

        TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());
        return transacaoRepository.save(transacao);
    }


public TransacaoModel operacaoCredito(TransacaoDto transacaoDto){
    if(transacaoDto.getCcOrigem().startsWith("159")){
        clienteService.creditar(transacaoDto.getCcOrigem(), transacaoDto.getValor(), 0);
    }
    if(transacaoDto.getCcDestino().startsWith("159")){
        clienteService.debitar(transacaoDto.getCcDestino(), transacaoDto.getValor(), operacaoService.getTaxaOperacao(transacaoDto.getTipoOperacao()));
    }
    TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());
    return transacaoRepository.save(transacao);
}

public TransacaoModel operacaoCreditoDeposito(TransacaoDto transacaoDto){
    if(transacaoDto.getOperacao() == OperacaoEnum.DEBITO){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao de DEPOSITO nao pode ser do tipo DEBITO");
    }
    if(!clienteService.isClienteAtivo(transacaoDto.getCcOrigem())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de origem inativa");
    }

    clienteService.creditar(transacaoDto.getCcOrigem(), transacaoDto.getValor(), operacaoService.getTaxaOperacao(transacaoDto.getTipoOperacao()));

    TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());
    return transacaoRepository.save(transacao);

}

public TransacaoModel operacaoDebito(TransacaoDto transacaoDto){
    if(transacaoDto.getCcOrigem().startsWith("159")){
        clienteService.debitar(transacaoDto.getCcOrigem(), transacaoDto.getValor(), operacaoService.getTaxaOperacao(transacaoDto.getTipoOperacao()));
    }
    
    if(transacaoDto.getCcDestino().startsWith("159")){
        clienteService.creditar(transacaoDto.getCcDestino(), transacaoDto.getValor(), 0);
    }

    TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());
    return transacaoRepository.save(transacao);

}

public TransacaoModel operacaoDebitoSaque(TransacaoDto transacaoDto){
    if(transacaoDto.getOperacao() == OperacaoEnum.CREDITO){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao de SAQUE nao pode ser do tipo CREDITO");
    }
    if(!clienteService.isClienteAtivo(transacaoDto.getCcOrigem())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de origem inativa");
    }

    clienteService.debitar(transacaoDto.getCcOrigem(), transacaoDto.getValor(),operacaoService.getTaxaOperacao(transacaoDto.getTipoOperacao()));

    TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());
    return transacaoRepository.save(transacao);
}


    public List<TransacaoModel> getAll() {
        return transacaoRepository.findAll();
    }

    public Optional<TransacaoModel> getById(UUID id) {
        return transacaoRepository.findById(id);
    }

    public void deleteById(UUID id) {
        if (!transacaoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacao não encontrado");
        }

        transacaoRepository.deleteById(id);
    }
}

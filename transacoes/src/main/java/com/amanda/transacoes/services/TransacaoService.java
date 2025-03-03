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
    private TransacaoRepository transacaoRepository;
    
    @Autowired 
    private ClienteService clienteService;
    
    @Autowired
    private OperacaoService operacaoService;

    public TransacaoModel create(TransacaoDto transacaoDto) {

        validarTransacao(transacaoDto);
    
        if(transacaoDto.getOperacao() == OperacaoEnum.CREDITO){
            return operacaoCredito(transacaoDto);
        }
        if(transacaoDto.getOperacao() == OperacaoEnum.DEBITO){
            return operacaoDebito(transacaoDto);
        }

        TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());
        return transacaoRepository.save(transacao);
    }

public void validarTransacao(TransacaoDto transacaoDto){
    if(transacaoDto.getValor() <= 0){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor da transação deve ser maior que zero.");}

    if(!operacaoService.isOperacaoAtiva(transacaoDto.getTipoOperacao())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operação inativa.");}

    if(!operacaoService.isLimiteValorValido(transacaoDto.getTipoOperacao(),transacaoDto.getValor())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor da transação excede o limite de " + operacaoService.getLimiteValor(transacaoDto.getTipoOperacao()) + " da operacao " + transacaoDto.getTipoOperacao() + ".");}

    if(!operacaoService.isHorarioValido(transacaoDto.getTipoOperacao(), LocalTime.now())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horario inválido para a operação");}

    if(transacaoDto.getTipoOperacao() == TipoOperacaoEnum.DEPOSITO || transacaoDto.getTipoOperacao() == TipoOperacaoEnum.SAQUE){

        if(!clienteService.isClienteAtivo(transacaoDto.getCcOrigem())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de origem inativa.");
        }

        if (!transacaoDto.getCcDestino().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tipo de operacao " + transacaoDto.getTipoOperacao() +" a conta de destino deve ser vazia.");}

    }else{

        if (transacaoDto.getCcOrigem().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A conta de origem não deve ser vazia.");
        }

        
        if (transacaoDto.getCcDestino().isEmpty() ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A conta de destino não deve ser vazia.");
        }

        if(!transacaoDto.getCcDestino().startsWith("159") && !transacaoDto.getCcOrigem().startsWith("159")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhuma conta nessa transação pertence a nossa instituição.");}

        if(transacaoDto.getCcDestino().equals(transacaoDto.getCcOrigem())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao é possivel enviar " + transacaoDto.getTipoOperacao() + " para a mesma conta.");}
    
    }

}

public TransacaoModel operacaoCredito(TransacaoDto transacaoDto){

    if(transacaoDto.getTipoOperacao() == TipoOperacaoEnum.DEPOSITO){
        operacaoCreditoDeposito(transacaoDto);
    }

    if(transacaoDto.getTipoOperacao() == TipoOperacaoEnum.SAQUE){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao de CREDITO não pode ser do tipo SAQUE.");
    }

    if(transacaoDto.getCcOrigem().startsWith("159")){
        if(!clienteService.isClienteAtivo(transacaoDto.getCcOrigem())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de ORIGEM inativa.");
        }
        clienteService.creditar(transacaoDto.getCcOrigem(), transacaoDto.getValor(), 0);
    }

    if(transacaoDto.getCcDestino().startsWith("159")){
        if(!clienteService.isClienteAtivo(transacaoDto.getCcDestino())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de destino inativa.");
        }
        clienteService.debitar(transacaoDto.getCcDestino(), transacaoDto.getValor(), operacaoService.getTaxaOperacao(transacaoDto.getTipoOperacao()));
    }

    TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());
    return transacaoRepository.save(transacao);
}

public TransacaoModel operacaoDebito(TransacaoDto transacaoDto){

    if(transacaoDto.getTipoOperacao() == TipoOperacaoEnum.DEPOSITO){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao de DEBITO não pode ser do tipo DEPOSITO.");
    }
    
    if(transacaoDto.getTipoOperacao() == TipoOperacaoEnum.SAQUE){
        operacaoDebitoSaque(transacaoDto);
    }

    if(transacaoDto.getCcOrigem().startsWith("159")){
        if(!clienteService.isClienteAtivo(transacaoDto.getCcOrigem())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de ORIGEM inativa.");
        }
        clienteService.debitar(transacaoDto.getCcOrigem(), transacaoDto.getValor(), operacaoService.getTaxaOperacao(transacaoDto.getTipoOperacao()));
    }
    
    if(transacaoDto.getCcDestino().startsWith("159")){
        if(!clienteService.isClienteAtivo(transacaoDto.getCcDestino())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de destino inativa.");
        }
        clienteService.creditar(transacaoDto.getCcDestino(), transacaoDto.getValor(), 0);
    }

    TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());
    return transacaoRepository.save(transacao);

}

public TransacaoModel operacaoCreditoDeposito(TransacaoDto transacaoDto){

    if(!clienteService.isClienteAtivo(transacaoDto.getCcOrigem())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de origem inativa.");
    }

    clienteService.creditar(transacaoDto.getCcOrigem(), transacaoDto.getValor(), operacaoService.getTaxaOperacao(transacaoDto.getTipoOperacao()));

    TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());
    return transacaoRepository.save(transacao);

}

public TransacaoModel operacaoDebitoSaque(TransacaoDto transacaoDto){
    if(!clienteService.isClienteAtivo(transacaoDto.getCcOrigem())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de origem inativa.");
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada.");
        }

        transacaoRepository.deleteById(id);
    }
}

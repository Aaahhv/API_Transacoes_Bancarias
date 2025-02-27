package com.amanda.transacoes.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.SituacaoOperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.TransacaoRepository;

@Service
public class TransacaoService {   
    
    @Autowired 
    private ClienteService clienteService;
    
    @Autowired
    private TransacaoRepository transacaoRepository;

    public TransacaoModel create(TransacaoDto transacaoDto) {
        
        //EXISTE ALGUMA MANEIRA MELHOR DE FAZER ISSO? TIPO, ACUMULAR TODOS OS ERROS E EXIBIR TUDO DE UMA VEZ?

        if(transacaoDto.getValor() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor da transacao deve ser maior que zero");
        }

        if(transacaoDto.getOperacao() == OperacaoEnum.CREDITO){
            if(transacaoDto.getTipoTransacao() == TipoOperacaoEnum.SAQUE){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao de credito nao pode ser do tipo saque");
            }
        }
        if(transacaoDto.getOperacao() == OperacaoEnum.DEBITO){
            if(transacaoDto.getTipoTransacao() == TipoOperacaoEnum.DEPOSITO){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao de debito nao pode ser do tipo deposito");
            }
        }
        
        if(!transacaoDto.getCcDestino().startsWith("159") && !transacaoDto.getCcDestino().startsWith("159")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhuma conta nessa transacao pertence a a nossa instituicao");
        }

        TransacaoModel transacao = new TransacaoModel(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(),transacaoDto.getValor(), transacaoDto.getOperacao(), transacaoDto.getTipoTransacao());
        return transacaoRepository.save(transacao);
    }


/*
 * 
 public TransacaoModel OperacaoSaque(TransacaoDto transacaoDto){
    
 
}
*/
/*
 
public TransacaoModel OperacaoDeposito(TransacaoDto transacaoDto) {
    if(transacaoDto.getCcOrigem().isEmpty() || transacaoDto.getCcOrigem() == null){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O numero da conta de origem nao deve ser nulo ou vazio");
    }
    
    if(transacaoDto.getCcDestino().isEmpty() || transacaoDto.getCcDestino() == null){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O numero da conta de destino deve ser nulo ou vazio");
    }
    
    if(transacaoDto.getValor() <= 0){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor da transacao deve ser maior que zero");
    }
    
    
}
public TransacaoModel OperacaoPixCredito(TransacaoDto transacaoDto) {
    
}

public TransacaoModel OperacaoPixDebito(TransacaoDto transacaoDto) {
    
}
*/


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

package com.amanda.transacoes.validators;

import java.time.LocalTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.TransacaoDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.repositories.TransacaoRepository;
import com.amanda.transacoes.services.ClienteService;
import com.amanda.transacoes.services.DispositivoService;
import com.amanda.transacoes.services.OperacaoService;
import com.amanda.transacoes.transacaoStrategy.TransacaoCredito;
import com.amanda.transacoes.transacaoStrategy.TransacaoDebito;

@Component
public class TransacaoValidator {

    private final TransacaoRepository transacaoRepository;
    private final ClienteService clienteService;
    private final OperacaoService operacaoService;
    private final DispositivoService dispositivoService;

    public TransacaoValidator(TransacaoRepository transacaoRepository, DispositivoService dispositivoService, ClienteService clienteService, OperacaoService operacaoService) {
        this.transacaoRepository = transacaoRepository;
        this.dispositivoService = dispositivoService;
        this.clienteService = clienteService;
        this.operacaoService = operacaoService;
    }

    public void validateCreate(TransacaoDto transacaoDto) {

        validarOperacao(transacaoDto.getOperacao(), transacaoDto.getTipoOperacao());

        validarContas(transacaoDto.getCcOrigem(), transacaoDto.getCcDestino(), transacaoDto.getTipoOperacao());

        validarDispositivo(transacaoDto.getDispositivoId());

        validarValor(transacaoDto.getTipoOperacao(), transacaoDto.getValor());

        validarHorario(transacaoDto.getTipoOperacao());
    }

    public void validarHorario(TipoOperacaoEnum tipo){
        if(!operacaoService.isHorarioValido(tipo, LocalTime.now())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horario inválido para a operação");}
    }

    public void validarValor(TipoOperacaoEnum tipo, double valor){

        if(valor <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor da transação deve ser maior que zero.");
        }

        if(!operacaoService.isLimiteValorValido(tipo,valor)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor da transação excede o limite do tipo de operação.");
        }
    }

    public void validarOperacao(OperacaoEnum operacao, TipoOperacaoEnum tipo){
        if(!operacaoService.isTipoDeOperacaoAtiva(tipo)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tipo de operação inativo.");}

        if(operacao == OperacaoEnum.CREDITO && tipo == TipoOperacaoEnum.SAQUE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao de CREDITO não pode ser do tipo SAQUE.");
        }

        if(operacao == OperacaoEnum.DEBITO && tipo == TipoOperacaoEnum.DEPOSITO){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operacao de DEBITO não pode ser do tipo DEPOSITO.");
        }
    }

    public void validarContas(String ccOrigem, String ccDestino, TipoOperacaoEnum tipo){

        switch (tipo) {
            case SAQUE:
            case DEPOSITO:
                validarContaDepositoSaque(ccOrigem, ccDestino, tipo);
                break;
            case PIX:
            case TED:
            case DOC:
                validarContaPixTedDoc(ccOrigem, ccDestino, tipo);
                break;
            default:
                throw new IllegalArgumentException("Operação inválida: " + tipo);
        }
    }

    //No DEPOSITO e SAQUE: ccOrigem deve pertencer a instituicao, ccOrigem deve estar ativo e ccDestino deve ser empty
    public void validarContaDepositoSaque(String ccOrigem, String ccDestino, TipoOperacaoEnum tipo){
        if(!ccOrigem.startsWith("159")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A conta não pertence ao banco");
        }

        if(!clienteService.isClienteAtivo(ccOrigem)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta de origem inativa.");
        }

        if (!ccDestino.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tipo de operacao " + tipo +" a conta de destino deve ser vazia.");}
    }

    //No PIX, TED e DOC: (ccOrigem ou ccDestino deve pertencer a instituicao) && (se cc pertence a instituicao, então deve estar ativo) && (ccOrigem != ccDestino)
    public void validarContaPixTedDoc(String ccOrigem, String ccDestino, TipoOperacaoEnum tipo){

        if (ccOrigem.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A conta de origem não deve ser vazia.");
        }

        if (ccDestino.isEmpty() ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A conta de destino não deve ser vazia.");
        }

        if(!ccDestino.startsWith("159") && !ccOrigem.startsWith("159")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhuma conta nessa transação pertence a nossa instituição.");}

        if(ccOrigem.startsWith("159")){
            if(!clienteService.isClienteAtivo(ccOrigem)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta de origem inativa.");
            }
        }

        if(ccDestino.startsWith("159")){
            if(!clienteService.isClienteAtivo(ccDestino)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta de destino inativa.");
            }
        }

        if(ccDestino.equals(ccOrigem)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao é possivel enviar " + tipo + " para a mesma conta.");}
    }


    public void validarDispositivo(UUID dispositivoId){
        
        if(!dispositivoService.isDispositivoAtivo(dispositivoId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Dispositivo inativo");
        }

        // ?? validar se dispositivo pertence a conta de origem, se a conta de origem for do banco ?
        // ?? se a transacao tiver origem em outro banco, deve existir dispositivoId ?
        // ?? se for SAQUE/DEPOSITO, o dispositvoID seria o id do caixa eletronico ?

    }

    public void ValidateDeleteById(UUID id) {
        if (!transacaoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada.");
        }
    }
}
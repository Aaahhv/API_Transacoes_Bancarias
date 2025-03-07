package com.amanda.transacoes.validators;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.ClienteDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.utils.CpfUtil;
import com.amanda.transacoes.utils.NomeUtil;

@Component
public class ClienteValidator {
    
    private final ClienteRepository clienteRepository;

    public ClienteValidator(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void validateCreate(ClienteDto clienteDto) {

        isCpfValido(clienteDto.getCpf());
        
        if(clienteRepository.existsByCpf(CpfUtil.formatsCpf(clienteDto.getCpf()))){
            //Essa mensagem de erro pode ser uma vulnerabilidade -> permite que atacantes descubram se o CPF XXXXXXXXX-XX é cliente do banco
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O CPF já está cadastrado no sistema.");
        }

        if(NomeUtil.isNomeNullOrEmpty(clienteDto.getNome())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome não deve ser vazio ou nulo.");
        }
    }

    public void validateUpdate(ClienteDto clienteDto, UUID id) {

        if(!CpfUtil.isCpfNullOrEmpty(clienteDto.getCpf())){

            isCpfValido(clienteDto.getCpf());   
            clienteDto.setCpf(CpfUtil.formatsCpf(clienteDto.getCpf()));

            ClienteModel clienteByCpf = clienteRepository.findByCpf(clienteDto.getCpf()).orElse(null);
            if (clienteByCpf != null && !clienteByCpf.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O CPF já está cadastrado no sistema.");
            }
        }
    }

    public void validateDebitar(ClienteModel cliente, double valor, double taxa) {
        if(cliente.getSaldo() < (valor+taxa)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente.");
        }

        //if (valor < taxa) nao permitir ?

    }

    public void validateDelete(ClienteModel cliente) {
        if(cliente.getSaldo() != 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O cliente não pode ser deletado porque ainda possui saldo em conta.");
        }
    }

    public void isCpfValido(String cpf){
        if(!CpfUtil.isFormatoValido(cpf)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de CPF invalido, o CPF deve ter 11 numeros.");
        }

        if(CpfUtil.isTodosDigitosIguais(cpf)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF invalido, todos os digitos sao iguais.");
        }

        if(!CpfUtil.isDigitosVerificadoresValidos(cpf)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido, dígitos verificadores não conferem.");
        }
    }
}

package com.amanda.transacoes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import com.amanda.transacoes.dtos.ClienteDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.utils.CpfUtil;
import com.amanda.transacoes.utils.NomeUtil;
import com.amanda.transacoes.validators.ClienteValidator;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    @Lazy //evita dependencia ciclina ente ClienteService e DispotividoService
    private DispositivoService dispositivoService;

    @Autowired
    private ClienteValidator clienteValidator;

    public ClienteModel create(ClienteDto clienteDto) {

        clienteValidator.validateCreate(clienteDto);

        ClienteModel cliente = new ClienteModel(clienteDto.getNome(), CpfUtil.formatsCpf(clienteDto.getCpf()), gerarNumConta(), true, 0);
        return clienteRepository.save(cliente);
    }
    
    public String gerarNumConta() {
        Random random = new Random();
        int numConta = 159000;
        int maxTentativas = 1000; // Evita loop infinito
        int tentativas = 0;
        do {
            if (tentativas >= maxTentativas) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível gerar um novo número de conta.");
            }
            numConta = 159000 + (random.nextInt(1000)); 
            tentativas ++;
        } while (clienteRepository.existsByNumConta(String.valueOf(numConta))); 

        return String.valueOf(numConta);
    }

    public List<ClienteModel> getAll() {
        return clienteRepository.findAll();
    }

    public Optional<ClienteModel> getById(UUID id) {
        return clienteRepository.findById(id);
    }

    public Optional<ClienteModel> getByCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    public boolean existsById(UUID id) {
        return clienteRepository.existsById(id);
    }

    public ClienteModel update(ClienteDto clienteDto, UUID id) {
        ClienteModel cliente = clienteRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
        
        clienteValidator.validateUpdate(clienteDto, id);

        if(!NomeUtil.isNomeNullOrEmpty(clienteDto.getNome())){
            cliente.setNome(clienteDto.getNome());
        }
        
        if(!CpfUtil.isCpfNullOrEmpty(clienteDto.getCpf())){
            cliente.setCpf(CpfUtil.formatsCpf(clienteDto.getCpf()));
        }

        return clienteRepository.save(cliente);
    }

    public void deleteById(UUID id) {
        ClienteModel cliente = clienteRepository.findById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));

        clienteValidator.validateDelete(cliente);

        dispositivoService.deleteByClienteId(id);
        clienteRepository.deleteById(id);
    }

    public ClienteModel ativar(UUID id, boolean ativo) {
        ClienteModel cliente = clienteRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
        cliente.setAtivo(ativo);
        
        return clienteRepository.save(cliente);
    }

    public ClienteModel debitar(String numConta, double valor, double taxa) {
        ClienteModel cliente = clienteRepository.findByNumConta(numConta).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Número de conta "+ numConta + " não encontrado."));
        
        clienteValidator.validateDebitar(cliente, valor, taxa);

        cliente.setSaldo((cliente.getSaldo() - valor) - taxa);
    
        return clienteRepository.save(cliente);
    }

    public ClienteModel creditar(String numConta, double valor, double taxa) {
        ClienteModel cliente = clienteRepository.findByNumConta(numConta).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Número de conta "+ numConta + " não encontrado."));
        
        cliente.setSaldo((cliente.getSaldo() + valor) - taxa);
        
        return clienteRepository.save(cliente);
    }

    public boolean isClienteAtivo(String numConta) {
        ClienteModel cliente = clienteRepository.findByNumConta(numConta).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Número de conta "+ numConta + " não encontrado"));

        return cliente.getAtivo();
    }
}

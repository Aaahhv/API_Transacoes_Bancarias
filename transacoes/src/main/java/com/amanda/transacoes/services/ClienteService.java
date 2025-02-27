package com.amanda.transacoes.services;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DispositivoService dispositivoService;

    public ClienteModel create(ClienteDto clienteDto) {
        CpfUtil.isValidCpf(clienteDto.getCpf());
        
        if(clienteRepository.existsByCpf(CpfUtil.formatsCpf(clienteDto.getCpf()))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esse cpf ja esta cadastrado no sistema");
        }

        if(clienteDto.getNome().isEmpty() || clienteDto.getNome() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome nao deve ser nulo ou vazio");
        }
        
        ClienteModel cliente = new ClienteModel(clienteDto.getNome(), CpfUtil.formatsCpf(clienteDto.getCpf()), gerarNumConta(), true, 0);
        return clienteRepository.save(cliente);
    }
    
    public String gerarNumConta() {
        Random random = new Random();
        int numConta = 159000;
        do {
            numConta = 159000 + (random.nextInt(1000)); 
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

    public ClienteModel update(ClienteDto clienteDto, UUID id) {
        ClienteModel cliente = clienteRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        
        if(!NomeUtil.isNomeNull(clienteDto.getNome())){
            cliente.setNome(clienteDto.getNome());
        }
        
        if(!CpfUtil.isCpfNull(clienteDto.getCpf())){
            CpfUtil.isValidCpf(clienteDto.getCpf());
            
            clienteDto.setCpf(CpfUtil.formatsCpf(clienteDto.getCpf()));

            if(clienteRepository.existsByCpf(clienteDto.getCpf()) ){
                if(!clienteRepository.findByCpf(clienteDto.getCpf()).get().getId().equals(id)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esse cpf ja esta cadastrado no sistema");
                }
            }

            cliente.setCpf(clienteDto.getCpf());
        }

        return clienteRepository.save(cliente);
    }

    public void deleteById(UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }

        dispositivoService.deleteByClienteId(id);
        clienteRepository.deleteById(id);
    }

    public ClienteModel ativar(UUID id, boolean ativo) {
        ClienteModel cliente = clienteRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        cliente.setAtivo(ativo);
        
        return clienteRepository.save(cliente);
    }

    public ClienteModel depositar(String numConta, double valor) {
        ClienteModel cliente = clienteRepository.findByNumConta(numConta).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Numero de conta "+ numConta + " não encontrado"));
        cliente.setSaldo(cliente.getSaldo() + valor);
    
        return clienteRepository.save(cliente);
    }

    public ClienteModel sacar(String numConta, double valor) {
        ClienteModel cliente = clienteRepository.findByNumConta(numConta).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Numero de conta "+ numConta + " não encontrado"));
        if(cliente.getSaldo() < valor){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        }
        cliente.setSaldo(cliente.getSaldo() - valor);
        
        return clienteRepository.save(cliente);
    }

}

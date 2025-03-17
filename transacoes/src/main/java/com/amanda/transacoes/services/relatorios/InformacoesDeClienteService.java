package com.amanda.transacoes.services.relatorios;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.PeriodoDataDto;
import com.amanda.transacoes.dtos.relatorios.ClienteETiposOperacaoDto;
import com.amanda.transacoes.dtos.relatorios.ClienteEValorDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.repositories.TransacaoRepository;
import java.util.stream.Stream;

@Service
public class InformacoesDeClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    public Map<String, Double> getSaldoBanco() {
        
        double saldoBanco = clienteRepository.findAll()
            .stream()
            .mapToDouble(ClienteModel::getSaldo)
            .sum();

        Map<String, Double> relatorio = new  HashMap<>();

        relatorio.put("Saldo do banco", saldoBanco);

        return relatorio;
    }

    public Map<String, Integer> getQuantidadeClientesAtivos() {

        int clientesAtivos = (int) clienteRepository.findAll()   
            .stream()
            .filter(ClienteModel::getAtivo)
            .count();

        Map<String, Integer> relatorio = new  HashMap<>();

        relatorio.put("Quantidade de clientes ativos", clientesAtivos);

        return relatorio;
    }

    public List<ClienteETiposOperacaoDto> getQuantidadeDeTipoOperacaoPorCliente() {
        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        List<ClienteETiposOperacaoDto> listaClientes =  listarTransacoesPorConta(transacoes).entrySet().stream()
            .map(entry -> {
                String ccCliente = entry.getKey();
                List<TransacaoModel> transacoesPorConta = entry.getValue();

                ClienteModel cliente = clienteRepository.findByNumConta(ccCliente)
                    .orElse(null);

                ClienteETiposOperacaoDto clienteDto = new ClienteETiposOperacaoDto(cliente);

                transacoesPorConta.forEach(transacao -> clienteDto.incrementarTipoOperacao(transacao.getTipoOperacao()));

                return clienteDto;
            })
            .collect(Collectors.toList()); 

        return listaClientes;
    }


    public Map<String, List<TransacaoModel>> listarTransacoesPorConta(List<TransacaoModel> transacoes) {
        Map<String, List<TransacaoModel>> transacoesPorCliente = new HashMap<>();

        transacoesPorCliente = transacoes.stream()
            .flatMap(transacao -> Stream.of(
                transacao.getCcOrigem() == null || transacao.getCcOrigem().isEmpty() ? null 
                    : Map.entry(transacao.getCcOrigem(), transacao),
                transacao.getCcDestino() == null || transacao.getCcDestino().isEmpty() ? null 
                    : Map.entry(transacao.getCcDestino(), transacao)
            ))
            .filter(Objects::nonNull)                                                               // essa funcao ja remove entradas nulas 
            .filter(entry -> clienteRepository.existsByNumConta(entry.getKey()))                    // e entradas que numConta nao existem
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList()) 
            ));

        return transacoesPorCliente;
    }

    public Map<String,List<TransacaoModel>> getExtratoClienteComFiltro(String numConta, OperacaoEnum operacao, TipoOperacaoEnum tipoOperacaoEnum){

        List<TransacaoModel> transacoes = getExtratoPorConta(numConta);
        List<TransacaoModel> transacoesFiltradas = new ArrayList<>();

        for(TransacaoModel transacao: transacoes){
            if((transacao.getOperacao() != null      &&  transacao.getOperacao() == operacao) &&
               (transacao.getTipoOperacao() != null  &&  transacao.getTipoOperacao() == tipoOperacaoEnum)){
                transacoesFiltradas.add(transacao);
            }
        }

        Map<String,List<TransacaoModel>> retorno = new HashMap<>();
        retorno.put(numConta,transacoesFiltradas);

        return retorno;
    }

    public Map<YearMonth,  List<ClienteEValorDto>> getClienteCincoMilPorMes() {
        
        TreeMap<YearMonth, List<ClienteEValorDto>> retorno =  transferenciaTotalDeClientesPorMes(transacaoRepository.findAll()).entrySet().stream()
        .map(entry -> Map.entry(entry.getKey(), 
            entry.getValue().entrySet().stream() 
                .filter(clienteEntry -> clienteEntry.getValue() > 5000)             //remove clientes abaixo de 5000
                .map(clienteEntry -> new ClienteEValorDto(clienteEntry.getKey(), clienteEntry.getValue()))  
                .collect(Collectors.toList())
        ))
        .filter(entry -> !entry.getValue().isEmpty())                              // remove meses sem clientes
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new));

        return retorno;
    }

    public Map<YearMonth, Map<ClienteModel, Double>> transferenciaTotalDeClientesPorMes(List<TransacaoModel> transacoes) {

        Map<YearMonth, Map<ClienteModel, Double>> transferenciaPorMes = new HashMap<>();

        for (TransacaoModel transacao : transacoes) {
            YearMonth mes = YearMonth.from(transacao.getDataTransacao());

            transferenciaPorMes.putIfAbsent(mes, new HashMap<>());
            
            Double valor = transacao.getValor();
            Map<ClienteModel, Double> gastosClientes = transferenciaPorMes.get(mes);
            
            String ccOrigem = transacao.getCcOrigem();
            if(clienteRepository.existsByNumConta(ccOrigem)){
                //pensar em como retirar essa excessao http daqui
                //o problema eh que findByNumConta retorna Optional<ClienteModel>, que pode ser vazio
                ClienteModel clienteOrigem = clienteRepository.findByNumConta(ccOrigem).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
                gastosClientes.put(clienteOrigem, gastosClientes.getOrDefault(clienteOrigem, 0.0) + valor);
            }

            String ccDestino = transacao.getCcDestino(); 
            if(clienteRepository.existsByNumConta(ccDestino)){
                //pensar em como retirar essa excessao http daqui
                //o problema eh que findByNumConta retorna Optional<ClienteModel>, que pode ser vazio
                ClienteModel clienteDestino = clienteRepository.findByNumConta(ccDestino).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operação não encontrada."));
                gastosClientes.put(clienteDestino, gastosClientes.getOrDefault(clienteDestino, 0.0) + valor);
            }
        }
        return transferenciaPorMes;
    }

    public Map<String, Map<LocalDate, List<TransacaoModel>>> getExtratoClientePorDiaDurantePeriodo(String numConta, PeriodoDataDto periodo){
        
        Map<LocalDate, List<TransacaoModel>> extrato  =  getExtratoPorConta(numConta).stream()
            .filter(transacao -> 
                transacao.getDataTransacao().isAfter(periodo.getDataInicio()) &&
                transacao.getDataTransacao().isBefore(periodo.getDataFim())
            ) 
            .collect(Collectors.groupingBy(
                transacao -> LocalDate.from(transacao.getDataTransacao()),      
                TreeMap::new,                                                   // dias ordenados
                Collectors.toList()                                             // agrupa as transacoes em lista
            ));

       Map<String, Map<LocalDate, List<TransacaoModel>>> retorno = new HashMap<>();

       retorno.put(numConta, extrato);

       return retorno;
    } 

    public List<TransacaoModel> getExtratoPorConta(String numConta){

        List<TransacaoModel> transacoes = transacaoRepository.findAll();
        List<TransacaoModel> transacoesNumConta = new ArrayList<>();

        for(TransacaoModel transacao : transacoes){
            if((transacao.getCcOrigem() != null && !transacao.getCcOrigem().isEmpty() && transacao.getCcOrigem().equals(numConta)) ||
            (transacao.getCcDestino() != null && !transacao.getCcDestino().isEmpty() && transacao.getCcDestino().equals(numConta))) {
                transacoesNumConta.add(transacao);
            }
        }
        return transacoesNumConta;
    }
}
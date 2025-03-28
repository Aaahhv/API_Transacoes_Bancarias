package com.amanda.transacoes.services.relatorios;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 

import com.amanda.transacoes.dtos.PeriodoDataDto;
import com.amanda.transacoes.dtos.relatorios.ClienteETiposOperacaoDto;
import com.amanda.transacoes.dtos.relatorios.ClienteEValorDto;
import com.amanda.transacoes.enums.OperacaoEnum;
import com.amanda.transacoes.enums.TipoOperacaoEnum;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.repositories.TransacaoRepository;
import com.amanda.transacoes.services.TransacaoService;

import java.util.stream.Stream;

@Service
public class InformacoesDeClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private TransacaoService transacaoService;

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

                ClienteModel cliente = clienteRepository.findByNumConta(ccCliente).get();  //aqui, findByNumConta() nao dever retornar Optional<null>, por isso o get()

                ClienteETiposOperacaoDto clienteDto = new ClienteETiposOperacaoDto(cliente);

                transacoesPorConta.forEach(transacao -> clienteDto.incrementarTipoOperacao(transacao.getTipoOperacao()));

                return clienteDto;
            })
            .collect(Collectors.toList()); 

        return listaClientes;
    }

    //Essa funcao filtra as transacoes em que a conta corrente (seja ccOrigem ou ccDestino) nao existe no banco de dados
    public Map<String, List<TransacaoModel>> listarTransacoesPorConta(List<TransacaoModel> transacoes) {
        Map<String, List<TransacaoModel>> transacoesPorCliente = new HashMap<>();

        transacoesPorCliente = transacoes.stream()
            .flatMap(transacao -> Stream.concat(
                (transacao.getCcOrigem() == null || transacao.getCcOrigem().isEmpty()) 
                    ? Stream.empty() 
                    : Stream.of(Map.entry(transacao.getCcOrigem(), transacao)),
                (transacao.getCcDestino() == null || transacao.getCcDestino().isEmpty()) 
                    ? Stream.empty() 
                    : Stream.of(Map.entry(transacao.getCcDestino(), transacao))
            ))                                             
            .filter(entry -> clienteRepository.existsByNumConta(entry.getKey()))               
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList()) 
            ));

        return transacoesPorCliente;
    }

    public Map<String,List<TransacaoModel>> getExtratoClienteComFiltro(String numConta, OperacaoEnum operacao, TipoOperacaoEnum tipoOperacaoEnum){

        List<TransacaoModel> transacoes = transacaoRepository.findAll();
        List<TransacaoModel> transacoesDoCliente = getExtratoDaConta(transacoes, numConta);
        List<TransacaoModel> transacoesFiltradas = transacoesDoCliente.stream()
            .filter(transacao -> (transacao.getOperacao() != null      &&  transacao.getOperacao() == operacao) &&
                                 (transacao.getTipoOperacao() != null  &&  transacao.getTipoOperacao() == tipoOperacaoEnum)).toList();

        Map<String,List<TransacaoModel>> retorno = new HashMap<>();
        retorno.put(numConta,transacoesFiltradas);

        return retorno;
    }

    public Map<YearMonth,  List<ClienteEValorDto>> getClientesCincoMilNoMes(YearMonth mes) {
        
        List<ClienteEValorDto> clienteEValorDto =  transferenciaTotalDasContaNoMes(mes).entrySet().stream()
        .filter(Entry -> Entry.getValue() >= 5000)            
                .map(clienteEntry -> new ClienteEValorDto(
                                            clienteRepository.findByNumConta(clienteEntry.getKey()).get(), //aqui, o findByNumConta() nao retorna Optional<null>, por isso o get()
                                            clienteEntry.getValue()))  
                .collect(Collectors.toList());
        
        Map<YearMonth,  List<ClienteEValorDto>>  retorno = new HashMap<>();
        retorno.put(mes, clienteEValorDto);
        
        return retorno;
    }

    private Map<String, Double> transferenciaTotalDasContaNoMes(YearMonth mes){

        List<TransacaoModel> transacoesDoMes = transacaoService.findByYearMonthBetween(mes);

        Map<String, Double> retorno = listarTransacoesPorConta(transacoesDoMes).entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().stream()
                          .mapToDouble(TransacaoModel::getValor)
                          .sum()                
                )
            );

        return retorno;
    }

    public Map<String, Map<LocalDate, List<TransacaoModel>>> getExtratoClientePorDiaDurantePeriodo(String numConta, PeriodoDataDto periodo){
        
        List<TransacaoModel> transacoesDuranteOPeriodo = transacaoRepository.findByDataTransacaoBetween(periodo.getDataInicio(), periodo.getDataFim());
        Map<LocalDate, List<TransacaoModel>> extrato  =  getExtratoDaConta(transacoesDuranteOPeriodo, numConta).stream()
            .collect(Collectors.groupingBy(
                transacao -> LocalDate.from(transacao.getDataTransacao()),      
                TreeMap::new,                                                   // dias ordenados
                Collectors.toList()                                           
            ));

       Map<String, Map<LocalDate, List<TransacaoModel>>> retorno = new HashMap<>();

       retorno.put(numConta, extrato);

       return retorno;
    } 

    //Essa funcao retorna o extrato da numConta com as transacoes ordenadas por datahora
    public List<TransacaoModel> getExtratoDaConta(List<TransacaoModel> transacoes, String numConta){

        List<TransacaoModel> transacoesNumConta = transacoes.stream()
            .filter(transacao -> (numConta.equals(transacao.getCcOrigem())  || numConta.equals(transacao.getCcDestino())))
            .sorted(Comparator.comparing(TransacaoModel::getDataTransacao))
            .toList();

        return transacoesNumConta;
    }
}
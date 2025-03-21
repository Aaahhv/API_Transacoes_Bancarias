package com.amanda.transacoes.services.relatorios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
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

        List<TransacaoModel> transacoesTodas = transacaoRepository.findAll();
        List<TransacaoModel> transacoes = getExtratoPorConta(transacoesTodas, numConta);
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

    public Map<YearMonth,  List<ClienteEValorDto>> getClientesCincoMilNoMes(YearMonth mes) {
        
        List<ClienteEValorDto> retorno =  transferenciaTotalDasContaNoMes(mes).entrySet().stream()
        .filter(Entry -> Entry.getValue() >= 5000)            
                .map(clienteEntry -> new ClienteEValorDto(
                                            clienteRepository.findByNumConta(clienteEntry.getKey()).get(), //aqui, findByNumConta() nao dever retornar Optional<null>, por isso o get()
                                            clienteEntry.getValue()))  
                .collect(Collectors.toList());
        
        Map<YearMonth,  List<ClienteEValorDto>>  retorno2 = new HashMap<>();
        retorno2.put(mes, retorno);
        
        return retorno2;
    }

    private Map<String, Double> transferenciaTotalDasContaNoMes(YearMonth mes){

        List<TransacaoModel> transacoesNoMes = listarTransacoesNoMes(mes);

        Map<String, Double> retorno = listarTransacoesPorConta(transacoesNoMes).entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().stream()
                          .mapToDouble(TransacaoModel::getValor)
                          .sum()                
                )
            );

        return retorno;
    }

    public List<TransacaoModel> listarTransacoesNoMes(YearMonth mes) {
 
            LocalDate inicioDia = mes.atDay(1);
            LocalDate fimDia = mes.atEndOfMonth();

            LocalDateTime inicio = inicioDia.atTime(0, 0, 0);
            LocalDateTime fim = fimDia.atTime(23, 59, 59);

        return transacaoRepository.findByDataTransacaoBetween(inicio, fim);
    }

    public Map<String, Map<LocalDate, List<TransacaoModel>>> getExtratoClientePorDiaDurantePeriodo(String numConta, PeriodoDataDto periodo){
        
        List<TransacaoModel> transacoesDuranteOPeriodo = transacaoRepository.findByDataTransacaoBetween(periodo.getDataInicio(), periodo.getDataFim());
        Map<LocalDate, List<TransacaoModel>> extrato  =  getExtratoPorConta(transacoesDuranteOPeriodo, numConta).stream()
            .collect(Collectors.groupingBy(
                transacao -> LocalDate.from(transacao.getDataTransacao()),      
                TreeMap::new,                                                   // dias ordenados
                Collectors.toList()                                             // agrupa as transacoes em lista
            ));

       Map<String, Map<LocalDate, List<TransacaoModel>>> retorno = new HashMap<>();

       retorno.put(numConta, extrato);

       return retorno;
    } 

    public List<TransacaoModel> getExtratoPorConta(List<TransacaoModel> transacoes, String numConta){

        List<TransacaoModel> transacoesNumConta = transacoes.stream()
            .filter(transacao -> (transacao.getCcOrigem()  != null && !transacao.getCcOrigem().isEmpty()  && transacao.getCcOrigem().equals(numConta))  ||
                                 (transacao.getCcDestino() != null && !transacao.getCcDestino().isEmpty() && transacao.getCcDestino().equals(numConta)))
            .toList();

        return transacoesNumConta;
    }
}
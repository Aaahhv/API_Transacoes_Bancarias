package com.amanda.transacoes.services.relatorios;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amanda.transacoes.dtos.PeriodoDataDto;
import com.amanda.transacoes.dtos.relatorios.ClienteETiposOperacaoDto;
import com.amanda.transacoes.dtos.relatorios.ClienteEValorDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.repositories.TransacaoRepository;

@Service
public class InformacoesDeClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    public Map<String, Double> getSaldoBanco() {
        List<ClienteModel> clientes = clienteRepository.findAll();

        double saldoBanco = 0;
        for(ClienteModel cliente: clientes){
            saldoBanco += cliente.getSaldo();
        }

        Map<String, Double> relatorio = new  HashMap<>();

        relatorio.put("Saldo do banco", saldoBanco);

        return relatorio;
    }

    public Map<String, Integer> getQuantidadeClientesAtivos() {
        List<ClienteModel> clientes = clienteRepository.findAll();

        int clientesAtivos = 0;
        for(ClienteModel cliente: clientes){
            if(cliente.getAtivo()){
                clientesAtivos += 1;
            }
        }

        Map<String, Integer> relatorio = new  HashMap<>();

        relatorio.put("Quantidade de clientes ativos", clientesAtivos);

        return relatorio;
    }
    
    public Map<YearMonth,  List<ClienteEValorDto>> getClienteCincoMilPorMes() {
        // 0L0 
        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        Map<YearMonth, Map<ClienteModel, Double>> transferenciasPorMes = new HashMap<>();

        transferenciasPorMes = transferenciaTotalDeClientesPorMes(transacoes);
        
        Map<YearMonth, List<ClienteEValorDto>> clientesAcimaDe5000PorMes = new HashMap<>();

        for (Map.Entry<YearMonth, Map<ClienteModel, Double>> mes : transferenciasPorMes.entrySet()) {
            Map<ClienteModel, Double> clienteEValor = mes.getValue();
            
            List<ClienteEValorDto> clientesAcima5000 = new ArrayList<>();
            for (Map.Entry<ClienteModel, Double> clienteEntry : clienteEValor.entrySet()) {
                if (clienteEntry.getValue().compareTo(Double.valueOf(5000)) > 0) {
                    ClienteEValorDto clienteRico = new ClienteEValorDto(clienteEntry.getKey(), clienteEntry.getValue());
                    clientesAcima5000.add(clienteRico);
                }
            }
            if (!clientesAcima5000.isEmpty()) {
                clientesAcimaDe5000PorMes.put(mes.getKey(), clientesAcima5000);
            }
        }

        TreeMap<YearMonth, List<ClienteEValorDto>> clientesAcimaDe5000PorMesOrdenado = new TreeMap<>(clientesAcimaDe5000PorMes);

        return clientesAcimaDe5000PorMesOrdenado;
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

    public List<ClienteETiposOperacaoDto> getQuantidadeDeTipoOperacaoPorCliente(){
        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        Map<String, List<TransacaoModel>> transacoesPorContaMap = listarTransacoesPorConta(transacoes);
        List<ClienteETiposOperacaoDto> listaClientes = new ArrayList<>();

        for(Map.Entry<String, List<TransacaoModel>> entry : transacoesPorContaMap.entrySet()){
            List<TransacaoModel> transacoesPorConta = entry.getValue();
            String ccCliente = entry.getKey();

            if( clienteRepository.existsByNumConta(ccCliente)){ 
                ClienteModel cliente = clienteRepository.findByNumConta(ccCliente).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
                ClienteETiposOperacaoDto clienteDto = new ClienteETiposOperacaoDto(cliente);
                
                for (TransacaoModel transacao : transacoesPorConta) {
                    clienteDto.incrementarTipoOperacao(transacao.getTipoOperacao());
                }
                listaClientes.add(clienteDto);
            }
        }
        return  listaClientes;
    }

    public Map<String, List<TransacaoModel>> listarTransacoesPorConta(List<TransacaoModel> transacoes) {
        Map<String, List<TransacaoModel>> transacoesPorCliente = new HashMap<>();

        for (TransacaoModel transacao : transacoes) {
            String ccOrigem = transacao.getCcOrigem();

            if( clienteRepository.existsByNumConta(ccOrigem)){ 
                if (!transacoesPorCliente.containsKey(ccOrigem)) {
                    transacoesPorCliente.put(ccOrigem, new ArrayList<>());
                }
                transacoesPorCliente.get(ccOrigem).add(transacao);
            }

            String ccDestino = transacao.getCcDestino();

            if( clienteRepository.existsByNumConta(ccDestino)){   
                if (!transacoesPorCliente.containsKey(ccDestino)) {
                    transacoesPorCliente.put(ccDestino, new ArrayList<>());
                }
                transacoesPorCliente.get(ccDestino).add(transacao);
            }
        }
        return transacoesPorCliente;
    }

    public Map<String, Map<LocalDate, List<TransacaoModel>>> getExtratoClientePorData(String numConta, PeriodoDataDto periodo){

        List<TransacaoModel> transacoesNumConta = listarTransacoesDaConta(numConta);

        List<TransacaoModel> extratoNoPeriodo = new ArrayList<>();
        for( TransacaoModel transacao : transacoesNumConta) {
            if( transacao.getDataTransacao().isAfter(periodo.getDataInicio()) &&
                transacao.getDataTransacao().isBefore(periodo.getDataFim())){
                    extratoNoPeriodo.add(transacao);
            }
        }
        
        Map<LocalDate, List<TransacaoModel>> extratoAgrupadoPorDia = AgruparExtratoPorDia(extratoNoPeriodo);

        Map<LocalDate, List<TransacaoModel>> extratoOrdenado = new TreeMap<>(extratoAgrupadoPorDia);

        Map<String, Map<LocalDate, List<TransacaoModel>>> retorno = new HashMap<>();

        retorno.put(numConta, extratoOrdenado);

        return retorno;
    }

    public List<TransacaoModel> listarTransacoesDaConta(String numConta){

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
 
    public Map<LocalDate, List<TransacaoModel>> AgruparExtratoPorDia(List<TransacaoModel> transacoes){
    
        Map<LocalDate, List<TransacaoModel>> extratoPorDia = new HashMap<>();

        for( TransacaoModel transacao : transacoes){ 

            LocalDate dia = LocalDate.from(transacao.getDataTransacao());

            extratoPorDia.putIfAbsent(dia, new ArrayList<>());

            extratoPorDia.get(dia).add(transacao);
        }
        return extratoPorDia;
    }
}
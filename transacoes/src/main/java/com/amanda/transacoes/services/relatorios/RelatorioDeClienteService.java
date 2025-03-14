package com.amanda.transacoes.services.relatorios;

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

import com.amanda.transacoes.dtos.relatorios.ClienteEValorDto;
import com.amanda.transacoes.models.ClienteModel;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.repositories.ClienteRepository;
import com.amanda.transacoes.repositories.TransacaoRepository;

@Service
public class RelatorioDeClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    public Map<String, Double> getRelatorioSaldoBanco() {
        List<ClienteModel> clientes = clienteRepository.findAll();

        double saldoBanco = 0;
        for(ClienteModel cliente: clientes){
            saldoBanco += cliente.getSaldo();
        }

        Map<String, Double> relatorio = new  HashMap<>();

        relatorio.put("Saldo do banco", saldoBanco);

        return relatorio;
    }

    public Map<String, Integer> getRelatorioQuantidadeClientesAtivos() {
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
    
    public Map<YearMonth,  List<ClienteEValorDto>> getRelatorioCincoMil() {
        // 0L0 
        List<TransacaoModel> transacoes = transacaoRepository.findAll();

        Map<YearMonth, Map<ClienteModel, Double>> transferenciasPorMes = new HashMap<>();

        transferenciasPorMes = TransferenciaTotalDeClientesPorMes(transacoes);
        
        Map<YearMonth, List<ClienteEValorDto>> clientesAcimaDe5000PorMes = new HashMap<>();

        for (Map.Entry<YearMonth, Map<ClienteModel, Double>> mes : transferenciasPorMes.entrySet()) {
            Map<ClienteModel, Double> clienteEValor = mes.getValue();
            
            List<ClienteEValorDto> clientesAcima5000 = new ArrayList<>();
            for (Map.Entry<ClienteModel, Double> clienteEntry : clienteEValor.entrySet()) {
                if (clienteEntry.getValue().compareTo(Double.valueOf(3000)) > 0) {
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

    public Map<YearMonth, Map<ClienteModel, Double>> TransferenciaTotalDeClientesPorMes(List<TransacaoModel> transacoes) {

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
}

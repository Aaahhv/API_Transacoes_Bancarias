package com.amanda.transacoes.controllers.relatorios;

import com.amanda.transacoes.dtos.relatorios.ClienteEValorDto;
import com.amanda.transacoes.services.relatorios.RelatorioDeClienteService;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping (value = "/relatoriosDeClientes", produces = {"application/json"})
@Tag(name = "Rotas de Relatorios de Clientes", description = "API de transações bancárias")

public class RelatorioDeClienteController {
    
    @Autowired
    private RelatorioDeClienteService relatorioService;

    @GetMapping("/RelatorioSaldoBanco")
    public Map<String, Double> readRelatorioSaldoBancario() {   
        return relatorioService.getRelatorioSaldoBanco();
    } 

    @GetMapping("/RelatorioQuantidadeClientesAtivos")
    public Map<String, Integer> readRelatorioQuantidadeClientesAtivos() {   
        return relatorioService.getRelatorioQuantidadeClientesAtivos();
    } 
    
    //Quantidade de transações por cliente, agrupando por tipo_operacao
    
    //listar objeto com cliente e os tipos de operação

    //Listar as transações do cliente, com filtro de operação e tipo_operacao

    @GetMapping("/RelatorioClientesMaisDeCincoMilAoMes")
    public  Map<YearMonth,  List<ClienteEValorDto>> readRelatorioClientesMaisCincoMil() {   
        return relatorioService.getRelatorioCincoMil();
    } 

    //Consultar extrato do cliente em um período informado, agrupando transações por dia.
}
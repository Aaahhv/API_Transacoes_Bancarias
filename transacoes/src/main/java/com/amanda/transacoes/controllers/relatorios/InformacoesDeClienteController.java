package com.amanda.transacoes.controllers.relatorios;

import com.amanda.transacoes.dtos.PeriodoDataDto;
import com.amanda.transacoes.dtos.relatorios.ClienteETiposOperacaoDto;
import com.amanda.transacoes.dtos.relatorios.ClienteEValorDto;
import com.amanda.transacoes.models.TransacaoModel;
import com.amanda.transacoes.services.relatorios.InformacoesDeClienteService;

import java.time.LocalDate;
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

public class InformacoesDeClienteController {
    
    @Autowired
    private InformacoesDeClienteService relatorioService;

    @GetMapping("/SaldoBanco")
    public Map<String, Double> readSaldoBancario() {   
        return relatorioService.getSaldoBanco();
    } 

    @GetMapping("/QuantidadeClientesAtivos")
    public Map<String, Integer> readQuantidadeClientesAtivos() {   
        return relatorioService.getQuantidadeClientesAtivos();
    } 
    
    @GetMapping("/ClientesMaisDeCincoMilAoMes")
    public  Map<YearMonth,  List<ClienteEValorDto>> readClienteCincoMilPorMes() {   
        return relatorioService.getClienteCincoMilPorMes();
    } 

    @GetMapping("/QuantidadeDeTipoOperacaoPorCliente")
    public  List<ClienteETiposOperacaoDto> readQuantidadeDeTipoDeOperacaoPorCliente() {   
        return relatorioService.getQuantidadeDeTipoOperacaoPorCliente();
    } 
     
    @GetMapping("/ExtratoDeClienteDurantePeriodo")
    public  Map<String, Map<LocalDate, List<TransacaoModel>>> readERRADo(String numConta, PeriodoDataDto periodo) {   
        return relatorioService.getExtratoClientePorData(numConta, periodo);
    } 
}
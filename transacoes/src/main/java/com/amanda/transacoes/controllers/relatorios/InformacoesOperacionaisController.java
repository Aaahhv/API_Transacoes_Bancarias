package com.amanda.transacoes.controllers.relatorios;

import com.amanda.transacoes.dtos.relatorios.RelatorioOperacionalDto;
import com.amanda.transacoes.services.relatorios.InformacoesOperacionaisService;

import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping (value = "/relatoriosOperacionais", produces = {"application/json"})
@Tag(name = "Rotas de Relatorios Operacionais", description = "API de transações bancárias")

public class InformacoesOperacionaisController {
    
    @Autowired
    private InformacoesOperacionaisService relatorioService;

    @GetMapping("/Operacoes")
    public RelatorioOperacionalDto readOperacoes(YearMonth mes) {   
        return relatorioService.getOperacoes(mes);
    }    

    @GetMapping("/TipoOperacoes")
    public RelatorioOperacionalDto readTipoOperacoes(YearMonth mes) {   
        return relatorioService.getTipoOperacoes(mes);
    }

    @GetMapping("/SituacaoOperacoes")
    public RelatorioOperacionalDto readSituacaoOperacao(YearMonth mes) {   
        return relatorioService.getSituacaoOperacao(mes);
    }

}
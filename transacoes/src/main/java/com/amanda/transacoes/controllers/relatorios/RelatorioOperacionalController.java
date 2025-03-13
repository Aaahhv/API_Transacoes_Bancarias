package com.amanda.transacoes.controllers.relatorios;

import com.amanda.transacoes.dtos.relatorios.RelatorioOperacionalDto;
import com.amanda.transacoes.services.relatorios.RelatorioOperacionalService;

import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping (value = "/relatoriosOperacionais", produces = {"application/json"})
@Tag(name = "Rotas de Relatorios Operacionais", description = "API de transações bancárias")

public class RelatorioOperacionalController {
    
    @Autowired
    private RelatorioOperacionalService relatorioService;

    @GetMapping("/RelatorioOpereacoes")
    public RelatorioOperacionalDto readRelatorioOperacoes() {   
        return relatorioService.getRelatorioOperacoes();
    }    

    @GetMapping("/RelatorioTipoOperacoes")
    public RelatorioOperacionalDto readRelatorioTipoOperacoes() {   
        return relatorioService.getRelatorioTipoOperacoes();
    }

    @GetMapping("/RelatorioSituacaoOperacoes")
    public RelatorioOperacionalDto readRelatorioSituacaoOperacao() {   
        return relatorioService.getRelatorioSituacaoOperacao();
    }

}
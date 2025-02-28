package com.amanda.transacoes.utils;

public class NomeUtil {

    public static boolean isNomeNullOrEmpty(String nome) {
        if (nome == null || nome.isEmpty()) {
            return true;
        }
        return false;
    }
    
}


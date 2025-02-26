package com.amanda.transacoes.utils;

public class NomeUtil {

    public static boolean isNomeNull(String nome) {
        if (nome == null || nome.isEmpty() || nome.equals("string")) {
            return true;
        }
        return false;
    }
    
}


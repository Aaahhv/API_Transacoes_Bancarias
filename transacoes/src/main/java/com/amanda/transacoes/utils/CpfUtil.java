package com.amanda.transacoes.utils;

import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CpfUtil {
    
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");

    public static boolean isCpfNull(String cpf) {
        if (cpf == null || cpf.isEmpty() || cpf.equals("string")) {
            return true;
        }
        return false;
    }
    
    public static String formatsCpf(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", ""); //remove pontos e hífens
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    public static boolean isValidCpf(String cpf) {
        
        cpf = cpf.replaceAll("[^\\d]", ""); //remove pontos e hífens

        if (cpf == null || !CPF_PATTERN.matcher(cpf).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de CPF invalido, o CPF deve ter 11 numeros");
        }

        int[] pesos1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF invalido, todos os digitos sao iguais.");
        }

        int primeiroDigito = calculatesDigits(cpf.substring(0, 9), pesos1);
        int segundoDigito = calculatesDigits(cpf.substring(0, 10), pesos2);

        
        if(!cpf.equals(cpf.substring(0, 9) + primeiroDigito + segundoDigito)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido, dígitos verificadores não conferem.");
        }

        return true;
    }

    private static int calculatesDigits(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : (11 - resto);
    }
    
}

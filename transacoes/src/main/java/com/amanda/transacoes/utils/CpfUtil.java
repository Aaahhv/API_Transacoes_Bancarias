package com.amanda.transacoes.utils;

import java.util.regex.Pattern;

public class CpfUtil {
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");

    public static boolean isCpfNullOrEmpty(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return true;
        }
        return false;
    }
    
    public static String formatsCpf(String cpf) {
        cpf = cpf.replaceAll("[.-]", ""); //remove apenas pontos e hífens

        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    public static boolean isFormatoValido(String cpf) {
        cpf = cpf.replaceAll("[.-]", ""); 
        return CPF_PATTERN.matcher(cpf).matches();
    }

    public static boolean isTodosDigitosIguais(String cpf) {
        cpf = cpf.replaceAll("[.-]", ""); 
        return cpf.matches("(\\d)\\1{10}");
    }

    public static boolean isDigitosVerificadoresValidos(String cpf) {
        cpf = cpf.replaceAll("[.-]", ""); 
        int[] pesos1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

        int primeiroDigito = calculatesDigits(cpf.substring(0, 9), pesos1);
        int segundoDigito = calculatesDigits(cpf.substring(0, 10), pesos2);

        return cpf.endsWith(String.valueOf(primeiroDigito) + segundoDigito);
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

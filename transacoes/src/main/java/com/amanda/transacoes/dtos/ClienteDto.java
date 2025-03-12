package com.amanda.transacoes.dtos;

public class ClienteDto {
    private String nome;
    private String cpf;

    public ClienteDto() {
    }

    public ClienteDto(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}

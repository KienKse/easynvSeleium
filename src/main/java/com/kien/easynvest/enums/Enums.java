package com.kien.easynvest.enums;

public enum Enums {

    /** Sites */
    SITE_AUTENTICACAO_EASY("https://www.easynvest.com.br/autenticacao/"),

    /** OTHER */


    /** Auth */
    CPF("RANDOM"),
    PASS("RANDOM");


    private String valor;

    Enums(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}

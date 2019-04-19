package com.kien.easynvest.enums;

public enum Enums {

    /** Sites */
    SITE_AUTENTICACAO_EASY("https://www.easynvest.com.br/autenticacao/"),

    /** OTHER */
    TELEGRAM_KEY(""),
    CHAT_ID(""),

    /** Auth - l */
    AUTH_1_1(""),
    AUTH_1_2(""),
    AUTH_1_3(""),

    /** Auth - p */
    AUTH_2_1(""),
    AUTH_2_2(""),
    AUTH_2_3(""),

    /** Investimentos */
    INVEST_1("Tesouro Selic 2023"),
    INVEST_2("Tesouro Selic 2025"),


    MSG_1("O valor liquido do Investimento\n"),
    MSG_2("Na data de\n");



    private String valor;

    Enums(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

}

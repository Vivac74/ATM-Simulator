package org.example;

public class KartaBankowa {
    private String numerKarty;
    private String numerKonta;

    public KartaBankowa(String numerKarty, String numerKonta) {
        this.numerKarty = numerKarty;
        this.numerKonta = numerKonta;
    }

    public String getNumerKarty() {
        return numerKarty;
    }

    public String getNumerKonta() {
        return numerKonta;
    }
}


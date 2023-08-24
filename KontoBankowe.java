package org.example;

public class KontoBankowe {
    private String numerKonta;
    private double saldo;

    public KontoBankowe(String numerKonta, double saldo) {
        this.numerKonta = numerKonta;
        this.saldo = saldo;
    }

    public String getNumerKonta() {
        return numerKonta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void wplata(double amount) {
        saldo += amount;
    }

    public void wyplata(double amount) {
        if (saldo >= amount) {
            saldo -= amount;
        }
    }
}

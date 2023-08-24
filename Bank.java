package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Bank
{
    private Map<String, KontoBankowe> kontaBankowe;
    private Map<String, KartaBankowa> kartyBankowe;
    private Connection connection;

    public Bank(Connection connection) {
        this.kontaBankowe = new HashMap<>();
        this.kartyBankowe = new HashMap<>();
        this.connection = connection;
    }

    public void addKonto(KontoBankowe konto)
    {
        kontaBankowe.put(konto.getNumerKonta(), konto);
    }

    public void addKarta(KartaBankowa karta)
    {
        kartyBankowe.put(karta.getNumerKarty(), karta);
    }

    public boolean verifyCard(String cardNumber, String pin)
    {
        try {
            String selectKartaQuery = "SELECT * FROM karty_bankowe WHERE numer_karty = ? AND pin = ?";
            PreparedStatement selectKartaStmt = connection.prepareStatement(selectKartaQuery);
            selectKartaStmt.setString(1, cardNumber);
            selectKartaStmt.setString(2, pin);
            ResultSet kartaResult = selectKartaStmt.executeQuery();

            if (kartaResult.next()) {
                System.out.println("Karta zweryfikowana");
                return true;
            } else {
                System.out.println("Niepoprawna karta lub PIN");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd podczas weryfikacji karty.");
            return false;
        }
    }

    public KontoBankowe getKontoBankoweByCardNumber(String cardNumber)
    {
        KartaBankowa karta = kartyBankowe.get(cardNumber);
        if (karta != null)
        {
            return kontaBankowe.get(karta.getNumerKonta());
        }
        return null;
    }

    public boolean deposit(String cardNumber, double amount)
    {
        KontoBankowe konto = getKontoBankoweByCardNumber(cardNumber);
        if (konto != null) {
            konto.wplata(amount);
            return true;
        }
        return false;
    }

    public boolean withdraw(String cardNumber, double amount)
    {
        KontoBankowe konto = getKontoBankoweByCardNumber(cardNumber);
        if (konto != null && konto.getSaldo() >= amount)
        {
            konto.wyplata(amount);
            return true;
        }
        return false;
    }

    public String getDaneWlasciciela(String cardNumber) {
        try {
            String selectWlascicielQuery = "SELECT informacje_o_klientach.wlasciciel FROM karty_bankowe " +
                    "JOIN informacje_o_klientach ON karty_bankowe.numer_konta = informacje_o_klientach.numer_konta " +
                    "WHERE karty_bankowe.numer_karty = ?";
            PreparedStatement selectWlascicielStmt = connection.prepareStatement(selectWlascicielQuery);
            selectWlascicielStmt.setString(1, cardNumber);
            ResultSet wlascicielResult = selectWlascicielStmt.executeQuery();

            if (wlascicielResult.next()) {
                return wlascicielResult.getString("wlasciciel");
            } else {
                return "Nieznane imię i nazwisko";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Błąd pobierania danych";
        }
    }
}

package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Bankomat {
    private Bank bank;
    private Connection connection;
    private Scanner scanner;
    private String cardNumber;
    private String pin;

    public Bankomat(Connection connection) {
        // Inicjalizacja pól klasy
        this.bank = new Bank(connection);
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.cardNumber = "";
        this.pin = "";
    }

    public void start() {
        // Rozpoczęcie interakcji z bankomatem
        System.out.println("Witaj w bankomacie!");

        // Wprowadzenie numeru karty i PIN'u
        System.out.print("Podaj numer karty: ");
        cardNumber = scanner.nextLine();
        System.out.print("Podaj PIN: ");
        pin = scanner.nextLine();

        boolean cardVerified = false;
        String ownerName = "";

        while (!cardVerified) {
            if (bank.verifyCard(cardNumber, pin)) {
                ownerName = bank.getDaneWlasciciela(cardNumber); // Pobranie imienia i nazwiska właściciela
                System.out.println("Weryfikacja karty powiodła się.");
                cardVerified = true;
            } else {
                // Obsługa błędnych danych karty lub PIN'u
                System.out.println("Niepoprawny numer karty lub PIN.");
                System.out.print("Podaj numer karty: ");
                cardNumber = scanner.nextLine();
                System.out.print("Podaj PIN: ");
                pin = scanner.nextLine();
            }
        }

        try {
            // Pobranie danych karty i konta z bazy danych
            String selectKartaQuery = "SELECT karty_bankowe.numer_karty, karty_bankowe.pin, informacje_o_klientach.numer_konta, informacje_o_klientach.saldo, informacje_o_klientach.wlasciciel " +
                    "FROM karty_bankowe " +
                    "JOIN informacje_o_klientach ON karty_bankowe.numer_konta = informacje_o_klientach.numer_konta " +
                    "WHERE karty_bankowe.numer_karty = ? AND karty_bankowe.pin = ?";
            PreparedStatement selectKartaStmt = connection.prepareStatement(selectKartaQuery);
            selectKartaStmt.setString(1, cardNumber);
            selectKartaStmt.setString(2, pin);
            ResultSet kartaResult = selectKartaStmt.executeQuery();

            if (kartaResult.next()) {
                String numerKonta = kartaResult.getString("numer_konta");
                double saldo = kartaResult.getDouble("saldo");
                String wlasciciel = kartaResult.getString("wlasciciel");

                KontoBankowe konto = new KontoBankowe(numerKonta, saldo);
                KartaBankowa karta = new KartaBankowa(cardNumber, numerKonta);
                bank.addKonto(konto);
                bank.addKarta(karta);
            } else {
                System.out.println("Niepoprawny numer karty lub PIN.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd podczas pobierania danych z bazy.");
            return;
        }

        // Wyświetlenie powitania z imieniem i nazwiskiem właściciela
        System.out.println("Miło Cię widzieć, " + ownerName + "!");
        showMenu();
        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            // Obsługa wyboru opcji w menu
            case 1:
                checkBalance();
                break;
            case 2:
                deposit();
                break;
            case 3:
                withdraw();
                break;
            case 4:
                showOwnerInfo();
                break;
            case 5:
                System.out.println("Dziękujemy za skorzystanie z bankomatu.");
                break;
            default:
                System.out.println("Niepoprawny wybór.");
        }
    }

    // Aktualizacja bazy danych po zmianach w saldzie konta
    private void updateDatabase() {
        KontoBankowe konto = bank.getKontoBankoweByCardNumber(cardNumber);
        if (konto != null) {
            try {
                // Przygotowanie zapytania aktualizującego saldo w bazie danych
                String updateSaldoQuery = "UPDATE informacje_o_klientach SET saldo = ? WHERE numer_konta = ?";
                PreparedStatement updateSaldoStmt = connection.prepareStatement(updateSaldoQuery);

                // Ustawienie parametrów zapytania na podstawie danych konta
                updateSaldoStmt.setDouble(1, konto.getSaldo());
                updateSaldoStmt.setString(2, konto.getNumerKonta());

                // Wykonanie zapytania aktualizującego
                updateSaldoStmt.executeUpdate();
                System.out.println("Baza danych została zaktualizowana.");
            } catch (SQLException e) {
                // Obsługa błędu w przypadku problemów z bazą danych
                e.printStackTrace();
                System.out.println("Wystąpił błąd podczas aktualizacji bazy danych.");
            }
        } else {
            System.out.println("Nie znaleziono konta dla podanej karty.");
        }
    }

    // Wyświetlenie menu
    private void showMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Sprawdź saldo");
        System.out.println("2. Wpłać pieniądze");
        System.out.println("3. Wypłać pieniądze");
        System.out.println("4. Dane posiadacza karty");
        System.out.println("5. Wyjście");
        System.out.print("Wybierz opcję: ");
    }

    // Sprawdzenie salda konta
    private void checkBalance() {
        KontoBankowe konto = bank.getKontoBankoweByCardNumber(cardNumber);
        if (konto != null) {
            System.out.println("Saldo konta: " + konto.getSaldo() + " zł");
        } else {
            System.out.println("Nie znaleziono konta dla podanej karty.");
        }
    }

    // Wpłacanie pieniędzy
    private void deposit() {
        System.out.print("Podaj kwotę do wpłaty: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (bank.deposit(cardNumber, amount)) {
            System.out.println("Wpłata zakończona pomyślnie.");
            updateDatabase(); // Aktualizacja bazy danych po wpłacie
        } else {
            System.out.println("Nieznany numer karty lub błąd podczas wpłaty.");
        }
    }

    // Wypłacanie pieniędzy
    private void withdraw() {
        System.out.print("Podaj kwotę do wypłaty: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (bank.withdraw(cardNumber, amount)) {
            System.out.println("Wypłata zakończona pomyślnie.");
            updateDatabase(); // Aktualizacja bazy danych po wypłacie
        } else {
            System.out.println("Brak wystarczających środków lub błąd podczas wypłaty.");
        }
    }

    // Wyświetlenie danych właściciela karty
    private void showOwnerInfo() {
        try {
            String selectOwnerInfoQuery = "SELECT informacje_o_klientach.wlasciciel, informacje_o_klientach.numer_konta," +
                    " karty_bankowe.numer_karty, informacje_o_klientach.cvv, informacje_o_klientach.data_waznosci_karty " +
                    "FROM karty_bankowe " +
                    "JOIN informacje_o_klientach ON karty_bankowe.numer_konta = informacje_o_klientach.numer_konta " +
                    "WHERE karty_bankowe.numer_karty = ?";
            PreparedStatement selectOwnerInfoStmt = connection.prepareStatement(selectOwnerInfoQuery);
            selectOwnerInfoStmt.setString(1, cardNumber);
            ResultSet ownerInfoResult = selectOwnerInfoStmt.executeQuery();

            if (ownerInfoResult.next()) {
                String ownerName = ownerInfoResult.getString("wlasciciel");
                String accountNumber = ownerInfoResult.getString("numer_konta");
                String cardNumber = ownerInfoResult.getString("numer_karty"); // Dodana linia
                String cvv = ownerInfoResult.getString("cvv");
                String expirationDate = ownerInfoResult.getString("data_waznosci_karty");

                System.out.println("Dane właściciela:");
                System.out.println("- - - - - - - - - - - -");
                System.out.println("Imię i nazwisko: " + ownerName);
                System.out.println("Numer konta: " + accountNumber);
                System.out.println("Numer karty: " + cardNumber); // Wyświetlenie numeru karty
                System.out.println("CVV karty: " + cvv);
                System.out.println("Data ważności karty: " + expirationDate);
            } else {
                System.out.println("Nie znaleziono danych właściciela.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd podczas pobierania danych z bazy.");
        }
    }
}

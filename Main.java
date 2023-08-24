package org.example;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main
{
    public static void main(String[] args)
    {
        Connection connection = null;

        try{
            // Nawiązywanie połączenia z bazą danych
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/KartyBankowe", "postgres", "qwerty");
        } catch (SQLException e) {
            e.printStackTrace();
            // Wyświetlenie okna dialogowego z informacją o błędzie połączenia
            JOptionPane.showMessageDialog(null, "Przepraszamy, trawają prace serwisowe, prosimy spróbować później.", "Błąd", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Zakończenie programu z kodem błędu
        }

        if (connection != null)
        {
            Bankomat bankomat;
            bankomat = new Bankomat(connection);
            // Rozpoczęcie interakcji z bankomatem
            bankomat.start();
        }
    }
}

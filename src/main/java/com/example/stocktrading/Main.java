package com.example.stocktrading;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        try {
            System.out.println("======================================");
            System.out.println("       STOCK TRADING SIMULATOR");
            System.out.println("======================================");
            System.out.print("Enter username (letters, numbers, _ or -): ");
            String username = input.nextLine().trim();

            if (!username.matches("[A-Za-z0-9_-]{1,24}")) {
                System.out.println("Invalid username. Use 1-24 letters, numbers, _ or -.");
                return;
            }

            new StockTradingPlatform(username.toLowerCase(Locale.ROOT), input).run();
        } catch (IOException e) {
            System.out.println("Could not initialize the application: " + e.getMessage());
        } finally {
            input.close();
        }
    }
}

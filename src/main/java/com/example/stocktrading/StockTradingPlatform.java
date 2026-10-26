package com.example.stocktrading;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StockTradingPlatform {
    private final Scanner scanner;
    private final FileStore store;
    private final Map<String, Stock> stocks;
    private final Investor investor;
    private final List<Transaction> transactions;

    public StockTradingPlatform(String username, Scanner scanner) throws IOException {
        this.scanner = scanner;
        store = new FileStore(Path.of("data"));
        stocks = store.loadStocks();
        investor = store.loadInvestor(username);
        transactions = store.loadTransactions(username);
    }

    public void run() {
        System.out.println("\nWelcome, " + investor.getUsername() + "!");
        System.out.printf("Available cash: Rs. %.2f%n", investor.getCash());
        int choice;
        do {
            printMenu();
            choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> displayMarket();
                    case 2 -> buyStock();
                    case 3 -> sellStock();
                    case 4 -> displayPortfolio();
                    case 5 -> displayTransactions();
                    case 6 -> updateMarket();
                    case 7 -> recordPerformance();
                    case 8 -> System.out.println("Goodbye! Your data is saved.");
                    default -> System.out.println("Invalid option. Choose 1-8.");
                }
            } catch (IOException e) {
                System.out.println("Could not save/load data: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        } while (choice != 8);
    }

    private void printMenu() {
        System.out.println("\n========== STOCK TRADING PLATFORM ==========");
        System.out.println("1. View market data");
        System.out.println("2. Buy shares");
        System.out.println("3. Sell shares");
        System.out.println("4. View portfolio and performance");
        System.out.println("5. View transaction history");
        System.out.println("6. Simulate market price movement");
        System.out.println("7. Save performance snapshot");
        System.out.println("8. Exit");
    }

    private void displayMarket() {
        System.out.println("\n---------------- MARKET ----------------");
        System.out.printf("%-12s %-32s %12s%n", "Symbol", "Company", "Price (Rs.)");
        for (Stock stock : stocks.values()) {
            System.out.printf("%-12s %-32s %12.2f%n",
                    stock.getSymbol(), stock.getCompanyName(), stock.getPrice());
        }
    }

    private void buyStock() throws IOException {
        displayMarket();
        String symbol = readText("Enter stock symbol to buy: ").toUpperCase(Locale.ROOT);
        Stock stock = requireStock(symbol);
        int quantity = readInt("Enter quantity: ");
        investor.buy(symbol, quantity, stock.getPrice());
        Transaction transaction = new Transaction(LocalDateTime.now(), "BUY",
                symbol, quantity, stock.getPrice());
        transactions.add(transaction);
        store.saveInvestor(investor);
        store.appendTransaction(investor.getUsername(), transaction);
        System.out.printf("Bought %d shares of %s for Rs. %.2f%n",
                quantity, symbol, transaction.getTotal());
    }

    private void sellStock() throws IOException {
        displayPortfolio();
        String symbol = readText("Enter stock symbol to sell: ").toUpperCase(Locale.ROOT);
        Stock stock = requireStock(symbol);
        int quantity = readInt("Enter quantity: ");
        investor.sell(symbol, quantity, stock.getPrice());
        Transaction transaction = new Transaction(LocalDateTime.now(), "SELL",
                symbol, quantity, stock.getPrice());
        transactions.add(transaction);
        store.saveInvestor(investor);
        store.appendTransaction(investor.getUsername(), transaction);
        System.out.printf("Sold %d shares of %s for Rs. %.2f%n",
                quantity, symbol, transaction.getTotal());
    }

    private void displayPortfolio() {
        double stockValue = 0;
        System.out.println("\n--------------- PORTFOLIO ---------------");
        System.out.printf("Investor: %s%nCash balance: Rs. %.2f%n",
                investor.getUsername(), investor.getCash());
        System.out.printf("%-12s %10s %14s %16s%n",
                "Symbol", "Quantity", "Market Price", "Holding Value");
        for (Map.Entry<String, Integer> entry : investor.getHoldings().entrySet()) {
            Stock stock = stocks.get(entry.getKey());
            if (stock == null) continue;
            double value = stock.getPrice() * entry.getValue();
            stockValue += value;
            System.out.printf("%-12s %10d %14.2f %16.2f%n",
                    stock.getSymbol(), entry.getValue(), stock.getPrice(), value);
        }
        double totalValue = investor.getCash() + stockValue;
        System.out.printf("Stock value: Rs. %.2f%n", stockValue);
        System.out.printf("Total portfolio value: Rs. %.2f%n", totalValue);
        System.out.printf("Net change vs. initial Rs. 100,000: Rs. %+.2f%n",
                totalValue - 100000.00);
    }

    private void displayTransactions() {
        System.out.println("\n--------------- TRANSACTION HISTORY ---------------");
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }
        System.out.printf("%-20s %-6s %-10s %8s %12s %14s%n",
                "Time", "Type", "Symbol", "Quantity", "Price", "Total");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Transaction t : transactions) {
            System.out.printf("%-20s %-6s %-10s %8d %12.2f %14.2f%n",
                    t.getTime().format(fmt), t.getType(), t.getSymbol(),
                    t.getQuantity(), t.getPrice(), t.getTotal());
        }
    }

    private void updateMarket() throws IOException {
        System.out.println("\nSimulating one market update (each stock moves randomly by up to 5%).");
        Random random = new Random();
        for (Stock stock : stocks.values()) {
            double change = (random.nextDouble() * 0.10) - 0.05;
            double newPrice = Math.max(1.0, stock.getPrice() * (1.0 + change));
            stock.setPrice(Math.round(newPrice * 100.0) / 100.0);
            System.out.printf("%-10s Rs. %.2f (%+.2f%%)%n",
                    stock.getSymbol(), stock.getPrice(), change * 100);
        }
        store.saveStocks(stocks);
        System.out.println("Market prices saved to data/stocks.csv.");
    }

    private void recordPerformance() throws IOException {
        double stockValue = 0;
        for (Map.Entry<String, Integer> entry : investor.getHoldings().entrySet()) {
            Stock stock = stocks.get(entry.getKey());
            if (stock != null) stockValue += stock.getPrice() * entry.getValue();
        }
        double totalValue = investor.getCash() + stockValue;
        store.appendPerformance(investor.getUsername(), investor.getCash(),
                stockValue, totalValue);
        System.out.println("Performance snapshot saved to data/" +
                investor.getUsername() + "_performance.csv");
        displayPortfolio();
    }

    private Stock requireStock(String symbol) {
        Stock stock = stocks.get(symbol);
        if (stock == null) throw new IllegalArgumentException("Unknown stock symbol: " + symbol);
        return stock;
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try { return Integer.parseInt(value); }
            catch (NumberFormatException e) { System.out.println("Please enter a whole number."); }
        }
    }

    private String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}

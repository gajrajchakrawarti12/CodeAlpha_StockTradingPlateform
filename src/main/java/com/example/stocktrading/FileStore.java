package com.example.stocktrading;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class FileStore {
    private final Path dataDir;

    public FileStore(Path dataDir) throws IOException {
        this.dataDir = dataDir;
        Files.createDirectories(dataDir);
    }

    private Path marketFile() { return dataDir.resolve("stocks.csv"); }
    private Path portfolioFile(String user) { return dataDir.resolve(user + "_portfolio.csv"); }
    private Path transactionsFile(String user) { return dataDir.resolve(user + "_transactions.csv"); }
    private Path performanceFile(String user) { return dataDir.resolve(user + "_performance.csv"); }

    public Map<String, Stock> loadStocks() throws IOException {
        Path file = marketFile();
        if (!Files.exists(file)) {
            List<String> initial = List.of(
                    "symbol,company,price",
                    "TCS,Tata Consultancy Services,3850.00",
                    "INFY,Infosys,1750.00",
                    "RELIANCE,Reliance Industries,2920.00",
                    "HDFCBANK,HDFC Bank,1680.00",
                    "ITC,ITC Limited,430.00"
            );
            Files.write(file, initial, StandardCharsets.UTF_8);
        }

        Map<String, Stock> stocks = new LinkedHashMap<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.startsWith("symbol,")) continue;
            String[] p = line.split(",", 3);
            if (p.length == 3) {
                stocks.put(p[0], new Stock(p[0], p[1], Double.parseDouble(p[2])));
            }
        }
        return stocks;
    }

    public void saveStocks(Map<String, Stock> stocks) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("symbol,company,price");
        for (Stock s : stocks.values()) {
            lines.add(s.getSymbol() + "," + s.getCompanyName() + "," +
                    String.format(Locale.US, "%.2f", s.getPrice()));
        }
        Files.write(marketFile(), lines, StandardCharsets.UTF_8);
    }

    public Investor loadInvestor(String username) throws IOException {
        Path file = portfolioFile(username);
        if (!Files.exists(file)) {
            Investor fresh = new Investor(username, 100000.00);
            saveInvestor(fresh);
            return fresh;
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        double cash = 100000.00;
        Map<String, Integer> holdings = new LinkedHashMap<>();
        for (String line : lines) {
            if (line.isBlank() || line.startsWith("key,")) continue;
            String[] p = line.split(",", 2);
            if (p.length != 2) continue;
            if (p[0].equals("cash")) cash = Double.parseDouble(p[1]);
            else if (p[0].startsWith("holding:")) {
                holdings.put(p[0].substring("holding:".length()), Integer.parseInt(p[1]));
            }
        }
        Investor investor = new Investor(username, cash);
        holdings.forEach(investor::setHolding);
        return investor;
    }

    public void saveInvestor(Investor investor) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("key,value");
        lines.add("cash," + String.format(Locale.US, "%.2f", investor.getCash()));
        investor.getHoldings().forEach((symbol, qty) ->
                lines.add("holding:" + symbol + "," + qty));
        Files.write(portfolioFile(investor.getUsername()), lines, StandardCharsets.UTF_8);
    }

    public List<Transaction> loadTransactions(String username) throws IOException {
        Path file = transactionsFile(username);
        if (!Files.exists(file)) {
            Files.writeString(file, "time,type,symbol,quantity,price" + System.lineSeparator(),
                    StandardCharsets.UTF_8);
        }
        List<Transaction> transactions = new ArrayList<>();
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line.isBlank() || line.startsWith("time,")) continue;
            try { transactions.add(Transaction.fromCsv(line)); }
            catch (RuntimeException ignored) { /* skip malformed rows */ }
        }
        return transactions;
    }

    public void appendTransaction(String username, Transaction transaction) throws IOException {
        Path file = transactionsFile(username);
        if (!Files.exists(file)) loadTransactions(username);
        Files.writeString(file, transaction.toCsv() + System.lineSeparator(),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void appendPerformance(String username, double cash, double stockValue,
                                  double totalValue) throws IOException {
        Path file = performanceFile(username);
        if (!Files.exists(file)) {
            Files.writeString(file, "time,cash,stock_value,total_value" + System.lineSeparator(),
                    StandardCharsets.UTF_8);
        }
        String row = LocalDateTime.now() + "," +
                String.format(Locale.US, "%.2f,%.2f,%.2f", cash, stockValue, totalValue);
        Files.writeString(file, row + System.lineSeparator(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}

package com.example.stocktrading;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final LocalDateTime time;
    private final String type;
    private final String symbol;
    private final int quantity;
    private final double price;
    private final double total;

    public Transaction(LocalDateTime time, String type, String symbol,
                       int quantity, double price) {
        this.time = time;
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.total = quantity * price;
    }

    public LocalDateTime getTime() { return time; }
    public String getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }

    public String toCsv() {
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," +
                type + "," + symbol + "," + quantity + "," +
                String.format(java.util.Locale.US, "%.2f", price);
    }

    public static Transaction fromCsv(String line) {
        String[] p = line.split(",");
        if (p.length != 5) throw new IllegalArgumentException("Invalid transaction row");
        return new Transaction(LocalDateTime.parse(p[0]), p[1], p[2],
                Integer.parseInt(p[3]), Double.parseDouble(p[4]));
    }
}

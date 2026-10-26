package com.example.stocktrading;

import java.util.LinkedHashMap;
import java.util.Map;

public class Investor {
    private final String username;
    private double cash;
    private final Map<String, Integer> holdings = new LinkedHashMap<>();

    public Investor(String username, double cash) {
        this.username = username;
        this.cash = cash;
    }

    public String getUsername() { return username; }
    public double getCash() { return cash; }
    public Map<String, Integer> getHoldings() { return holdings; }

    public void buy(String symbol, int quantity, double price) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        double cost = quantity * price;
        if (cost > cash + 0.000001) throw new IllegalArgumentException("Insufficient cash balance.");
        cash -= cost;
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + quantity);
    }

    public void sell(String symbol, int quantity, double price) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        int owned = holdings.getOrDefault(symbol, 0);
        if (quantity > owned) throw new IllegalArgumentException("You do not own enough shares.");
        cash += quantity * price;
        int remaining = owned - quantity;
        if (remaining == 0) holdings.remove(symbol);
        else holdings.put(symbol, remaining);
    }

    public void setCash(double cash) {
        if (cash < 0) throw new IllegalArgumentException("Cash cannot be negative.");
        this.cash = cash;
    }

    public void setHolding(String symbol, int quantity) {
        if (quantity > 0) holdings.put(symbol, quantity);
    }
}

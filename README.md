# Stock Trading Platform (Java Console + File Storage)

A beginner-friendly stock trading **simulator** built with Java and object-oriented programming. It does not connect to a real stock exchange and does not use real money.

## Features

- View market prices for five sample stocks.
- Buy and sell shares with validation for quantity, cash balance, and available holdings.
- View portfolio holdings, cash balance, total portfolio value, and change against the initial Rs. 100,000.
- View transaction history.
- Simulate market price movement (each stock changes randomly by up to 5% per update).
- Save a timestamped portfolio performance snapshot.
- Persist market prices, holdings, cash, transactions, and performance history to CSV files.

## Requirements

- JDK 17 or later
- Terminal / command prompt

No external libraries are required.

## Run

From the project root:

```bash
javac -d out src/main/java/com/example/stocktrading/*.java
java -cp out com.example.stocktrading.Main
```

On Windows, the same commands work in Command Prompt or PowerShell if `javac` and `java` are on `PATH`.

## First run

Enter a username containing only letters, digits, `_`, or `-` (up to 24 characters). A new user starts with Rs. 100,000 virtual cash. The app creates the `data/` directory and the CSV files automatically.

## Menu

1. View market data
2. Buy shares
3. Sell shares
4. View portfolio and performance
5. View transaction history
6. Simulate market price movement
7. Save performance snapshot
8. Exit

## Files created

- `data/stocks.csv` — current simulated market prices shared by all users of this local copy.
- `data/<username>_portfolio.csv` — cash balance and share quantities for that user.
- `data/<username>_transactions.csv` — append-only transaction history.
- `data/<username>_performance.csv` — timestamped cash, stock value, and total portfolio value snapshots.

Keep the `data/` directory when moving or backing up the project. Delete a user's portfolio file only if you intentionally want to reset that user's balance and holdings. Deleting only the portfolio file resets holdings/cash but does not remove transaction history.

## OOP design

- `Stock`: stores a stock's symbol, company name, and current simulated price.
- `Investor`: owns the virtual cash balance and stock quantities; validates buy/sell operations.
- `Transaction`: represents a buy/sell record with timestamp, quantity, price, and total.
- `FileStore`: reads and writes CSV files.
- `StockTradingPlatform`: coordinates menu actions, market updates, portfolio calculations, and transaction recording.
- `Main`: validates the username and starts the application.

## Important limitations

- This is an educational simulator, not financial software.
- Prices are randomly changed only when the user chooses the market-update menu item. They are not live quotes.
- No real authentication, concurrent-user locking, tax, fees, dividends, short selling, or cost-basis/realized-profit accounting is implemented.
- CSV storage is intentionally simple. It is suitable for a small assignment, not for concurrent or production trading.
# CodeAlpha_StockTradingPlateform

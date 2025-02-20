package pt.ua.deti.tqs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StocksPortfolio {
    IStockmarketService stockMarket;
    private List<Stock> stocks;

    public StocksPortfolio(IStockmarketService stockMarket) {
        this.stockMarket = stockMarket;
        this.stocks = new ArrayList<>();
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public double totalValue() {
        double sum = 0;
        for (Stock s : stocks) {
            sum += stockMarket.lookUpPrice(s.getLabel()) * s.getQuantity();
        }

        return sum;
    }

    public List<Stock> mostValuableStocks(int topN) {
        Map<String, Double> stockPrices = new HashMap<>();
        for (Stock stock : stocks) {
            stockPrices.put(stock.getLabel(), stockMarket.lookUpPrice(stock.getLabel()));
        }

        return stocks.stream()
                .sorted((s1, s2) -> Double.compare(
                        stockPrices.get(s2.getLabel()) * s2.getQuantity(),
                        stockPrices.get(s1.getLabel()) * s1.getQuantity()))
                .limit(topN)
                .collect(Collectors.toList());
    }



}

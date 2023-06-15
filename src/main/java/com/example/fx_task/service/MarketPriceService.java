package com.example.fx_task.service;

import com.example.fx_task.entity.MarketPrice;
import com.example.fx_task.repository.MarketPriceRepository;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarketPriceService {
    private final MarketPriceRepository marketPriceRepository;

    public MarketPriceService(MarketPriceRepository marketPriceRepository) {
        this.marketPriceRepository = marketPriceRepository;
    }

    public MarketPrice getLatestMarketPriceByInstrumentName(String instrumentName) {

        var currencyQuery = instrumentName.substring(0, 3) + "/" + instrumentName.substring(3);
        var recordFromDb =  marketPriceRepository.findFirstByInstrumentNameOrderByTimeDesc(currencyQuery);
        return adjustBidAndAskPrice(recordFromDb);

    }

    public List<MarketPrice> getLatestAllMarketPrice() {
       var  listFromDb = marketPriceRepository.findLatestRecords();
       return listFromDb.stream().map(this::adjustBidAndAskPrice).collect(Collectors.toList());
    }

    public void insertCSVRecords(String data) {
        var marketPrices = parseCSVString(data);
        marketPriceRepository.saveAll(marketPrices);
    }

    private MarketPrice adjustBidAndAskPrice(MarketPrice record) {
        var currentBidPrice = record.getBidPrice();
        var newBidPrice = currentBidPrice.multiply(new BigDecimal("0.999").setScale(4));
        record.setBidPrice(newBidPrice);

        var currentAskPrice = record.getAskPrice();
        var newAskPrice = currentAskPrice.multiply(new BigDecimal("1.001").setScale(4));
        record.setAskPrice(newAskPrice);

        return record;
    }

    private List<MarketPrice> parseCSVString(String csvString) {

        List<String[]> csvRows;
        try {
            if (csvString.isEmpty()) {
                throw new RuntimeException("CSV string is empty!");
            }
            var csvReader = new CSVReader(new StringReader(csvString));
            csvRows = csvReader.readAll();
            csvReader.close();
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Failed to process CSV string.", e);
        }

        var marketPrices = new ArrayList<MarketPrice>();

        for (var row : csvRows) {
            MarketPrice marketPrice = new MarketPrice();
            marketPrice.setInstrumentName(row[0]);
            marketPrice.setBidPrice(new BigDecimal(row[1]));
            marketPrice.setAskPrice(new BigDecimal(row[2]));
            marketPrice.setTime(LocalDateTime.parse(row[3]));
            marketPrices.add(marketPrice);
        }

        return marketPrices;
    }
}

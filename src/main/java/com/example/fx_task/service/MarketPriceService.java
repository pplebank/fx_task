package com.example.fx_task.service;

import com.example.fx_task.entity.MarketPrice;
import com.example.fx_task.repository.MarketPriceRepository;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;
import java.util.function.Function;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MarketPriceService {
    private final MarketPriceRepository marketPriceRepository;

    public MarketPriceService(MarketPriceRepository marketPriceRepository) {
        this.marketPriceRepository = marketPriceRepository;
    }

    public MarketPrice getLatestMarketPriceByInstrumentName(String instrumentName) {

        String currencyQuery = instrumentName.substring(0, 3) + "/" + instrumentName.substring(3);
        return marketPriceRepository.findFirstByInstrumentNameOrderByTimeDesc(currencyQuery);
    }

    public List<MarketPrice> getLatestAllMarketPrice() {

        List<MarketPrice> listFromDb = marketPriceRepository.findLatestRecords();

        Function<MarketPrice, MarketPrice> adjustBidAndAskPrice = record -> {
            BigDecimal currentBidPrice = record.getBidPrice();
            BigDecimal newBidPrice = currentBidPrice.multiply(new BigDecimal("1.1"));
            record.setBidPrice(newBidPrice);

            BigDecimal currentAskPrice = record.getAskPrice();
            BigDecimal newAskPrice = currentAskPrice.multiply(new BigDecimal("0.9"));
            record.setAskPrice(newAskPrice);
            return record;
        };

       return listFromDb.stream().flatMap(record -> Stream.of(adjustBidAndAskPrice.apply(record))).collect(Collectors.toList());
    }

    public void insertCSVRecords(String data) {
        List<MarketPrice> marketPrices = parseCSVString(data);
        marketPriceRepository.saveAll(marketPrices);
    }

    private List<MarketPrice> parseCSVString(String csvString) {

        List<String[]> csvRows;
        try {
            CSVReader csvReader = new CSVReader(new StringReader(csvString));
            csvRows = csvReader.readAll();
            csvReader.close();
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Failed to process CSV string.", e);
        }

        List<MarketPrice> marketPrices = new ArrayList<>();

        for (String[] row : csvRows) {
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

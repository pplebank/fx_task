package com.example.fx_task.controller;

import com.example.fx_task.entity.MarketPrice;
import com.example.fx_task.service.MarketPriceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Log4j2
@RequestMapping("/market-price")
public class MarketPriceController {
    private final MarketPriceService marketPriceService;

    public MarketPriceController(MarketPriceService marketPriceService) {
        this.marketPriceService = marketPriceService;
    }

    @GetMapping("/single/{name}")
    public MarketPrice getLatestMarketPriceByName(@PathVariable String name) {
        return marketPriceService.getLatestMarketPriceByInstrumentName(name);
    }

    @GetMapping("/")
    public List<MarketPrice> getAllLatestMarketPrice() {
        return marketPriceService.getLatestAllMarketPrice();
    }

    @PostMapping(value="/", headers="Accept=application/json")
    public ResponseEntity<String> insertNewRecords(@RequestBody String data) {
        marketPriceService.insertCSVRecords(data);
        return ResponseEntity.ok("Records created successfully");
    }
}
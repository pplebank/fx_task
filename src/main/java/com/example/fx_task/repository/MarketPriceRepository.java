package com.example.fx_task.repository;

import com.example.fx_task.entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {
    MarketPrice findFirstByInstrumentNameOrderByTimeDesc(String instrumentName);

    @Query("SELECT mp FROM MarketPrice mp WHERE mp.time = (SELECT MAX(m.time) FROM MarketPrice m WHERE m.instrumentName = mp.instrumentName)")
    List<MarketPrice> findLatestRecords();
}
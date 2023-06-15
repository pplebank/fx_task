package com.example.fx_task;

import com.example.fx_task.entity.MarketPrice;
import com.example.fx_task.repository.MarketPriceRepository;
import com.example.fx_task.service.MarketPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class FxTaskApplicationTests {

	@Autowired
	private MarketPriceService marketPriceService;

	@MockBean
	private MarketPriceRepository marketPriceRepository;

	private static final ArrayList<MarketPrice> mockAllMarketPrices = new ArrayList<>();

	@BeforeEach
	public void setup(){

		mockAllMarketPrices.add(MarketPrice.builder()
				.askPrice(new BigDecimal("1.2000"))
				.bidPrice(new BigDecimal("1.1000"))
				.time(LocalDateTime.now())
				.instrumentName("EUR/USD")
				.build());

		mockAllMarketPrices.add(MarketPrice.builder()
				.askPrice(new BigDecimal("1.2001"))
				.bidPrice(new BigDecimal("1.1001"))
				.time(LocalDateTime.now().minusDays(2))
				.instrumentName("EUR/PLN")
				.build());

		mockAllMarketPrices.add(MarketPrice.builder()
				.askPrice(new BigDecimal("1.2001"))
				.bidPrice(new BigDecimal("1.1001"))
				.time(LocalDateTime.now().minusDays(2))
				.instrumentName("USD/PLN")
				.build());

		mockAllMarketPrices.add(MarketPrice.builder()
				.askPrice(new BigDecimal("1.2001"))
				.bidPrice(new BigDecimal("1.1001"))
				.time(LocalDateTime.now().minusDays(2))
				.instrumentName("USD/EUR")
				.build());

		var mockSingleMarketPrice = MarketPrice.builder()
				.askPrice(new BigDecimal("1.2001"))
				.bidPrice(new BigDecimal("1.1001"))
				.time(LocalDateTime.now().minusDays(2))
				.instrumentName("EUR/PLN")
				.build();

		Mockito.when(marketPriceRepository.findLatestRecords()).thenReturn(mockAllMarketPrices);
		Mockito.when(marketPriceRepository.findFirstByInstrumentNameOrderByTimeDesc("EUR/PLN")).thenReturn(mockSingleMarketPrice);

	}


	@Test
	public void testGetLatestAllMarketPriceSuccess() {

		var latestMarketPrices = marketPriceService.getLatestAllMarketPrice();
		assertEquals(4, latestMarketPrices.size());

	}

	@Test
	public void testGetLatestMarketPriceSuccess() {

		var latestMarketPrices = marketPriceService.getLatestMarketPriceByInstrumentName("EURPLN");
		assertNotEquals(null, latestMarketPrices);
	}

	@Test
	public void testGetLatestMarketPriceDoesNotExists() {

		assertThrows(Exception.class, () -> {marketPriceService.getLatestMarketPriceByInstrumentName("JPYEUR");});
	}

	@Test
	public void testParseCSVStringSuccess() {
		var csvData = "EUR/USD,1.1000,1.2000,2023-06-15T10:30:00\n" +
				"EUR/GBP,0.9000,0.9500,2023-06-15T11:30:00";

		var expectedMarketPrices = new ArrayList<MarketPrice>();

		expectedMarketPrices.add(MarketPrice.builder()
				.bidPrice(new BigDecimal("1.1000"))
				.askPrice(new BigDecimal("1.2000"))
				.time(LocalDateTime.parse("2023-06-15T10:30:00"))
				.instrumentName("EUR/USD")
				.build());

		expectedMarketPrices.add(MarketPrice.builder()
				.bidPrice(new BigDecimal("0.9000"))
				.askPrice(new BigDecimal("0.9500"))
				.time(LocalDateTime.parse("2023-06-15T11:30:00"))
				.instrumentName("EUR/GBP")
				.build());


		marketPriceService.insertCSVRecords(csvData);

		ArgumentCaptor<List<MarketPrice>> captor = ArgumentCaptor.forClass(List.class);
		verify(marketPriceRepository).saveAll(captor.capture());
		var actualMarketPrices = captor.getValue();

		for (var price : actualMarketPrices) {
			var matchedPrice = expectedMarketPrices.stream().filter(marketPrice -> marketPrice.getInstrumentName().equals(price.getInstrumentName())).findFirst();
			assertTrue(matchedPrice.isPresent());
			assertEquals(price.getAskPrice(), matchedPrice.get().getAskPrice());
			assertEquals(price.getBidPrice(), matchedPrice.get().getBidPrice());
			assertEquals(price.getTime(), matchedPrice.get().getTime());

		}

	}

	@Test
	public void testParseCSVStringInvalid() {

		var csvData = "invalidCSV";
		assertThrows(Exception.class, () -> {marketPriceService.insertCSVRecords(csvData);});

	}

	@Test
	public void testParseCSVStringEmpty() {

		var csvData = "";
		assertThrows(Exception.class, () -> {marketPriceService.insertCSVRecords(csvData);});

	}
}

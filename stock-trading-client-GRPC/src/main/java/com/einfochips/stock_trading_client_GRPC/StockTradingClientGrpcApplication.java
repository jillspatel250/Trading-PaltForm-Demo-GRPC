package com.einfochips.stock_trading_client_GRPC;

import com.einfochips.stock_trading_client_GRPC.service.StockClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockTradingClientGrpcApplication implements CommandLineRunner {
	@Autowired
	private StockClientService stockClientService;

	public static void main(String[] args) {
		SpringApplication.run(StockTradingClientGrpcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		stockClientService.subscibeStockPrice("AMZN");
	}
}

package com.einfochips.stock_trading_server_GRPC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class StockTradingServerGrpcApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockTradingServerGrpcApplication.class, args);
	}

}

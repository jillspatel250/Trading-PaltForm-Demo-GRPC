package com.einfochips.stock_trading_server_GRPC.repository;

import com.einfochips.stock_trading_server_GRPC.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {
    Stock findByStockSymbol(String stockSymbol);
}

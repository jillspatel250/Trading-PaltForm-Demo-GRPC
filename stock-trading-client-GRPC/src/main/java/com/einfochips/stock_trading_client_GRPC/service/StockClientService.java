package com.einfochips.stock_trading_client_GRPC.service;

import com.einfochips.grpc.StockRequest;
import com.einfochips.grpc.StockResponse;
import com.einfochips.grpc.StockTradingServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service // ahiya apde normal service j lakhsu km ke aa GRPC service nathi etle we cannot write @GrpcClient
public class StockClientService {

    //so we now access GRPC server using Stub
    //blocking stub is correct only for UNARY. we cannot use this for server streaming client streaming and bi-directional streaming
    //there we use Async stub
    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceBlockingStub stockTradingServiceBlockingStub;

    // method:- StockResponse getStockPrice(stockRequest)
    public StockResponse getStockPirce(String stockSymbol){//method ma toh apde req laiee chiee server side tohh ahiya km amm???
        //so here we take symbol and make our stockRequest in method
        StockRequest stockRequest=StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        return stockTradingServiceBlockingStub.getStockPrice(stockRequest);
    }


}

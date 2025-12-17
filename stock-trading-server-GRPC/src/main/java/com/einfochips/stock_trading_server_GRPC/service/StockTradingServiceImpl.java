package com.einfochips.stock_trading_server_GRPC.service;

import com.einfochips.grpc.StockRequest;
import com.einfochips.grpc.StockResponse;
import com.einfochips.grpc.StockTradingServiceGrpc;
import com.einfochips.stock_trading_server_GRPC.entity.Stock;
import com.einfochips.stock_trading_server_GRPC.repository.StockRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

//@Service this is not we use so it won't be exopose class as a Grpc Service we need to use @GrpcService annotation..
@GrpcService //now using this class is expose as a GrpcService by GrpcServer
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {

    @Autowired
    private StockRepository stockRepository;

    @Override
    public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        //AA TOH vvoid return kare che have ?? so we have responseObserver
        //from request we get stockSymbol from that -> DB -> map response -> return
        String stockSymbol=request.getStockSymbol();
        Stock stockEntity = stockRepository.findByStockSymbol(stockSymbol);

        //SteamObserver need StockResponse as a res and in proto file also we define in returns StockResponse
        //so we make stockResponse with Builder
        StockResponse stockResponse=StockResponse.newBuilder()
                .setStockSymbol(stockEntity.getStockSymbol())
                .setPrice(stockEntity.getPrice())
                .setTimestamp(stockEntity.getLastUpdated().toString())
                .build();

        responseObserver.onNext(stockResponse);//onNext is keep emit the response to client..
        responseObserver.onCompleted();//we send the success signal to client and say task is done


    }
}

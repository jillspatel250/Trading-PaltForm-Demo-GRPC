package com.einfochips.stock_trading_client_GRPC.service;

import com.einfochips.grpc.StockRequest;
import com.einfochips.grpc.StockResponse;
import com.einfochips.grpc.StockTradingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service // ahiya apde normal service j lakhsu km ke aa GRPC service nathi etle we cannot write @GrpcClient
public class StockClientService {

    //so we now access GRPC server using Stub
    //blocking stub is correct only for UNARY. we cannot use this for server streaming client streaming and bi-directional streaming
    //there we use Async stub
//    @GrpcClient("stockService")
//    private StockTradingServiceGrpc.StockTradingServiceBlockingStub stockTradingServiceBlockingStub;

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceStub stockTradingServiceStub;

    // method:- StockResponse getStockPrice(stockRequest)
//    public StockResponse getStockPirce(String stockSymbol){//method ma toh apde req laiee chiee server side tohh ahiya km amm???
//        //so here we take symbol and make our stockRequest in method
//        StockRequest stockRequest=StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
//        return stockTradingServiceBlockingStub.getStockPrice(stockRequest);
//    }

    public void subscibeStockPrice(String symbol){
        StockRequest request=StockRequest.newBuilder()
                .setStockSymbol(symbol)
                .build();
        stockTradingServiceStub.subscibeStockPrice(request, new StreamObserver<StockResponse>() {
            @Override
            //server kai emit karse toh client onNext execute thase
            public void onNext(StockResponse stockResponse) {
                System.out.println("Stock Price Update: "+ stockResponse.getStockSymbol()+
                        "Price "+stockResponse.getPrice()+
                        "Time: "+stockResponse.getTimestamp()
                );
            }

            @Override
            //kai error avse server side thi toh ama capture thase
            public void onError(Throwable throwable) {
                System.out.println("Error: "+throwable.getMessage());
            }

            @Override
            //response pati jase toh ama capture thase
            public void onCompleted() {
                System.out.println("stock price stream live is completed!!!");
            }
        });
    }


}

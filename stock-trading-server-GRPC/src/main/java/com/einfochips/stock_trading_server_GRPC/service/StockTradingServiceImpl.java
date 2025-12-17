package com.einfochips.stock_trading_server_GRPC.service;

import com.einfochips.grpc.*;
import com.einfochips.stock_trading_server_GRPC.entity.Stock;
import com.einfochips.stock_trading_server_GRPC.repository.StockRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//@Service this is not we use so it won't be exopose class as a Grpc Service we need to use @GrpcService annotation..
@GrpcService //now using this class is expose as a GrpcService by GrpcServer
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {

    @Autowired
    private StockRepository stockRepository;

    @Override
    public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        //AA TOH vvoid return kare che have ?? so we have responseObserver
        //from request we get stockSymbol from that -> DB -> map response -> return
        String stockSymbol = request.getStockSymbol();
        Stock stockEntity = stockRepository.findByStockSymbol(stockSymbol);

        //SteamObserver need StockResponse as a res and in proto file also we define in returns StockResponse
        //so we make stockResponse with Builder
        StockResponse stockResponse = StockResponse.newBuilder()
                .setStockSymbol(stockEntity.getStockSymbol())
                .setPrice(stockEntity.getPrice())
                .setTimestamp(stockEntity.getLastUpdated().toString())
                .build();

        responseObserver.onNext(stockResponse);//onNext is keep emit the response to client..
        responseObserver.onCompleted();//we send the success signal to client and say task is done
    }


    @Override
    public void subscibeStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        String symbol = request.getStockSymbol();
        //other value we hardcoded we not have real time data thats why here we generate fake.. price and time
        //and we repeat 10 time in loop and send continue
        try {
            for (int i = 0; i <= 10; i++) {
                StockResponse stockResponse = StockResponse.newBuilder()
                        .setStockSymbol(symbol)
                        .setPrice(new Random().nextDouble(200))
                        .setTimestamp(Instant.now().toString())
                        .build();
                //after return one response to the client thread sleep for 1 sec
                responseObserver.onNext(stockResponse);
                TimeUnit.SECONDS.sleep(1);
            }
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }

    @Override
    public StreamObserver<StockOrder> bulkStockOrder(StreamObserver<OrderSummary> responseObserver) {
        return new StreamObserver<StockOrder>() {

            private int totalOrders=0;
            private double totalAmount=0;
            private int successCount=0;
            @Override
            public void onNext(StockOrder stockOrder) {
                totalOrders++;
                totalAmount=totalAmount+stockOrder.getPrice()*stockOrder.getQuantity();
                successCount++;
                System.out.println("Recived order :"+stockOrder);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Server unable to process request "+throwable.getMessage());
            }

            //in thhis we return single response when all request process from server side..
            //so this method is imp in client-side streaming
            @Override
            public void onCompleted() {
                OrderSummary summary=OrderSummary.newBuilder()
                        .setTotalOrders(totalOrders)
                        .setSuccessCount(successCount)
                        .setTotalAmount(totalAmount)
                        .build();
                responseObserver.onNext(summary);
                responseObserver.onCompleted();
            }
        };
    }
}

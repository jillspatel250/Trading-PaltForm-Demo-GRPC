package com.einfochips.stock_trading_client_GRPC.service;

import com.einfochips.grpc.*;
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

        public void placeBulkOrder(){
            StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
                @Override
                public void onNext(OrderSummary orderSummary) {
                    System.out.println("OrderSummary recived from server:");
                    System.out.println("Total Orders:"+orderSummary.getTotalOrders());
                    System.out.println("Successfull orders"+orderSummary.getSuccessCount());
                    System.out.println("Total Amount : $"+orderSummary.getTotalAmount());
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("Order summary Recived error from server"+throwable.getMessage());
                }

                @Override
                public void onCompleted() {
                    System.out.println("Steam Completed , sever id done sending summary!!");
                }
            };
            StreamObserver<StockOrder> requestObserver = stockTradingServiceStub.bulkStockOrder(responseObserver);

            //send multiple stream of stock order message/request
            try {

                requestObserver.onNext(StockOrder.newBuilder()
                        .setOrderId("1")
                        .setStockSymbol("AAPL")
                        .setOrderType("BUY")
                        .setPrice(150.5)
                        .setQuantity(10)
                        .build());

                requestObserver.onNext(StockOrder.newBuilder()
                        .setOrderId("2")
                        .setStockSymbol("GOOGL")
                        .setOrderType("SELL")
                        .setPrice(2700.0)
                        .setQuantity(5)
                        .build());

                requestObserver.onNext(StockOrder.newBuilder()
                        .setOrderId("3")
                        .setStockSymbol("TSLA")
                        .setOrderType("BUY")
                        .setPrice(700.0)
                        .setQuantity(8)
                        .build());

                //done sending orders
                requestObserver.onCompleted();
            } catch (Exception ex) {
                requestObserver.onError(ex);
            }
        }


    }




package com.espalier.vertx;

import static com.espalier.vertx.OurAsyncHandler.handler;
import static java.lang.System.out;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.six_group.ao.twint.mp.ifc.acquiring.v2.AcquirerCheckSystemStatusRequestType;
import com.six_group.ao.twint.mp.ifc.acquiring.v2.AcquirerTransactionInterfacePort;
import com.six_group.ao.twint.mp.ifc.acquiring.v2.MerchantInformationType;
import com.six_group.ao.twint.mp.ifc.acquiring.v2.TWINTAcquirerTransactionInterfaceV2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

@Component
public class TwintVerticle extends AbstractVerticle {

    @Autowired
    private DatabaseVerticle databaseVerticle;

    @Value("${ws.endpoint}")
    private String endpoint;

    @Value("${server.port}")
    private int port;

    @Override
    public void start(Future<Void> fut) throws Exception {
        getServicePort();
        Router router = Router.router(vertx);
        router.get("/status").handler(this::status);
        router.get("/status/:merchantAliasId").handler(this::status);
        router.get("/status/:merchantAliasId/:terminalExternalId").handler(this::status);

        vertx.deployVerticle(databaseVerticle, ar -> {
            if (ar.succeeded()){
                out.println("Starting http server");
                vertx.createHttpServer().requestHandler(router).listen(port,
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail("it failed");
                            }
                        });
            }
        });
    }

    private AcquirerTransactionInterfacePort servicePort;

    private AcquirerTransactionInterfacePort getServicePort(){
        if (servicePort == null){
            TWINTAcquirerTransactionInterfaceV2 service = new TWINTAcquirerTransactionInterfaceV2();
            servicePort = service.getAcquirerTransactionInterfacePort();
            BindingProvider provider = (BindingProvider) servicePort;
            provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://fo-itu:15081/service/acquirer/TWINTAcquirerTransactionInterfaceV2");

            List<Handler> handlerChain = new ArrayList<Handler>();
            handlerChain.add(new ClientLoggingSOAPHandler());
            provider.getBinding().setHandlerChain(handlerChain);
        }
        return servicePort;
    }

    private void status(RoutingContext routingContext){
        AcquirerCheckSystemStatusRequestType request = new AcquirerCheckSystemStatusRequestType();
        MerchantInformationType mi = new MerchantInformationType();
        ofNullable(routingContext.pathParam("merchantAliasId")).ifPresent( v -> mi.setMerchantAliasId(v));
        ofNullable(routingContext.pathParam("terminalExternalId")).ifPresent( v -> mi.setTerminalExternalId(v));
        request.setMerchantInformation(mi);
        java.util.concurrent.Future<?> future = getServicePort().checkSystemStatusAsync(request, handler(vertx.getOrCreateContext(), routingContext));
    }
}

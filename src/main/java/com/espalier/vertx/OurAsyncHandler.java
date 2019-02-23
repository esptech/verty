package com.espalier.vertx;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import com.six_group.ao.twint.mp.ifc.acquiring.v2.AcquirerCheckSystemStatusResponseType;

import io.vertx.core.Context;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class OurAsyncHandler implements AsyncHandler<AcquirerCheckSystemStatusResponseType> {

	private Context context;
	private RoutingContext routingContext;

	private OurAsyncHandler(Context context, RoutingContext routingContext){
		this.context = context;
		this.routingContext = routingContext;
	}

	public static OurAsyncHandler handler(Context context, RoutingContext routingContext){
		return new OurAsyncHandler(context, routingContext);
	}

	@Override
	public void handleResponse(Response<AcquirerCheckSystemStatusResponseType> response) {
		context.runOnContext( v -> {
			if (response.isDone()) {
				HttpServerResponse res = routingContext.response();
				try {
					res.setStatusCode(200)
							.putHeader("content-type", "application/json")
							.end(JsonObject.mapFrom(response.get()).toString());
				} catch (Exception e) {
					e.printStackTrace();
					res.setStatusCode(400).end();
				}
			}
		});
	}
	
	public Context getContext() {
		return context;
	}
}

package com.espalier.vertx;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class ClientLoggingSOAPHandler implements SOAPHandler<SOAPMessageContext> {

    public ClientLoggingSOAPHandler(){
        System.out.println("ClientLoggingSOAPHandler");
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        return isMessageOutbound(context) ? this.handleOutboundMessage(context) : this.handleInboundMessage(context);
    }

    @Override
    public void close(MessageContext context) {

    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    private boolean handleInboundMessage(SOAPMessageContext smc) {
        System.out.println("handleInboundMessage: " + Thread.currentThread().getName() + ": " + soapToString(smc.getMessage()));
        return true;
    }

    private boolean handleOutboundMessage(SOAPMessageContext smc) {
        System.out.println("handleOutboundMessage: " + Thread.currentThread().getName() + ": " + soapToString(smc.getMessage()));
        return true;
    }

    protected static boolean isMessageOutbound(SOAPMessageContext context) {
        return (Boolean)context.get("javax.xml.ws.handler.message.outbound");
    }

    protected static String soapToString(SOAPMessage message) {
        try {
            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer tf = tff.newTransformer();
            tf.setOutputProperty("indent", "yes");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source sc = message.getSOAPPart().getContent();
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(streamOut);
            tf.transform(sc, result);
            return streamOut.toString();
        } catch (Exception ex) {
            return null;
        }
    }
}

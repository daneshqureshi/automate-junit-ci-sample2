package com.browserstack.sample;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class SampleServer {

    public static final int PORT = Integer.parseInt(System.getProperty("port", "5000"));

    public static Undertow newServer(String host, int port) {
        return Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(new HttpHandler() {

                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                        exchange.getResponseSender().send("<html>" +
                                "<head><title>BrowserStack</title></head>" +
                                "<body><h1>Test Page</h1></body>" +
                                "</html>");
                    }

                }).build();
    }

    public static void main(String[] args) {
        newServer("localhost", PORT).start();
    }
}

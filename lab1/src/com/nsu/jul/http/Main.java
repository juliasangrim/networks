package com.nsu.jul.http;

public class Main {

    public static void main(String[] args) {
	try {
	    HttpServer server = new HttpServer();
	    HttpServer.runServer();
    }
	catch (Exception e) {
	    e.printStackTrace();
      }
    }
}

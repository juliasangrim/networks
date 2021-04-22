package com.nsu.jul.http;

public class Main {

    public static void main(String[] args) {
	try {
	    HttpServer.runServer();
    }
	catch (Exception e) {
	    e.printStackTrace();
      }
    }
}

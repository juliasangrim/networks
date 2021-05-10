package com.nsu.jul.http;

public class Request {
    private String fullRequest;
    private String method;
    private String path;
    private String version;
    private String host;
    private String connection;
    private String accept;

    public Request(StringBuilder requestBuilder) {
        String fullRequest = requestBuilder.toString();
        String[] requestLines = fullRequest.split("\r\n");
        for (String string : requestLines) {
            if (string.contains("GET")) {
                method = string.split(" ")[0];
                path = string.split(" ")[1];
                version = string.split(" ")[2];
            }
            if (string.contains("Host:")) {
                host = string.split(" ")[1];
            }
            if (string.contains("Connection:")) {
               connection = string.split(" ")[1];
            }
           if (string.contains("Accept:")) {
                accept = string.split(" ")[1];
           }
        }
    }

    public String getPath() {
        return path;
    }

    public String getTypeConnection() {
        return connection;
    }

    public String getMethod() { return method; }

    public String getAccept() {return  accept; }

    public void print() {
        System.out.println(fullRequest);
    }
}

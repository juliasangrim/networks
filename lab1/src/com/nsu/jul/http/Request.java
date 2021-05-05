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
        String[] firstLine = requestLines[0].split(" ");

        method = firstLine[0];
        path = firstLine[1];
        version = firstLine[2];
        host = requestLines[1].split(" ")[1];
        connection = requestLines[2].split(" ")[1];
        accept = requestLines[5].split(" ")[1];
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

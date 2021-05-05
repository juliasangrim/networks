package com.nsu.jul.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Date;


public class HttpServer {
    //define const
    static final int PORT = 8000;
    //run server
    public static void runServer() throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while(true) {
                System.out.println("Server started!");
                try (Socket client  = serverSocket.accept()) {
                    Request request = handleRequest(client);
                    if (request == null) {
                        System.out.println("Empty package");
                        continue;
                    }
                    String path = request.getPath();
                    Path filePath = getFilePath(path);
                    String contentType = getType(filePath);
                    if (Files.exists(filePath)) {
                        // file exist
                        FileTime lastModified = Files.getLastModifiedTime(filePath, LinkOption.NOFOLLOW_LINKS);
                        if (request.getMethod().equals("GET") && (request.getAccept().contains(contentType) || request.getAccept().contains("*/*"))) {
                            sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath), request, lastModified);
                        } else {
                            byte[] error = "<h1>405 Method Not Allowed<h1>".getBytes(StandardCharsets.UTF_8);
                            sendResponse(client, "405 Method Not Allowed", "text/html", error, request, null);
                        }
                    } else {
                        if (request.getAccept().contains("image/x-icon")) continue;
                        byte[] error = "<h1>404 Not Found </h1>".getBytes(StandardCharsets.UTF_8);
                        sendResponse(client, "404 Not Found", "text/html", error, request, null);
                    }
                }
            }
        }
    }
    //handle request from client
    public static Request handleRequest(Socket client) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        Request request = null;
        while ((line = input.readLine()) != null) {
            if (line.isEmpty()) break;
            requestBuilder.append(line).append("\r\n");
            System.out.println(line);
        }
        if (requestBuilder.length() == 0) {
            client.close();
        } else {
            request = new Request(requestBuilder);
            request.print();
        }
        return request;
    }

//get type for content
    public static String getType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }
//get path of content file
    private static Path getFilePath(String path) {
        if ("/".equals(path)) {
            path = "/index.html";
        }
        if ("/text.txt".equals(path)) {
            path = "/text.txt";
        }
        if ("/image.jpg".equals(path)) {
            path = "/image.jpg";
        }
        return Paths.get("src", path);
    }
//send response to client
    public static void sendResponse(Socket client, String status, String contentType, byte[] content, Request request, FileTime lastModified ) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes(StandardCharsets.UTF_8));
        clientOutput.write(("Server: server" + "\r\n").getBytes(StandardCharsets.UTF_8));
        Date date = new Date();
        clientOutput.write(("Data:" + date.toString() +"\r\n").getBytes(StandardCharsets.UTF_8));
        clientOutput.write(("Connection: " + request.getTypeConnection() + "\r\n").getBytes(StandardCharsets.UTF_8));
        clientOutput.write(("Content-Type: " + contentType + "\r\n").getBytes(StandardCharsets.UTF_8));
        clientOutput.write(("Content-Length:" + content.length + "\r\n").getBytes(StandardCharsets.UTF_8));
        clientOutput.write("\r\n".getBytes(StandardCharsets.UTF_8));
        clientOutput.write(content);
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        if (!request.getTypeConnection().equals("keep-alive")) {
            client.close();
        }
    }
}
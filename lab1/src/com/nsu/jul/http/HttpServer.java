package com.nsu.jul.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//TODO
//Необходимые возможности:
//- Поддержка только GET запроса: возвращение файла, лежащего по данному адресу на машине, где запущен сервер
//- - Пример: если сервер запущен в папке C:/server и был сделан запрос GET /dir/file.jpg, в теле ответа надо вернуть файл, лежащий по адресу C:/server/dir/file.jpg
//- - поддерживаемые MIME типы: text/plain, text/html, image/jpeg (см. заголовок Accept)
//- Обработка заголовков:
//- - Заголовки запроса:
//- - - Accept (https://developer.mozilla.org/ru/docs/Web/HTTP/Headers/Accept), Connection (как персистентные, так и нет) (внимание: персистентное соединение не обязано выдерживать перегрузки кучей подключений, но должно работать если подключается 1 клиент и периодически чото просит)
//- - Заголовки ответа:
//- - - content-type, date, last-modified, content-length, server (просто придумайте своё название сервера и пишите туда)
//- Необрабатываемые заголовки можно просто игнорировать
//- Обработка ошибок: 405 Method Not Allowed на неподдерживаемый метод, 404 Not Found если файла нет

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
                    if (request.getMethod().equals("GET")) {
                        String path = request.getPath();
                        Path filePath = getFilePath(path);
                        if (Files.exists(filePath)) {
                            // file exist
                            String contentType = getType(filePath);
                            //sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath), request);
                        } else {
                            byte[] error = "<h1>404 Not found </h1>".getBytes(StandardCharsets.UTF_8);
                           // sendResponse(client, "404 Not found", "text/html", error, request);
                        }
                    } else {
                        byte[] error = "<h1>405 Method Not Allowed </h1>".getBytes(StandardCharsets.UTF_8);
                       // sendResponse();
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
    public static void sendResponse(Socket client, String status, String contentType, byte[] content, Request request) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes(StandardCharsets.UTF_8));
        clientOutput.write(("Content-Type: " + contentType + "\r\n").getBytes(StandardCharsets.UTF_8));
        clientOutput.write("\r\n".getBytes(StandardCharsets.UTF_8));
        //TODO a
        clientOutput.write(content);
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        if (!request.getTypeConnection().equals("keep-alive")) {
            client.close();
        }
    }
}
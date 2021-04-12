package com.nsu.jul.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

    public static void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started!");

            while (true) {
                // ожидаем подключения
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");

                // для подключившегося клиента открываем потоки 
                // чтения и записи
                //get for text
                //TODO get for jpeg MIME типы: text/plain, text/html, image/jpeg
                try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                     PrintWriter output = new PrintWriter(socket.getOutputStream())) {

                    // ждем первой строки запроса
                    while (!input.ready()) ;

                    // считываем и печатаем все что было отправлено клиентом
                    System.out.println();
                    while (input.ready()) {
                        System.out.println(input.readLine());
                    }
                    //
                    //Примечание: картинки можно передавать просто как набор байт в сокет.
                    // отправляем ответ
                    output.println("HTTP/1.1 200 OK");
                    output.println("Content-Type: text/html; charset=utf-8");
                    output.println();
                    output.println("<p>Привет всем!</p>");
                    output.flush();

                    // по окончанию выполнения блока try-with-resources потоки, 
                    // а вместе с ними и соединение будут закрыты
                    System.out.println("Client disconnected!");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
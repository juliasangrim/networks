package com.nsu.jul.tcp;

import java.net.SocketException;

public class MainServer {
    public static void main(String[] args) {
        try {
            TcpServer serverSocket = new TcpServer("localhost", 0, 8001, 8000);
            serverSocket.exec();
        } catch (
                SocketException e) {
            e.printStackTrace();
        }
    }
}

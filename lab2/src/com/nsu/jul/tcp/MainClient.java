package com.nsu.jul.tcp;

import java.net.SocketException;

public class MainClient {
    public static void main(String[] args) {
        try {

            TcpClient clientSocket = new TcpClient("localhost", 0.8, 8000, 8001);
            clientSocket.exec();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}

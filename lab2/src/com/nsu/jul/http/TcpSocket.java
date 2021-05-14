package com.nsu.jul.http;

import java.io.*;
import java.net.*;
import java.util.Random;

public class TcpSocket {
    private final Random randNum;
    private final double lostPackage;
    private InetAddress addressHost;

    final int SIZE_DATA = 1024;


    TcpSocket(String hostName, double lostPackage) {
        this.randNum = new Random();
        this.lostPackage = lostPackage;
        try {
            this.addressHost = InetAddress.getByName(hostName);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
        }
    }

    private void sendPacket(DatagramSocket datagramSocket, int destPort, Package pack) throws IOException {
        if (randNum.nextDouble() > lostPackage) {
            //serializing packet
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(arrayOutputStream));
            outputStream.writeObject(pack);
            outputStream.flush();
            //debug
            System.out.println("sending packet: " + pack);
            //send packet
            byte[] sendData = arrayOutputStream.toByteArray();
            DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length, this.addressHost, destPort);

            datagramSocket.send(datagramPacket);
            outputStream.close();
        } else {
            System.out.println("Lost packet: " + pack);
        }
    }

    private Package recvPacket(DatagramSocket datagramSocket) throws IOException, ClassNotFoundException {
        byte[] recvData = new byte[SIZE_DATA];
        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
        try {
            datagramSocket.receive(recvPacket);
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(recvData);
        ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(arrayInputStream));
        Package packet = (Package)inputStream.readObject();
        inputStream.close();
        return packet;
    }
}

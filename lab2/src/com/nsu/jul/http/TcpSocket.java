package com.nsu.jul.http;

import java.io.*;
import java.net.*;
import java.util.Random;

public class TcpSocket {
    protected static Random randNum;
    private final double lostPackage;
    private InetAddress addressHost;

    final int SIZE_DATA = 1024;


    public TcpSocket(String hostName, double lostPackage) {
        randNum = new Random();
        this.lostPackage = lostPackage;
        try {
            this.addressHost = InetAddress.getByName(hostName);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
        }
    }

    protected void sendPacket(DatagramSocket datagramSocket, int destPort, Packet... packets) throws IOException {
        for (Packet packet : packets) {
            if ((randNum.nextDouble() > lostPackage) || packet.isSyn) {
                //serializing packet
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(arrayOutputStream));
                outputStream.writeObject(packet);
                outputStream.flush();
                outputStream.close();
                //send packet
                byte[] sendData = arrayOutputStream.toByteArray();
                DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length, this.addressHost, destPort);

                datagramSocket.send(datagramPacket);
            } else {
                System.out.println("Lost packet: " + packet);
            }
        }
    }

    protected Packet recvPacket(DatagramSocket datagramSocket) throws IOException, ClassNotFoundException {
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
        Packet packet = (Packet)inputStream.readObject();
        inputStream.close();
        return packet;
    }
}

package com.nsu.jul.http;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class TcpServer extends TcpSocket{

    private int seqNum;
    private int lastAck;
    private DatagramSocket datagramSocket;
    private int senderPort;


    public TcpServer(String hostName, double lostPacket, int clientPort, int myPort) throws SocketException {
        super(hostName, lostPacket);
        this.seqNum = 0;
        this.senderPort = clientPort;
        this.datagramSocket = new DatagramSocket (myPort);
        this.lastAck = 0;
    }

    private void handShakeTcpServer() throws IOException, ClassNotFoundException {
        try {

            //receive packet syn with seqNum = clientNum
            Packet syn = recvPacket(datagramSocket);
            //if it's not syn and it's ack  so it's skipped
            if (!syn.isSyn || syn.isAck) {
                System.err.println("it's not a syn packet, try again");
            } else {
                //sen synack packet with seqnum = clientSeqNum + 1 and ack = clientseqnum + 1
                lastAck = syn.seqNum + 1;
                Packet synAck = new Packet(syn.seqNum, lastAck, 1, true, true);
                sendPacket(datagramSocket, senderPort, synAck);

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private Packet receivePacket() throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("d");
            Packet receivePacket = recvPacket(datagramSocket);
            if (receivePacket != null) {
                if (lastAck <= receivePacket.seqNum) {
                    int newAck = receivePacket.seqNum + 1;
                    Packet ack = new Packet(seqNum, newAck, 1, true,false);
                    seqNum++;
                    sendPacket(datagramSocket, senderPort, ack);
                    lastAck = newAck;
                    return receivePacket;

                } else {
                    Packet duplicateAck = new Packet(seqNum, lastAck + 1, 1, true, false);
                    sendPacket(datagramSocket, senderPort);
                    System.out.println("Send duplicate ACK");
                }
            }
        }
    }

    public void exec() {
        try {
            handShakeTcpServer();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                Packet hihi = receivePacket();
                System.out.println("Server receive:" + hihi.data);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}

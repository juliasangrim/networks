package com.nsu.jul.http;

import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramSocket;
import java.net.SocketException;


public class TcpClient extends TcpSocket{

    private int seqNum;
    private final int receiverPort;
    private int lastAck;
    private final DatagramSocket datagramSocket;
    private static final int timeout = 2000;
    private Packet currPacket;

    public TcpClient(String hostName, double lostPacket, int serverPort, int myPort) throws SocketException {
        super(hostName, lostPacket);
        this.seqNum = randNum.nextInt(100);
        this.receiverPort = serverPort;
        this.lastAck = 0;
        this.datagramSocket = new DatagramSocket(myPort);
        datagramSocket.setSoTimeout(timeout);
        currPacket = null;
    }

    private boolean handShakeTcpClient() {
        try {
            //make syn packet seqnum = clientSeqNum it's not ack and it's syn
            Packet syn = new Packet(seqNum, 0, 1, false, true);
            seqNum++;
            sendPacket(datagramSocket, receiverPort, syn);
            //receive synack packet with seqnum = clienSeqNum + 1 and ack = clientSeqNum + 1

            Packet synAck = recvPacket(datagramSocket);
            if (synAck == null) {
                System.err.println("Connection is not happen");
                return false;

            }
            if (!synAck.isAck || !synAck.isSyn || synAck.ack != seqNum) {
                throw new ConnectException();

            } else {
                //sen ack packet with seqNum = clientSeqNum + 2 and ack  = clientSeqNum + 2
                Packet ack = new Packet(seqNum, synAck.seqNum + 1, 1, true, false);
                seqNum++;
                sendPacket(datagramSocket, receiverPort, ack);
                lastAck = seqNum;
            }


        } catch (ConnectException e) {
            System.out.println("Connect is not happen");
            return false;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    private Packet receiveAck() throws IOException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();
        while (true){
            //waiting for packet
            Packet receivePacket = recvPacket(datagramSocket);
            if (receivePacket != null) {
                if (!receivePacket.isSyn && receivePacket.isAck) {
                    int newAck = receivePacket.ack;
                    if (newAck > lastAck) {
                        lastAck = newAck;
                        return receivePacket;
                    }
                }
            } else {
                long currTime = System.currentTimeMillis();
                if (currTime >= timeout) {
                    sendPacket(datagramSocket, receiverPort, currPacket);
                }
            }
        }
    }


    private void send(Packet sendingPacket) throws IOException {
        this.currPacket = sendingPacket;
        sendPacket(datagramSocket, receiverPort, currPacket);
    }

    public void exec() {
        if (handShakeTcpClient()) {
            String hihi = "hihi";
            Packet packetHihi = new Packet(seqNum, 0, hihi.length(), false, false, hihi);
            try {
                sendPacket(datagramSocket, receiverPort, packetHihi);
                System.out.println("Client sent: " + packetHihi);
                Packet ack = receiveAck();
                System.out.println("Client received: " + ack);
                seqNum++;
                String haha = "haha";
                Packet packetHaha = new Packet(seqNum, ack.seqNum, haha.length(), false, false, haha);
                System.out.println("Client sent: " + packetHaha);
                send(packetHaha);
                ack = receiveAck();
                System.out.println("Client received: " + ack);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

}

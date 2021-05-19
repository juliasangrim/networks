package com.nsu.jul.http;

import java.io.Serializable;

public class Packet implements Serializable {
    public int seqNum;
    public int ack;
    public int headerLength;
    public boolean isAck;
    public boolean isSyn;
    public String data;


    Packet(int seqNum, int ack, int headerLength, boolean isAck, boolean isSyn, String data) {
        this.seqNum = seqNum;
        this.ack = ack;
        this.headerLength = headerLength;
        this.isAck = isAck;
        this.isSyn = isSyn;
        this.data = data;
    }

    @Override
    public String toString() {
        return ("Segment seqNum = " + seqNum + ", ack = " + ack + ", headerLength = " + headerLength + ", isSyn = " + isSyn + ", isAck = " + isAck);
    }
}

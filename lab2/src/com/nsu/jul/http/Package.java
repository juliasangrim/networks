package com.nsu.jul.http;

import java.io.Serializable;

public class Package implements Serializable {
    private int seqNum;
    private int ack;
    private int headerLength;
    private boolean isAck;
    private boolean isSyn;

    Package(int seqNum, int ack, int headerLength, boolean isAck, boolean isSyn) {
        this.seqNum = seqNum;
        this.ack = ack;
        this.headerLength = headerLength;
        this.isAck = isAck;
        this.isSyn = isSyn;
    }
    //for debugging
    @Override
    public String toString() {
        return ("Segment seqNum = " + seqNum + ", ack = " + ack + ", headerLength = " + headerLength);
    }
}

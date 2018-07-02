package com.hua.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {
    @org.junit.Test
    public void testRandom() {
        System.out.println((int)(Math.random() * 10) * 1000);
    }

    @org.junit.Test
    public void testInet() {
        try {
            System.out.println(InetAddress.getByName("localhost"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

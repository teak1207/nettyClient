package com.devGong.client;

import com.devGong.client.sockets.NonSslSocket;

import java.util.Scanner;


public class ClientSocketApplication {

    public static void main(String[] args) throws InterruptedException {
        //String host = "127.0.0.1";
        String host = "127.0.0.1";
        int port = 9999;
        try {
            System.out.println("Enter message length: ");
            Scanner sc = new Scanner(System.in);
            int messageLength = Integer.parseInt(sc.nextLine());

            NonSslSocket socket = new NonSslSocket(host, port);

            socket.run(messageLength);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

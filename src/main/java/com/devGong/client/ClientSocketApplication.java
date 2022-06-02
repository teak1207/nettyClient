package com.devGong.client;

import com.devGong.client.sockets.NonSslSocket;

import java.util.Scanner;


public class ClientSocketApplication {

    public static void main(String[] args) throws InterruptedException {
        // 현재 서버에 쓰인 디코더는 정해진 길이만큼 데이터가 들어오기를 기다리도록 구현되어 있습니다.
        // 이를 테스트하기 위해 전송할 메세지 길이를 입력하도록 구현
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

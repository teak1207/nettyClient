package com.devGong.client.sockets;

import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;


@AllArgsConstructor
public class ClientSocket {
    private Socket socket;
    static Scanner scanner = new Scanner(System.in);


    public void sendFixedLength(int messageLength) {
        int delimiterLength = 256;
        int key;

        StringBuilder stringBuilder = new StringBuilder();
        /*
        for (int i = 0; i < messageLength; i++) {


            stringBuilder.append("B");
            stringBuilder.append("A");
        }
        */


        do {
            System.out.println("0: PRE-INSTALL / 1:SETTING / 2:REQUEST / 3:REPORT / 4:DATA ");
            key = scanner.nextInt();

            switch (key) {

                case 0:
                    System.out.println("PRE-INSTALL selected ");
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append("000000000000000");
                        stringBuilder.append("00NONE");
                        stringBuilder.append("0xFFFF");
                    }
                    break;
               /* case 1:
                    System.out.println("SETTING selected ");
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append("a1");
                        stringBuilder.append("b1");
                        stringBuilder.append("c1");
                        stringBuilder.append("d1");
                        stringBuilder.append("e1");
                    }
                    break;

                case 2:
                    System.out.println("REQUEST selected ");
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append("a2");
                        stringBuilder.append("b2");
                        stringBuilder.append("c2");
                        stringBuilder.append("d2");
                        stringBuilder.append("e2");
                    }
                    break;

                case 3:
                    System.out.println("REPORT selected ");
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append("a3");
                        stringBuilder.append("b3");
                        stringBuilder.append("c3");
                        stringBuilder.append("d3");
                        stringBuilder.append("e3");
                    }
                    break;

                case 4:
                    System.out.println("DATA selected ");
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append("a4");
                        stringBuilder.append("b4");
                        stringBuilder.append("c4");
                        stringBuilder.append("d4");
                        stringBuilder.append("e4");
                    }
                    break;*/
            }

        }
        while (key >= 10);
        System.out.println("종료");


        byte[] totalData = stringBuilder.toString().getBytes();

        System.out.println("Sending message");

        try {
            OutputStream os = socket.getOutputStream();

            for (int i = 0; i < messageLength / delimiterLength; i++) {
                byte[] sending = Arrays.copyOfRange(totalData, i * delimiterLength, (i + 1) * delimiterLength);
                System.out.println("sending... " + (i + 1));
                os.write(sending);
                os.flush();
                Thread.sleep(500);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("Receiving message");
        try {
            InputStream is = socket.getInputStream();

            byte[] reply = new byte[messageLength];
            byte[] test = new byte[messageLength];
            ByteStreams.read(is, reply, 0, reply.length);

            //totalData 배열 0번지 위치부터 read 길이까지 two 배열에 0 번부터 저장하겠다
            System.arraycopy(totalData, 0, test, 0, 42);

            String converted = new String(test);
            System.out.println(converted);
//            System.out.println(new String(reply));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
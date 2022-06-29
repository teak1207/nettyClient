package com.devGong.client.sockets;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;


@AllArgsConstructor
public class ClientSocket {
    private Socket socket;
    static Scanner sc = new Scanner(System.in);

    public void sendFixedLength() {
        int delimiterLength = 256;
        int key;

        StringBuilder stringBuilder = new StringBuilder();
        /* PRE-INSTALL(31byte) / SETTING / 2:REQUEST / 3:REPORT(141byte) / 4:DATA  */

        System.out.println("Input => 0: PRE-INSTALL(31byte) / 1:SETTING / 2:REPORT / 3:REQUEST / 4:DATA / 8: ACK / 9: NAK");
        key = sc.nextInt();

        switch (key) {
            case 0:
                System.out.println("PRE-INSTALL selected");
                stringBuilder.append("0"); //Flag 1
                stringBuilder.append("SWSLB-20220530-0000-0001"); // SerialNumber  24
                stringBuilder.append("20200101 000014"); //DateTime  15
                stringBuilder.append("00"); //paraLen  2
                stringBuilder.append("862785043595621"); //Modem(phone ,기존) Number=> 15자리
                stringBuilder.append("00"); //debug message,  변동사항 거의 있을수 있음. 2
                stringBuilder.append("AAAA"); //check sum 2
        }

        byte[] totalData = stringBuilder.toString().getBytes();

        try {
            OutputStream os = socket.getOutputStream();
            os.write(totalData);
            os.flush();
            Thread.sleep(500);

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("Receiving message");


        try {
            InputStream is = socket.getInputStream();
            byte[] reply = new byte[50];
            if (is.read(reply) < 0)
                throw new SocketException();
            System.out.println("<<receive preinstall>> " + new String(reply));


            /*
            1. reply가 맞는 값이 왔다면
            2. ACK or NAK 와 report 값을 서버에 보내줌!
            */

            OutputStream os = socket.getOutputStream();
            System.out.println("=== REPORT PROCESS ===");

            if (reply != null) {
                /*============ 저장공간 리셋 ========*/
                Arrays.fill(totalData, (byte) 0);   //  pre-install 값 담긴 바이트배열  0으로 초기화.
                stringBuilder.setLength(0); // stringBuilder를 초기화
                /*============ Header ============*/
                stringBuilder.append("2");  // Flag
                stringBuilder.append("SWSLB-20220530-0000-0001");  // SerialNum
                stringBuilder.append("20200101 000014");  // DataTime
                stringBuilder.append("00");  // paraLen
                /*============ Body ============*/
                stringBuilder.append("2");   // DebugMessage
                stringBuilder.append("0200");   // RecordingTime1
                stringBuilder.append("0300");   // RecordingTime2
                stringBuilder.append("0400");   // RecordingTime3
                stringBuilder.append("L7.300");   // Firmware Version
                stringBuilder.append("3.5900");   // Battery Voltage
                stringBuilder.append("0x5b");   // Modem RSSI
                stringBuilder.append("0x03");   // Sampling Time
                stringBuilder.append("4");   // SampleRate
                stringBuilder.append("862785043595621");   // Modem Number
                stringBuilder.append("test_hkchoi");   // Project
                stringBuilder.append("producttest");   // SID
                stringBuilder.append("60");   // Period
                stringBuilder.append("thingsware.co.kr");   // Server URL
                stringBuilder.append("6669");   // Server Port
                stringBuilder.append("274a");   // CheckSum

                totalData = stringBuilder.toString().getBytes();

                try {
                    os = socket.getOutputStream();
                    os.write(totalData);
                    os.flush();
                    Thread.sleep(500);

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }


            } else {
                /*============ 저장공간 리셋 ========*/
                Arrays.fill(totalData, (byte) 0);  //  pre-install 값 담긴 바이트배열  0으로 초기화.
                stringBuilder.setLength(0); // stringBuilder를 초기화

                /*============ Header ============*/
                stringBuilder.append("9");  // Flag
                stringBuilder.append("SWSLB-20220530-0000-0001");  // SerialNum
                stringBuilder.append("20200101 000014");  // DataTime
                stringBuilder.append("00");  // paraLen
                /*============ Body ============*/
                stringBuilder.append("2");   // DebugMessage
                stringBuilder.append("0200");   // RecordingTime1
                stringBuilder.append("0300");   // RecordingTime2
                stringBuilder.append("0400");   // RecordingTime3
                stringBuilder.append("L7.300");   // Firmware Version
                stringBuilder.append("3.5900");   // Battery Voltage
                stringBuilder.append("0x5b");   // Modem RSSI
                stringBuilder.append("0x03");   // Sampling Time
                stringBuilder.append("4");   // SampleRate
                stringBuilder.append("862785043595621");   // Modem Number
                stringBuilder.append("test_hkchoi");   // Project
                stringBuilder.append("producttest");   // SID
                stringBuilder.append("60");   // Period
                stringBuilder.append("thingsware.co.kr");   // Server URL
                stringBuilder.append("6669");   // Server Port
                stringBuilder.append("274a");   // CheckSum

                totalData = stringBuilder.toString().getBytes();

                try {
                    os = socket.getOutputStream();
                    os.write(totalData);
                    os.flush();
                    Thread.sleep(500);

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
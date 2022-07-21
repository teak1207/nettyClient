package com.devGong.client.sockets;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;


@AllArgsConstructor
public class ClientSocket {
    private Socket socket;
    static Scanner sc = new Scanner(System.in);

    public byte[] makeChecksum(String totalData) {
        int total = 0;
        for (int i = 0; i < totalData.length(); i++) {
            total += totalData.charAt(i);    // 문자열 10진수로 바꿔서 저장
        }
        System.out.println(total);

        String hex = Integer.toHexString(total);

        String first = "";
        String second = "";

        if (hex.length() == 3) {
            first = hex.substring(0, 1);
            second = hex.substring(1, 3);
        } else if (hex.length() == 4) {
            first = hex.substring(0, 2);
            second = hex.substring(2, 4);
        }
        System.out.println(first);
        System.out.println(second);
        // "c" -> "0x0c" (byte)
        byte[] firstByte = new BigInteger(first, 16).toByteArray();
        byte[] secondByte = new BigInteger(second, 16).toByteArray();

        byte[] totalByte = new byte[2];
        totalByte[0] = firstByte[0];
        totalByte[1] = secondByte[0];

        return totalByte;

    }

    public void preinstallProcess() {

        int key;
        OutputStream os;
        InputStream is;

        StringBuilder stringBuilder = new StringBuilder();
        /* PRE-INSTALL(31byte) / SETTING / 2:REQUEST / 3:REPORT(141byte) / 4:DATA  */

        System.out.println("Input => 0: PRE-INSTALL / 1:SETTING / 2:REPORT / 3:REQUEST / 4:DATA / 8: ACK / 9: NAK");
        key = sc.nextInt();

        if (key == 0) {
            System.out.println("=== [ PREINSTALL PROCESS START ] ===");

            stringBuilder.append("0"); //Flag 1
            stringBuilder.append("SWSLB-20220530-0000-0001"); // SerialNumber  24
            stringBuilder.append("20200101 000014"); //DateTime  15
            stringBuilder.append("D"); //request type 1 char
            stringBuilder.append("00"); //paraLen  4      number
            stringBuilder.append("862785043595621"); //   number   Modem(phone ,기존) Number=> 15자리  =====> hex 로 바뀔거임...
            stringBuilder.append("00"); //debug message,  number   변동사항 거의 있을수 있음. 2
//              stringBuilder.append("fe"); //check sum 2   number
        }

        byte[] preinstallTotalData = stringBuilder.toString().getBytes();

        System.out.println("totalData " + new String(preinstallTotalData));

        try {
            os = socket.getOutputStream();
            os.write(preinstallTotalData);
            os.write(makeChecksum(stringBuilder.toString()));
            os.flush();

        } catch (IOException e) {
//        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("[ PreInstall Receiving message ]");

        try {
            is = socket.getInputStream();

            byte[] reply = new byte[100];

            if (is.read(reply) < 0)
                throw new SocketException();

            System.out.println(" [Receive preinstall INFO] : " + new String(reply));


            /* reply가 맞는 값이 왔다면 ACK or NAK 와 report 값을 서버에 보내줌! */
            System.out.println("=== [PreInstall_REPORT PROCESS START ] ===");

            if (reply != null) {    // 서버에서 날라온 값이 있을 경우, ACK을 Flag로 가져감.
                /*============ 저장공간 리셋 ========*/
                Arrays.fill(preinstallTotalData, (byte) 0);   //  pre-install 값 담긴 바이트배열  0으로 초기화.

                System.out.println("totalData reset" + new String(preinstallTotalData));
//                System.out.println("stringBuilder-->" + stringBuilder);

                stringBuilder.setLength(0); // stringBuilder를 초기화.

                /*============ Header ============*/
                stringBuilder.append("8");  // Flag
                stringBuilder.append("SWSLB-20220530-0000-7877");  // SerialNum
                stringBuilder.append("20200101 000014");  // DateTime
                stringBuilder.append("00");  // paraLen
                /*============ Body ============*/
                stringBuilder.append("00");   // DebugMessage
                stringBuilder.append("0200");   // RecordingTime1
                stringBuilder.append("0300");   // RecordingTime2
                stringBuilder.append("0400");   // RecordingTime3
                stringBuilder.append("L7.300");   // Firmware Version
                stringBuilder.append("3.590");   // Battery Voltage
                stringBuilder.append("1");   // Modem RSSI
                stringBuilder.append("3");   // Sampling Time
                stringBuilder.append("4");   // SampleRate
                stringBuilder.append("862785043595621");   // Modem Number
                stringBuilder.append("test_hkchoi                     ");   // Project, 32바이트가 안되도 펌웨어에서 채워서 보내준다함.
                stringBuilder.append("producttest     ");   // SID
                stringBuilder.append(5);   // Period 1
                stringBuilder.append("thingsware.co.kr                ");   // Server URL   32
                stringBuilder.append("6669");   // Server Port  4
                stringBuilder.append("274a");   // CheckSum 2
                preinstallTotalData = stringBuilder.toString().getBytes();
                String report = new String(preinstallTotalData);
                System.out.println("report-->" + report);

                try {
                    os = socket.getOutputStream();
                    os.write(preinstallTotalData);
                    os.flush();
//                    Thread.sleep(2000);
                    //보내고 reply를 초기화시키고 거기다가 report result를 받아야함
                    Arrays.fill(reply, (byte) 0);   //  pre-install 값 담긴 바이트배열  0으로 초기화.

                    byte[] resultArr = new byte[1];
                    stringBuilder.setLength(0); // stringBuilder를 초기화.

                    is = socket.getInputStream();
                    if (is.read(resultArr) < 0)
                        throw new SocketException();

                    System.out.println("[ACK/NAK Result] : " + new String(resultArr));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                // 서버에서 날라온 값이 없을 경우, NAK(9)을 Flag로 가져감.
                /*============ 저장공간 리셋 ========*/
                Arrays.fill(preinstallTotalData, (byte) 0);  //  pre-install 값 담긴 바이트배열  0으로 초기화.
                stringBuilder.setLength(0); // stringBuilder를 초기화

                /*============ Header ============*/
                stringBuilder.append("9");  // Flag
                stringBuilder.append("SWSLB-20220530-0000-0001");  // SerialNum
                stringBuilder.append("20200101 000014");  // DataTime
                stringBuilder.append("00");  // paraLen
                /*============ Body ============*/
                stringBuilder.append("00");   // DebugMessage
                stringBuilder.append("0200");   // RecordingTime1
                stringBuilder.append("0300");   // RecordingTime2
                stringBuilder.append("0400");   // RecordingTime3
                stringBuilder.append("L7.300");   // Firmware Version
                stringBuilder.append("3.5900");   // Battery Voltage
                stringBuilder.append("0x5b");   // Modem RSSIhttps://www.hankyung.com/realestate/article/202206231960i
                stringBuilder.append("0x03");   // Sampling Time
                stringBuilder.append("4");   // SampleRate
                stringBuilder.append("862785043595621");   // Modem Number
                stringBuilder.append("test_hkchoi");   // Project
                stringBuilder.append("producttest");   // SID
                stringBuilder.append("60");   // Period
                stringBuilder.append("thingsware.co.kr");   // Server URL
                stringBuilder.append("6669");   // Server Port
                stringBuilder.append("274a");   // CheckSum

                preinstallTotalData = stringBuilder.toString().getBytes();
                try {
                    os = socket.getOutputStream();
                    os.write(preinstallTotalData);
                    os.flush();

                    Arrays.fill(reply, (byte) 0);
                    byte[] testNak = new byte[1];
                    stringBuilder.setLength(0);

                    is = socket.getInputStream();
                    if (is.read(testNak) < 0)
                        throw new SocketException();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void settingProcess() {
        System.out.println("=== [ SETTING PROCESS START ] ===");

        int key;
        OutputStream os;
        InputStream is;

        StringBuilder stringBuilder = new StringBuilder();

        System.out.println("Input => 0: PRE-INSTALL / 1:SETTING / 2:REPORT / 3:REQUEST / 4:DATA / 8: ACK / 9: NAK");
        key = sc.nextInt();

        if (key == 1) {
//            ============ Header ============
            stringBuilder.append("1");  // Flag | char | 1
            stringBuilder.append("SWSLB-20220530-0000-0001");  // SerialNum | char | 24
            stringBuilder.append("20200101 000014");  // DataTime | char 15
            stringBuilder.append("00");  // paraLen
//            ============ Body ============
            stringBuilder.append("0200"); // recordingtime1 | char| 4
            stringBuilder.append("0300"); // recordingtime2 | char| 4
            stringBuilder.append("0400"); // recordingtime3 | char| 4
//            stringBuilder.append(30) // period | number | 1 | 0,1,5,10,30,60 (min)
            stringBuilder.append("5"); // samplingTime | number | 1 | 2~9 sec
            stringBuilder.append("4"); // SampleRate |number| 1 |  4->4k(4000)  Or 8->8k(8000)
            stringBuilder.append("0"); // sleep | char| 1 | 0:Off, 1:On
            stringBuilder.append("0"); // Active | char| 1 | 0:Off, 1:On | 0 or 1
            stringBuilder.append("0"); // F-reset | number| 1 | 0:Off, 1:On
            stringBuilder.append("127.2411553"); // Px | char| 10
            stringBuilder.append("37.315906"); // Py | char| 10
            stringBuilder.append("thingsware.co.kr"); // ServerUrl |char| 32
            stringBuilder.append("0000"); // serverPort |char| 4
//            stringBuilder.append("0000"); // checksum |HEX| 2
        }

        byte[] SettingTotalData = stringBuilder.toString().getBytes();

        System.out.println("[SettingTotalData] : " + new String(SettingTotalData));

        try {
            os = socket.getOutputStream();
            os.write(SettingTotalData);
            os.write(makeChecksum(stringBuilder.toString()));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestProcess() {
        System.out.println("[requestProcess]");
    }

    public void reportProcess() {
        System.out.println("[reportProcess]");
    }

    public void dataProcess() {
        System.out.println("[dataProcess]");
    }
}
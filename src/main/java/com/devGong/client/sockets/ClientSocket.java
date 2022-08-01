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
        System.out.println("[글자를 전부 더한 수] : " + total);

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
        System.out.println("[first] : " + first);
        System.out.println("[second] : " + second);
        // "c" -> "0x0c" (byte)
        byte[] firstByte = new BigInteger(first, 16).toByteArray();
        byte[] secondByte = new BigInteger(second, 16).toByteArray();

        byte[] totalByte = new byte[2];
        totalByte[0] = firstByte[0];
        totalByte[1] = secondByte[0];

        return totalByte;

    }

    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public void preinstallProcess() {

        String key;
        OutputStream os;
        InputStream is;

        StringBuilder stringBuilder = new StringBuilder();
        /* PRE-INSTALL(31byte) / SETTING / 2:REQUEST / 3:REPORT(141byte) / 4:DATA  */

        System.out.println("Input => A: PRE_INSTALL / 6:SETTING / 7:REPORT / 4:REQUEST / 5:DATA / 8: ACK / 9: NAK");
        key = sc.nextLine();

        if (isNumeric(key) == true && Character.getNumericValue(key.toString().charAt(0)) == 4) { //REQUEST
            System.out.println("REQUEST");

        } else if (isNumeric(key) == true && Character.getNumericValue(key.toString().charAt(0)) == 5) { //DATA
            System.out.println("DATA");
        } else if (isNumeric(key) == true && Character.getNumericValue(key.toString().charAt(0)) == 6) { //SETTING
            System.out.println("SETTING");
        } else if (isNumeric(key) == true && Character.getNumericValue(key.toString().charAt(0)) == 7) { //REPORT
            System.out.println("REPORT");
        } else if (isNumeric(key) == false && key.equals("A")) {  //PRE_INSTALL
            System.out.println("=== [ PREINSTALL PROCESS START ] ===");

            stringBuilder.append("A"); //Flag 1
            stringBuilder.append("SWSLB-20220530-0000-0001"); // SerialNumber  24
            stringBuilder.append("20200101 000014"); //DateTime  15
            stringBuilder.append("D"); //request type 1 char
            stringBuilder.append("00"); //paraLen  4      number
            stringBuilder.append("862785043595621"); //   number   Modem(phone ,기존) Number=> 15자리  =====> hex 로 바뀔거임...
            stringBuilder.append("00"); //debug message,  number   변동사항 거의 있을수 있음. 2


            byte[] totalData = stringBuilder.toString().getBytes();

            System.out.println("totalData " + new String(totalData));

            try {
                os = socket.getOutputStream();
                os.write(totalData);
                os.write(makeChecksum(stringBuilder.toString()));
                os.flush();
                Thread.sleep(500);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

            System.out.println("===[ PreInstall Receiving message ]===");

            try {
                is = socket.getInputStream();

                byte[] reply = new byte[3000];
                System.out.println("reply-->" + new String(reply));
                System.out.println("length-->" + reply.length);


                if (is.read(reply) < 0)
                    throw new SocketException();
                // 넘겨 받은 값을 찍어줌
                System.out.println(" [Receive preinstall INFO] : " + new String(reply));
                System.out.println("=== [PreInstall_REPORT PROCESS START ] ===");

                if (reply != null) {    // 서버에서 날라온 값이 있을 경우, ACK을 Flag로 가져감.
                    /*============ 저장공간 리셋 ========*/
                    Arrays.fill(totalData, (byte) 0);   //  pre-install 값 담긴 바이트배열  0으로 초기화.
                    System.out.println("[totalData reset] : " + new String(totalData));
                    System.out.println("stringBuilder-->" + stringBuilder);
                    stringBuilder.setLength(0); // stringBuilder를 초기화.
                    System.out.println("stringBuilder reset -->" + stringBuilder);
                    /*============ Header ============*/
                    stringBuilder.append("8");  // Flag
                    stringBuilder.append("SWSLB-20220530-0000-7877");  // SerialNum
                    stringBuilder.append("20200101 000014");  // DateTime
                    stringBuilder.append("00");  // paraLen
                    /*============ Body ============*/
                    stringBuilder.append("00");   // DebugMessage | char 13    --->>>>  1에서 13으로 변함
                    stringBuilder.append("0200");   // RecordingTime1 | char 4
                    stringBuilder.append("0300");   // RecordingTime2 | char 4
                    stringBuilder.append("0400");   // RecordingTime3 | char 4
                    stringBuilder.append("0959");   // FM Radio | char 4
                    stringBuilder.append("L7.300");   // Firmware Version | char 6
                    stringBuilder.append("3.590");   // Battery Voltage | char 6
                    stringBuilder.append(1);   // Modem RSSI | number 1
                    stringBuilder.append("00"); //device status | char 2
                    stringBuilder.append(3);   // Sampling Time | number 1
                    stringBuilder.append("128.593085");//Px | char 10
                    stringBuilder.append("35.845037 ");//Py | char 10
                    stringBuilder.append("821227203543");//Pname(Phone) | char 16
                    stringBuilder.append("producttest     ");   // SID |char 16
                    stringBuilder.append(5);   // Period number 1
                    stringBuilder.append("thingsware.co.kr                ");   // Server URL   32
                    stringBuilder.append("6669");   // Server Port  4
                    stringBuilder.append("thingsware.co.kr                ");// DB URL | char 32
                    stringBuilder.append("3306");// DB PORT | char 4
                    stringBuilder.append(3);// radio time | number 1
                    stringBuilder.append("0");// baudrate | char 1
                    stringBuilder.append("0");// baudrate next | char 1
                    stringBuilder.append(2);// pcb version | number 1

                    totalData = stringBuilder.toString().getBytes();
                    String report = new String(totalData);
                    System.out.println("[report(D->S)]-->" + report);

                    try {
                        os = socket.getOutputStream();
                        os.write(totalData);
                        os.flush();
                        Thread.sleep(500);
                        Arrays.fill(reply, (byte) 0);

                        byte[] test = new byte[1];
                        stringBuilder.setLength(0);

                        is = socket.getInputStream();
                        if (is.read(test) < 0)
                            throw new SocketException();

                        System.out.println("[ACK/NAK Result] : " + new String(test));
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }

                    // ACK ---> setting process 타야함





                } else {
                    // 서버에서 날라온 값이 없을 경우, NAK(9)을 Flag로 가져감.
                    /*============ 저장공간 리셋 ========*/
                    Arrays.fill(totalData, (byte) 0);  //  pre-install 값 담긴 바이트배열  0으로 초기화.
                    stringBuilder.setLength(0); // stringBuilder를 초기화
                    /*============ Header ============*/
                    stringBuilder.append("9");  // Flag
                    stringBuilder.append("SWSLB-20220530-0000-7877");  // SerialNum
                    stringBuilder.append("20200101 000014");  // DateTime
                    stringBuilder.append("00");  // paraLen
                    /*============ Body ============*/
                    stringBuilder.append("00");   // DebugMessage | char 13    --->>>>  1에서 13으로 변함
                    stringBuilder.append("0200");   // RecordingTime1 | char 4
                    stringBuilder.append("0300");   // RecordingTime2 | char 4
                    stringBuilder.append("0400");   // RecordingTime3 | char 4
                    stringBuilder.append("0959");   // FM Radio | char 4
                    stringBuilder.append("L7.300");   // Firmware Version | char 6
                    stringBuilder.append("3.590");   // Battery Voltage | char 6
                    stringBuilder.append(1);   // Modem RSSI | number 1
                    stringBuilder.append("00"); //device status | char 2
                    stringBuilder.append(3);   // Sampling Time | number 1
                    stringBuilder.append("128.593085");//Px | char 10
                    stringBuilder.append("35.845037 ");//Py | char 10
                    stringBuilder.append("821227203543");//Pname(Phone) | char 16
                    stringBuilder.append("producttest     ");   // SID |char 16
                    stringBuilder.append(5);   // Period number 1
                    stringBuilder.append("thingsware.co.kr                ");   // Server URL   32
                    stringBuilder.append("6669");   // Server Port  4
                    stringBuilder.append("thingsware.co.kr                ");// DB URL | char 32
                    stringBuilder.append("3306");// DB PORT | char 4
                    stringBuilder.append(3);// radio time | number 1
                    stringBuilder.append("0");// baudrate | char 1
                    stringBuilder.append("0");// baudrate next | char 1
                    stringBuilder.append(2);// pcb version | number 1

                    totalData = stringBuilder.toString().getBytes();
                    String report = new String(totalData);
                    System.out.println("[report(D->S)]-->" + report);

                    totalData = stringBuilder.toString().getBytes();

                    try {
                        os = socket.getOutputStream();
                        os.write(totalData);
                        os.flush();
                        Thread.sleep(500);
                        Arrays.fill(reply, (byte) 0);

                        byte[] test = new byte[1];
                        stringBuilder.setLength(0);

                        is = socket.getInputStream();
                        if (is.read(test) < 0)
                            throw new SocketException();

                        System.out.println("[ACK/NAK Result] : " + new String(test));
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                    // NAK ---> 다시 프리인스톨 처음 단계로 돌아감.


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

  /*  public void settingProcess() {
        System.out.println("=== [ SETTING PROCESS START ] ===");

        int key;
        OutputStream os;
        InputStream is;

        StringBuilder stringBuilder = new StringBuilder();

        System.out.println("Input => 0: PRE-INSTALL / 6:SETTING / 2:REPORT / 3:REQUEST / 4:DATA / 8: ACK / 9: NAK");
        key = sc.nextInt();

        if (key == 6) {
//            ============ Header ============
            stringBuilder.append("6");  // Flag | char | 1
            stringBuilder.append("SWSLB-20220530-0000-0001");  // SerialNum | char | 24
            stringBuilder.append("20200101 000014");  // DataTime | char 15
            stringBuilder.append("D");  // request type
            stringBuilder.append("00");  // paraLen | number 4
//            ============ Body ============
            stringBuilder.append("0200"); // recordingtime1 | char| 4
            stringBuilder.append("0300"); // recordingtime2 | char| 4
            stringBuilder.append("0400"); // recordingtime3 | char| 4
            stringBuilder.append("0959"); // FM Radio | char| 4
            stringBuilder.append("producttest"); // sid | char| 16
            stringBuilder.append("5"); // samplingTime | number | 1 | 2~9 sec
            stringBuilder.append("0"); // sleep | char| 1 | 0:Off, 1:On
            stringBuilder.append("0"); // F-reset | number| 1 | 0:Off, 1:On
            stringBuilder.append("0"); // Px | char| 1 | 0:Off, 1:On | 0 or 1
            stringBuilder.append("0"); // Py | char| 1 | 0:Off, 1:On | 0 or 1
            stringBuilder.append("0"); // Active | char| 1 | 0:Off, 1:On | 0 or 1
            stringBuilder.append("4"); // SampleRate |number| 1 |  4->4k(4000)  Or 8->8k(8000)
            stringBuilder.append("thingsware.co.kr"); // ServerUrl |char| 32
            stringBuilder.append("0000"); // serverPort |char| 4
            stringBuilder.append("thingsware.co.kr"); // DB Url |char| 4
            stringBuilder.append("0000"); // DB Port |char| 4
        }
        byte period = 30;
        String px = "127.241155";
        String py = "37.315906";

        byte[] settingTotalData = stringBuilder.toString().getBytes();

        System.out.println(Arrays.toString(settingTotalData));

        byte[] arr1 = Arrays.copyOfRange(settingTotalData, 0, 54);
        byte[] arr2 = Arrays.copyOfRange(settingTotalData, 54, 59);
        byte[] arr3 = Arrays.copyOfRange(settingTotalData, 59, 99);

        System.out.println("[arr1] : " + new String(arr1));
        System.out.println("[period] : " + period);
        System.out.println("[arr2] : " + new String(arr2));

        try {
            os = socket.getOutputStream();

            os.write(arr1);
            os.write(period);
            os.write(arr2);
//            os.write(makeLocation(px, py));    // 여기서 지금 합쳐서 보내던데 ㅅㅂ
            os.write(arr3);
            os.write(makeChecksum(stringBuilder.toString()));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

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
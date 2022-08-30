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

    public byte fudnsml(String input) {
        return input.length() == 1 ? (byte) Character.digit(input.charAt(0), 16)
                : (byte) ((Character.digit(input.charAt(0), 16) << 4) + Character.digit(input.charAt(1), 16));
    }


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
        byte firstByte = fudnsml(first);
        byte secondByte = fudnsml(second);

        byte[] totalByte = new byte[2];
        totalByte[0] = firstByte;
        totalByte[1] = secondByte;

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

    /*=======================================================================*/
    public boolean requestProcess(boolean reportResult) {
        System.out.println("[REQUEST PROCESS START ]");

        String key;
        OutputStream os;
        InputStream is;
        StringBuilder stringBuilder = new StringBuilder();
        if (reportResult) {
            System.out.println("Input => A: PRE_INSTALL / 6:SETTING / 7:REPORT / 4:REQUEST / 5:DATA / 8: ACK / 9: NAK");
            key = sc.nextLine();
            if (isNumeric(key) && Character.getNumericValue(key.toString().charAt(0)) == 4 && reportResult) {

                stringBuilder.append("4"); // Flag 1
                stringBuilder.append("SWFLB-20220708-0760-3465"); // Sensor ID  24
                stringBuilder.append("20220819_110021"); // DateTime  15
                stringBuilder.append("D"); // request type 1 char
                stringBuilder.append("00  "); // paraLen  4      number

                stringBuilder.append(5); // frame 개수       2 number
                stringBuilder.append(256); //data size/frame  4 number
                stringBuilder.append(4); //samplerate       1 number

                byte[] totalData = stringBuilder.toString().getBytes();
                byte[] chkSumData = makeChecksum(stringBuilder.toString());
                int arrayLength = totalData.length + chkSumData.length;
                byte[] result = new byte[1];
                byte[] finalArr = new byte[arrayLength];

                System.arraycopy(totalData, 0, finalArr, 0, totalData.length);
                System.arraycopy(chkSumData, 0, finalArr, finalArr.length - 2, chkSumData.length);

                System.out.println("totalData " + new String(totalData));
                System.out.println("chkSumData " + new String(chkSumData));
                System.out.println("finalArr " + new String(finalArr));

                try {
                    os = socket.getOutputStream();
                    os.write(finalArr);
                    os.flush();
                    Thread.sleep(500);

                    is = socket.getInputStream();
                    is.read(result);
                    System.out.println("[ACK/NAK Result] : " + new String(result));
                    System.out.println("[END] : request process finish");

                    // NAK 이면 False Return
                    if (result[0] == '9') {
                        return false;
                    }
                    return true;
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    /*=======================================================================*/
    public boolean reportProcess(boolean settingResult) throws IOException {

        System.out.println("[REPORT PROCESS START ]");

        String key;
        OutputStream os;
        InputStream is;

        // Setting result가 true이면 동작 --> 분기 조건 다시보기!!! NAK or ack
        if (settingResult) {

            StringBuilder stringBuilder = new StringBuilder();

            System.out.println("Input => A: PRE_INSTALL / 6:SETTING / 7:REPORT / 4:REQUEST / 5:DATA / 8: ACK / 9: NAK");
            key = sc.nextLine();
            if (isNumeric(key) && Character.getNumericValue(key.toString().charAt(0)) == 7 && settingResult) {

                stringBuilder.append("7"); //Flag 1
                stringBuilder.append("SWFLB-20220708-0760-3465"); // Sensor ID  24
//              DB에서 직접 넣어줘야함
                stringBuilder.append("D"); //request type 1 char
                stringBuilder.append("00  "); //paraLen  4      number
                stringBuilder.append("20220720 0031"); // end Record Time 13
                stringBuilder.append("0200"); // time1 4
                stringBuilder.append("0300"); // time2 4
                stringBuilder.append("0400"); // time3 4
                stringBuilder.append("0911"); // FM Radio 4
                stringBuilder.append("L7.400"); // Firmware version 6
                stringBuilder.append("4.0200"); // Battery Value 6
                stringBuilder.append(116); // Modem RSSI 1
                stringBuilder.append("00"); // Device Status 2
                stringBuilder.append("2"); // Sampling Time 1
                stringBuilder.append("128.323352"); // px 10
                stringBuilder.append("34.972365 "); // py 10
                stringBuilder.append("8212-3268-8662  "); // P_name 16
                stringBuilder.append("goseong_kw      "); // SID 16
                stringBuilder.append("1"); // period 1
                stringBuilder.append("thingsware.co.kr                "); // Server URL 32
                stringBuilder.append("6699"); // Server Port 477
                stringBuilder.append("thingsware.co.kr                "); // DB URL 32
                stringBuilder.append("3306"); // DB Port 4
                stringBuilder.append(" "); // Sleep 1
                stringBuilder.append(" "); // Active 1
                stringBuilder.append(" "); // F_Reset 1
                stringBuilder.append(" "); // Reset 1
                stringBuilder.append(" "); // SampleRate 1
                stringBuilder.append("1"); // Radio Time 1
                stringBuilder.append("1  "); // CREG Count 1
                stringBuilder.append("2  "); // Sleep Count 1


                byte[] totalData = stringBuilder.toString().getBytes();
                byte[] chkSumData = makeChecksum(stringBuilder.toString());
                int arrayLength = totalData.length + chkSumData.length;
                byte[] finalArr = new byte[arrayLength];

                System.arraycopy(totalData, 0, finalArr, 0, totalData.length);
                System.arraycopy(chkSumData, 0, finalArr, finalArr.length - 2, chkSumData.length);

                System.out.println("totalData " + new String(totalData));
                System.out.println("chkSumData " + new String(chkSumData));

                try {
                    os = socket.getOutputStream();
                    os.write(finalArr);
                    os.flush();
                    Thread.sleep(500);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

                byte[] signalResponse = new byte[1];
                stringBuilder.setLength(0);

                is = socket.getInputStream();
                if (is.read(signalResponse) < 0)
                    throw new SocketException();
                System.out.println("[signalResponse (ACK / NAK)] : " + new String(signalResponse));
                return true;
            }
        }
        return false;
    }

    /*=======================================================================*/
    public boolean dataProcess(boolean requestResult) {

        String key;
        OutputStream os;
        InputStream is;

        StringBuilder stringBuilder = new StringBuilder();

        if (requestResult) {
            System.out.println("Input => A: PRE_INSTALL / 6:SETTING / 7:REPORT / 4:REQUEST / 5:DATA / 8: ACK / 9: NAK");
            key = sc.nextLine();
            if (isNumeric(key) && Character.getNumericValue(key.toString().charAt(0)) == 5 && requestResult) {

                stringBuilder.append("5"); //Flag 1
                stringBuilder.append("SWFLB-20220708-0760-3465"); // Sensor ID  24
                stringBuilder.append("20220819_110021"); //DateTime  15
                stringBuilder.append("D"); //request type 1 char
                stringBuilder.append("00  "); //paraLen  4      number

                //data  256 byte
                stringBuilder.append("scsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsolutionscsols");

                byte[] totalData = stringBuilder.toString().getBytes();
                byte[] chkSumData = makeChecksum(stringBuilder.toString());
                int arrayLength = totalData.length + chkSumData.length;
                byte[] finalArr = new byte[arrayLength];

                System.arraycopy(totalData, 0, finalArr, 0, totalData.length);
                System.arraycopy(chkSumData, 0, finalArr, finalArr.length - 2, chkSumData.length);

                System.out.println("totalData " + new String(totalData));
                System.out.println("chkSumData " + new String(chkSumData));

                try {
                    os = socket.getOutputStream();
                    os.write(finalArr);
                    os.flush();
                    Thread.sleep(500);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

            }
        }


        return true;
    }
    /*=======================================================================*/


    public boolean settingProcess(boolean preinstallResult) {
        String key;
        OutputStream os;
        InputStream is;

        StringBuilder stringBuilder = new StringBuilder();

        System.out.println("Input => A: PRE_INSTALL / 6:SETTING / 7:REPORT / 4:REQUEST / 5:DATA / 8: ACK / 9: NAK");
        key = sc.nextLine();
        if (isNumeric(key) && Character.getNumericValue(key.toString().charAt(0)) == 6 && preinstallResult) { //SETTING

            stringBuilder.append("6"); //Flag 1
            stringBuilder.append("SWFLB-20210812-0106-1678"); // Sensor ID  24
            stringBuilder.append("20220205 999914"); //DateTime  15
            stringBuilder.append("D"); //request type 1 char
            stringBuilder.append("00  "); //paraLen  4      number
            stringBuilder.append("daeguf          "); // SID  16
            stringBuilder.append("0109_debec      "); // pname 16

            byte[] totalData = stringBuilder.toString().getBytes();
            byte[] chkSumData = makeChecksum(stringBuilder.toString());
            int arrayLength = totalData.length + chkSumData.length;

            byte[] finalArr = new byte[arrayLength];

            System.arraycopy(totalData, 0, finalArr, 0, totalData.length);
            System.arraycopy(chkSumData, 0, finalArr, finalArr.length - 2, chkSumData.length);

            System.out.println("totalData " + new String(totalData));
            System.out.println("chkSumData " + new String(chkSumData));
            System.out.println("finalArr " + new String(finalArr));

            try {
                os = socket.getOutputStream();
                os.write(finalArr);
                os.flush();
                Thread.sleep(500);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

            System.out.println("===[ Setting Receiving Result ]===");
            try {
                is = socket.getInputStream();

                byte[] SettingReceiveResult = new byte[200];
                System.out.println("[Setting Receive] -->" + new String(SettingReceiveResult));


                if (is.read(SettingReceiveResult) < 0)
                    throw new SocketException();
                // 넘겨 받은 값을 찍어줌
                System.out.println("[Setting Receive INFO] : " + new String(SettingReceiveResult));

                //ack를 던져줌
                if (SettingReceiveResult != null) {
                    Arrays.fill(totalData, (byte) 0);   //  pre-install 값 담긴 바이트배열  0으로 초기화.
                    stringBuilder.setLength(0); // stringBuilder를 초기화.
                    stringBuilder.append("8");
                    System.out.println(stringBuilder.toString());

                    if (stringBuilder.toString().equals("8")) {
                        return true;
                    }

                    // 재요청
                } else {
                    Arrays.fill(totalData, (byte) 0);   //  pre-install 값 담긴 바이트배열  0으로 초기화.

                    stringBuilder.setLength(0); // stringBuilder를 초기화.
                    System.out.println("==============[재요청]================");
                    stringBuilder.append("6"); //Flag 1
                    stringBuilder.append("SWFLB-20210812-0106-1678"); // Sensor ID  24
                    stringBuilder.append("20220205 999914"); //DateTime  15
                    stringBuilder.append("D"); //request type 1 char
                    stringBuilder.append("00  "); //paraLen  4      number
                    stringBuilder.append("daeguf          "); // SID  16
                    stringBuilder.append("0109_debec      "); // pname 16
                    System.out.println("[Re-request] : " + stringBuilder.toString());

                    //---------------------------------------------
                    System.out.println("[totalData] : " + new String(totalData));
                    System.out.println("[chkSumData] " + new String(chkSumData));
                    System.out.println("finalArr " + new String(finalArr));

                    try {
                        os = socket.getOutputStream();
                        os.write(finalArr);
                        os.flush();
                        Thread.sleep(500);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("===[ Setting  2nd Receiving result   ]===");
                    SettingReceiveResult = new byte[200];
                    if (is.read(SettingReceiveResult) < 0)
                        throw new SocketException();
                    System.out.println("[2nd SettingReceiveResult] : " + new String(SettingReceiveResult));
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean preinstallProcess() {

        String key;
        OutputStream os;
        InputStream is;

        StringBuilder stringBuilder = new StringBuilder();
        /* PRE-INSTALL(31byte) / SETTING / 2:REQUEST / 3:REPORT(141byte) / 4:DATA  */

        System.out.println("Input => A: PRE_INSTALL / 6:SETTING / 7:REPORT / 4:REQUEST / 5:DATA / 8: ACK / 9: NAK");
        key = sc.nextLine();

        if (isNumeric(key) == false && key.equals("A")) {  //PRE_INSTALL
            System.out.println("=== [ PREINSTALL PROCESS START ] ===");

            stringBuilder.append("A"); //Flag 1
            stringBuilder.append("SWSLB-20220530-0000-0001"); // SerialNumber  24
            stringBuilder.append("20200101 000014"); //DateTime  15
            stringBuilder.append("D"); //request type 1 char
            stringBuilder.append("00"); //paraLen  4      number
            stringBuilder.append("862785043595621"); //   number   Modem(phone ,기존) Number=> 15자리  =====> hex 로 바뀔거임...
            stringBuilder.append("  "); //debug message,  number   변동사항 거의 있을수 있음. 2


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

            System.out.println("===[ PreInstall Receiving Result ]===");

            try {
                is = socket.getInputStream();

                byte[] reply = new byte[150];
                System.out.println("reply-->" + new String(reply));
//                System.out.println("length-->" + reply.length);


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
                    byte[] result = new byte[1];
                    try {
                        os = socket.getOutputStream();
                        os.write(totalData);
                        os.flush();
                        Thread.sleep(500);
                        Arrays.fill(reply, (byte) 0);
                        stringBuilder.setLength(0);

                        is = socket.getInputStream();
                        if (is.read(result) < 0)
                            throw new SocketException();

                        System.out.println("[ACK/NAK Result] : " + new String(result));
                        return true;
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }

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

                        byte[] returnResult = new byte[1];
                        stringBuilder.setLength(0);

                        is = socket.getInputStream();
                        if (is.read(returnResult) < 0)
                            throw new SocketException();

                        System.out.println("[ACK/NAK Result] : " + new String(returnResult));
                        return false;
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                    // NAK ---> 다시 프리인스톨 처음 단계로 돌아감.
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}
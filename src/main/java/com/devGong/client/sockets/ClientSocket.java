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
        /* PRE-INSTALL(31byte) / SETTING / 2:REQUEST / 3:REPORT(141byte) / 4:DATA
         */
        do {
            System.out.println("Input => 0: PRE-INSTALL(31byte) / 1:SETTING / 2:REQUEST / 3:REPORT / 4:DATA / 8: ACK / 9: NAK  ");
            key = scanner.nextInt();

            switch (key) {

                case 0:
                    System.out.println("PRE-INSTALL selected");
                    for (int i = 0; i < messageLength; i++) {
                    /*===HEADER=======================================================*/
                        stringBuilder.append("A"); //flag
                        stringBuilder.append("000000000000000000000000"); //modem number
                        stringBuilder.append("20200101 000014"); //date time
                        stringBuilder.append("0x00"); //para_len
                        stringBuilder.append("0x00"); //para_len
                        stringBuilder.append("0x00"); //para_len
                        stringBuilder.append("0x1f"); //para_len
                    /*===REQUEST=====================================================*/
                        stringBuilder.append("8212-3294-5260"); //Modem Number
                        stringBuilder.append("255 NONE"); //debug message
                        stringBuilder.append("0cc1"); //check sum


                    }
                    break;
                case 1:
                    System.out.println("SETTING selected ");
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append("RecordingTime1");
                        stringBuilder.append("RecordingTime2");
                        stringBuilder.append("RecordingTime3");
                        stringBuilder.append("S/N");
                        stringBuilder.append("period");
                        stringBuilder.append("SamplingTime");
                        stringBuilder.append("ServreURL");
                        stringBuilder.append("ServerPort");
                        stringBuilder.append("CheckSum");
                    }
                    break;
                case 2:
                    System.out.println("REQUEST selected ");
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append("DebugMessage");
                        stringBuilder.append("RecordingTime1");
                        stringBuilder.append("RecordingTime2");
                        stringBuilder.append("RecordingTime3");
                        stringBuilder.append("FirmwareVersion");
                        stringBuilder.append("BatteryVoltage");
                        stringBuilder.append("ModernRSSI");
                        stringBuilder.append("Project");
                        stringBuilder.append("SID");
                        stringBuilder.append("Period");
                        stringBuilder.append("ServerURL");
                        stringBuilder.append("ServerPort");
                        stringBuilder.append("CheckSUM");

                    }
                    break;
                case 3:
                    System.out.println("REPORT selected ");
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append("RecordingTime1");
                        stringBuilder.append("RecordingTime2");
                        stringBuilder.append("RecordingTime3");
                        stringBuilder.append("Sleep");
                        stringBuilder.append("Period");
                        stringBuilder.append("SamplingTime");
                        stringBuilder.append("F-reset");
                        stringBuilder.append("Px");
                        stringBuilder.append("Py");
                        stringBuilder.append("Active");
                        stringBuilder.append("SampleRate");
                        stringBuilder.append("ServerURL");
                        stringBuilder.append("ServerPort");
                        stringBuilder.append("CheckSUM");
                    }
                    break;
                case 4:
                    System.out.println("DATA selected ");
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append("EndRecordTime");
                        stringBuilder.append("RecordingTime1");
                        stringBuilder.append("RecordingTime2");
                        stringBuilder.append("RecordingTime3");
                        stringBuilder.append("FirmwareVersion");
                        stringBuilder.append("BatteryVoltage");
                        stringBuilder.append("ModernRSSI");
                        stringBuilder.append("DeviceStatus");
                        stringBuilder.append("SamplingTime");
                        stringBuilder.append("Px");
                        stringBuilder.append("Py");
                        stringBuilder.append("Period");
                        stringBuilder.append("ServerURL");
                        stringBuilder.append("ServerPort");
                        stringBuilder.append("Sleep");
                        stringBuilder.append("Active");
                        stringBuilder.append("F-reset");
                        stringBuilder.append("SampleRate");
                        stringBuilder.append("CregCount");
                        stringBuilder.append("CheckSum");
                    }
                    break;
            }

        }
        while (key >= 10);
        System.out.println("종료");


        byte[] totalData = stringBuilder.toString().getBytes();

//        System.out.println("<<<Sending message>>>");

        try {
            OutputStream os = socket.getOutputStream();

         /*   for (int i = 0; i < messageLength / delimiterLength; i++) {  //delimiterLength = 256
                byte[] sending = Arrays.copyOfRange(totalData, i * delimiterLength, (i + 1) * delimiterLength);
                //               Arrays.copyOfRange(원본 배열, 복사할 시작인덱스, 복사할 끝인덱스) 인덱스는 0부터 시작하는것 기준
                System.out.println("sending... " + (i + 1));
                os.write(sending); //write( byte[] sending ) 매개값으로 주어진 바이트 배열의 모든 바이트를 출력 스트림으로 보냅니다
                os.flush(); // flush()는 버퍼에 남아있는 데이터를 모두 출력시키고, 버퍼를 비우는 역할을 합니다.
                Thread.sleep(500);
            }*/
            byte[] sending = Arrays.copyOf(totalData, messageLength);  ///
            //byte[] sending = totalData ;

            os.write(sending); //write( byte[] sending ) 매개값으로 주어진 바이트 배열의 모든 바이트를 출력 스트림으로 보냅니다
            os.flush(); // flush()는 버퍼에 남아있는 데이터를 모두 출력시키고, 버퍼를 비우는 역할을 합니다.
            Thread.sleep(500);


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
            System.out.println(new String(reply));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
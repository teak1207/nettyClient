package com.devGong.client.sockets;

import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;


@AllArgsConstructor
public class ClientSocket {
    private Socket socket;

    public void sendFixedLength(int messageLength) {
        int delimiterLength = 256;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < messageLength; i++)
            stringBuilder.append("B");
            stringBuilder.append("a");
//            stringBuilder.append("C");
//            stringBuilder.append("D");
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
        }
        catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("Receiving message");
        try {
            InputStream is = socket.getInputStream();

            byte[] reply = new byte[messageLength];
            ByteStreams.read(is, reply, 0, reply.length);

            System.out.println(new String(reply));
            // 여기


        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
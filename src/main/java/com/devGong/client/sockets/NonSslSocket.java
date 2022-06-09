package com.devGong.client.sockets;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

@AllArgsConstructor
public class NonSslSocket{
    private String host;
    private int port;
    // 길이 + 주소 + 포트번혼
    public void run(int messageLength) {
        try {
            Socket socket = new Socket();
            SocketAddress address = new InetSocketAddress(host, port);
            socket.connect(address);

            ClientSocket clientSocket = new ClientSocket(socket);
            clientSocket.sendFixedLength(messageLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
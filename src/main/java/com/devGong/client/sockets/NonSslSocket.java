package com.devGong.client.sockets;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

@AllArgsConstructor
public class NonSslSocket {
    private String host;
    private int port;

    // 길이 + 주소 + 포트번혼
    public void run() {
        try {
            Socket socket = new Socket();
            SocketAddress address = new InetSocketAddress(host, port);
            socket.connect(address);

            ClientSocket clientSocket = new ClientSocket(socket);

            boolean preinstallResult = clientSocket.preinstallProcess();


            boolean settingResult = clientSocket.settingProcess(preinstallResult);
            boolean dataResult = clientSocket.dataProcess(settingResult);;
            boolean requestResult;


//            clientSocket.dataProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
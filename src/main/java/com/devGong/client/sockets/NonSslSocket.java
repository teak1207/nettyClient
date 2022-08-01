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
            clientSocket.settingProcess(preinstallResult);
            boolean requestResult;
            boolean settingResult;
            boolean dataResult;

//            clientSocket.requestProcess();
//            clientSocket.reportProcess();
//            clientSocket.dataProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
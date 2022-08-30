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

            if (preinstallResult) {
                boolean settingResult = clientSocket.settingProcess(true);
                if (settingResult) {
                    boolean reportResult = clientSocket.reportProcess(true);

                    if (reportResult) {
                        boolean requestResult = clientSocket.requestProcess(true);
                        if (requestResult) {
                            clientSocket.dataProcess(true);
                        }
                    }
                }
            }
            boolean requestResult;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
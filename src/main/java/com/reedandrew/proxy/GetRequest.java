package com.reedandrew.proxy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GetRequest implements Runnable {

    private static final byte[] SERVER_ERROR = "HTTP/1.0 500 Server Error\r\nConnection: close\r\n\r\n".getBytes();

    private Socket socket;

    public GetRequest(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            OutputStream outputStream = socket.getOutputStream();

            String line;
            List<String> headerLines = new ArrayList<>();
            while (!StringUtils.isEmpty((line = in.readLine()))) {
                headerLines.add(line);
            }

            try {
                ParsedRequest parsedRequest = RequestParser.parseHeader(headerLines);
                String domainPlusPort = parsedRequest.url();
                String[] domainAndPort = domainPlusPort.split(":");
                String remoteDomain;
                int remotePort;
                if (domainAndPort.length == 1) {
                    remotePort = 80;
                } else {
                    remotePort = Integer.parseInt(domainAndPort[1]);
                }
                remoteDomain = domainAndPort[0];
                Socket remoteSocket = new Socket(remoteDomain, remotePort);
                remoteSocket.getOutputStream().write(parsedRequest.fullRequest().getBytes());

                InputStream stream =
                        remoteSocket.getInputStream();


                byte[] buffer = new byte[5000];

                int read = stream.read(buffer, 0, 4999);
                while (read > 0) {
                    outputStream.write(buffer, 0, read);
                    read = stream.read(buffer, 0, 4999);
                }

                remoteSocket.close();


            } catch (IllegalArgumentException e) {
                socket.getOutputStream().write(SERVER_ERROR);
            } catch (SocketException e) {
                log.error("Socket Exception: {}", headerLines);
            } finally {
                if (!socket.isClosed()) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }
}

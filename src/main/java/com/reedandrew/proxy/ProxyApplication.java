package com.reedandrew.proxy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class ProxyApplication {

	private static final String END_LINE = "\n";

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: `proxy port#`");
		} else {
			String portString = args[0];

			try {
				int port = Integer.parseInt(portString);
				ServerSocket ss = new ServerSocket(port);
				while (true) {
					Socket clientSocket = ss.accept();
					BufferedReader in = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));
					OutputStream outputStream = clientSocket.getOutputStream();

					String s;
					while (!StringUtils.isEmpty((s = in.readLine()))) {
						outputStream.write(s.getBytes());
						outputStream.write(END_LINE.getBytes());
						outputStream.flush();
					}
					clientSocket.close();
				}
			} catch (IOException e) {
				log.debug("", e);
				System.err.println("Unable to bind to port: " + portString);
			} catch (NumberFormatException e) {
				System.err.println("Port argument must be integer but was: " + portString);
			}
		}
  }

}

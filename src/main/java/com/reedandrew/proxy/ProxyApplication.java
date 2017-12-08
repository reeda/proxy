package com.reedandrew.proxy;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ProxyApplication {

	private static ExecutorService executorService = Executors.newFixedThreadPool(30);

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
					executorService.execute(new GetRequest(clientSocket));
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

package com.reedandrew.proxy;

public class ProxyApplication {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: `proxy port#`");
		} else {
			String port = args[0];
			System.out.println("using port " + port);
		}
  }

}

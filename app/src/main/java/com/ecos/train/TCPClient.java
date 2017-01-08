package com.ecos.train;

import com.ecos.train.activity.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient extends Thread {

	private String serverMessage;
	private boolean mRun = false;

	Socket socket;
	public PrintWriter out;
	BufferedReader in;


	public TCPClient() {
		this.start();
	}

	public void stopClient(){
		mRun = false;
		try {
			socket.close();
		} catch (Exception e) {
		}
	}

	public void run() {

		mRun = true;

		try {
			InetAddress serverAddr = InetAddress.getByName(Settings.consoleIp);
			socket = new Socket(serverAddr, Settings.consolePort);

			try {
				//send the message to the server
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);

				//pass the out poitner to writer thread
				MainActivity.mTcpWrite.setOut(out);

				//receive the message which the server sends back
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				StringBuilder sb = new StringBuilder();

				MainActivity.getMessage("READY");

				//in this while the client listens for the messages sent by the server
				while (mRun) {

					serverMessage = in.readLine();

					if (serverMessage.startsWith("<REPLY") || 
							serverMessage.startsWith("<EVENT")) {
						sb = new StringBuilder();
						sb.append(serverMessage).append("\n");
					}
					else if (serverMessage.startsWith("<END")) {
						sb.append(serverMessage).append("\n");
						if (serverMessage != null) {
							//call the method messageReceived from MyActivity class
							MainActivity.getMessage(sb.toString().trim());
						}
						serverMessage = null;
					}
					else {
						sb.append(serverMessage).append("\n");
					}
				}
				
			} catch (Exception e) {
				MainActivity.getMessage("DISCONNECT");
			} finally {
				//the socket must be closed. It is not possible to reconnect to this socket
				// after it is closed, which means a new socket instance has to be created.
				socket.close();
			}

		} catch (Exception e) {
			MainActivity.getMessage("DISCONNECT");
		}
	}
}
package com.ecos.train;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class TCPClient {

	private String serverMessage;
	private OnMessageReceived mMessageListener = null;
	private boolean mRun = false;

	Socket socket;
	PrintWriter out;
	BufferedReader in;
	
	/**
	 *  Constructor of the class. 
	 *  OnMessagedReceived listens for the messages received from server
	 */
	public TCPClient(OnMessageReceived listener) {
		mMessageListener = listener;
	}

	/**
	 * Sends the message entered by client to the server
	 * @param message text entered by client
	 */
	public void sendMessage(String message){
		if (out != null && !out.checkError()) {
			out.println(message);
			out.flush();
		}
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

				//receive the message which the server sends back
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				StringBuilder sb = new StringBuilder();
				
				mMessageListener.messageReceived("READY");
				
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
						if (serverMessage != null && mMessageListener != null) {
							//call the method messageReceived from MyActivity class
							mMessageListener.messageReceived(sb.toString().trim());
						}
						serverMessage = null;
					}
					else {
						sb.append(serverMessage).append("\n");
					}
				}
				
			} catch (Exception e) {
				mMessageListener.messageReceived("ERROR: " + e.toString());
			} finally {
				//the socket must be closed. It is not possible to reconnect to this socket
				// after it is closed, which means a new socket instance has to be created.
				socket.close();
			}

		} catch (Exception e) {
			mMessageListener.messageReceived("ERROR: " + e.toString());
		}

	}

	//Declare the interface. The method messageReceived(String message) 
	//will must be implemented in the MyActivity
	//class at on asynckTask doInBackground
	public interface OnMessageReceived {
		public void messageReceived(String message);
	}
	
	

	public void getEmergencyState() {
		sendMessage("get(1, status)");
	}
	
	public void getAllTrains() {
		sendMessage("queryObjects(10, name, addr)");
	}
	
	public void getTrainMainState() {
		sendMessage("get("+Settings.trainId+",name,speed,dir)");
	}
	
	public void getTrainButtonState() {
		sendMessage("get("+Settings.trainId+",func[0],func[1],func[2],func[3]," +
				"func[4],func[5],func[6],func[7])");
	}
	
	public void getTrainButtonStateExtra() {
		sendMessage("get("+Settings.trainId+",func[8],func[9],func[10],func[11]," +
				"func[12],func[13],func[14],func[15])");
	}
	
	public void setButton(int i, boolean enabled) {
		int value = (enabled) ? 1 : 0;
		sendMessage("set("+Settings.trainId+", func["+i+", "+value+"])");
	}
	
	public void emergencyStop(boolean state) {
		if(state) {
			sendMessage("set(1, stop)");
		}
		else {
			sendMessage("set(1, go)");
		}
	}
	
	public void setSpeed(int speed) {
		sendMessage("set("+Settings.trainId+", speed["+speed+"])");
	}
	
	public void setDir(int dir) {
		sendMessage("set("+Settings.trainId+", dir["+dir+"])");
	}
	
	public void takeControl() {
		sendMessage("request("+Settings.trainId+", control, force)");
	}
	
	public void releaseControl() {
		sendMessage("release("+Settings.trainId+", control)");
	}

	public void takeViewTrain() {
		sendMessage("request("+Settings.trainId+", view)");
	}
	
	public void releaseViewTrain() {
		sendMessage("release("+Settings.trainId+", view)");
	}
	
	public void viewConsole() {
		sendMessage("request(1, view)");
	}
	
	public void setName(String name) {
		sendMessage("set("+Settings.trainId+", name["+name+"]");
	}
}

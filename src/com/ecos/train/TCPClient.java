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
				mMessageListener.messageReceived("DISCONNECT");
			} finally {
				//the socket must be closed. It is not possible to reconnect to this socket
				// after it is closed, which means a new socket instance has to be created.
				socket.close();
			}

		} catch (Exception e) {
			mMessageListener.messageReceived("DISCONNECT");
		}

	}

	//Declare the interface. The method messageReceived(String message) 
	//will must be implemented in the MyActivity
	//class at on asynckTask doInBackground
	public interface OnMessageReceived {
		public void messageReceived(String message);
	}
	
	
	/**************************************************************************/
	/** Command Console **/
	/**************************************************************************/
	
	public void getEmergencyState() {
		sendMessage("get(1, status)");
	}
	
	public void emergencyStop(boolean state) {
		if(state) {
			sendMessage("set(1, stop)");
		}
		else {
			sendMessage("set(1, go)");
		}
	}
	
	public void viewConsole() {
		sendMessage("request(1, view)");
	}
	
	public void getInfo() {
		sendMessage("get(1, info)");
	}
	
	/**************************************************************************/
	/** Command Train Manager **/
	/**************************************************************************/
	
	public void getAllTrains() {
		sendMessage("queryObjects(10, name, addr)");
	}
	
	/**************************************************************************/
	/** Command Train **/
	/**************************************************************************/
	
	public void getTrainMainState() {
		sendMessage("get("+Settings.currentTrain.getId()+",name,speed,dir)");
	}
	
	public void getTrainButtonState() {
		sendMessage("get("+Settings.currentTrain.getId()+",func[0],func[1],func[2],func[3]," +
				"func[4],func[5],func[6],func[7])");
	}
	
	public void getTrainButtonStateF8F15() {
		sendMessage("get("+Settings.currentTrain.getId()+",func[8],func[9],func[10],func[11]," +
				"func[12],func[13],func[14],func[15])");
	}
	
	public void getTrainButtonStateF16F23() {
		sendMessage("get("+Settings.currentTrain.getId()+",func[16],func[17],func[18],func[19]," +
				"func[20],func[21],func[22],func[23])");
	}
	
	public void setButton(int i, boolean enabled) {
		int value = (enabled) ? 1 : 0;
		sendMessage("set("+Settings.currentTrain.getId()+", func["+i+", "+value+"])");
	}
	
	public void setSpeed(int speed) {
		sendMessage("set("+Settings.currentTrain.getId()+", speed["+speed+"])");
	}
	
	public void setDir(int dir) {
		sendMessage("set("+Settings.currentTrain.getId()+", dir["+dir+"])");
	}
	
	public void takeControl() {
		sendMessage("request("+Settings.currentTrain.getId()+", control, force)");
	}
	
	public void releaseControl() {
		sendMessage("release("+Settings.currentTrain.getId()+", control)");
	}

	public void takeViewTrain() {
		sendMessage("request("+Settings.currentTrain.getId()+", view)");
	}
	
	public void releaseViewTrain() {
		sendMessage("release("+Settings.currentTrain.getId()+", view)");
	}
	
	public void setName(String name) {
		sendMessage("set("+Settings.currentTrain.getId()+", name[\""+name+"\"])");
	}
	
	public void getButtonName() {
		sendMessage("get("+Settings.currentTrain.getId()+", funcexists[0], " +
				"funcexists[1], funcexists[2], funcexists[3], funcexists[4], funcexists[5], funcexists[6], funcexists[7])");
	}
	
	public void getButtonNameF8F15() {
		sendMessage("get("+Settings.currentTrain.getId()+", funcexists[8], " +
				"funcexists[9], funcexists[10], funcexists[11], funcexists[12], funcexists[13], funcexists[14], funcexists[15])");
	}
	
	public void getButtonNameF16F23() {
		sendMessage("get("+Settings.currentTrain.getId()+", funcexists[16], " +
				"funcexists[17], funcexists[18], funcexists[19], funcexists[20], funcexists[21], funcexists[22], funcexists[23])");
	}
	
	/**************************************************************************/
	/** Command Switching Objects **/
	/**************************************************************************/
	
	public void getAllObject() {
		sendMessage("queryObjects(11, name1, name2, addrext)");
	}
	
	public void takeViewObject() {
		sendMessage("request(11, view)");
	}
	
	public void releaseViewObject() {
		sendMessage("release(11, view)");
	}
	
	public void getState(int id) {
		sendMessage("request("+id+",view)");
		sendMessage("get("+id+", state)");
	}
	
	public void changeState(int id, int val) {
		sendMessage("request("+id+",control)");
		sendMessage("set("+id+", state["+val+"])");
		sendMessage("release("+id+",control)");
	}
}

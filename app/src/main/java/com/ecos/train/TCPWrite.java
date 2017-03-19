package com.ecos.train;

import android.util.Log;

import com.ecos.train.activity.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TCPWrite extends Thread {

	private boolean mRun = false;
	private Queue lstMessage;

	PrintWriter out;

	public TCPWrite() {
		lstMessage = new ConcurrentLinkedDeque();
	}

	public void setOut(PrintWriter pw) {
		out = pw;
		this.start();
	}

	/**
	 * Sends the message entered by client to the server
	 * @param message text entered by client
	 */
	public void sendMessage(String message) {
		lstMessage.offer(message);
	}

	public void sendTCP(String message){
		if (out != null && !out.checkError()) {
			Log.d("SEND", message);
			out.println(message);
			out.flush();
		}
	}

	public void stopClient(){
		mRun = false;
	}

	public void run() {
		mRun = true;
		while (mRun) {

			synchronized(lstMessage) {
				while(!lstMessage.isEmpty()) {
					String m = (String) lstMessage.poll();
					if (m != null) {
						sendTCP(m);
					}
				}
			}
		}
	}

	
	/**************************************************************************/
	/** Command Console **/
	/**************************************************************************/
	
	public void getConsoleState() {
		sendMessage("get(1, status, info)");
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
	
	/**************************************************************************/
	/** Command Train Manager **/
	/**************************************************************************/
	
	public void getAllTrains() {
		sendMessage("queryObjects(10, name, addr)");
	}
	
	/**************************************************************************/
	/** Command Train **/
	/**************************************************************************/
	
	public void getTrainMainState(int id) {
		sendMessage("get("+id+",name,speed,dir,speedindicator)");
	}
	
	public void getTrainSymbol(int id) {
		sendMessage("get("+id+",locodesc)");
	}
	
	public void getTrainButtonState(int id) {
		sendMessage("get("+id+",func[0],func[1],func[2],func[3]," +
				"func[4],func[5],func[6],func[7])");
	}
	
	public void getTrainButtonStateF8F15(int id) {
		sendMessage("get("+id+",func[8],func[9],func[10],func[11]," +
				"func[12],func[13],func[14],func[15])");
	}
	
	public void getTrainButtonStateF16F23(int id) {
		sendMessage("get("+id+",func[16],func[17],func[18],func[19]," +
				"func[20],func[21],func[22],func[23])");
	}
	
	public void setButton(int id, int i, boolean enabled) {
		int value = (enabled) ? 1 : 0;
		sendMessage("set("+id+", func["+i+", "+value+"])");
	}
	
	public void setSpeed(int id,int speed) {
		sendMessage("set("+id+", speed["+speed+"])");
	}
	
	public void setDir(int id,int dir) {
		sendMessage("set("+id+", dir["+dir+"])");
	}
	
	public void takeControl(int id) {
		sendMessage("request("+id+", control, force)");
	}
	
	public void releaseControl(int id) {
		sendMessage("release("+id+", control)");
	}

	public void takeViewTrain(int id) {
		sendMessage("request("+id+", view)");
	}
	
	public void releaseViewTrain(int id) {
		sendMessage("release("+id+", view)");
	}
	
	public void setName(int id, String name) {
		sendMessage("set("+id+", name[\""+name+"\"])");
	}
	
	public void getButtonName(int id) {
		sendMessage("get("+id+", funcexists[0], " +
				"funcexists[1], funcexists[2], funcexists[3], funcexists[4], funcexists[5], funcexists[6], funcexists[7])");
	}
	
	public void getButtonNameF8F15(int id) {
		sendMessage("get("+id+", funcexists[8], " +
				"funcexists[9], funcexists[10], funcexists[11], funcexists[12], funcexists[13], funcexists[14], funcexists[15])");
	}
	
	public void getButtonNameF16F23(int id) {
		sendMessage("get("+id+", funcexists[16], " +
				"funcexists[17], funcexists[18], funcexists[19], funcexists[20], funcexists[21], funcexists[22], funcexists[23])");
	}
	
	public void delete(int id) {
		sendMessage("delete("+id+")");
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
		sendMessage("get("+id+", state, symbol)");
	}
	
	public void changeState(int id, int val) {
		sendMessage("request("+id+",control)");
		sendMessage("set("+id+", state["+val+"])");
		sendMessage("release("+id+",control)");
	}
}

/*******************************************************************************
 * Copyright (c) 2011 LSIIT - Université de Strasbourg
 * Copyright (c) 2011 Erkan VALENTIN <erkan.valentin[at]unistra.fr>
 * http://www.senslab.info/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.ecos.train.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ecos.train.R;
import com.ecos.train.Settings;
import com.ecos.train.TCPClient;
import com.ecos.train.object.Train;
import com.ecos.train.ui.SpinAdapter;

public class TrainManagerActivity 
extends SherlockActivity 
implements OnClickListener, OnSeekBarChangeListener, OnCheckedChangeListener, OnItemSelectedListener {

	SharedPreferences pref;
	private TCPClient mTcpClient = null;

	TextView tvState;
	ToggleButton btnConnect;
	CheckBox cbReverse;
	Spinner sTrainId;
	ToggleButton btnControl;
	ToggleButton btnEmergency;
	SeekBar sbSpeed;
	TextView tvSpeed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//get elements.
		setContentView(R.layout.main);
		btnConnect = (ToggleButton) findViewById(R.id.btnConnect);
		cbReverse = (CheckBox) findViewById(R.id.cbReverse);
		sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
		sTrainId = (Spinner) findViewById(R.id.sTrainId);
		btnControl = (ToggleButton) findViewById(R.id.tbControl);
		tvState = (TextView) findViewById(R.id.tvState);
		btnEmergency = (ToggleButton) findViewById(R.id.btnEmergency);
		tvSpeed = (TextView) findViewById(R.id.tvSpeed);

		//init buttons
		setStateButtons(false);
		setStateEmergency(false);
		setStateList(false);
		setStateControl(false);

		//add listeners
		btnConnect.setOnClickListener(this);
		cbReverse.setOnCheckedChangeListener(this);
		sbSpeed.setOnSeekBarChangeListener(this);
		sTrainId.setOnItemSelectedListener(this);
		btnControl.setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF0)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF1)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF2)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF3)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF4)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF5)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF6)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF7)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF8)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF9)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF10)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF11)).setOnClickListener(this);

		((ToggleButton) findViewById(R.id.btnEmergency)).setOnClickListener(this);

		((TextView) findViewById(R.id.tvMore)).setOnClickListener(this);


		pref = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public void onClick(View v) {

		if(v.getId() == R.id.btnF0) {	//light
			mTcpClient.setButton(0, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF1) {
			mTcpClient.setButton(1, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF2) {
			mTcpClient.setButton(2, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF3) {
			mTcpClient.setButton(3, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF4) {
			mTcpClient.setButton(4, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF5) {
			mTcpClient.setButton(5, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF6) {
			mTcpClient.setButton(6, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF7) {
			mTcpClient.setButton(7, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF8) {
			mTcpClient.setButton(7, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF9) {
			mTcpClient.setButton(7, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF10) {
			mTcpClient.setButton(7, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF11) {
			mTcpClient.setButton(7, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnEmergency) {
			mTcpClient.emergencyStop(((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnConnect) {
			connectToStation(((ToggleButton) v).isChecked());
		} 
		else if(v.getId() == R.id.tbControl) {
			if(((ToggleButton) v).isChecked()) {
				mTcpClient.takeControl();
				setStateButtons(true);
			}
			else {
				mTcpClient.releaseControl();
				setStateButtons(false);
			}
		}
		else if(v.getId() == R.id.tvMore) {
			LinearLayout l = (LinearLayout) findViewById(R.id.llButtonsExtras);
			if(l.getVisibility() == LinearLayout.VISIBLE) {
				l.setVisibility(LinearLayout.GONE);
			}
			else {
				l.setVisibility(LinearLayout.VISIBLE);
			}
		}
	}


	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) { 
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {    
	}

	@Override
	public void onStopTrackingTouch(SeekBar sb) {
		mTcpClient.setSpeed(sb.getProgress());
		tvSpeed.setText(this.getString(R.string.tv_speed) + " " + sb.getProgress());
	}

	@Override
	public void onCheckedChanged(CompoundButton cb, boolean state) {
		if(cb.getId() == R.id.cbReverse) {
			mTcpClient.setDir(((CheckBox) cb).isChecked()?1:0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}   

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.iSettings:
			Intent i = new Intent(this, PreferencesActivity.class);
			startActivity(i);
			return true;
		case R.id.iAbout:
			try {
				PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
				Toast toast = Toast.makeText(
						this, this.getString(R.string.app_name) + " " + manager.versionName , Toast.LENGTH_SHORT);
				toast.show();
			} catch (Exception e) {
				//
			}

			return true;
			/*case R.id.iReport:
			ACRA.getErrorReporter().handleException(null);
			return true;*/
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	/**
	 * 
	 * @param isEnabled
	 */
	private void setFnButtons(boolean isEnabled) {
		((ToggleButton) findViewById(R.id.btnF0)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF1)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF2)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF3)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF4)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF5)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF6)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF7)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF8)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF9)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF10)).setEnabled(isEnabled);
		((ToggleButton) findViewById(R.id.btnF11)).setEnabled(isEnabled);

		if(!checkSig(this)) {
			((ToggleButton) findViewById(R.id.btnF8)).setEnabled(false);
			((ToggleButton) findViewById(R.id.btnF9)).setEnabled(false);
			((ToggleButton) findViewById(R.id.btnF10)).setEnabled(false);
			((ToggleButton) findViewById(R.id.btnF11)).setEnabled(false);
		}

	}

	public void setStateButtons(boolean state) {
		sbSpeed.setEnabled(state);
		cbReverse.setEnabled(state);
		sbSpeed.setEnabled(state);
		setFnButtons(state);
	}

	public void setStateControl(boolean state) {
		btnControl.setEnabled(state);
	}

	public void setStateEmergency(boolean state) {

		btnEmergency.setEnabled(state);
	}

	public void setStateList(boolean state) {
		sTrainId.setEnabled(state);
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			mTcpClient.stopClient();
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 * @param error
	 */
	public void displayError(String error) {
		Toast.makeText(this, error , Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

		setStateButtons(false);

		//release old train
		if(Settings.trainId != -1) {
			mTcpClient.releaseViewTrain();
			mTcpClient.releaseControl();
		}

		//get train and take control
		String value = ((Train)parent.getItemAtPosition(pos)).getId();
		Settings.trainId = Integer.parseInt(value);

		mTcpClient.takeControl();
		btnControl.setChecked(true);
		mTcpClient.takeViewTrain();
		Settings.state = Settings.State.GET_TRAIN_MAIN_STATE;
		mTcpClient.getTrainMainState();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}



	/**
	 * 
	 * AsyncTask for TCPClient creation
	 *
	 */
	public class connectTask extends AsyncTask<String,String,TCPClient> {

		@Override
		protected TCPClient doInBackground(String... message) {

			//we create a TCPClient object and
			mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
				@Override
				//here the messageReceived method is implemented
				public void messageReceived(String message) {
					//this method calls the onProgressUpdate
					publishProgress(message);
				}
			});

			mTcpClient.run();

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			Log.d("ECOS","state: " + Settings.state);
			Log.d("ECOS","onProgressUpdate: " + values[0]);

			String respLine[] = values[0].split("\n");

			//state machine
			if(Settings.state == Settings.State.NONE) {
				if(values[0].equals("READY")) {
					setStateList(true);
					setStateEmergency(true);
					mTcpClient.viewConsole();

					tvState.setText("Connected");
					btnConnect.setChecked(true);

					Settings.state = Settings.State.INIT_GET_EMERGENCY;
					mTcpClient.getEmergencyState();
				}
				else {
					tvState.setText(getApplicationContext().getString(R.string.tv_state) + " " + values[0]);
					btnConnect.setChecked(false);
				}
			}
			else if(Settings.state == Settings.State.INIT_GET_EMERGENCY) {
				if(respLine[0].equals("<REPLY get(1, status)>")) {
					Settings.state = Settings.State.INIT_GET_TRAINS;
					boolean isEmergency = getEmergencyState(values[0]);
					btnEmergency.setChecked(isEmergency);	
					mTcpClient.getAllTrains();
				}
			}
			else if(Settings.state == Settings.State.INIT_GET_TRAINS) {
				if(respLine[0].equals("<REPLY queryObjects(10, name, addr)>")) {
					Settings.state = Settings.State.GET_TRAIN_MAIN_STATE;
					SpinAdapter dataAdapter = new SpinAdapter(getApplicationContext(),
							android.R.layout.simple_spinner_item, getAllTrains(values[0]));
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sTrainId.setAdapter(dataAdapter);
				}
			}
			else if(Settings.state == Settings.State.GET_TRAIN_MAIN_STATE) {
				if(respLine[0].equals("<REPLY get("+Settings.trainId+",name,speed,dir)>")) {
					Settings.state = Settings.State.GET_TRAIN_BUTTON_STATE;
					getTrainMainState(values[0]);
					mTcpClient.getTrainButtonState();
				}
			}
			else if(Settings.state == Settings.State.GET_TRAIN_BUTTON_STATE) {
				if(respLine[0].equals("<REPLY get("+Settings.trainId+",func[0],func[1],func[2],func[3]," +
						"func[4],func[5],func[6],func[7])>")) {
					Settings.state = Settings.State.GET_TRAIN_BUTTON_STATE_EXTRA;
					getTrainButtonState(values[0]);
					mTcpClient.getTrainButtonStateExtra();
				}
			}
			else if(Settings.state == Settings.State.GET_TRAIN_BUTTON_STATE_EXTRA) {
				if(respLine[0].equals("<REPLY get("+Settings.trainId+",func[8],func[9],func[10],func[11])>")) {
					Settings.state = Settings.State.IDLE;
					getTrainButtonStateExtra(values[0]);
				}
			}
			else if(Settings.state == Settings.State.IDLE) {
				if(values[0].startsWith("<EVENT")) {	//manage event

					String list[] = values[0].split("\n");

					parseEventSpeed(list);
					parseEventButtons(list);
					parseEventDir(list);
					parseEventEmergency(list);
					parseEventLostControl(list);
				}
			}
		}
	}

	public void connectToStation(boolean state) {
		//connect
		if(state) {
			Settings.consoleIp = pref.getString("ip", "");

			try{
				Settings.consolePort = Integer.parseInt(
						pref.getString("port", Settings.CONSOLE_PORT+""));
			}
			catch(Exception e) {
				Settings.consolePort = Settings.CONSOLE_PORT;
			}

			//connect, begin state machine
			Settings.state = Settings.State.NONE;
			new connectTask().execute("");

		}
		else {	//disconnect
			mTcpClient.stopClient();
			tvState.setText("Not connected");
			setStateButtons(false);
			setStateControl(false);
			setStateEmergency(false);
			setStateList(false);
		}
	}

	/**
	 * 
	 * @param result
	 * @return
	 */
	public boolean getEmergencyState(String result) {

		int index1 = result.lastIndexOf('[');
		int index2 = result.lastIndexOf(']');

		try {
			return result.substring(index1+1, index2).toUpperCase().equals("STOP");
		}
		catch(Exception s) {
			return false;
		}
	}


	/**
	 * 
	 * @param result
	 * @return
	 */
	public List<Train> getAllTrains(String result) {

		List<Train> trainId = new ArrayList<Train>();

		String list[] = result.split("\n");
		Pattern p = Pattern.compile("(.*) name\\[\"(.*)\"\\] addr\\[(.*)\\]");
		String id = "";
		String addr = "";
		String name = "";

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);

			while (m.find() == true) {
				id = m.group(1).trim();
				name = m.group(2).trim();
				addr = m.group(3).trim();
			}

			trainId.add(new Train(id, name, addr));
		}
		return trainId;
	}

	/**
	 * 
	 * @param result
	 */
	public void getTrainMainState(String result) {
		String list[] = result.split("\n");

		//speed
		int index1 = list[2].lastIndexOf('[');
		int index2 = list[2].lastIndexOf(']');

		int speed = 0;
		try {
			speed = Integer.parseInt(list[2].substring(index1+1, index2));
		}
		catch(Exception s) {

		}
		sbSpeed.setProgress(speed);
		tvSpeed.setText(this.getString(R.string.tv_speed) + speed);


		//direction
		index1 = list[3].lastIndexOf('[');
		index2 = list[3].lastIndexOf(']');

		boolean dir = false;
		try {
			dir = Integer.parseInt(list[3].substring(index1+1, index2)) == 0
					? true : false;
		}
		catch(Exception s) {
		}

		cbReverse.setChecked(!dir);
	}


	/**
	 * 
	 * @param result
	 */
	public void getTrainButtonState(String result) {

		String list[] = result.split("\n");

		List<Boolean> state = new ArrayList<Boolean>();

		for(int i=1;i<9;i++) {
			int index1 = list[i].lastIndexOf(' ');
			int index2 = list[i].lastIndexOf(']');
			boolean s = false;
			try {
				s = Integer.parseInt(list[i].substring(index1+1, index2)) == 1
						? true : false;
			}
			catch(Exception e) {
			}
			state.add(s);
		}

		((ToggleButton) findViewById(R.id.btnF0)).setChecked(state.get(0));
		((ToggleButton) findViewById(R.id.btnF1)).setChecked(state.get(1));
		((ToggleButton) findViewById(R.id.btnF2)).setChecked(state.get(2));
		((ToggleButton) findViewById(R.id.btnF3)).setChecked(state.get(3));
		((ToggleButton) findViewById(R.id.btnF4)).setChecked(state.get(4));
		((ToggleButton) findViewById(R.id.btnF5)).setChecked(state.get(5));
		((ToggleButton) findViewById(R.id.btnF6)).setChecked(state.get(6));
		((ToggleButton) findViewById(R.id.btnF7)).setChecked(state.get(7));
	}

	public void getTrainButtonStateExtra(String result) {
		String list[] = result.split("\n");

		List<Boolean> state = new ArrayList<Boolean>();

		for(int i=1;i<5;i++) {
			int index1 = list[i].lastIndexOf(' ');
			int index2 = list[i].lastIndexOf(']');
			boolean s = false;
			try {
				s = Integer.parseInt(list[i].substring(index1+1, index2)) == 1
						? true : false;
			}
			catch(Exception e) {
			}
			state.add(s);
		}

		((ToggleButton) findViewById(R.id.btnF8)).setChecked(state.get(0));
		((ToggleButton) findViewById(R.id.btnF9)).setChecked(state.get(1));
		((ToggleButton) findViewById(R.id.btnF10)).setChecked(state.get(2));
		((ToggleButton) findViewById(R.id.btnF11)).setChecked(state.get(3));

		//activate button
		setStateButtons(true);
		setStateList(true);
		setStateControl(true);
	}


	public void parseEventSpeed(String[] list) {
		Pattern p = Pattern.compile("(.*) speed\\[(.*)\\]");
		String id = "";
		int iid = 0;

		String speed = "";

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);

			while (m.find() == true) {
				id = m.group(1).trim();
				try {
					iid = Integer.parseInt(id);
				}
				catch(Exception e) {	
				}
				speed = m.group(2).trim();

				if(iid == Settings.trainId) {
					int ispeed = Integer.parseInt(speed);
					sbSpeed.setProgress(ispeed);
					tvSpeed.setText(getApplicationContext().getString(R.string.tv_speed) + speed);
				}
			}
		}
	}

	public void parseEventButtons(String[] list) {
		Pattern p = Pattern.compile("(.*) func\\[(.*),(.*)\\]");
		String id = "";
		int iid = 0;

		String btn = "";
		int ibtn = -1;
		String state = "";
		boolean istate = false;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);

			while (m.find() == true) {
				id = m.group(1).trim();
				btn = m.group(2).trim();
				state = m.group(3).trim();
				try {
					iid = Integer.parseInt(id);
				}
				catch(Exception e) {		
				}

				if(iid == Settings.trainId) {
					ibtn = -1;
					try {
						ibtn = Integer.parseInt(btn);
					}
					catch(Exception e) {	
					}

					istate = false;
					if(state.equals("1")) {
						istate = true;
					}

					if(ibtn == 0) {
						((ToggleButton) findViewById(R.id.btnF0)).setChecked(istate);
					}
					else if(ibtn == 1) {
						((ToggleButton) findViewById(R.id.btnF1)).setChecked(istate);
					}
					else if(ibtn == 2) {
						((ToggleButton) findViewById(R.id.btnF2)).setChecked(istate);
					}
					else if(ibtn == 3) {
						((ToggleButton) findViewById(R.id.btnF3)).setChecked(istate);
					}
					else if(ibtn == 4) {
						((ToggleButton) findViewById(R.id.btnF4)).setChecked(istate);
					}
					else if(ibtn == 5) {
						((ToggleButton) findViewById(R.id.btnF5)).setChecked(istate);
					}
					else if(ibtn == 6) {
						((ToggleButton) findViewById(R.id.btnF6)).setChecked(istate);
					}
					else if(ibtn == 7) {
						((ToggleButton) findViewById(R.id.btnF7)).setChecked(istate);
					}
					else if(ibtn == 8) {
						((ToggleButton) findViewById(R.id.btnF8)).setChecked(istate);
					}
					else if(ibtn == 9) {
						((ToggleButton) findViewById(R.id.btnF9)).setChecked(istate);
					}
					else if(ibtn == 10) {
						((ToggleButton) findViewById(R.id.btnF10)).setChecked(istate);
					}
					else if(ibtn == 11) {
						((ToggleButton) findViewById(R.id.btnF11)).setChecked(istate);
					}
				}
			}
		}
	}

	public void parseEventDir(String[] list) {
		Pattern p = Pattern.compile("(.*) dir\\[(.*)\\]");
		String id = "";
		int iid = 0;

		String dir = "";
		boolean idir = true;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);

			while (m.find() == true) {
				id = m.group(1).trim();
				try {
					iid = Integer.parseInt(id);
				}
				catch(Exception e) {	
				}
				dir = m.group(2).trim();

				if(iid == Settings.trainId) {

					try {
						idir = Integer.parseInt(dir) == 0 ? true : false;
					}
					catch(Exception s) {
					}

					cbReverse.setChecked(!idir);
				}
			}
		}
	}

	public void parseEventEmergency(String[] list) {
		Pattern p = Pattern.compile("(.*) status\\[(.*)\\]");
		String state = "";

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);
			while (m.find() == true) {
				state = m.group(2).trim();

				if(state.equals("GO")) {
					btnEmergency.setChecked(false);
					//setStateButtons(true);
					//setStateControl(true);
				}
				else {
					btnEmergency.setChecked(true);
					//setStateButtons(false);
					//setStateControl(false);
				}
			}
		}
	}

	public void parseEventLostControl(String[] list) {
		Pattern p = Pattern.compile("(.*) msg\\[(.*)\\]");
		String event = "";

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);
			while (m.find() == true) {
				event = m.group(2).trim();

				if(event.equals("CONTROL_LOST")) {
					btnControl.setChecked(false);
					setStateButtons(false);
				}
			}
		}
	}

	static boolean checkSig(Context context) {
		boolean match = false;
		if (context.getPackageManager().checkSignatures("com.ecos.train",
				"com.ecos.train.plus") == PackageManager.SIGNATURE_MATCH)
			match = true;
		return match;
	}

}
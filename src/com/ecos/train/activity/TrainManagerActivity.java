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

import org.acra.ACRA;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ecos.train.R;
import com.ecos.train.Settings;
import com.ecos.train.TCPClient;
import com.ecos.train.object.Train;
import com.ecos.train.ui.SpinAdapter;

public class TrainManagerActivity 
extends Activity 
implements OnClickListener, OnSeekBarChangeListener, OnCheckedChangeListener, OnItemSelectedListener {

	private static int DEFAULT_PORT = 15471;
	private TCPClient mTcpClient = null;
	TextView tvState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//get elements
		setContentView(R.layout.main);
		ToggleButton btnControl = (ToggleButton) findViewById(R.id.btnConnect);
		CheckBox cbReverse = (CheckBox) findViewById(R.id.cbReverse);
		SeekBar sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);

		tvState = (TextView) findViewById(R.id.tvState);

		setStateButtons(false);

		//add listeners
		btnControl.setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF0)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF1)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF2)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF3)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF4)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF5)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF6)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnF7)).setOnClickListener(this);
		((ToggleButton) findViewById(R.id.btnEmergency)).setOnClickListener(this);
		cbReverse.setOnCheckedChangeListener(this);
		sbSpeed.setOnSeekBarChangeListener(this);

		Spinner sTrainId = (Spinner) findViewById(R.id.sTrainId);
		sTrainId.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

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
		else if(v.getId() == R.id.btnEmergency) {
			mTcpClient.emergencyStop(((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnConnect) { //click on connect


			//connect
			if(((ToggleButton) v).isChecked()) {

				Settings.consoleIp = pref.getString("ip", "");

				Settings.consolePort = TrainManagerActivity.DEFAULT_PORT;
				try{
					Settings.consolePort = Integer.parseInt(
							pref.getString("port", TrainManagerActivity.DEFAULT_PORT+""));
				}
				catch(Exception e) {
					Settings.consolePort = TrainManagerActivity.DEFAULT_PORT;
				}

				//connect, begin state machine
				new connectTask().execute("");

			}
			else {	//disconnect

				//TODO: disconnect

				setStateButtons(false);
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
		((TextView) findViewById(R.id.tvSpeed)).setText(this.getString(R.string.tv_speed) + " " + sb.getProgress());
	}

	@Override
	public void onCheckedChanged(CompoundButton cb, boolean state) {
		if(cb.getId() == R.id.cbReverse) {
			mTcpClient.setDir(((CheckBox) cb).isChecked()?1:0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
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
		case R.id.iReport:
			ACRA.getErrorReporter().handleException(null);
			return true;
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

		if(Settings.trainId != -1) {
			mTcpClient.releaseViewTrain();
			mTcpClient.releaseControl();
		}

		String value = ((Train)parent.getItemAtPosition(pos)).getId();
		Settings.trainId = Integer.parseInt(value);

		mTcpClient.takeControl();
		mTcpClient.takeViewTrain();

		Settings.state = Settings.State.GET_TRAIN_MAIN_STATE;
		mTcpClient.getTrainMainState();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	public void setStateButtons(boolean state) {
		//get elements

		TextView tvState = (TextView) findViewById(R.id.tvState);
		CheckBox cbReverse = (CheckBox) findViewById(R.id.cbReverse);
		TextView tvSpeed = (TextView) findViewById(R.id.tvSpeed);
		SeekBar sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
		ToggleButton btnEmergency = (ToggleButton) findViewById(R.id.btnEmergency);
		Spinner sTrainId = (Spinner) findViewById(R.id.sTrainId);

		//default values
		if(!state) {
			tvSpeed.setText(this.getString(R.string.tv_speed) + " 0");
			tvState.setText(this.getString(R.string.tv_state) + " " + this.getString(R.string.tv_disconnect));
		}

		sbSpeed.setEnabled(state);
		setFnButtons(state);
		cbReverse.setEnabled(state);
		sbSpeed.setEnabled(state);
		btnEmergency.setEnabled(state);
		sTrainId.setEnabled(state);

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
					//mTcpClient.viewConsole();

					tvState.setText("Connected");
					((ToggleButton) findViewById(R.id.btnConnect)).setChecked(true);

					Settings.state = Settings.State.INIT_GET_EMERGENCY;
					mTcpClient.getEmergencyState();
				}
				else {
					tvState.setText(getApplicationContext().getString(R.string.tv_state) + " " + values[0]);
					((ToggleButton) findViewById(R.id.btnConnect)).setChecked(false);
				}
			}
			else if(Settings.state == Settings.State.INIT_GET_EMERGENCY) {
				if(respLine[0].equals("<REPLY get(1, status)>")) {
					Settings.state = Settings.State.INIT_GET_TRAINS;
					boolean isEmergency = getEmergencyState(values[0]);
					ToggleButton btnEmergency = (ToggleButton) findViewById(R.id.btnEmergency);
					btnEmergency.setChecked(isEmergency);	
					mTcpClient.getAllTrains();
				}
			}
			else if(Settings.state == Settings.State.INIT_GET_TRAINS) {
				if(respLine[0].equals("<REPLY queryObjects(10, name, addr)>")) {
					Settings.state = Settings.State.GET_TRAIN_MAIN_STATE;
					Spinner sTrainId = (Spinner) findViewById(R.id.sTrainId);
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
					Settings.state = Settings.State.IDLE;
					getTrainButtonState(values[0]);
				}
			}
			else if(Settings.state == Settings.State.IDLE) {
				if(values[0].startsWith("<EVENT")) {	//manage event

					String list[] = values[0].split("\n");

					//check speed event
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
								TextView tvSpeed = (TextView) findViewById(R.id.tvSpeed);
								SeekBar sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
								int ispeed = Integer.parseInt(speed);
								sbSpeed.setProgress(ispeed);
								tvSpeed.setText(getApplicationContext().getString(R.string.tv_speed) + speed);
							}
						}
					}

					//check button event
					p = Pattern.compile("(.*) func\\[(.*),(.*)\\]");
					String btn = "";
					String state = "";

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
								int ibtn = -1;
								try {
									ibtn = Integer.parseInt(btn);
								}
								catch(Exception e) {	
								}

								boolean istate = false;
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
							}
						}

					}
				}
			}
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
		TextView tvSpeed = (TextView) findViewById(R.id.tvSpeed);
		SeekBar sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
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

		CheckBox cbReverse = (CheckBox) findViewById(R.id.cbReverse);
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

		//activate button
		setStateButtons(true);
	}

}
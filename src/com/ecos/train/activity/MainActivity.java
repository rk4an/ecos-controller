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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ecos.train.R;
import com.ecos.train.Settings;
import com.ecos.train.TCPClient;
import com.ecos.train.object.SwitchSymbol;
import com.ecos.train.object.FunctionSymbol;
import com.ecos.train.object.Train;
import com.ecos.train.ui.TrainSpinAdapter;

public class MainActivity 
extends ActionBarActivity 
implements OnClickListener, OnSeekBarChangeListener, OnItemSelectedListener, OnTouchListener {

	public static final String LITE_PACKAGE = "com.ecos.train";  
	public static final String FULL_PACKAGE = "com.ecos.train.unlock";
	public static final String CONTACT = "erkan2005+ecos@gmail.com";

	SharedPreferences pref = null;
	private TCPClient mTcpClient = null;

	TextView tvState = null;
	ToggleButton btnConnect = null;
	ToggleButton cbReverse = null;
	Spinner sTrainId = null;
	ToggleButton btnControl = null;
	ToggleButton btnEmergency = null;
	SeekBar sbSpeed = null;
	TextView tvSpeed = null;
	LinearLayout llSwitch = null;
	LinearLayout llTrain = null;

	TrainSpinAdapter dataAdapter;
	private MenuItem editItem = null;
	private MenuItem infoItem = null;

	Dialog infoDialog = null;
	TextView protocolVersion = null;
	TextView applicationVersion = null;
	TextView hardwareVersion = null;
	TextView ecosVersion = null;

	List<ToggleButton> listSwitch = new ArrayList<ToggleButton>();
	List<SeekBar> listSwitchMulti = new ArrayList<SeekBar>();
	List<TextView> listSwitchMultiValue = new ArrayList<TextView>();
	List<TextView> listSwitchMultiLabel = new ArrayList<TextView>();
	List<ToggleButton> listButtons = new ArrayList<ToggleButton>();

	private static final int SETTINGS = 0;
	private int speedStep = Settings.SPEED_STEP;

	private boolean symbolLoaded = false;
	private boolean switchLoaded = false;

	/**************************************************************************/
	/** Listeners **/
	/**************************************************************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//get elements
		setContentView(R.layout.main);
		btnConnect = (ToggleButton) findViewById(R.id.btnConnect);
		cbReverse = (ToggleButton) findViewById(R.id.cbReverse);
		sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
		sTrainId = (Spinner) findViewById(R.id.sTrainId);
		btnControl = (ToggleButton) findViewById(R.id.tbControl);
		tvState = (TextView) findViewById(R.id.tvState);
		btnEmergency = (ToggleButton) findViewById(R.id.btnEmergency);
		tvSpeed = (TextView) findViewById(R.id.tvSpeed);

		//add listeners
		btnConnect.setOnClickListener(this);
		cbReverse.setOnClickListener(this);
		sbSpeed.setOnSeekBarChangeListener(this);
		sTrainId.setOnItemSelectedListener(this);
		btnControl.setOnClickListener(this);

		//get the function buttons
		Resources res = getResources();
		for(int i=0; i<Settings.FUNCTION_BUTTONS; i++) {
			int resourceId = res.getIdentifier("btnF"+i, "id", getPackageName());
			listButtons.add(((ToggleButton) findViewById(resourceId)));
		}

		initFunctionButtons();

		//init buttons
		setStateButtons(false);
		setStateEmergency(false);
		setStateList(false);
		setStateControl(false);

		((ToggleButton) findViewById(R.id.btnEmergency)).setOnClickListener(this);

		((TextView) findViewById(R.id.tvF8_F15)).setOnClickListener(this);
		((TextView) findViewById(R.id.tvF16_F23)).setOnClickListener(this);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		Settings.fullVersion = checkSig(this);

		//get speed step in save preference
		speedStep = Integer.parseInt(pref.getString("pref_speed", Settings.SPEED_STEP+""));

		//info dialog
		infoDialog = new Dialog(this);
		LayoutInflater inflater = getLayoutInflater();
		final View infoView = inflater.inflate(R.layout.info_dialog, null);
		infoDialog.setContentView(infoView);
		infoDialog.setTitle(getString(R.string.app_name));

		protocolVersion = ((TextView) infoView.findViewById(R.id.tvProtocolVersion));
		applicationVersion = ((TextView) infoView.findViewById(R.id.tvApplicationVersion));
		hardwareVersion = ((TextView) infoView.findViewById(R.id.tvHardwareVersion));
		ecosVersion = ((TextView) infoView.findViewById(R.id.tvEcosVersion));

		llSwitch = ((LinearLayout) findViewById(R.id.llSwitch));
		llSwitch.setVisibility(LinearLayout.GONE);

		llTrain = ((LinearLayout) findViewById(R.id.llTrain));
		llTrain.setVisibility(LinearLayout.VISIBLE);

		boolean lockRotation = pref.getBoolean("pref_lockrotation", false);
		if(lockRotation) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		displayArrow(R.id.tvF8_F15, "down");
		displayArrow(R.id.tvF16_F23, "down");
	}


	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();

		switch(view.getId()) {
		case R.id.rTrain:
			if (checked) {
				llTrain.setVisibility(LinearLayout.VISIBLE);
				llSwitch.setVisibility(LinearLayout.GONE);
			}
			break;
		case R.id.rSwitch:
			if (checked) {
				llSwitch.setVisibility(LinearLayout.VISIBLE);
				llTrain.setVisibility(LinearLayout.GONE);
				if(Settings.fullVersion && Settings.state == Settings.State.IDLE) {
					if(!switchLoaded) {
						tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.state_switching_objects));
						mTcpClient.getAllObject();
						switchLoaded = true;
					}
				}
			}
			break;
		}
	}


	@Override
	public void onClick(View v) {

		//click on a function buttons
		if(v.getTag(R.string.btn_name) != null) {

			if(v.getTag(R.string.btn_name).toString().startsWith("btn")) {
				String token[] = v.getTag(R.string.btn_name).toString().split(";");
				mTcpClient.setButton(Integer.parseInt(
						token[1]), ((ToggleButton) v).isChecked());
				return;
			}
		}

		//click on emergency button
		if(v.getId() == R.id.btnEmergency) {
			mTcpClient.emergencyStop(((ToggleButton) v).isChecked());
		}
		//click on connect button
		else if(v.getId() == R.id.btnConnect) {
			symbolLoaded = false;
			switchLoaded = false;
			connectToStation(((ToggleButton) v).isChecked());
		} 
		//click on control button
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
		//click on F8-F15 banner
		else if(v.getId() == R.id.tvF8_F15) {
			LinearLayout l = (LinearLayout) findViewById(R.id.llF8_F15);
			if(l.getVisibility() == LinearLayout.VISIBLE) {
				l.setVisibility(LinearLayout.GONE);
				displayArrow(R.id.tvF8_F15, "down");
			}
			else {
				l.setVisibility(LinearLayout.VISIBLE);
				displayArrow(R.id.tvF8_F15, "up");
				if(Settings.state == Settings.State.IDLE) {
					tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.state_f8_f15));
					mTcpClient.getTrainButtonStateF8F15();
				}
			}
		}
		//click on F16-F23 banner
		else if(v.getId() == R.id.tvF16_F23) {
			LinearLayout l = (LinearLayout) findViewById(R.id.llF16_F23);
			if(l.getVisibility() == LinearLayout.VISIBLE) {
				l.setVisibility(LinearLayout.GONE);
				displayArrow(R.id.tvF16_F23, "down");
			}
			else {
				l.setVisibility(LinearLayout.VISIBLE);
				displayArrow(R.id.tvF16_F23, "up");
				if(Settings.state == Settings.State.IDLE) {
					tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.state_f16_f23));
					mTcpClient.getTrainButtonStateF16F23();
				}
			}
		}
		//click on reverse button
		else if(v.getId() == R.id.cbReverse) {
			mTcpClient.setDir(((ToggleButton) v).isChecked()?1:0);
		}
		//click on switching objects button
		else {
			mTcpClient.changeState(Integer.parseInt(v.getTag().toString()), ((ToggleButton) v).isChecked()?1 :0);
		}
	}

	public void displayArrow(int label, String state) {
		TextView t = (TextView) findViewById(label);
		Resources res = getResources();
		int resourceId = res.getIdentifier(state, "drawable", getPackageName());
		Drawable img = res.getDrawable(resourceId);
		t.setCompoundDrawablesWithIntrinsicBounds(img, null , null, null);
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) { 
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {    
	}

	@Override
	public void onStopTrackingTouch(SeekBar sb) {
		//speed seekbar
		if(sb.getId() == R.id.sbSpeed) {
			mTcpClient.setSpeed(sb.getProgress());
			displaySpeed(sb.getProgress());
		}
		//switching object seekbar
		else {
			try {
				int id = Integer.parseInt(sb.getTag().toString());
				mTcpClient.changeState(id, sb.getProgress());

				for(TextView t: listSwitchMultiValue) {
					if(Integer.parseInt(t.getTag().toString()) == id) {
						t.setText(sb.getProgress()+"");
					}
				}
			}
			catch(Exception e) {}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		editItem = menu.getItem(2);
		infoItem = menu.getItem(3);

		return true;
	}   

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		if(Settings.fullVersion) {
			menu.getItem(1).setEnabled(false);
		}
		if(Settings.currentTrainIndex == -1) {
			menu.getItem(2).setEnabled(false);
			menu.getItem(3).setEnabled(false);
		}
		if(Settings.state == Settings.State.IDLE) {
			menu.getItem(3).setEnabled(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.iSettings:
			Intent i = new Intent(this, PreferencesActivity.class);
			startActivityForResult(i, MainActivity.SETTINGS);
			return true;
		case R.id.iPack:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="+FULL_PACKAGE));
			startActivity(intent);
			return true;
		case R.id.iEdit:
			//show alert dialog to edit train name
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			LayoutInflater inflater = getLayoutInflater();
			final View dialogView = inflater.inflate(R.layout.edit_form, null);
			final EditText edName = ((EditText)dialogView.findViewById(R.id.edName));
			final TextView edId = ((TextView)dialogView.findViewById(R.id.tv_id));
			final TextView edAddress = ((TextView)dialogView.findViewById(R.id.tv_address));
			edName.setText(Settings.getCurrentTrain().getName());
			edId.setText(Settings.getCurrentTrain().getId()+"");
			edAddress.setText(Settings.getCurrentTrain().getAddress());

			builder.setView( dialogView);
			builder.setTitle(getString(R.string.btn_edit))
			.setPositiveButton(R.string.tv_save, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					String name = edName.getText().toString();
					mTcpClient.setName(name);

					for (Train t : Settings.allTrains) {
						if(t.getId() == Settings.getCurrentTrain().getId()) {
							t.setName(name);
						}
					}
					dataAdapter.notifyDataSetChanged();
				}
			})
			.setNegativeButton(R.string.tv_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();

			return true;
		case R.id.iInfo:
			//show alert dialog to display console info
			infoDialog.show();

			try {
				PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
				ecosVersion.setText(manager.versionName);
			} catch (Exception e) { }

			return true;
		case R.id.iContact:
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
					"mailto", MainActivity.CONTACT, null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ECoS Controller Feedback");
			startActivity(Intent.createChooser(emailIntent, "Send email..."));

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MainActivity.SETTINGS) {

			//get speed step
			speedStep = Integer.parseInt(pref.getString("pref_speed", Settings.SPEED_STEP+""));

			//check if something change on ip address
			if(!Settings.consoleIp.equals(pref.getString("ip", ""))){
				disconnect();
				return;
			}

			//check if something change on sorting preference
			if(Settings.sortById != pref.getBoolean("pref_sort", false)) {
				Settings.sortById = pref.getBoolean("pref_sort", false);
				if(Settings.state != Settings.State.NONE) {
					sortTrainsList(Settings.sortById);
					dataAdapter.notifyDataSetChanged();
					//restore the latest selection
					for(int i=0; i<Settings.allTrains.size(); i++) {
						if(Settings.allTrains.get(i).getId() == Settings.getCurrentTrain().getId()) {
							sTrainId.setSelection(i);
							break;
						}
					}
				}
			}

			//check if locodesc enable
			boolean locodesc = pref.getBoolean("pref_locodesc", false);
			if(locodesc) {
				if(mTcpClient != null) {
					getTrainsSymbol();
				}
			}

			//check if lock screen rotation enable
			boolean lockRotation = pref.getBoolean("pref_lockrotation", false);
			if(lockRotation) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

		setStateButtons(false);
		hideExtraFunctionButtons();

		//release old train
		if(Settings.currentTrainIndex != -1) {
			mTcpClient.releaseViewTrain();
			mTcpClient.releaseControl();
		}

		//get train and take control
		Settings.currentTrainIndex = pos;

		mTcpClient.takeControl();
		btnControl.setChecked(true);
		mTcpClient.takeViewTrain();
		Settings.state = Settings.State.GET_TRAIN_MAIN_STATE;
		tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.state_train_state));
		mTcpClient.getTrainMainState();

		displayArrow(R.id.tvF8_F15, "down");
		displayArrow(R.id.tvF16_F23, "down");

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onStop();
		try {
			mTcpClient.stopClient();
		} catch (Exception e) {

		}
	}

	/**************************************************************************/
	/** Buttons management **/
	/**************************************************************************/	

	public void setFnButtons(boolean isEnabled) {

		for(ToggleButton t: listButtons) {
			t.setEnabled(isEnabled);
		}

		if(!Settings.fullVersion) {
			for(int i=8; i<listButtons.size(); i++) {
				listButtons.get(i).setEnabled(false);
			}
		}
	}

	public void setStateButtons(boolean state) {
		sbSpeed.setEnabled(state);
		cbReverse.setEnabled(state);
		sbSpeed.setEnabled(state);

		if(editItem != null) {
			editItem.setEnabled(state);

			if(!Settings.fullVersion) {
				editItem.setEnabled(false);
			}
		}

		setFnButtons(state);
	}

	public void hideExtraFunctionButtons() {
		((LinearLayout) findViewById(R.id.llF8_F15)).setVisibility(LinearLayout.GONE);
		((LinearLayout) findViewById(R.id.llF16_F23)).setVisibility(LinearLayout.GONE);
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

	/**************************************************************************/
	/** AsyncTask for UI change **/
	/**************************************************************************/
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

			//check command result before
			String cmd_result = respLine[respLine.length-1];
			if(cmd_result.equals("DISCONNECT")) {
				disconnect();
			}
			else if(cmd_result.equals("READY")) {
			}
			else {
				if(!cmd_result.equals("<END 0 (OK)>")) {
					Toast.makeText(getApplicationContext(), cmd_result, Toast.LENGTH_SHORT).show();
					return;
				}
			}

			//state machine
			if(Settings.state == Settings.State.NONE) {
				//check if connection ok
				if(values[0].equals("READY")) {
					setStateList(true);
					setStateEmergency(true);

					mTcpClient.viewConsole();

					tvState.setText(getApplicationContext().getString(R.string.tv_state) + " " + 
							getApplicationContext().getString(R.string.tv_connect));
					btnConnect.setChecked(true);

					Settings.state = Settings.State.INIT_GET_CONSOLE;
					tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.state_console_info));
					mTcpClient.getConsoleState();
				}
				else {
					tvState.setText(getApplicationContext().getString(R.string.tv_state) + " " + getString(R.string.tv_disconnect));
					btnConnect.setChecked(false);
				}
			}
			//emergency state response
			else if(Settings.state == Settings.State.INIT_GET_CONSOLE) {
				if(respLine[0].equals("<REPLY get(1, status, info)>")) {
					Settings.state = Settings.State.INIT_GET_TRAINS;
					parseEmergency(respLine);
					parseConsoleVersion(respLine);
					tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.state_train_list));
					mTcpClient.getAllTrains();
				}
			}
			//train list response
			else if(Settings.state == Settings.State.INIT_GET_TRAINS) {
				if(respLine[0].equals("<REPLY queryObjects(10, name, addr)>")) {
					Settings.state = Settings.State.GET_TRAIN_MAIN_STATE;
					Settings.allTrains = parseTrainsList(values[0]);

					Settings.sortById = pref.getBoolean("pref_sort", false);
					sortTrainsList(Settings.sortById);

					dataAdapter = new TrainSpinAdapter(getApplicationContext(),
							android.R.layout.simple_spinner_item, Settings.allTrains);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sTrainId.setAdapter(dataAdapter);
				}
			}
			//train state response
			else if(Settings.state == Settings.State.GET_TRAIN_MAIN_STATE) {
				if(respLine[0].equals("<REPLY get("+Settings.getCurrentTrain().getId()+",name,speed,dir,speedindicator)>")) {
					Settings.state = Settings.State.GET_TRAIN_BUTTON_STATE;
					parseTrainState(respLine);
					initFunctionButtons();
					tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.state_train_buttons));
					mTcpClient.getTrainButtonState();
				}
			}
			//train buttons state response
			else if(Settings.state == Settings.State.GET_TRAIN_BUTTON_STATE) {
				if(respLine[0].equals("<REPLY get("+Settings.getCurrentTrain().getId()+",func[0],func[1],func[2],func[3]," +
						"func[4],func[5],func[6],func[7])>")) {
					Settings.state = Settings.State.IDLE;

					parseButtons(respLine);
					setStateButtons(true);
					hideExtraFunctionButtons();
					setStateList(true);
					setStateControl(true);

					if(!Settings.protocolVersion.equals("0.1")) {
						mTcpClient.getButtonName();
					}
				}
			}
			else if(Settings.state == Settings.State.IDLE) {
				//manage event
				if(respLine[0].startsWith("<EVENT")) {	
					parseEvent(respLine);
				}
				//console info response
				else if(respLine[0].equals("<REPLY get(1, info)>")) {
					parseConsoleVersion(respLine);
				}
				//switching objects list response
				else if(respLine[0].equals("<REPLY queryObjects(11, name1, name2, addrext)>")) {
					parseSwitchList(respLine);
				}
				//train buttons name response 0-7
				else if(respLine[0].equals("<REPLY get("+Settings.getCurrentTrain().getId()+", funcexists[0], " +
						"funcexists[1], funcexists[2], funcexists[3], funcexists[4], funcexists[5], funcexists[6], funcexists[7])>")){
					parseButtonSymbol(respLine);

					boolean locodesc = pref.getBoolean("pref_locodesc", false);
					if(locodesc) {
						if(!symbolLoaded) {
							symbolLoaded = true;
							getTrainsSymbol();
						}
					}

				}
				//train buttons name response 8-15
				else if(respLine[0].equals("<REPLY get("+Settings.getCurrentTrain().getId()+", funcexists[8], " +
						"funcexists[9], funcexists[10], funcexists[11], funcexists[12], funcexists[13], funcexists[14], funcexists[15])>")){
					parseButtonSymbol(respLine);
				}
				//train buttons name response 16-23
				else if(respLine[0].equals("<REPLY get("+Settings.getCurrentTrain().getId()+", funcexists[16], " +
						"funcexists[17], funcexists[18], funcexists[19], funcexists[20], funcexists[21], funcexists[22], funcexists[23])>")){
					parseButtonSymbol(respLine);
				}
				//train buttons response 8-15
				else if(respLine[0].equals("<REPLY get("+Settings.getCurrentTrain().getId()+",func[8],func[9],func[10],func[11]," +
						"func[12],func[13],func[14],func[15])>")) {

					parseButtons(respLine);

					if(!Settings.protocolVersion.equals("0.1")) {
						mTcpClient.getButtonNameF8F15();
					}
				}
				//train buttons response 16-23
				else if(respLine[0].equals("<REPLY get("+Settings.getCurrentTrain().getId()+",func[16],func[17],func[18],func[19]," +
						"func[20],func[21],func[22],func[23])>")) {

					parseButtons(respLine);

					if(!Settings.protocolVersion.equals("0.1")) {
						mTcpClient.getButtonNameF16F23();
					}
				}
				//a switching object response
				else {
					parseTrainsSymbol(respLine);
					parseSwitch(respLine);
					parseSwitchSymbol(respLine);
				}

				if(Settings.state == Settings.State.IDLE) {
					tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.state_ready));
				}
			}
		}
	}

	/**************************************************************************/
	/** Connect/Disconnect to console **/
	/**************************************************************************/

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
		else {
			disconnect();
		}
	}

	public void disconnect() {
		if(mTcpClient != null) {
			mTcpClient.stopClient();
		}
		tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.tv_disconnect));
		setStateButtons(false);
		hideExtraFunctionButtons();
		setStateControl(false);
		setStateEmergency(false);
		setStateList(false);
		infoItem.setEnabled(false);
		Settings.state = Settings.State.NONE;
		llSwitch.removeAllViews();
		llSwitch.setVisibility(LinearLayout.GONE);

		displayArrow(R.id.tvF8_F15, "down");
		displayArrow(R.id.tvF16_F23, "down");
	}

	/**************************************************************************/
	/** Parse output **/
	/**************************************************************************/	

	public List<Train> parseTrainsList(String result) {

		List<Train> listTrain = new ArrayList<Train>();

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

			int idd = -1;
			try {
				idd = Integer.parseInt(id);
			}
			catch(Exception e) {

			}

			listTrain.add(new Train(idd, name, addr));
		}
		return listTrain;
	}

	public void parseTrainState(String[] result) {
		parseSpeed(result);
		parseDir(result);
	}

	public boolean parseSpeed(String[] list) {

		String id = "";
		int iid = 0;
		boolean match = false;


		Pattern p = Pattern.compile("(.*) speedindicator\\[(.*)\\]");

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);

			while (m.find() == true) {
				match = true;
				id = m.group(1).trim();
				try {
					iid = Integer.parseInt(id);
					Settings.getCurrentTrain().setSpeedIndicator(Integer.parseInt(m.group(2).trim()));
				}
				catch(Exception e) {	
				}
			}
		}


		p = Pattern.compile("(.*) speed\\[(.*)\\]");
		int speed = 0;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);

			while (m.find() == true) {
				match = true;
				id = m.group(1).trim();
				try {
					iid = Integer.parseInt(id);
					speed = Integer.parseInt(m.group(2).trim());
				}
				catch(Exception e) {	
				}

				if(iid == Settings.getCurrentTrain().getId()) {
					sbSpeed.setProgress(speed);
					displaySpeed(speed);
				}
			}
		}

		return match;
	}	

	public boolean parseDir(String[] list) {
		Pattern p = Pattern.compile("(.*) dir\\[(.*)\\]");
		String id = "";
		int iid = 0;

		String dir = "";
		boolean idir = true;
		boolean match = false;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);

			while (m.find() == true) {
				match = true;
				id = m.group(1).trim();
				try {
					iid = Integer.parseInt(id);
				}
				catch(Exception e) {	
				}
				dir = m.group(2).trim();

				if(iid == Settings.getCurrentTrain().getId()) {

					try {
						idir = Integer.parseInt(dir) == 0 ? true : false;
					}
					catch(Exception s) {
					}

					cbReverse.setChecked(!idir);
				}
			}
		}
		return match;
	}

	public boolean parseButtons(String[] list) {
		Pattern p = Pattern.compile("(.*) func\\[(.*),(.*)\\]");
		String id = "";
		int iid = 0;

		String btn = "";
		int ibtn = -1;
		String state = "";
		boolean istate = false;

		boolean match = false;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);

			while (m.find() == true) {
				match = true;
				id = m.group(1).trim();
				btn = m.group(2).trim();
				state = m.group(3).trim();
				try {
					iid = Integer.parseInt(id);
				}
				catch(Exception e) {		
				}

				if(iid == Settings.getCurrentTrain().getId()) {
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

					if(ibtn < listButtons.size()) {
						listButtons.get(ibtn).setChecked(istate);
					}
				}
			}
		}
		return match;
	}

	public void parseButtonSymbol(String[] result) {

		Pattern p = Pattern.compile("(.*) funcexists\\[(.*)\\]");

		for(int i=1; i<result.length-1; i++) {
			Matcher m = p.matcher(result[i]);

			int fctNum = -1;
			int fctSymbol = -1;

			while (m.find() == true) {

				String[] f = m.group(2).split(",");

				if(f.length >= 2) {
					fctNum = Integer.parseInt(f[0].trim());
					fctSymbol = Integer.parseInt(f[1].trim());

					if(fctSymbol == -1) {
						listButtons.get(fctNum).setEnabled(false);
					}
					else if(!FunctionSymbol.getInstance().getSymbols().get(fctSymbol,"").equals("")) {
						Resources res = getResources();
						int resourceId = res.getIdentifier("f"+fctSymbol, "drawable", getPackageName());
						Drawable img = res.getDrawable( resourceId );
						listButtons.get(fctNum).setCompoundDrawablesWithIntrinsicBounds(img, null , null, null);

						//check if moment function
						if(f.length == 3) {
							listButtons.get(fctNum).setTag(R.string.btn_type,"moment");
							listButtons.get(fctNum).setText(getString(R.string.btn_f) + fctNum + "*");
							listButtons.get(fctNum).setTextOn(getString(R.string.btn_f) + fctNum + "*");
							listButtons.get(fctNum).setTextOff(getString(R.string.btn_f) + fctNum + "*");
						}
						else {
							listButtons.get(fctNum).setTag(R.string.btn_type,"permanent");
						}
					}
				}
			}
		}
	}

	public void parseConsoleVersion(String[] result) {

		//read Protocol Version
		Pattern p = Pattern.compile("(.*) ProtocolVersion\\[(.*)\\]");

		for(int i=1; i<result.length-1; i++) {
			Matcher m = p.matcher(result[i]);

			while (m.find() == true) {
				Settings.protocolVersion = m.group(2).trim();
				protocolVersion.setText(Settings.protocolVersion);
			}
		}

		//read Application Version
		p = Pattern.compile("(.*) ApplicationVersion\\[(.*)\\]");

		for(int i=1; i<result.length-1; i++) {
			Matcher m = p.matcher(result[i]);

			while (m.find() == true) {
				String applicationVersionNumber = m.group(2).trim();
				applicationVersion.setText(applicationVersionNumber);
			}
		}

		//read Hardware Version
		p = Pattern.compile("(.*) HardwareVersion\\[(.*)\\]");

		for(int i=1; i<result.length-1; i++) {
			Matcher m = p.matcher(result[i]);

			while (m.find() == true) {
				String hardwareVersionNumber = m.group(2).trim();
				hardwareVersion.setText(hardwareVersionNumber);
			}
		}
	}

	public void parseEvent(String[] result) {

		Pattern p = Pattern.compile("<EVENT (.*)>");

		Matcher m = p.matcher(result[0]);
		while (m.find() == true) {

			try {
				int eventId = Integer.parseInt(m.group(1).trim());

				if(eventId == 1) {
					parseEmergency(result);
				}
				else if(eventId == Settings.getCurrentTrain().getId()) {
					parseSpeed(result);
					parseButtons(result);
					parseDir(result);
					parseLostControl(result);
				}
				else {
					parseSwitch(result);
				}
			}
			catch(Exception e) {
				return;
			}
		}
	}


	public boolean parseTrainsSymbol(String[] list) {
		//1000 symbol[LOCO_TYPE_DIESEL, IMAGE_TYPE_INT, 8]
		Pattern p = Pattern.compile("(.*) symbol\\[(.*), (.*), (.*)]");
		boolean match = false;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);
			int id = 0;
			String type = "";
			String category = "";
			int index = 0;

			while (m.find() == true) {
				match = true;
				try {
					id = Integer.parseInt(m.group(1).trim());
					category = m.group(2).trim();
					type = m.group(3).trim();
					index = Integer.parseInt(m.group(4).trim());

					if(type.equals("IMAGE_TYPE_INT")) {
						for (Train t : Settings.allTrains) {
							if(t.getId() == id) {
								t.setSymbol(index);
								dataAdapter.notifyDataSetChanged();
							}
						}
					}
					else if(type.equals("IMAGE_TYPE_USER")) {
						if(category.equals("LOCO_TYPE_DIESEL"))		index = 1;
						if(category.equals("LOCO_TYPE_STEAM"))		index = 0;
						if(category.equals("LOCO_TYPE_MISC"))		index = 3;
						if(category.equals("LOCO_TYPE_E"))			index = 2;

						for (Train t : Settings.allTrains) {
							if(t.getId() == id) {
								t.setSymbol(index);
								dataAdapter.notifyDataSetChanged();
							}
						}
					}
				}
				catch(Exception e) {
				}
			}
		}
		return match;
	}

	public boolean parseSwitch(String[] list) {
		Pattern p = Pattern.compile("(.*) state\\[(.*)\\]");
		boolean match = false;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);
			int id = 0;
			int state = 0;

			while (m.find() == true) {
				match = true;
				try {
					id = Integer.parseInt(m.group(1).trim());
					state = Integer.parseInt(m.group(2).trim());

					for(ToggleButton t : listSwitch) {
						if(Integer.parseInt(t.getTag().toString()) == id) {
							if(state == 1) {
								t.setChecked(true);
							}
							else {
								t.setChecked(false);
							}
						}
					}
					for(SeekBar t : listSwitchMulti) {
						if(Integer.parseInt(t.getTag().toString()) == id) {
							t.setProgress(state);

							for(TextView v: listSwitchMultiValue) {
								if(Integer.parseInt(v.getTag().toString()) == id) {
									v.setText(t.getProgress()+"");
								}
							}
						}
					}
				}
				catch(Exception e) {
				}
			}
		}
		return match;
	}

	public boolean parseSwitchSymbol(String[] list) {
		Pattern p = Pattern.compile("(.*) symbol\\[(.*)\\]");
		boolean match = false;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);
			int id = 0;
			int symbol = 0;

			while (m.find() == true) {
				match = true;
				try {
					id = Integer.parseInt(m.group(1).trim());
					symbol = Integer.parseInt(m.group(2).trim());

					for(ToggleButton t : listSwitch) {
						if(Integer.parseInt(t.getTag().toString()) == id) {

							if(!SwitchSymbol.getInstance().getSymbols().get(symbol,"").equals("")) {
								Resources res = getResources();
								int resourceId = res.getIdentifier("s"+symbol, "drawable", getPackageName());
								Drawable img = res.getDrawable(resourceId);
								t.setCompoundDrawablesWithIntrinsicBounds(img, null , null, null);
							}
						}
					}

					for(TextView v: listSwitchMultiLabel) {
						if(Integer.parseInt(v.getTag().toString()) == id) {
							if(!SwitchSymbol.getInstance().getSymbols().get(symbol,"").equals("")) {
								Resources res = getResources();
								int resourceId = res.getIdentifier("s"+symbol, "drawable", getPackageName());
								Drawable img = res.getDrawable(resourceId);
								v.setCompoundDrawablesWithIntrinsicBounds(img, null , null, null);
							}
						}
					}

				}
				catch(Exception e) {
				}
			}
		}
		return match;
	}

	public void parseSwitchList(String[] result) {
		((LinearLayout) findViewById(R.id.llSwitch)).removeAllViews();

		Pattern p = Pattern.compile("(.*) name1\\[\"(.*)\"\\] name2\\[\"(.*)\"\\] addrext\\[(.*)\\]");

		listSwitch = new ArrayList<ToggleButton>();
		listSwitchMulti = new ArrayList<SeekBar>();
		listSwitchMultiValue = new ArrayList<TextView>();
		listSwitchMultiLabel = new ArrayList<TextView>();

		String id = "";
		String name1 = "";
		String name2 = "";
		String addrext = "";
		for(int i=1; i<result.length-1; i++) {
			Matcher m = p.matcher(result[i]);

			while (m.find() == true) {
				id = m.group(1).trim();
				name1 = m.group(2).trim();
				name2 = m.group(3).trim();
				addrext = m.group(4).trim();

				String[] addr = addrext.split(",");
				//for 2 states create ToggleButton
				if(addr.length == 2) {
					ToggleButton tg = createButton(id, name1 + " " + name2);
					listSwitch.add(tg);
					((LinearLayout) findViewById(R.id.llSwitch)).addView(tg);
				}
				else { //for multi states create SeekBar
					SeekBar sb = createSeekBar(id, addr.length - 1);
					listSwitchMulti.add(sb);

					TextView name = new TextView(getApplicationContext());
					name.setText(name1 + " " + name2);
					name.setTag(id);
					listSwitchMultiLabel.add(name);

					TextView value = new TextView(getApplicationContext());
					value.setText(sb.getProgress()+"");
					value.setTag(id);
					value.setGravity(Gravity.CENTER);
					listSwitchMultiValue.add(value);

					((LinearLayout) findViewById(R.id.llSwitch)).addView(name);
					((LinearLayout) findViewById(R.id.llSwitch)).addView(sb);
					((LinearLayout) findViewById(R.id.llSwitch)).addView(value);
				}
			}
		}

		//get initial state
		for (ToggleButton t : listSwitch) {
			mTcpClient.getState(Integer.parseInt(t.getTag().toString()));
		}
		for (SeekBar s : listSwitchMulti) {
			mTcpClient.getState(Integer.parseInt(s.getTag().toString()));
		}
	}

	public boolean parseEmergency(String[] list) {
		Pattern p = Pattern.compile("(.*) status\\[(.*)\\]");
		String state = "";
		boolean match = false;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);
			while (m.find() == true) {
				match = true;
				state = m.group(2).trim();

				if(state.equals("GO")) {
					btnEmergency.setChecked(false);
				}
				else {
					btnEmergency.setChecked(true);
				}
			}
		}
		return match;
	}

	public boolean parseLostControl(String[] list) {
		Pattern p = Pattern.compile("(.*) msg\\[(.*)\\]");
		String event = "";
		boolean match = false;

		for(int i=1; i<list.length-1; i++) {
			Matcher m = p.matcher(list[i]);
			while (m.find() == true) {
				match = true;
				event = m.group(2).trim();

				if(event.equals("CONTROL_LOST")) {
					btnControl.setChecked(false);
					setStateButtons(false);
				}
			}
		}
		return match;
	}

	/**************************************************************************/
	/** Utils **/
	/**************************************************************************/
	public void sortTrainsList(boolean sortById) {
		if(sortById) {
			Collections.sort(Settings.allTrains, Train.TrainIdComparator);
		}
		else {
			Collections.sort(Settings.allTrains, Train.TrainNameComparator);
		}
	}

	public static boolean checkSig(Context context) {
		boolean match = false;
		if (context.getPackageManager().checkSignatures(
				LITE_PACKAGE, FULL_PACKAGE) == PackageManager.SIGNATURE_MATCH)
			match = true;
		return match;
	}

	public ToggleButton createButton(String id, String name) {

		ToggleButton tg = new ToggleButton(getApplicationContext());
		tg.setText(name);
		tg.setTextOn(name);
		tg.setTextOff(name);
		tg.setTag(id);
		tg.setOnClickListener(this);

		return tg;
	}

	public void initFunctionButtons() {
		for(int i=0; i<listButtons.size(); i++) {
			listButtons.get(i).setOnClickListener(this);
			listButtons.get(i).setOnTouchListener(this);
			listButtons.get(i).setTag(R.string.btn_name,"btn;"+i);
			listButtons.get(i).setText(getString(R.string.btn_f) + i);
			listButtons.get(i).setTextOn(getString(R.string.btn_f) + i);
			listButtons.get(i).setTextOff(getString(R.string.btn_f) + i);
			listButtons.get(i).setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
		}
	}

	public SeekBar createSeekBar(String id, int max) {

		SeekBar sb = new SeekBar(getApplicationContext());
		sb.setMax(max);
		sb.setProgress(0);
		sb.setTag(id);
		sb.setOnSeekBarChangeListener(this);

		return sb;
	}

	public void displaySpeed(int speed) {
		int speedIndicator = Settings.getCurrentTrain().getSpeedIndicator();
		if(speedIndicator == 0) {
			tvSpeed.setText(getApplicationContext().getString(
					R.string.tv_speed) + " " + speed + "/" + Settings.SPEED_MAX);
		}
		else {
			tvSpeed.setText(getApplicationContext().getString(
					R.string.tv_speed) + " " + speed + "/" + Settings.SPEED_MAX + 
					" (" + ((int)speedIndicator*speed/Settings.SPEED_MAX) + "/" + speedIndicator + "km/h)");
		}
	}

	public void displayError(String error) {
		Toast.makeText(this, error , Toast.LENGTH_SHORT).show();
	}

	/**************************************************************************/
	/** Change speed with volume button **/
	/**************************************************************************/

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_UP) {
				changeSpeed(true);
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (action == KeyEvent.ACTION_DOWN) {
				changeSpeed(false);
			}
			return true;
		default:
			return super.dispatchKeyEvent(event);
		}
	}

	public void changeSpeed(boolean increase) {

		if(!Settings.fullVersion) {
			return;
		}

		if(sbSpeed.isEnabled()) {
			int current_value = sbSpeed.getProgress();
			int new_value = current_value;

			if(increase) {
				new_value = current_value + speedStep;
				if(new_value > Settings.SPEED_MAX) {
					new_value = Settings.SPEED_MAX;
				}
				sbSpeed.setProgress(new_value);
			}
			else {
				new_value = current_value - speedStep;
				if(new_value < Settings.SPEED_MIN) {
					new_value = Settings.SPEED_MIN;
				}
				sbSpeed.setProgress(new_value);

			}
			mTcpClient.setSpeed(sbSpeed.getProgress());
			displaySpeed(sbSpeed.getProgress());
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent me) {

		if(v.getTag(R.string.btn_type) != null) {
			if(v.getTag(R.string.btn_type).toString().equals("moment")) {
				String token[] = v.getTag(R.string.btn_name).toString().split(";");
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					mTcpClient.setButton(Integer.parseInt(token[1]), true);
					((ToggleButton) v).setChecked(true);
					return true;
				}
				else if (me.getAction() == MotionEvent.ACTION_UP) {
					mTcpClient.setButton(Integer.parseInt(token[1]), false);
					((ToggleButton) v).setChecked(false);
					return true;
				}
			}
		}
		return false;
	}

	private void getTrainsSymbol() {
		List<Train> list = Settings.allTrains;

		for (Train train : list) {
			mTcpClient.getTrainSymbol(train.getId());
		}
	}
}

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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
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
import com.ecos.train.object.Symbols;
import com.ecos.train.object.Train;
import com.ecos.train.ui.SpinAdapter;

public class TrainManagerActivity 
extends SherlockActivity 
implements OnClickListener, OnSeekBarChangeListener, OnItemSelectedListener {

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
	TextView tvSwitching = null;
	LinearLayout llSwitch = null;

	SpinAdapter dataAdapter;
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
	List<ToggleButton> listButtons = new ArrayList<ToggleButton>();

	private static final int SETTINGS = 0;

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

		listButtons.add(((ToggleButton) findViewById(R.id.btnF0)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF1)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF2)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF3)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF4)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF5)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF6)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF7)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF8)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF9)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF10)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF11)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF12)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF13)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF14)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF15)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF16)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF17)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF18)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF19)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF20)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF21)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF22)));
		listButtons.add(((ToggleButton) findViewById(R.id.btnF23)));

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

		tvSwitching = ((TextView) infoView.findViewById(R.id.tvSwitch));
		((TextView) findViewById(R.id.tvSwitch)).setOnClickListener(this);
		llSwitch = ((LinearLayout) findViewById(R.id.llSwitch));
		llSwitch.setVisibility(LinearLayout.GONE);

	}

	private void initFunctionButtons() {
		for(int i=0; i<listButtons.size(); i++) {
			listButtons.get(i).setOnClickListener(this);
			listButtons.get(i).setTag("btn;"+i);
			if(i>0) {
				listButtons.get(i).setText(getString(R.string.btn_f) + i);
				listButtons.get(i).setTextOn(getString(R.string.btn_f) + i);
				listButtons.get(i).setTextOff(getString(R.string.btn_f) + i);
				listButtons.get(i).setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
			}
		}
	}

	@Override
	public void onClick(View v) {

		//function buttons
		if(v.getTag() != null) {
			if(v.getTag().toString().startsWith("btn")) {
				String token[] = v.getTag().toString().split(";");
				mTcpClient.setButton(Integer.parseInt(
						token[1]), ((ToggleButton) v).isChecked());
				return;
			}
		}

		if(v.getId() == R.id.btnEmergency) {
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
		else if(v.getId() == R.id.tvF8_F15) {
			LinearLayout l = (LinearLayout) findViewById(R.id.llF8_F15);
			if(l.getVisibility() == LinearLayout.VISIBLE) {
				l.setVisibility(LinearLayout.GONE);
			}
			else {
				l.setVisibility(LinearLayout.VISIBLE);
			}
		}
		else if(v.getId() == R.id.tvF16_F23) {
			LinearLayout l = (LinearLayout) findViewById(R.id.llF16_F23);
			if(l.getVisibility() == LinearLayout.VISIBLE) {
				l.setVisibility(LinearLayout.GONE);
			}
			else {
				l.setVisibility(LinearLayout.VISIBLE);
			}
		}
		else if(v.getId() == R.id.cbReverse) {
			mTcpClient.setDir(((ToggleButton) v).isChecked()?1:0);
		}
		else if(v.getId() == R.id.tvSwitch) {

			if(llSwitch.getVisibility() == LinearLayout.VISIBLE) {
				llSwitch.setVisibility(LinearLayout.GONE);
				return;
			}
			else {
				llSwitch.setVisibility(LinearLayout.VISIBLE);
				if(Settings.fullVersion && Settings.state == Settings.State.IDLE) {
					mTcpClient.getAllObject();
				}
			}
		}
		else {
			mTcpClient.changeState(Integer.parseInt(v.getTag().toString()), ((ToggleButton) v).isChecked()?1 :0);
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
		if(sb.getId() == R.id.sbSpeed) {
			mTcpClient.setSpeed(sb.getProgress());
			tvSpeed.setText(this.getString(R.string.tv_speed) + " " + sb.getProgress());
		}
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
		MenuInflater inflater = getSupportMenuInflater();
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
		if(Settings.currentTrain.getId() == -1) {
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
			startActivityForResult(i, TrainManagerActivity.SETTINGS);
			return true;
		case R.id.iPack:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="+FULL_PACKAGE));
			startActivity(intent);
			return true;
		case R.id.iEdit:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			LayoutInflater inflater = getLayoutInflater();
			final View dialogView = inflater.inflate(R.layout.edit_form, null);
			final EditText edName = ((EditText)dialogView.findViewById(R.id.edName));
			final TextView edId = ((TextView)dialogView.findViewById(R.id.tv_id));
			final TextView edAddress = ((TextView)dialogView.findViewById(R.id.tv_address));
			edName.setText(Settings.currentTrain.getName());
			edId.setText(Settings.currentTrain.getId()+"");
			edAddress.setText(Settings.currentTrain.getAddress());

			builder.setView( dialogView);
			builder.setTitle(getString(R.string.btn_edit))
			.setPositiveButton(R.string.tv_save, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					String name = edName.getText().toString();
					mTcpClient.setName(name);

					for (Train t : Settings.allTrains) {
						if(t.getId() == Settings.currentTrain.getId()) {
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
			infoDialog.show();

			try {
				PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
				ecosVersion.setText(manager.versionName);
			} catch (Exception e) { }

			mTcpClient.getInfo();

			return true;
		case R.id.iContact:
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
					"mailto", TrainManagerActivity.CONTACT, null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ECoS Controller Feedback");
			startActivity(Intent.createChooser(emailIntent, "Send email..."));

			return true;
			/*case R.id.iReport:
			ACRA.getErrorReporter().handleException(null);
			return true;*/
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == TrainManagerActivity.SETTINGS) {
			//check if something change
			if(!Settings.consoleIp.equals(pref.getString("ip", ""))){
				disconnect();
				return;
			}

			if(Settings.sortById != pref.getBoolean("pref_sort", false)) {
				Settings.sortById = pref.getBoolean("pref_sort", false);
				if(Settings.state != Settings.State.NONE) {
					sortList(Settings.sortById);
					dataAdapter.notifyDataSetChanged();
					//restore the latest selection
					for(int i=0; i<Settings.allTrains.size(); i++) {
						if(Settings.allTrains.get(i).getId() == Settings.currentTrain.getId()) {
							sTrainId.setSelection(i);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

		setStateButtons(false);

		//release old train
		if(Settings.currentTrain.getId() != -1) {
			mTcpClient.releaseViewTrain();
			mTcpClient.releaseControl();
		}

		//get train and take control
		Settings.currentTrain = ((Train)parent.getItemAtPosition(pos));

		mTcpClient.takeControl();
		btnControl.setChecked(true);
		mTcpClient.takeViewTrain();
		Settings.state = Settings.State.GET_TRAIN_MAIN_STATE;
		mTcpClient.getTrainMainState();
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
				if(values[0].equals("READY")) {
					setStateList(true);
					setStateEmergency(true);

					mTcpClient.viewConsole();

					tvState.setText(getApplicationContext().getString(R.string.tv_state) + " " + 
							getApplicationContext().getString(R.string.tv_connect));
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
					Settings.allTrains = getAllTrains(values[0]);

					Settings.sortById = pref.getBoolean("pref_sort", false);
					sortList(Settings.sortById);

					dataAdapter = new SpinAdapter(getApplicationContext(),
							android.R.layout.simple_spinner_item, Settings.allTrains);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sTrainId.setAdapter(dataAdapter);
				}
			}
			else if(Settings.state == Settings.State.GET_TRAIN_MAIN_STATE) {
				if(respLine[0].equals("<REPLY get("+Settings.currentTrain.getId()+",name,speed,dir)>")) {
					Settings.state = Settings.State.GET_TRAIN_BUTTON_STATE;
					getTrainMainState(respLine);
					initFunctionButtons();
					mTcpClient.getTrainButtonState();
				}
			}
			else if(Settings.state == Settings.State.GET_TRAIN_BUTTON_STATE) {
				if(respLine[0].equals("<REPLY get("+Settings.currentTrain.getId()+",func[0],func[1],func[2],func[3]," +
						"func[4],func[5],func[6],func[7])>")) {
					Settings.state = Settings.State.GET_TRAIN_BUTTON_STATE_F8_F15;
					getTrainButtonState(respLine);
					mTcpClient.getTrainButtonStateF8F15();
				}
			}
			else if(Settings.state == Settings.State.GET_TRAIN_BUTTON_STATE_F8_F15) {
				if(respLine[0].equals("<REPLY get("+Settings.currentTrain.getId()+",func[8],func[9],func[10],func[11]," +
						"func[12],func[13],func[14],func[15])>")) {
					Settings.state = Settings.State.GET_TRAIN_BUTTON_STATE_F16_F23;
					getTrainButtonStateF8F15(respLine);
					mTcpClient.getTrainButtonStateF16F23();
				}
			}
			else if(Settings.state == Settings.State.GET_TRAIN_BUTTON_STATE_F16_F23) {
				if(respLine[0].equals("<REPLY get("+Settings.currentTrain.getId()+",func[16],func[17],func[18],func[19]," +
						"func[20],func[21],func[22],func[23])>")) {
					Settings.state = Settings.State.IDLE;
					getTrainButtonStateF16F23(respLine);
					mTcpClient.getButtonName();
				}
			}
			else if(Settings.state == Settings.State.IDLE) {
				if(respLine[0].startsWith("<EVENT")) {	//manage event
					parseEvent(respLine);
				}
				else if(respLine[0].equals("<REPLY get(1, info)>")) {
					getInfo(respLine);
				}
				else if(respLine[0].equals("<REPLY queryObjects(11, name1, name2, addrext)>")) {
					getSwitching(respLine);
				}
				else if(respLine[0].equals("<REPLY get("+Settings.currentTrain.getId()+", funcexists[0], " +
						"funcexists[1], funcexists[2], funcexists[3], funcexists[4], funcexists[5], funcexists[6], funcexists[7])>")){
					getButtonName(respLine);
				}
				else {
					parseEventSwitch(respLine);
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
		else {	//disconnect
			disconnect();
		}
	}

	public void disconnect() {
		if(mTcpClient != null) {
			mTcpClient.stopClient();
		}
		tvState.setText(getString(R.string.tv_state) + " " + getString(R.string.tv_disconnect));
		setStateButtons(false);
		setStateControl(false);
		setStateEmergency(false);
		setStateList(false);
		infoItem.setEnabled(false);
		Settings.state = Settings.State.NONE;
		llSwitch.removeAllViews();
	}

	/**************************************************************************/
	/** Get state **/
	/**************************************************************************/	

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

	public List<Train> getAllTrains(String result) {

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

	public void getTrainMainState(String[] result) {

		//speed
		int index1 = result[2].lastIndexOf('[');
		int index2 = result[2].lastIndexOf(']');

		int speed = 0;
		try {
			speed = Integer.parseInt(result[2].substring(index1+1, index2));
		}
		catch(Exception s) {

		}
		sbSpeed.setProgress(speed);
		tvSpeed.setText(this.getString(R.string.tv_speed) + speed);


		//direction
		index1 = result[3].lastIndexOf('[');
		index2 = result[3].lastIndexOf(']');

		boolean dir = false;
		try {
			dir = Integer.parseInt(result[3].substring(index1+1, index2)) == 0
					? true : false;
		}
		catch(Exception s) {
		}

		cbReverse.setChecked(!dir);
	}

	public void getTrainButtonState(String[] result) {

		List<Boolean> state = new ArrayList<Boolean>();

		for(int i=1;i<9;i++) {
			int index1 = result[i].lastIndexOf(' ');
			int index2 = result[i].lastIndexOf(']');
			boolean s = false;
			try {
				s = Integer.parseInt(result[i].substring(index1+1, index2)) == 1
						? true : false;
			}
			catch(Exception e) {
			}
			state.add(s);
		}

		for(int i=0; i<=7; i++) {
			listButtons.get(i).setChecked(state.get(i));
		}
	}

	public void getTrainButtonStateF8F15(String[] result) {

		List<Boolean> state = new ArrayList<Boolean>();

		for(int i=1;i<9;i++) {
			int index1 = result[i].lastIndexOf(' ');
			int index2 = result[i].lastIndexOf(']');
			boolean s = false;
			try {
				s = Integer.parseInt(result[i].substring(index1+1, index2)) == 1
						? true : false;
			}
			catch(Exception e) {
			}
			state.add(s);
		}

		for(int i=0; i<=7; i++) {
			listButtons.get(i+8).setChecked(state.get(i));
		}

	}

	public void getTrainButtonStateF16F23(String[] result) {

		List<Boolean> state = new ArrayList<Boolean>();

		for(int i=1;i<9;i++) {
			int index1 = result[i].lastIndexOf(' ');
			int index2 = result[i].lastIndexOf(']');
			boolean s = false;
			try {
				s = Integer.parseInt(result[i].substring(index1+1, index2)) == 1
						? true : false;
			}
			catch(Exception e) {
			}
			state.add(s);
		}

		for(int i=0; i<=7; i++) {
			listButtons.get(i+16).setChecked(state.get(i));
		}

		//activate button
		setStateButtons(true);
		setStateList(true);
		setStateControl(true);
	}

	private void getButtonName(String[] result) {
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
					else if(!Symbols.getInstance().getSymbols().get(fctSymbol,"").equals("")) {
						Resources res = getResources();
						int resourceId = res.getIdentifier("f"+fctSymbol, "drawable", getPackageName());
						Drawable img = res.getDrawable( resourceId );
						listButtons.get(fctNum).setCompoundDrawablesWithIntrinsicBounds(img, null , null, null);
					}
				}
			}
		}
	}

	public void getSwitching(String[] result) {
		((LinearLayout) findViewById(R.id.llSwitch)).removeAllViews();

		Pattern p = Pattern.compile("(.*) name1\\[\"(.*)\"\\] name2\\[\"(.*)\"\\] addrext\\[(.*)\\]");

		listSwitch = new ArrayList<ToggleButton>();
		listSwitchMulti = new ArrayList<SeekBar>();
		listSwitchMultiValue = new ArrayList<TextView>();

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
				if(addr.length == 2) {
					ToggleButton tg = createButton(id, name1 + " " + name2);
					listSwitch.add(tg);
					((LinearLayout) findViewById(R.id.llSwitch)).addView(tg);
				}
				else {
					SeekBar sb = createSeekBar(id, addr.length - 1);
					listSwitchMulti.add(sb);

					TextView name = new TextView(getApplicationContext());
					name.setText(name1 + " " + name2);

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
	}

	public void getInfo(String[] result) {

		//read Protocol Version
		Pattern p = Pattern.compile("(.*) ProtocolVersion\\[(.*)\\]");

		for(int i=1; i<result.length-1; i++) {
			Matcher m = p.matcher(result[i]);

			while (m.find() == true) {
				protocolVersion.setText(m.group(2).trim());
			}
		}

		//read Application Version
		p = Pattern.compile("(.*) ApplicationVersion\\[(.*)\\]");

		for(int i=1; i<result.length-1; i++) {
			Matcher m = p.matcher(result[i]);

			while (m.find() == true) {
				applicationVersion.setText(m.group(2).trim());
			}
		}

		//read Hardware Version
		p = Pattern.compile("(.*) HardwareVersion\\[(.*)\\]");

		for(int i=1; i<result.length-1; i++) {
			Matcher m = p.matcher(result[i]);

			while (m.find() == true) {
				hardwareVersion.setText(m.group(2).trim());
			}
		}
	}

	/**************************************************************************/
	/** Parse events **/
	/**************************************************************************/

	public void parseEvent(String[] result) {

		Pattern p = Pattern.compile("<EVENT (.*)>");

		Matcher m = p.matcher(result[0]);
		while (m.find() == true) {

			try {
				int eventId = Integer.parseInt(m.group(1).trim());

				if(eventId == 1) {
					parseEventEmergency(result);
				}
				else if(eventId == Settings.currentTrain.getId()) {
					if(parseEventSpeed(result))			return;
					if(parseEventButtons(result))		return;
					if(parseEventDir(result))			return;
					if(parseEventLostControl(result))	return;
				}
				else {
					parseEventSwitch(result);
				}
			}
			catch(Exception e) {
				return;
			}
		}
	}

	public boolean parseEventSpeed(String[] list) {
		Pattern p = Pattern.compile("(.*) speed\\[(.*)\\]");
		String id = "";
		int iid = 0;
		boolean match = false;

		String speed = "";

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
				speed = m.group(2).trim();

				if(iid == Settings.currentTrain.getId()) {
					int ispeed = Integer.parseInt(speed);
					sbSpeed.setProgress(ispeed);
					tvSpeed.setText(getApplicationContext().getString(R.string.tv_speed) + speed);
				}
			}
		}
		return match;
	}

	public boolean parseEventSwitch(String[] list) {
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

	public boolean parseEventButtons(String[] list) {
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

				if(iid == Settings.currentTrain.getId()) {
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

	public boolean parseEventDir(String[] list) {
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

				if(iid == Settings.currentTrain.getId()) {

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

	public boolean parseEventEmergency(String[] list) {
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
		return match;
	}

	public boolean parseEventLostControl(String[] list) {
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
	public void sortList(boolean sortById) {
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

	public SeekBar createSeekBar(String id, int max) {

		SeekBar sb = new SeekBar(getApplicationContext());
		sb.setMax(max);
		sb.setProgress(0);
		sb.setTag(id);
		sb.setOnSeekBarChangeListener(this);

		return sb;
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
			int step = Settings.SPEED_STEP;

			if(increase) {
				new_value = current_value + step;
				if(new_value > Settings.SPEED_MAX) {
					new_value = Settings.SPEED_MAX;
				}
				sbSpeed.setProgress(new_value);
			}
			else {
				new_value = current_value - step;
				if(new_value < Settings.SPEED_MIN) {
					new_value = Settings.SPEED_MIN;
				}
				sbSpeed.setProgress(new_value);

			}
			mTcpClient.setSpeed(sbSpeed.getProgress());
			tvSpeed.setText(this.getString(R.string.tv_speed) + " " + sbSpeed.getProgress());
		}
	}
}

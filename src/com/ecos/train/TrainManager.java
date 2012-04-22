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

package com.ecos.train;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class TrainManager 
extends Activity 
implements OnClickListener, OnSeekBarChangeListener, OnCheckedChangeListener {

	private static int DEFAULT_PORT = 15471;
	private static int DEFAULT_TRAINID = 1000;
			
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		TextView tvState = (TextView) findViewById(R.id.tvState);
		ToggleButton btnControl = (ToggleButton) findViewById(R.id.btnControl);
		ToggleButton btnF0 = (ToggleButton) findViewById(R.id.btnF0);
		ToggleButton btnF1 = (ToggleButton) findViewById(R.id.btnF1);
		CheckBox cbReverse = (CheckBox) findViewById(R.id.cbReverse);
		TextView tvSpeed = (TextView) findViewById(R.id.tvSpeed);
		SeekBar sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
		tvSpeed.setText(this.getString(R.string.tv_speed) + " 0");
		tvState.setText(this.getString(R.string.tv_state) + " " + this.getString(R.string.tv_disconnect));
		sbSpeed.setEnabled(false);
		btnF0.setEnabled(false);
		btnF1.setEnabled(false);
		cbReverse.setEnabled(false);
		sbSpeed.setEnabled(false);

		btnControl.setOnClickListener(this);
		btnF0.setOnClickListener(this);
		btnF1.setOnClickListener(this);
		cbReverse.setOnCheckedChangeListener(this);
		sbSpeed.setOnSeekBarChangeListener(this);


		//restore previous state
		if(savedInstanceState != null) {
		
			btnControl.setChecked(savedInstanceState.getBoolean("btnControl"));

			if(btnControl.isChecked()) {
				btnF0.setEnabled(true);
				btnF1.setEnabled(true);
				cbReverse.setEnabled(true);
				sbSpeed.setEnabled(true);
			}

			cbReverse.setChecked(savedInstanceState.getBoolean("cbReverse"));
			sbSpeed.setProgress(savedInstanceState.getInt("sbSpeed"));
			tvSpeed.setText(savedInstanceState.getString("tvSpeed"));
			tvState.setText(savedInstanceState.getString("tvState"));
		}
		
		TrainManagerController.setActivity(this);
	}

	@Override
	public void onClick(View v) {
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		//check trainID
		int trainId = TrainManager.DEFAULT_TRAINID;
		try{
			trainId = Integer.parseInt(pref.getString("id", TrainManager.DEFAULT_TRAINID+""));
		}
		catch(Exception e) {
			trainId = TrainManager.DEFAULT_TRAINID;
		}
		
		TrainManagerController.trainId = trainId;
		
		if(v.getId() == R.id.btnF0) {	//light
			TrainManagerController.getInstance().setButton(0, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnF1) {
			TrainManagerController.getInstance().setButton(1, ((ToggleButton) v).isChecked());
		}
		else if(v.getId() == R.id.btnControl) {

			TextView tvState = (TextView) findViewById(R.id.tvState);
			ToggleButton btnF0 = (ToggleButton) findViewById(R.id.btnF0);
			ToggleButton btnF1 = (ToggleButton) findViewById(R.id.btnF1);
			CheckBox cbReverse = (CheckBox) findViewById(R.id.cbReverse);
			SeekBar sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
			TextView tvSpeed = (TextView) findViewById(R.id.tvSpeed);

			if(((ToggleButton) v).isChecked()) {

				String consoleIp = pref.getString("ip", "");

				//check port
				int consolePort = TrainManager.DEFAULT_PORT;
				try{
					consolePort = Integer.parseInt(
							pref.getString("port", TrainManager.DEFAULT_PORT+""));
				}
				catch(Exception e) {
					consolePort = TrainManager.DEFAULT_PORT;
				}
				
				try {
					TrainManagerController.getInstance().openSocket(consoleIp, consolePort,trainId);
					tvState.setText(
							this.getString(R.string.tv_state) + " " + this.getString(R.string.tv_connect));
					TrainManagerController.getInstance().setConnected(true);
				} catch (IOException e) {
					tvState.setText(
							this.getString(R.string.tv_state) + " " + e.getMessage());
					TrainManagerController.getInstance().setConnected(false);
				}

				if(TrainManagerController.getInstance().isConnected()) {
					TrainManagerController.getInstance().takeControl();

					btnF0.setEnabled(true);
					btnF1.setEnabled(true);
					cbReverse.setEnabled(true);
					sbSpeed.setEnabled(true);

					btnF0.setChecked(TrainManagerController.getInstance().getButton(0));
					btnF1.setChecked(TrainManagerController.getInstance().getButton(1));
					cbReverse.setChecked(!TrainManagerController.getInstance().getDir());
					int speed = TrainManagerController.getInstance().getSpeed();
					sbSpeed.setProgress(speed);
					tvSpeed.setText(this.getString(R.string.tv_speed) + speed);
				}
				else {
					((ToggleButton) v).setChecked(false);
				}
			}
			else {

				if(TrainManagerController.getInstance().isConnected()) {
					TrainManagerController.getInstance().releaseControl();

					try {
						tvState.setText(
								this.getString(R.string.tv_state) + " " + this.getString(R.string.tv_disconnect));
						TrainManagerController.getInstance().closeSocket();
						TrainManagerController.getInstance().setConnected(false);
					} catch (Exception e) {
						tvState.setText(
								this.getString(R.string.tv_state) + " " + e.getMessage());
						TrainManagerController.getInstance().setConnected(false);
					}

					btnF0.setEnabled(false);
					btnF1.setEnabled(false);
					cbReverse.setEnabled(false);
					sbSpeed.setEnabled(false);
				}
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
		TrainManagerController.getInstance().setSpeed(sb.getProgress());
		((TextView) findViewById(R.id.tvSpeed)).setText(this.getString(R.string.tv_speed) + " " + sb.getProgress());
	}

	@Override
	public void onCheckedChanged(CompoundButton cb, boolean state) {
		if(cb.getId() == R.id.cbReverse) {
			TrainManagerController.getInstance().setDir(((CheckBox) cb).isChecked()?1:0);
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
			Intent i = new Intent(this, Preferences.class);
			startActivity(i);
			return true;
		case R.id.iAbout:
			try {
				PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
				Toast toast = Toast.makeText(
						this, this.getString(R.string.app_name) + " " + manager.versionName , 1000);
				toast.show();
			} catch (Exception e) {
				//
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		TextView tvState = (TextView) findViewById(R.id.tvState);
		ToggleButton btnControl = (ToggleButton) findViewById(R.id.btnControl);
		ToggleButton btnF0 = (ToggleButton) findViewById(R.id.btnF0);
		ToggleButton btnF1 = (ToggleButton) findViewById(R.id.btnF1);
		CheckBox cbReverse = (CheckBox) findViewById(R.id.cbReverse);
		TextView tvSpeed = (TextView) findViewById(R.id.tvSpeed);
		SeekBar sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);

		outState.putBoolean("btnControl", btnControl.isChecked());
		outState.putBoolean("btnF0", btnF0.isChecked());
		outState.putBoolean("btnF1", btnF1.isChecked());
		outState.putBoolean("cbReverse", cbReverse.isChecked());
		outState.putInt("sbSpeed", sbSpeed.getProgress());
		outState.putString("tvSpeed", tvSpeed.getText()+"");
		outState.putString("tvState", tvState.getText()+"");

		super.onSaveInstanceState(outState);
	}


	@Override
	protected void onStop() {
		super.onStop();
		try {
			//TrainManagerController.getInstance().setConnected(false);
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 * @param error
	 */
	public void displayError(String error) {
		Toast.makeText(this, error , 2000).show();
	}
}